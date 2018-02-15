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

import com.emergentmud.core.exception.NoSuchRoomException;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.repository.EntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class MovementService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MovementService.class);

    private EntityRepository entityRepository;
    private RoomService roomService;

    @Inject
    public MovementService(EntityRepository entityRepository, RoomService roomService) {
        this.entityRepository = entityRepository;
        this.roomService = roomService;
    }

    public Entity put(Entity entity, long x, long y, long z) throws NoSuchRoomException {
        Room room = roomService.fetchRoom(x, y, z);

        if (room == null) {
            room = roomService.createRoom(x, y, z);

            if (room == null) {
                throw new NoSuchRoomException("Alas, you cannot go that way.");
            }
        }

        entity.setX(x);
        entity.setY(y);
        entity.setZ(z);
        entityRepository.save(entity);

        LOGGER.trace("Put {} into room ({}, {}, {})", entity.getName(), x, y, z);

        return entity;
    }

    public void remove(Entity entity) {
        LOGGER.trace("Remove {} from room ({}, {}, {})",
                entity.getName(),
                entity.getX(),
                entity.getY(),
                entity.getZ());

        entity.setX(null);
        entity.setY(null);
        entity.setZ(null);
        entityRepository.save(entity);
    }
}
