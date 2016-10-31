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

package com.emergentmud.core.config;

import com.emergentmud.core.repository.zonebuilder.polygonal.Biome;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BiomeConfiguration {
    @Bean
    public Biome oceanBiome() {
        return new Biome("Ocean", 0x444471);
    }

    @Bean
    public Biome lakeBiome() {
        return new Biome("Lake", 0x336699);
    }

    @Bean
    public Biome beachBiome() {
        return new Biome("Beach", 0xa09077);
    }

    @Bean
    public Biome snowBiome() {
        return new Biome("Snow", 0xfffffff);
    }

    @Bean
    public Biome tundraBiome() {
        return new Biome("Tundra", 0xbbbbaa);
    }

    @Bean
    public Biome bareBiome() {
        return new Biome("Bare", 0x888888);
    }

    @Bean
    public Biome scorchedBiome() {
        return new Biome("Scorched", 0x555555);
    }

    @Bean
    public Biome taigaBiome() {
        return new Biome("Taiga", 0x99aa77);
    }

    @Bean
    public Biome shrublandBiome() {
        return new Biome("Shrubland", 0x889977);
    }

    @Bean
    public Biome temperateDesertBiome() {
        return new Biome("Temperate Desert", 0xc9d29b);
    }

    @Bean
    public Biome temperateRainforestBiome() {
        return new Biome("Temperate Rainforest", 0x448855);
    }

    @Bean
    public Biome temperateDeciduousForestBiome() {
        return new Biome("Temperate Deciduous Forest", 0x679459);
    }

    @Bean
    public Biome grasslandBiome() {
        return new Biome("Grassland", 0x88aa55);
    }

    @Bean
    public Biome subtropicalDesertBiome() {
        return new Biome("Subtropical Desert", 0xd2b98b);
    }

    @Bean
    public Biome iceBiome() {
        return new Biome("Ice", 0x99ffff);
    }

    @Bean
    public Biome marshBiome() {
        return new Biome("Marsh", 0x2f6666);
    }

    @Bean
    public Biome tropicalRainforestBiome() {
        return new Biome("Tropical Rainforest", 0x337755);
    }

    @Bean
    public Biome tropicalSeasonalForestBiome() {
        return new Biome("Tropical Seasonal Forest", 0x559944);
    }

    @Bean
    public Biome riverBiome() {
        return new Biome("River", 0x225588);
    }
}
