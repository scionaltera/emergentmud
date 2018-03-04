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

import com.emergentmud.core.model.Coordinate;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.Zone;
import com.emergentmud.core.repository.RoomRepository;
import com.emergentmud.core.service.maze.ZoneFillStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component
public class RoomService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomService.class);

    private ZoneService zoneService;
    private RoomRepository roomRepository;
    private ZoneFillStrategy zoneFillStrategy;

    @Inject
    public RoomService(ZoneService zoneService,
                       RoomRepository roomRepository,
                       ZoneFillStrategy zoneFillStrategy) {

        this.roomRepository = roomRepository;
        this.zoneService = zoneService;
        this.zoneFillStrategy = zoneFillStrategy;
    }

    public Room fetchRoom(Long x, Long y, Long z) {
        return roomRepository.findByXAndYAndZ(x, y, z);
    }

    public List<Room> fetchRooms(Long xFrom, Long xTo, Long yFrom, Long yTo, Long zFrom, Long zTo) {
        return roomRepository.findByXBetweenAndYBetweenAndZBetween(xFrom, xTo, yFrom, yTo, zFrom, zTo);
    }

    public Room createRoom(Long x, Long y, Long z) {
        Room room = fetchRoom(x, y, z);

        if (room != null) {
            LOGGER.debug("Request to create room that already exists: ({}, {}, {})", x, y, z);
            return room;
        }

        Zone zone = zoneService.fetchZone(x, y);

        if (zone == null) {
            zone = zoneService.createZone(x, y);

            return zoneFillStrategy.fillZone(zone, x, y, z);
        }

        return null;
    }

    public boolean isWithinDistance(Entity origin, Coordinate distant, double distance) {
        return Math.sqrt(Math.pow(origin.getLocation().getX() - distant.getX(), 2)
                + Math.pow(origin.getLocation().getY() - distant.getY(), 2)
                + Math.pow(origin.getLocation().getZ() - distant.getZ(), 2)) <= distance;
    }
}
