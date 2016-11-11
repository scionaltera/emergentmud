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

import com.hoten.delaunay.geom.Point;
import com.hoten.delaunay.geom.Rectangle;
import com.hoten.delaunay.voronoi.Corner;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ElevationBuilderTest {
    @Mock
    private IslandShape islandShape;

    @Mock
    private LandCornerFilter landCornerFilter;

    @Mock
    private Rectangle bounds;

    @Mock
    private Point point;

    private ElevationBuilder elevationBuilder;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        elevationBuilder = new ElevationBuilder(islandShape, landCornerFilter);
    }

    @Test
    public void testAssignCornerElevationsWaterBorder() throws Exception {
        Corner corner = new Corner();
        List<Corner> corners = new ArrayList<>();

        corner.border = true;
        corner.loc = point;

        corners.add(corner);

        when(islandShape.isWater(eq(bounds), eq(point))).thenReturn(true);

        elevationBuilder.assignCornerElevations(bounds, corners);

        assertEquals(0, corner.elevation, 0.001);
        assertTrue(corner.water);
    }

    @Test
    public void testAssignCornerElevationsLandBorder() throws Exception {
        Corner corner = new Corner();
        List<Corner> corners = new ArrayList<>();

        corner.border = true;
        corner.loc = point;

        corners.add(corner);

        when(islandShape.isWater(eq(bounds), eq(point))).thenReturn(false);

        elevationBuilder.assignCornerElevations(bounds, corners);

        assertEquals(0, corner.elevation, 0.001);
        assertFalse(corner.water);
    }

    @Test
    public void testAssignCornerElevationsWaterMiddle() throws Exception {
        Corner corner = new Corner();
        List<Corner> corners = new ArrayList<>();

        corner.border = false;
        corner.loc = point;

        corners.add(corner);

        when(islandShape.isWater(eq(bounds), eq(point))).thenReturn(true);

        elevationBuilder.assignCornerElevations(bounds, corners);

        assertEquals(Double.MAX_VALUE, corner.elevation, 0.001);
        assertTrue(corner.water);
    }

    @Test
    public void testAssignCornerElevationsLandMiddle() throws Exception {
        Corner corner = new Corner();
        List<Corner> corners = new ArrayList<>();

        corner.border = false;
        corner.loc = point;

        corners.add(corner);

        when(islandShape.isWater(eq(bounds), eq(point))).thenReturn(false);

        elevationBuilder.assignCornerElevations(bounds, corners);

        assertEquals(Double.MAX_VALUE, corner.elevation, 0.001);
        assertFalse(corner.water);
    }

    @Test
    public void testAssignCornerElevationsAdjacentBorderWater() throws Exception {
        Corner corner = new Corner();
        Corner adjacent = new Corner();
        List<Corner> corners = new ArrayList<>();

        corner.border = true;
        corner.loc = point;
        corner.adjacent.add(adjacent);

        adjacent.border = false;
        adjacent.loc = point;
        adjacent.adjacent.add(corner);

        corners.add(corner);
        corners.add(adjacent);

        when(islandShape.isWater(eq(bounds), eq(point))).thenReturn(true);

        elevationBuilder.assignCornerElevations(bounds, corners);

        assertEquals(0, corner.elevation, 0.001);
        assertTrue(corner.water);

        assertEquals(0.01, adjacent.elevation, 0.001);
        assertTrue(adjacent.water);
    }

    @Test
    public void testAssignCornerElevationsAdjacentBorderLand() throws Exception {
        Corner corner = new Corner();
        Corner adjacent = new Corner();
        List<Corner> corners = new ArrayList<>();

        corner.border = true;
        corner.loc = point;
        corner.adjacent.add(adjacent);

        adjacent.border = false;
        adjacent.loc = point;
        adjacent.adjacent.add(corner);

        corners.add(corner);
        corners.add(adjacent);

        when(islandShape.isWater(eq(bounds), eq(point))).thenReturn(false);

        elevationBuilder.assignCornerElevations(bounds, corners);

        assertEquals(0, corner.elevation, 0.001);
        assertFalse(corner.water);

        assertEquals(1.01, adjacent.elevation, 0.001);
        assertFalse(adjacent.water);
    }
}
