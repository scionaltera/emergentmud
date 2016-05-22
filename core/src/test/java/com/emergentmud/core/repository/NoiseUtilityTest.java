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

package com.emergentmud.core.repository;

import opensimplex.OpenSimplexNoise;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class NoiseUtilityTest {
    @Mock
    private OpenSimplexNoise elevationBigSimplexNoise;

    @Mock
    private OpenSimplexNoise elevationDetailSimplexNoise;

    @Mock
    private OpenSimplexNoise waterTableBigSimplexNoise;

    @Mock
    private OpenSimplexNoise waterTableDetailSimplexNoise;

    private NoiseUtility noiseUtility;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(elevationBigSimplexNoise.eval(anyDouble(), anyDouble())).thenReturn(0.0);
        when(elevationDetailSimplexNoise.eval(anyDouble(), anyDouble())).thenReturn(1.0);
        when(waterTableBigSimplexNoise.eval(anyDouble(), anyDouble())).thenReturn(2.0);
        when(waterTableDetailSimplexNoise.eval(anyDouble(), anyDouble())).thenReturn(3.0);

        noiseUtility = new NoiseUtility(
                elevationBigSimplexNoise,
                elevationDetailSimplexNoise,
                waterTableBigSimplexNoise,
                waterTableDetailSimplexNoise
        );
    }

    @Test
    public void testElevationNoise() throws Exception {
        byte result = noiseUtility.elevationNoise(0L, 0L);

        assertEquals(52, result);
    }

    @Test
    public void testWaterTableNoise() throws Exception {
        byte result = noiseUtility.waterTableNoise(0L, 0L);

        assertEquals(127, result);
    }
}
