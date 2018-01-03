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

package com.emergentmud.core.repository.loader;

import com.emergentmud.core.model.WhittakerGridLocation;
import com.emergentmud.core.model.Biome;
import com.emergentmud.core.repository.BiomeRepository;
import com.emergentmud.core.repository.WhittakerGridLocationRepository;
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

    private WhittakerGridLocationRepository whittakerGridLocationRepository;
    private BiomeRepository biomeRepository;

    @Inject
    public BiomeLoader(WhittakerGridLocationRepository whittakerGridLocationRepository,
                       BiomeRepository biomeRepository) {
        this.whittakerGridLocationRepository = whittakerGridLocationRepository;
        this.biomeRepository = biomeRepository;
    }

    @PostConstruct
    public void onConstruct() {
        if (biomeRepository.count() == 0) {
            LOGGER.warn("No biomes found! Loading default biomes...");

            List<Biome> biomes = new ArrayList<>();

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
            biomes.add(new Biome("Tropical Rain Forest", 0x337755));
            biomes.add(new Biome("Tropical Seasonal Forest", 0x559944));

            biomeRepository.save(biomes);
        }

        if (whittakerGridLocationRepository.count() == 0) {
            LOGGER.warn("No Whittaker grid data found! Loading default grid...");

            List<WhittakerGridLocation> gridLocations = new ArrayList<>();

            gridLocations.add(new WhittakerGridLocation(1, 1, biomeRepository.findByName("Subtropical Desert")));
            gridLocations.add(new WhittakerGridLocation(1, 2, biomeRepository.findByName("Grassland")));
            gridLocations.add(new WhittakerGridLocation(1, 3, biomeRepository.findByName("Tropical Seasonal Forest")));
            gridLocations.add(new WhittakerGridLocation(1, 4, biomeRepository.findByName("Tropical Seasonal Forest")));
            gridLocations.add(new WhittakerGridLocation(1, 5, biomeRepository.findByName("Tropical Rain Forest")));
            gridLocations.add(new WhittakerGridLocation(1, 6, biomeRepository.findByName("Tropical Rain Forest")));

            gridLocations.add(new WhittakerGridLocation(2, 1, biomeRepository.findByName("Temperate Desert")));
            gridLocations.add(new WhittakerGridLocation(2, 2, biomeRepository.findByName("Grassland")));
            gridLocations.add(new WhittakerGridLocation(2, 3, biomeRepository.findByName("Grassland")));
            gridLocations.add(new WhittakerGridLocation(2, 4, biomeRepository.findByName("Temperate Deciduous Forest")));
            gridLocations.add(new WhittakerGridLocation(2, 5, biomeRepository.findByName("Temperate Deciduous Forest")));
            gridLocations.add(new WhittakerGridLocation(2, 6, biomeRepository.findByName("Temperate Rain Forest")));

            gridLocations.add(new WhittakerGridLocation(3, 1, biomeRepository.findByName("Temperate Desert")));
            gridLocations.add(new WhittakerGridLocation(3, 2, biomeRepository.findByName("Temperate Desert")));
            gridLocations.add(new WhittakerGridLocation(3, 3, biomeRepository.findByName("Shrubland")));
            gridLocations.add(new WhittakerGridLocation(3, 4, biomeRepository.findByName("Shrubland")));
            gridLocations.add(new WhittakerGridLocation(3, 5, biomeRepository.findByName("Taiga")));
            gridLocations.add(new WhittakerGridLocation(3, 6, biomeRepository.findByName("Taiga")));

            gridLocations.add(new WhittakerGridLocation(4, 1, biomeRepository.findByName("Scorched")));
            gridLocations.add(new WhittakerGridLocation(4, 2, biomeRepository.findByName("Bare")));
            gridLocations.add(new WhittakerGridLocation(4, 3, biomeRepository.findByName("Tundra")));
            gridLocations.add(new WhittakerGridLocation(4, 4, biomeRepository.findByName("Snow")));
            gridLocations.add(new WhittakerGridLocation(4, 5, biomeRepository.findByName("Snow")));
            gridLocations.add(new WhittakerGridLocation(4, 6, biomeRepository.findByName("Snow")));

            whittakerGridLocationRepository.save(gridLocations);
        }
    }
}
