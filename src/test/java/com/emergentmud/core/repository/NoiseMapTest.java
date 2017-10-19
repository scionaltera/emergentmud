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

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

public class NoiseMapTest {
    private long seedElevation = 1L;
    private long seedMoisture = 2L;
    private int worldExtent = 256;
    private double worldScale = 0.007;
    private int worldOctaves = 8;

    private NoiseMap noiseMap;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        noiseMap = new NoiseMap(
                seedElevation,
                seedMoisture,
                worldExtent,
                worldScale,
                worldOctaves
        );
    }

    @Test
    public void testElevation() throws Exception {
        int elevation = noiseMap.getElevation(128L, 128L);

        assertTrue(elevation >= NoiseMap.MIN_ELEVATION);
        assertTrue(elevation <= NoiseMap.MAX_ELEVATION);
    }

    @Test
    public void testElevationOutOfBounds() throws Exception {
        int elevation = noiseMap.getElevation(1024L, 1024L);

        assertTrue(elevation == 0);
    }

    @Test
    public void testMoisture() throws Exception {
        int moisture = noiseMap.getMoisture(128L, 128L);

        assertTrue(moisture >= NoiseMap.MIN_MOISTURE);
        assertTrue(moisture <= NoiseMap.MAX_MOISTURE);
    }
}
