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

package com.emergentmud.core.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

public class WhittakerGridLocationTest {
    @Mock
    private Biome biome;

    private WhittakerGridLocation whittakerGridLocation;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        whittakerGridLocation = new WhittakerGridLocation(1, 2, biome);
    }

    @Test
    public void testId() throws Exception {
        String string = "string";

        whittakerGridLocation.setId(string);

        assertEquals(string, whittakerGridLocation.getId());
    }

    @Test
    public void testBiome() throws Exception {
        whittakerGridLocation.setBiome(biome);

        assertEquals(biome, whittakerGridLocation.getBiome());
    }

    @Test
    public void testElevation() throws Exception {
        whittakerGridLocation.setElevation(1);

        assertEquals(1L, (long)whittakerGridLocation.getElevation());
    }

    @Test
    public void testMoisture() throws Exception {
        whittakerGridLocation.setMoisture(1);

        assertEquals(1L, (long)whittakerGridLocation.getMoisture());
    }
}
