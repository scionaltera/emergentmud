/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016 Peter Keeler
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

package com.emergentmud.core.repository.zonebuilder;

import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.Zone;
import com.emergentmud.core.repository.RoomRepository;
import com.emergentmud.core.repository.ZoneBuilder;
import com.emergentmud.core.repository.ZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class DrunkWalkZoneBuilder implements ZoneBuilder {
    static final int ZONE_SIZE = 100;

    private static final Logger LOGGER = LoggerFactory.getLogger(DrunkWalkZoneBuilder.class);
    private static final int MAX_ITERATIONS = 50;
    private static final Random RANDOM = new Random();
    private static final List<int[]> DIRECTIONS = new ArrayList<>();

    private ZoneRepository zoneRepository;
    private RoomRepository roomRepository;

    @Inject
    public DrunkWalkZoneBuilder(ZoneRepository zoneRepository, RoomRepository roomRepository) {
        this.zoneRepository = zoneRepository;
        this.roomRepository = roomRepository;

        DIRECTIONS.add(new int[] {1, 1});
        DIRECTIONS.add(new int[] {-1, 1});
        DIRECTIONS.add(new int[] {1, 0});
        DIRECTIONS.add(new int[] {-1, 0});
    }

    @Override
    public Zone build(Long x, Long y, Long z) {
        int iterations = 0;
        List<Room> in = new ArrayList<>();
        List<Room> out = new ArrayList<>();
        Map<String, Room> rooms = new HashMap<>();

        in.add(createRoom(rooms, x, y, z));

        while (!in.isEmpty() && out.size() < ZONE_SIZE) {
            Room room = in.remove(in.size() - 1);
            int neighbors = countNeighbors(room);
            int limit = 1;
            double chance = RANDOM.nextDouble() + (iterations * 0.01);

            if (chance > 0.90) {
                limit++;
            }

            LOGGER.trace("Chance: {}", chance);
            LOGGER.trace("Assigning {} neighbors to ({}, {}, {})", limit, room.getX(), room.getY(), room.getZ());

            Collections.shuffle(DIRECTIONS);

            for (int[] direction : DIRECTIONS) {
                if (neighbors < limit) {
                    int mod = direction[0];
                    int axis = direction[1];
                    long xMod = axis == 0 ? mod : 0;
                    long yMod = axis == 1 ? mod : 0;

                    Room neighbor = roomRepository.findByXAndYAndZ(
                            room.getX() + xMod,
                            room.getY() + yMod,
                            room.getZ());

                    if (neighbor == null) {
                        LOGGER.trace("New neighbor: ({}, {}, {})", room.getX() + xMod, room.getY() + yMod, room.getZ());
                        in.add(createRoom(rooms,
                                room.getX() + xMod,
                                room.getY() + yMod,
                                room.getZ()));
                        neighbors++;
                    }
                }
            }

            out.add(room);

            if (in.isEmpty() && out.size() < ZONE_SIZE) {
                if (iterations < MAX_ITERATIONS) {
                    iterations++;

                    LOGGER.debug("Pass {} completed, but zone only has {} of {} rooms.", iterations, out.size(), ZONE_SIZE);
                    in.addAll(out);
                    out.clear();
                    Collections.shuffle(in);
                } else {
                    LOGGER.error("Maximum iterations reached, but zone only has {} of {} rooms.", out.size(), ZONE_SIZE);
                    return null;
                }
            }
        }

        Zone zone = new Zone();
        zone = zoneRepository.save(zone);

        for (Room room : out) {
            room.setZone(zone);
        }

        roomRepository.save(out);

        return zone;
    }

    Room createRoom(Map<String, Room> rooms, long x, long y, long z) {
        Room room = rooms.get(String.format("%d-%d-%d", x, y, z));

        if (room != null) {
            return room;
        }

        room = new Room();
        room.setX(x);
        room.setY(y);
        room.setZ(z);

        rooms.put(String.format("%d-%d-%d", x, y, z), room);

        return room;
    }

    int countNeighbors(Room room) {
        Room north = roomRepository.findByXAndYAndZ(room.getX(), room.getY() + 1, room.getZ());
        Room east = roomRepository.findByXAndYAndZ(room.getX() + 1, room.getY(), room.getZ());
        Room south = roomRepository.findByXAndYAndZ(room.getX(), room.getY() - 1, room.getZ());
        Room west = roomRepository.findByXAndYAndZ(room.getX() - 1, room.getY(), room.getZ());
        int neighborCount = 0;

        neighborCount += north == null ? 0 : 1;
        neighborCount += east == null ? 0 : 1;
        neighborCount += south == null ? 0 : 1;
        neighborCount += west == null ? 0 : 1;

        return neighborCount;
    }
}
