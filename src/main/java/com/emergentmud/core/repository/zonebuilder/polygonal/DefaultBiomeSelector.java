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
 *
 * Parts of this file are adapted from Connor Clark's map generation
 * implementation available here: https://github.com/Hoten/Java-Delaunay
 */

package com.emergentmud.core.repository.zonebuilder.polygonal;

import com.emergentmud.core.model.Biome;
import com.emergentmud.core.repository.BiomeRepository;
import com.hoten.delaunay.voronoi.Center;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DefaultBiomeSelector extends AbstractBiomeSelector {
    private BiomeRepository biomeRepository;
    private Map<String, Biome> biomesByName = new HashMap<>();

    @Inject
    public DefaultBiomeSelector(BiomeRepository biomeRepository) {
        this.biomeRepository = biomeRepository;
    }

    @Override
    public Biome getBiome(Center p) {
        if (biomesByName.isEmpty()) {
            lazyLoad();
        }

        if (p.ocean) {
            return biomesByName.get("Ocean");
        } else if (p.water) {
            if (p.elevation < 0.1) {
                return biomesByName.get("Marsh");
            }
            if (p.elevation > 0.8) {
                return biomesByName.get("Ice");
            }
            return biomesByName.get("Lake");
        } else if (p.coast) {
            return biomesByName.get("Beach");
        } else if (p.elevation > 0.8) {
            if (p.moisture > 0.50) {
                return biomesByName.get("Snow");
            } else if (p.moisture > 0.33) {
                return biomesByName.get("Tundra");
            } else if (p.moisture > 0.16) {
                return biomesByName.get("Bare");
            } else {
                return biomesByName.get("Scorched");
            }
        } else if (p.elevation > 0.6) {
            if (p.moisture > 0.66) {
                return biomesByName.get("Taiga");
            } else if (p.moisture > 0.33) {
                return biomesByName.get("Shrubland");
            } else {
                return biomesByName.get("Temperate Desert");
            }
        } else if (p.elevation > 0.3) {
            if (p.moisture > 0.83) {
                return biomesByName.get("Temperate Rainforest");
            } else if (p.moisture > 0.50) {
                return biomesByName.get("Temperate Deciduous Forest");
            } else if (p.moisture > 0.16) {
                return biomesByName.get("Grassland");
            } else {
                return biomesByName.get("Temperate Desert");
            }
        } else {
            if (p.moisture > 0.66) {
                return biomesByName.get("Tropical Rainforest");
            } else if (p.moisture > 0.33) {
                return biomesByName.get("Tropical Seasonal Forest");
            } else if (p.moisture > 0.16) {
                return biomesByName.get("Grassland");
            } else {
                return biomesByName.get("Subtropical Desert");
            }
        }
    }

    private void lazyLoad() {
        List<Biome> allBiomes = biomeRepository.findAll();
        allBiomes.forEach(biome -> biomesByName.put(biome.getName(), biome));
    }
}
