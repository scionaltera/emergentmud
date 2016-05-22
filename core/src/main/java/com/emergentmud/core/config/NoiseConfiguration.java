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

package com.emergentmud.core.config;

import opensimplex.OpenSimplexNoise;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NoiseConfiguration {
    @Value("${simplex.elevation.big}")
    private Long elevationBigSeed;

    @Value("${simplex.elevation.detail}")
    private Long elevationDetailSeed;

    @Value("${simplex.waterTable.big}")
    private Long waterTableBigSeed;

    @Value("${simplex.waterTable.detail}")
    private Long waterTableDetailSeed;

    @Bean
    public OpenSimplexNoise elevationBigSimplexNoise() {
        return new OpenSimplexNoise(elevationBigSeed);
    }

    @Bean
    public OpenSimplexNoise elevationDetailSimplexNoise() {
        return new OpenSimplexNoise(elevationDetailSeed);
    }

    @Bean
    public OpenSimplexNoise waterTableBigSimplexNoise() {
        return new OpenSimplexNoise(waterTableBigSeed);
    }

    @Bean
    public OpenSimplexNoise waterTableDetailSimplexNoise() {
        return new OpenSimplexNoise(waterTableDetailSeed);
    }
}
