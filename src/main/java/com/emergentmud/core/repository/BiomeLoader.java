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

import com.emergentmud.core.model.Biome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Component
public class BiomeLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(BiomeLoader.class);

    private BiomeRepository biomeRepository;

    @Inject
    public BiomeLoader(BiomeRepository biomeRepository) {
        this.biomeRepository = biomeRepository;
    }

    @PostConstruct
    public void loadWorld() {
        if (biomeRepository.count() == 0) {
            LOGGER.warn("No biomes found! Loading default biomes...");

            List<Biome> biomes = new ArrayList<>();

            biomes.add(new Biome("Ocean", 0x444471));
            biomes.add(new Biome("Lake", 0x336699));
            biomes.add(new Biome("Beach", 0xa09077));
            biomes.add(new Biome("Snow", 0xffffff));
            biomes.add(new Biome("Tundra", 0xbbbbaa));
            biomes.add(new Biome("Bare", 0x888888));
            biomes.add(new Biome("Scorched", 0x555555));
            biomes.add(new Biome("Taiga", 0x99aa77));
            biomes.add(new Biome("Shrubland", 0x889977));
            biomes.add(new Biome("Temperate Desert", 0xc9d29b));
            biomes.add(new Biome("Temperate Rain Forest", 0x448855));
            biomes.add(new Biome("Temperate Deciduous Forest", 0x679459));
            biomes.add(new Biome("Grassland", 0x88aa55));
            biomes.add(new Biome("Subtropical Desert", 0xd2b98b));
            biomes.add(new Biome("Ice", 0x99ffff));
            biomes.add(new Biome("Marsh", 0x2f6666));
            biomes.add(new Biome("Tropical Rain Forest", 0x337755));
            biomes.add(new Biome("Tropical Seasonal Forest", 0x559944));
            biomes.add(new Biome("River", 0x225588));

            biomeRepository.save(biomes);
        }
    }
}
