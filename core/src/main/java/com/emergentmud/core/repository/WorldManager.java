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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class WorldManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldManager.class);

    private RoomRepository roomRepository;
    private EntityRepository entityRepository;

    @Inject
    public WorldManager(RoomRepository roomRepository, EntityRepository entityRepository) {
        this.roomRepository = roomRepository;
        this.entityRepository = entityRepository;
    }

    public boolean put(Entity entity, long x, long y, long z) {
        Room room = getRoom(x, y, z);

        if (!room.getContents().contains(entity)) {
            room.getContents().add(entity);
            roomRepository.save(room);

            LOGGER.info("Put {} into room ({}, {}, {})", entity.getName(), x, y, z);
        }

        entity.setRoom(room);
        entityRepository.save(entity);

        return true;
    }

    public boolean remove(Entity entity, long x, long y, long z) {
        Room room = getRoom(x, y, z);

        entity.setRoom(null);
        entityRepository.save(entity);

        if (room.getContents().contains(entity)) {
            room.getContents().remove(entity);
            roomRepository.save(room);

            LOGGER.info("Remove {} from room ({}, {}, {})", entity.getName(), x, y, z);
            return true;
        }

        return false;
    }

    public Room getRoom(long x, long y, long z) {
        Room room = roomRepository.findByXAndYAndZ(x, y, z);

        if (room == null) {
            room = new Room();
            room.setX(x);
            room.setY(y);
            room.setZ(z);

            room = roomRepository.save(room);
            LOGGER.info("Generated room ({}, {}, {})", x, y, z);
        }

        return room;
    }
}
