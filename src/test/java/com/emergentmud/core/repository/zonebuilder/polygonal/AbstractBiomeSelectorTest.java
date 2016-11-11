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
import com.hoten.delaunay.voronoi.Center;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AbstractBiomeSelectorTest {
    @Mock
    private Biome biome;

    private List<Center> centers = new ArrayList<>();

    private AbstractBiomeSelector abstractBiomeSelector;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        for (int i = 0; i < 5; i++) {
            centers.add(mock(Center.class));
        }

        abstractBiomeSelector = new AbstractBiomeSelector() {
            @Override
            public Biome getBiome(Center center) {
                return biome;
            }
        };
    }

    @Test
    public void testAssignBiomes() throws Exception {
        abstractBiomeSelector.assignBiomes(centers);

        centers.forEach(c -> assertEquals(biome, c.biome));
    }
}
