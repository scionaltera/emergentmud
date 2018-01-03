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

package com.emergentmud.core.service;

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.Zone;
import com.emergentmud.core.repository.RoomRepository;
import com.emergentmud.core.repository.ZoneRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class RoomService {
    private ZoneService zoneService;
    private ZoneRepository zoneRepository;
    private RoomRepository roomRepository;

    @Inject
    public RoomService(ZoneService zoneService,
                       ZoneRepository zoneRepository,
                       RoomRepository roomRepository) {

        this.zoneRepository = zoneRepository;
        this.roomRepository = roomRepository;
        this.zoneService = zoneService;
    }

    public Room fetchRoom(Long x, Long y, Long z, boolean generateZone) {
        Zone zone = zoneRepository.findZoneByBottomLeftXLessThanEqualAndTopRightXGreaterThanEqualAndBottomLeftYLessThanEqualAndTopRightYGreaterThanEqual(x, x, y, y);

        if (zone == null) {
            if (generateZone) {
                zone = zoneService.generateZone(x, y);
            } else {
                return null;
            }
        }

        Room room = roomRepository.findByXAndYAndZ(x, y, z);

        if (room == null) {
            room = new Room();

            room.setZone(zone);
            room.setLocation(x, y, z);
        }

        return room;
    }

    public boolean isWithinDistance(Entity origin, Long x, Long y, Long z, double distance) {
        return Math.sqrt(Math.pow(origin.getX() - x, 2)
                + Math.pow(origin.getY() - y, 2)
                + Math.pow(origin.getZ() - z, 2)) <= distance;
    }
}
