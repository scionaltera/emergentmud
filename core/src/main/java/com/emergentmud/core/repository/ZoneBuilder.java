/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016 BoneVM, LLC
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

import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.Zone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class ZoneBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZoneBuilder.class);
    private static final int ZONE_SIZE = 100;
    private static final int MAX_ITERATIONS = 30;
    private static final Random RANDOM = new Random();

    private ZoneRepository zoneRepository;
    private RoomRepository roomRepository;

    @Inject
    public ZoneBuilder(ZoneRepository zoneRepository, RoomRepository roomRepository) {
        this.zoneRepository = zoneRepository;
        this.roomRepository = roomRepository;
    }

    public Zone build(Long x, Long y, Long z) {
        int iterations = 0;
        List<Room> in = new ArrayList<>();
        List<Room> out = new ArrayList<>();
        Zone zone = new Zone();

        zone.setColor(new int[] {RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256)});
        zone = zoneRepository.save(zone);

        in.add(createRoom(zone, x, y, z));

        while (!in.isEmpty() && out.size() < ZONE_SIZE) {
            Room room = in.remove(in.size() - 1);
            int neighbors = countNeighbors(room);
            int limit = 2;

            if (RANDOM.nextDouble() > 0.65) {
                limit++;
            }

            LOGGER.info("Assigning {} neighbors to ({}, {}, {})", limit, room.getX(), room.getY(), room.getZ());

            while (neighbors < limit) {
                int mod = RANDOM.nextBoolean() ? 1 : -1;
                int axis = RANDOM.nextBoolean() ? 1 : 0;
                long xMod = axis == 0 ? mod : 0;
                long yMod = axis == 1 ? mod : 0;

                Room neighbor = roomRepository.findByXAndYAndZ(
                        room.getX() + xMod,
                        room.getY() + yMod,
                        room.getZ());

                if (neighbor == null) {
                    LOGGER.info("New neighbor: ({}, {}, {})", room.getX() + xMod, room.getY() + yMod, room.getZ());
                    in.add(createRoom(zone,
                            room.getX() + xMod,
                            room.getY() + yMod,
                            room.getZ()));
                    neighbors++;
                }
            }

            out.add(room);

            if (in.isEmpty() && out.size() < ZONE_SIZE) {
                if (iterations < MAX_ITERATIONS) {
                    iterations++;

                    LOGGER.info("Pass {} completed, but zone is still too small.", iterations);
                    in.addAll(out);
                    out.clear();
                } else {
                    LOGGER.info("Maximum iterations reached, but zone is still too small.");
                }
            }
        }

        return zone;
    }

    private Room createRoom(Zone zone, Long x, Long y, Long z) {
        Room room = roomRepository.findByXAndYAndZ(x, y, z);

        if (room != null) {
            return room;
        }

        room = new Room();
        room.setX(x);
        room.setY(y);
        room.setZ(z);
        room.setZone(zone);

        return roomRepository.save(room);
    }

    private int countNeighbors(Room room) {
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
