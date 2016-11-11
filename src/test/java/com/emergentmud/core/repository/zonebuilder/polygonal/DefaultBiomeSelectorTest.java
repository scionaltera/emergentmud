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

package com.emergentmud.core.repository.zonebuilder.polygonal;

import com.emergentmud.core.model.Biome;
import com.emergentmud.core.repository.BiomeRepository;
import com.hoten.delaunay.voronoi.Center;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DefaultBiomeSelectorTest {
    @Mock
    private BiomeRepository biomeRepository;

    private Center center = new Center();
    private List<Biome> biomes = new ArrayList<>();

    private DefaultBiomeSelector defaultBiomeSelector;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

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
        biomes.add(new Biome("Temperate Rainforest", 0x448855));
        biomes.add(new Biome("Temperate Deciduous Forest", 0x679459));
        biomes.add(new Biome("Grassland", 0x88aa55));
        biomes.add(new Biome("Subtropical Desert", 0xd2b98b));
        biomes.add(new Biome("Ice", 0x99ffff));
        biomes.add(new Biome("Marsh", 0x2f6666));
        biomes.add(new Biome("Tropical Rainforest", 0x337755));
        biomes.add(new Biome("Tropical Seasonal Forest", 0x559944));
        biomes.add(new Biome("River", 0x225588));

        when(biomeRepository.findAll()).thenReturn(biomes);

        defaultBiomeSelector = new DefaultBiomeSelector(biomeRepository);
    }

    @Test
    public void testGetBiomeUnloaded() throws Exception {
        center.ocean = true;

        Biome biome = defaultBiomeSelector.getBiome(center);

        assertEquals("Ocean", biome.getName());
        verify(biomeRepository).findAll();
    }

    @Test
    public void testGetBiomeLoaded() throws Exception {
        center.ocean = true;

        defaultBiomeSelector.getBiome(center);
        Biome biome = defaultBiomeSelector.getBiome(center);

        assertEquals("Ocean", biome.getName());
        verify(biomeRepository).findAll(); // just once, even though we called getBiome twice
    }

    @Test
    public void testGetBiomeMarsh() throws Exception {
        center.water = true;
        center.elevation = 0.05;

        Biome biome = defaultBiomeSelector.getBiome(center);

        assertEquals("Marsh", biome.getName());
    }

    @Test
    public void testGetBiomeIce() throws Exception {
        center.water = true;
        center.elevation = 0.9;

        Biome biome = defaultBiomeSelector.getBiome(center);

        assertEquals("Ice", biome.getName());
    }

    @Test
    public void testGetBiomeLake() throws Exception {
        center.water = true;
        center.elevation = 0.5;

        Biome biome = defaultBiomeSelector.getBiome(center);

        assertEquals("Lake", biome.getName());
    }

    @Test
    public void testGetBiomeBeach() throws Exception {
        center.coast = true;

        Biome biome = defaultBiomeSelector.getBiome(center);

        assertEquals("Beach", biome.getName());
    }

    @Test
    public void testGetBiomeSnow() throws Exception {
        center.elevation = 0.9;
        center.moisture = 0.7;

        Biome biome = defaultBiomeSelector.getBiome(center);

        assertEquals("Snow", biome.getName());
    }

    @Test
    public void testGetBiomeTundra() throws Exception {
        center.elevation = 0.9;
        center.moisture = 0.4;

        Biome biome = defaultBiomeSelector.getBiome(center);

        assertEquals("Tundra", biome.getName());
    }

    @Test
    public void testGetBiomeBare() throws Exception {
        center.elevation = 0.9;
        center.moisture = 0.2;

        Biome biome = defaultBiomeSelector.getBiome(center);

        assertEquals("Bare", biome.getName());
    }

    @Test
    public void testGetBiomeScorched() throws Exception {
        center.elevation = 0.9;
        center.moisture = 0.1;

        Biome biome = defaultBiomeSelector.getBiome(center);

        assertEquals("Scorched", biome.getName());
    }

    @Test
    public void testGetBiomeTaiga() throws Exception {
        center.elevation = 0.7;
        center.moisture = 0.8;

        Biome biome = defaultBiomeSelector.getBiome(center);

        assertEquals("Taiga", biome.getName());
    }

    @Test
    public void testGetBiomeShrubland() throws Exception {
        center.elevation = 0.7;
        center.moisture = 0.5;

        Biome biome = defaultBiomeSelector.getBiome(center);

        assertEquals("Shrubland", biome.getName());
    }

    @Test
    public void testGetBiomeTemperateDesertHighElevation() throws Exception {
        center.elevation = 0.7;
        center.moisture = 0.2;

        Biome biome = defaultBiomeSelector.getBiome(center);

        assertEquals("Temperate Desert", biome.getName());
    }

    @Test
    public void testGetBiomeTemperateRainforest() throws Exception {
        center.elevation = 0.4;
        center.moisture = 0.9;

        Biome biome = defaultBiomeSelector.getBiome(center);

        assertEquals("Temperate Rainforest", biome.getName());
    }

    @Test
    public void testGetBiomeTemperateDeciduousForest() throws Exception {
        center.elevation = 0.4;
        center.moisture = 0.6;

        Biome biome = defaultBiomeSelector.getBiome(center);

        assertEquals("Temperate Deciduous Forest", biome.getName());
    }

    @Test
    public void testGetBiomeGrasslandHighElevation() throws Exception {
        center.elevation = 0.4;
        center.moisture = 0.2;

        Biome biome = defaultBiomeSelector.getBiome(center);

        assertEquals("Grassland", biome.getName());
    }

    @Test
    public void testGetBiomeTemperateDesertLowElevation() throws Exception {
        center.elevation = 0.4;
        center.moisture = 0.1;

        Biome biome = defaultBiomeSelector.getBiome(center);

        assertEquals("Temperate Desert", biome.getName());
    }

    @Test
    public void testGetBiomeTropicalRainforest() throws Exception {
        center.elevation = 0.1;
        center.moisture = 0.8;

        Biome biome = defaultBiomeSelector.getBiome(center);

        assertEquals("Tropical Rainforest", biome.getName());
    }

    @Test
    public void testGetBiomeTropicalSeasonalForest() throws Exception {
        center.elevation = 0.1;
        center.moisture = 0.5;

        Biome biome = defaultBiomeSelector.getBiome(center);

        assertEquals("Tropical Seasonal Forest", biome.getName());
    }

    @Test
    public void testGetBiomeGrasslandLowElevation() throws Exception {
        center.elevation = 0.1;
        center.moisture = 0.2;

        Biome biome = defaultBiomeSelector.getBiome(center);

        assertEquals("Grassland", biome.getName());
    }

    @Test
    public void testGetBiomeSubtropicalDesert() throws Exception {
        center.elevation = 0.1;
        center.moisture = 0.1;

        Biome biome = defaultBiomeSelector.getBiome(center);

        assertEquals("Subtropical Desert", biome.getName());
    }
}
