/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016-2018 Peter Keeler
 *
 * This file is part of EmergentMUD.
 *
 * EmergentMUD is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EmergentMUD is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.emergentmud.core.service.maze;

import com.emergentmud.core.model.Direction;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.Zone;
import com.emergentmud.core.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Algorithm inspired by two of Jamis Buck's blog articles.
 * http://weblog.jamisbuck.org/2011/1/27/maze-generation-growing-tree-algorithm#
 * http://weblog.jamisbuck.org/2015/10/31/mazes-blockwise-geometry.html
 */
@Component
public class GrowingTreeMazeStrategy implements ZoneFillStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrowingTreeMazeStrategy.class);
    private static final List<Direction> DIRECTIONS;

    static {
        DIRECTIONS = new ArrayList<>(Direction.DIRECTIONS);
    }

    private RoomRepository roomRepository;
    private Map<String, CellSelectionStrategy> cellSelectionStrategies;
    private LinkedList<Cell> queue = new LinkedList<>();
    private List<Cell> carvedRooms = new ArrayList<>();

    @Inject
    public GrowingTreeMazeStrategy(RoomRepository roomRepository, Map<String, CellSelectionStrategy> cellSelectionStrategies) {
        this.roomRepository = roomRepository;
        this.cellSelectionStrategies = cellSelectionStrategies;
    }

    @Override
    public Room fillZone(Zone zone, Long x, Long y, Long z) {
        long start = System.currentTimeMillis();
        CellSelectionStrategy selectionStrategy = cellSelectionStrategies.get(zone.getBiome().getCellSelectionStrategy());
        Cell current = new Cell(x, y, z);

        // queue the first room
        queue.addFirst(current);
        LOGGER.debug("Carved initial cell: {}", current);

        while (!queue.isEmpty()) {
            current = selectionStrategy.selectCell(queue);
            LOGGER.debug("Selected cell from queue: {}", current);

            Cell neighbor = selectValidNeighbor(current, zone);

            if (neighbor == null) {
                carvedRooms.add(current);
                queue.remove(current);

                LOGGER.debug("Removed cell from queue: {}", current);
            } else {
                queue.add(neighbor);

                LOGGER.debug("Queued new cell: {}", neighbor);
            }
        }

        LOGGER.debug("Building rooms from cells...");

        List<Room> roomBatch = new ArrayList<>();

        carvedRooms.forEach(cell -> {
            Room room = new Room();

            room.setLocation(cell.getX(), cell.getY(), cell.getZ());
            room.setZone(zone);

            roomBatch.add(room);
        });

        roomRepository.save(roomBatch);

        LOGGER.debug("Generated maze in {} ms", System.currentTimeMillis() - start);
        return roomRepository.findByXAndYAndZ(x, y, z);
    }

    private Cell selectValidNeighbor(Cell current, Zone zone) {
        Collections.shuffle(DIRECTIONS);

        for (Direction direction : DIRECTIONS) {
            Cell target = new Cell(
                    current.getX() + direction.getX(),
                    current.getY() + direction.getY(),
                    current.getZ() + direction.getZ()
            );

            if (carvedRooms.contains(target)) {
                LOGGER.debug("Cannot select neighbor: cell is already carved");
                continue;
            }

            if (queue.contains(target)) {
                LOGGER.debug("Cannot select neighbor: cell is already in the queue");
                continue;
            }

            if (!isWithinZone(target, zone)) {
                LOGGER.debug("Cannot select neighbor: it is outside our zone");
                continue;
            }

            if (countNeighbors(target, zone) > 1) {
                LOGGER.debug("Cannot select neighbor: it has another neighbor");
                continue;
            }

            LOGGER.debug("Selected neighbor: {}", target);
            return target;
        }

        LOGGER.debug("No valid neighbors to select");
        return null;
    }

    private int countNeighbors(Cell query, Zone zone) {
        int neighbors = 0;

        for (Direction direction : Direction.DIRECTIONS) {
            Cell search = new Cell(
                    query.getX() + direction.getX(),
                    query.getY() + direction.getY(),
                    query.getZ() + direction.getZ()
            );

            if (isWithinZone(search, zone) && (carvedRooms.contains(search) || queue.contains(search))) {
                neighbors++;
            }
        }

        LOGGER.debug("Cell {} has {} neighbors", query, neighbors);

        return neighbors;
    }

    private boolean isWithinZone(Cell query, Zone zone) {
        return query.getX() >= zone.getBottomLeftX()
                && query.getX() <= zone.getTopRightX()
                && query.getY() >= zone.getBottomLeftY()
                && query.getY() <= zone.getTopRightY();

    }
}