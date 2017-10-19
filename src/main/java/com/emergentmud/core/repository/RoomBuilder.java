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

package com.emergentmud.core.repository;

import com.emergentmud.core.model.room.Biome;
import com.emergentmud.core.model.room.Room;
import com.emergentmud.core.model.WhittakerGridLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class RoomBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoomBuilder.class);

    private NoiseMap noiseMap;
    private WhittakerGridLocationRepository whittakerGridLocationRepository;
    private BiomeRepository biomeRepository;

    @Inject
    public RoomBuilder(NoiseMap noiseMap,
                       WhittakerGridLocationRepository whittakerGridLocationRepository,
                       BiomeRepository biomeRepository) {
        this.noiseMap = noiseMap;
        this.whittakerGridLocationRepository = whittakerGridLocationRepository;
        this.biomeRepository = biomeRepository;
    }

    public Room generateRoom(long x, long y, long z) {
        return assembleRoom(x, y, z);
    }

    private Room assembleRoom(long x, long y, long z) {
        Room room = new Room();
        Biome biome;
        int elevation = noiseMap.getElevation(x, y);
        int moisture = noiseMap.getMoisture(x, y);
        WhittakerGridLocation whittaker = whittakerGridLocationRepository.findByElevationAndMoisture(
                elevation,
                moisture);

        if (whittaker == null) {
            biome = biomeRepository.findByName("Ocean");

            LOGGER.warn("Defaulted to Ocean for ({}, {}, {}) due to out of range elevation={} moisture={}",
                    x, y, z, room.getElevation(), room.getMoisture());
        } else {
            biome = whittaker.getBiome();
        }

        room.setX(x);
        room.setY(y);
        room.setZ(z);
        room.setBiome(biome);
        room.setElevation(elevation);
        room.setMoisture(moisture);

        return room;
    }
}
