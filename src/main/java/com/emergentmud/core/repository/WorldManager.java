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

    private EntityRepository entityRepository;
    private RoomRepository roomRepository;

    @Inject
    public WorldManager(EntityRepository entityRepository,
                        RoomRepository roomRepository) {
        this.entityRepository = entityRepository;
        this.roomRepository = roomRepository;
    }

    public boolean test(long x, long y, long z) {
        return roomRepository.findByXAndYAndZ(x, y, z) != null;
    }

    public Room put(Entity entity, long x, long y, long z) {
        Room room = roomRepository.findByXAndYAndZ(x, y, z);

        if (room == null) {
            throw new IllegalArgumentException("No such room exists.");
        }

        LOGGER.trace("Put {} into room ({}, {}, {})", entity.getName(), x, y, z);

        entity.setRoom(room);
        entityRepository.save(entity);

        return room;
    }

    public void remove(Entity entity) {
        if (entity.getRoom() == null) {
            return;
        }

        LOGGER.trace("Remove {} from room ({}, {}, {})",
                entity.getName(),
                entity.getRoom().getX(),
                entity.getRoom().getY(),
                entity.getRoom().getZ());

        entity.setRoom(null);
        entityRepository.save(entity);
    }
}
