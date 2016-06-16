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
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class ZoneBuilder {
    private static final int ZONE_SIZE = 100;
    private static final Random RANDOM = new Random();

    private ZoneRepository zoneRepository;
    private RoomRepository roomRepository;

    private List<Room> in = new ArrayList<>();
    private List<Room> out = new ArrayList<>();

    @Inject
    public ZoneBuilder(ZoneRepository zoneRepository, RoomRepository roomRepository) {
        this.zoneRepository = zoneRepository;
        this.roomRepository = roomRepository;
    }

    public Zone build(Long x, Long y, Long z) {
        Zone zone = new Zone();
        zone = zoneRepository.save(zone);

        in.add(createRoom(zone, x, y, z));

        while (!in.isEmpty() && out.size() <= ZONE_SIZE) {
            Room room = in.remove(in.size() - 1);
            Room north = roomRepository.findByXAndYAndZ(room.getX(), room.getY() + 1, room.getZ());
            Room east = roomRepository.findByXAndYAndZ(room.getX() + 1, room.getY(), room.getZ());
            Room south = roomRepository.findByXAndYAndZ(room.getX(), room.getY() - 1, room.getZ());
            Room west = roomRepository.findByXAndYAndZ(room.getX() - 1, room.getY(), room.getZ());
            int neighborCount = 0;

            neighborCount += north == null ? 0 : 1;
            neighborCount += east == null ? 0 : 1;
            neighborCount += south == null ? 0 : 1;
            neighborCount += west == null ? 0 : 1;

            for (int i = 4; i > neighborCount; i--) {
                long mod = RANDOM.nextBoolean() ? 1 : -1;
                long axis = RANDOM.nextInt(2);
                long neighborX = room.getX() + (axis == 0 ? mod : 0);
                long neighborY = room.getY() + (axis == 1 ? mod : 0);
                long neighborZ = room.getZ();

                Room neighbor = roomRepository.findByXAndYAndZ(neighborX, neighborY, neighborZ);

                if (neighbor == null) {
                    in.add(createRoom(zone, neighborX, neighborY, neighborZ));
                }
            }

            out.add(room);
        }

        return zone;
    }

    private Room createRoom(Zone zone, Long x, Long y, Long z) {
        Room room = new Room();
        room.setX(x);
        room.setY(y);
        room.setZ(z);
        room.setZone(zone);

        return roomRepository.save(room);
    }
}
