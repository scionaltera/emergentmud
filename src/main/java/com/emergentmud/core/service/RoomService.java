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

package com.emergentmud.core.service;

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.WhittakerGridLocation;
import com.emergentmud.core.model.Zone;
import com.emergentmud.core.repository.RoomRepository;
import com.emergentmud.core.repository.WhittakerGridLocationRepository;
import com.emergentmud.core.repository.ZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
public class RoomService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomService.class);
    private static final Random RANDOM = new Random();

    private ZoneRepository zoneRepository;
    private RoomRepository roomRepository;

    private List<WhittakerGridLocation> allWhittakerGridLocations;

    @Inject
    public RoomService(ZoneRepository zoneRepository,
                       RoomRepository roomRepository,
                       WhittakerGridLocationRepository whittakerGridLocationRepository) {

        this.zoneRepository = zoneRepository;
        this.roomRepository = roomRepository;

        allWhittakerGridLocations = whittakerGridLocationRepository.findAll();
    }

    public Room fetchRoomReadOnly(Long x, Long y, Long z) {
        Zone zone = zoneRepository.findZoneByBottomLeftXLessThanEqualAndTopRightXGreaterThanEqualAndBottomLeftYLessThanEqualAndTopRightYGreaterThanEqual(x, x, y, y);
        Room room = new Room();

        room.setZone(zone);
        room.setLocation(x, y, z);

        return room;
    }

    public Room fetchRoom(Long x, Long y, Long z) {
        Zone zone = zoneRepository.findZoneByBottomLeftXLessThanEqualAndTopRightXGreaterThanEqualAndBottomLeftYLessThanEqualAndTopRightYGreaterThanEqual(x, x, y, y);

        if (zone == null) {
            zone = new Zone();

            Collections.shuffle(allWhittakerGridLocations);

            zone.setBiome(allWhittakerGridLocations.get(0).getBiome());

            zone.setTopRightX(x);
            zone.setTopRightY(y);
            zone.setBottomLeftX(x);
            zone.setBottomLeftY(y);

            List<Zone> collisions;
            int tries = 0;

            do {
                int direction = RANDOM.nextInt(4);
                int value = RANDOM.nextInt(10) + 1;

                switch (direction) {
                    case 0:
                        zone.setTopRightX(zone.getTopRightX() + value);
                        LOGGER.debug("Expanding right edge to {}", zone.getTopRightX());
                        break;
                    case 1:
                        zone.setTopRightY(zone.getTopRightY() + value);
                        LOGGER.debug("Expanding top edge to {}", zone.getTopRightY());
                        break;
                    case 2:
                        zone.setBottomLeftX(zone.getBottomLeftX() - value);
                        LOGGER.debug("Expanding left edge to {}", zone.getBottomLeftX());
                        break;
                    case 3:
                        zone.setBottomLeftY(zone.getBottomLeftY() - value);
                        LOGGER.debug("Expanding bottom edge to {}", zone.getBottomLeftY());
                        break;
                }

                collisions = zoneRepository.findZonesByBottomLeftXLessThanEqualAndTopRightXGreaterThanEqualAndBottomLeftYLessThanEqualAndTopRightYGreaterThanEqual(
                        zone.getTopRightX(),
                        zone.getBottomLeftX(),
                        zone.getTopRightY(),
                        zone.getBottomLeftY()
                );

                if (!collisions.isEmpty()) {
                    LOGGER.debug("Collision detected");

                    switch (direction) {
                        case 0: zone.setTopRightX(zone.getTopRightX() - value); break;
                        case 1: zone.setTopRightY(zone.getTopRightY() - value); break;
                        case 2: zone.setBottomLeftX(zone.getBottomLeftX() + value); break;
                        case 3: zone.setBottomLeftY(zone.getBottomLeftY() + value); break;
                    }
                }

                tries++;
            } while ((zone.getTopRightX() - zone.getBottomLeftX() < 10 || zone.getTopRightY() - zone.getBottomLeftY() < 10) && tries < 25);

            zone = zoneRepository.save(zone);

            LOGGER.info("Saving new zone: ({}, {}) ({}, {})",
                    zone.getBottomLeftX(),
                    zone.getBottomLeftY(),
                    zone.getTopRightX(),
                    zone.getTopRightY());
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
