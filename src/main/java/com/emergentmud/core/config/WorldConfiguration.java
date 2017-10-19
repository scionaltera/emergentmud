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

package com.emergentmud.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorldConfiguration {
    @Value("${world.seed.elevation}")
    private long seedElevation;

    @Value("${world.seed.moisture}")
    private long seedMoisture;

    @Value("${world.extent}")
    private int worldExtent;

    @Value("${world.scale}")
    private double worldScale;

    @Value("${world.octaves}")
    private int worldOctaves;

    @Bean(name = "worldSeedElevation")
    public long getSeedElevation() {
        return seedElevation;
    }

    @Bean(name = "worldSeedMoisture")
    public long getSeedMoisture() {
        return seedMoisture;
    }

    @Bean(name = "worldExtent")
    public int getWorldExtent() {
        return worldExtent;
    }

    @Bean(name = "worldScale")
    public double getWorldScale() {
        return worldScale;
    }

    @Bean(name = "worldOctaves")
    public int getWorldOctaves() {
        return worldOctaves;
    }
}
