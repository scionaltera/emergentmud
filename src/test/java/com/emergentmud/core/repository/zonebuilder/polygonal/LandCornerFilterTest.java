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

import com.hoten.delaunay.voronoi.Corner;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class LandCornerFilterTest {
    private List<Corner> corners = new ArrayList<>();

    private LandCornerFilter landCornerFilter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Corner ocean = new Corner();
        Corner coast = new Corner();
        Corner grassland = new Corner();

        ocean.ocean = true;
        coast.coast = true;

        corners.add(ocean);
        corners.add(coast);
        corners.add(grassland);

        landCornerFilter = new LandCornerFilter();
    }

    @Test
    public void testLandCornerFilter() throws Exception {
        List<Corner> result = landCornerFilter.landCorners(corners);

        assertEquals(1, result.size());
        assertFalse(result.get(0).ocean);
        assertFalse(result.get(0).coast);
    }
}
