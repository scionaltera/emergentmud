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

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.Zone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class WorldManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldManager.class);

    private EntityRepository entityRepository;
    private RoomRepository roomRepository;
    private ZoneRepository zoneRepository;

    @Inject
    public WorldManager(EntityRepository entityRepository,
                        RoomRepository roomRepository,
                        ZoneRepository zoneRepository) {
        this.entityRepository = entityRepository;
        this.roomRepository = roomRepository;
        this.zoneRepository = zoneRepository;
    }

    public void put(Entity entity, long x, long y, long z) {
        Room room = roomRepository.findByXAndYAndZ(x, y, z);

        if (room == null) {
            Zone zone = new Zone();
            zone = zoneRepository.save(zone);

            room = new Room();
            room.setX(x);
            room.setY(y);
            room.setZ(z);
            room.setZone(zone);
            roomRepository.save(room);

            LOGGER.info("Generated new zone {} starting at ({}, {}, {})", zone.getId(), x, y, z);
        }

        LOGGER.trace("Put {} into room ({}, {}, {})", entity.getName(), x, y, z);

        entity.setX(x);
        entity.setY(y);
        entity.setZ(z);
        entityRepository.save(entity);
    }

    public void remove(Entity entity) {
        LOGGER.trace("Remove {} from room ({}, {}, {})", entity.getName(), entity.getX(), entity.getY(), entity.getZ());

        entity.setX(null);
        entity.setY(null);
        entity.setZ(null);
        entityRepository.save(entity);
    }
}
