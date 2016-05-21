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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class WorldManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldManager.class);

    private EntityRepository entityRepository;

    @Inject
    public WorldManager(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    public boolean put(Entity entity, long x, long y, long z) {
        LOGGER.debug("Put {} into room ({}, {}, {})", entity.getName(), x, y, z);

        entity.setX(x);
        entity.setY(y);
        entity.setZ(z);
        entityRepository.save(entity);

        return true;
    }

    public boolean remove(Entity entity, long x, long y, long z) {
        entity.setX(null);
        entity.setY(null);
        entity.setZ(null);
        entity = entityRepository.save(entity);

        LOGGER.debug("Remove {} from room ({}, {}, {})", entity.getName(), x, y, z);

        return true;
    }
}
