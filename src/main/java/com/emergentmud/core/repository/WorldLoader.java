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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

@Component
public class WorldLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldLoader.class);

    private ZoneBuilder zoneBuilder;
    private RoomRepository roomRepository;

    @Inject
    public WorldLoader(ZoneBuilder zoneBuilder, RoomRepository roomRepository) {
        this.zoneBuilder = zoneBuilder;
        this.roomRepository = roomRepository;
    }

    @PostConstruct
    public void loadWorld() {
        if (roomRepository.count() == 0) {
            LOGGER.warn("No rooms found! Generating a world using strategy: {}", zoneBuilder.getClass().getName());
            LOGGER.warn("This could take some time...");

            zoneBuilder.build(0L, 0L, 0L);
        }
    }
}
