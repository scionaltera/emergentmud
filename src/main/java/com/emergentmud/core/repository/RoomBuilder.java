/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016-2017 Peter Keeler
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

package com.emergentmud.core.repository;

import com.emergentmud.core.model.Direction;
import com.emergentmud.core.model.Exit;
import com.emergentmud.core.model.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
public class RoomBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomBuilder.class);
    private static final Random RANDOM = new Random();

    private RoomRepository roomRepository;
    private BiomeRepository biomeRepository;

    @Inject
    public RoomBuilder(RoomRepository roomRepository, BiomeRepository biomeRepository) {
        this.roomRepository = roomRepository;
        this.biomeRepository = biomeRepository;
    }

    public Room generateRoom(long x, long y, long z) {
        Room room = roomRepository.findByXAndYAndZ(x, y, z);

        if (room != null) {
            return room;
        }

        room = generateRandomRoom(x, y, z);

        LOGGER.debug("Generated {} at ({}, {}, {})", room.getBiome().getName(), x, y, z);
        return roomRepository.save(room);
    }

    private Room generateRandomRoom(long x, long y, long z) {
        Room room = new Room();

        room.setLocation(x, y, z);
        room.setBiome(biomeRepository.findByName("Grassland"));

        List<Direction> openExits = new ArrayList<>();

        for (Direction direction : Direction.DIRECTIONS) {
            Room neighbor = roomRepository.findByXAndYAndZ(
                    x + direction.getX(),
                    y + direction.getY(),
                    z + direction.getZ());

            if (neighbor != null) {
                // ensure we have a reciprocal exit for any neighbors that have exits to us
                if (neighbor.getExit(direction.getOpposite()) != null) {
                    if (room.getExit(direction) == null) {
                        room.setExit(new Exit(direction));
                    }
                }
            } else {
                openExits.add(direction);
            }
        }

        // add additional exits if possible
        Collections.shuffle(openExits);

        if (openExits.size() > 0) {
            room.setExit(new Exit(openExits.get(0)));

            if (openExits.size() > 1 && RANDOM.nextDouble() < 0.5) {
                room.setExit(new Exit(openExits.get(1)));

                if (openExits.size() > 2 && RANDOM.nextDouble() < 0.25) {
                    room.setExit(new Exit(openExits.get(2)));
                }
            }
        }

        return room;
    }
}
