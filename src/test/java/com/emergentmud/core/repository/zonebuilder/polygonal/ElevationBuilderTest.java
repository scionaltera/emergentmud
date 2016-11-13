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
import com.hoten.delaunay.voronoi.Center;
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

        when(landCornerFilter.landCorners(anyListOf(Corner.class))).thenCallRealMethod();

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

    @Test
    public void testRedistributeElevations() throws Exception {
        List<Corner> corners = new ArrayList<>();
        Corner corner1 = new Corner();
        Corner corner2 = new Corner();
        Corner corner3 = new Corner();

        corner1.elevation = 0.1;
        corner2.elevation = 0.2;
        corner3.elevation = 0.3;

        corner1.ocean = false;
        corner2.ocean = false;
        corner3.ocean = false;

        corner1.coast = false;
        corner2.coast = false;
        corner3.coast = false;

        corners.add(corner1);
        corners.add(corner2);
        corners.add(corner3);

        elevationBuilder.redistributeElevations(corners);

        assertEquals(0.1, corner1.elevation, 0.1);
        assertEquals(0.2, corner2.elevation, 0.1);
        assertEquals(0.4, corner3.elevation, 0.1);
    }

    @Test
    public void testAssignPolygonElevations() throws Exception {
        List<Center> centers = new ArrayList<>();

        Center center = new Center();
        Corner corner1 = new Corner();
        Corner corner2 = new Corner();
        Corner corner3 = new Corner();

        corner1.elevation = 1;
        corner2.elevation = 1;
        corner3.elevation = 1;

        center.corners.add(corner1);
        center.corners.add(corner2);
        center.corners.add(corner3);

        centers.add(center);

        elevationBuilder.assignPolygonElevations(centers);

        assertEquals(1, center.elevation, 0.1);
    }

    @Test
    public void testCalculateDownslopes() throws Exception {
        List<Corner> corners = new ArrayList<>();

        Corner a = new Corner();
        Corner b = new Corner();
        Corner c = new Corner();

        a.elevation = 0.6;
        a.adjacent.add(b);
        a.adjacent.add(c);

        b.elevation = 0.5;
        b.adjacent.add(a);
        b.adjacent.add(c);

        c.elevation = 0.4;
        c.adjacent.add(a);
        c.adjacent.add(b);

        corners.add(a);
        corners.add(b);
        corners.add(c);

        elevationBuilder.calculateDownslopes(corners);

        assertTrue(a.downslope == c);
        assertTrue(b.downslope == c);
        assertTrue(c.downslope == c);
    }
}
