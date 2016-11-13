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


import com.hoten.delaunay.geom.Rectangle;
import com.hoten.delaunay.voronoi.Center;
import com.hoten.delaunay.voronoi.Corner;
import com.hoten.delaunay.voronoi.Edge;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MoistureBuilderTest {
    @Mock
    private LandCornerFilter landCornerFilter;

    @Mock
    private Random random;

    private Rectangle bounds;
    private List<Corner> corners = new ArrayList<>();
    private List<Center> centers = new ArrayList<>();

    private MoistureBuilder moistureBuilder;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        buildDataStructure();

        when(random.nextInt(anyInt()))
                .thenReturn(0)
                .thenReturn(1)
                .thenReturn(2)
                .thenReturn(3)
                .thenReturn(4);
        when(landCornerFilter.landCorners(anyListOf(Corner.class))).thenCallRealMethod();

        bounds = new Rectangle(0, 0, 10, 10);

        moistureBuilder = new MoistureBuilder(random, landCornerFilter);
    }

    @Test
    public void testCreateRivers() throws Exception {
        moistureBuilder.createRivers(bounds, corners);

        assertEquals(0, corners.get(0).river); // ocean
        assertEquals(0, corners.get(1).river); // low elevation
        assertEquals(0, corners.get(2).river); // high elevation
        assertEquals(1, corners.get(3).river); // middle elevation
        assertEquals(1, corners.get(4).river); // coast
    }

    @Test
    public void testAssignCornerMoisture() throws Exception {
        corners.get(3).river = 1; // middle elevation
        corners.get(4).river = 1; // coast

        moistureBuilder.assignCornerMoisture(corners);

        assertEquals(1, corners.get(0).moisture, 0.1);
        assertEquals(0.18, corners.get(1).moisture, 0.1);
        assertEquals(0, corners.get(2).moisture, 0.1);
        assertEquals(0.2, corners.get(3).moisture, 0.1);
        assertEquals(1, corners.get(4).moisture, 0.1);
    }

    @Test
    public void testRedistributeMoisture() throws Exception {
        corners.get(0).moisture = 1;
        corners.get(1).moisture = 0.18;
        corners.get(2).moisture = 0;
        corners.get(3).moisture = 0.2;
        corners.get(4).moisture = 1;

        moistureBuilder.redistributeMoisture(corners);

        assertEquals(1, corners.get(0).moisture, 0.1);
        assertEquals(0.3, corners.get(1).moisture, 0.1); // land
        assertEquals(0, corners.get(2).moisture, 0.1); // land
        assertEquals(0.6, corners.get(3).moisture, 0.1); // land
        assertEquals(1, corners.get(4).moisture, 0.1);
    }

    @Test
    public void testAssignPolygonMoisture() throws Exception {
        corners.get(0).moisture = 1;
        corners.get(1).moisture = 0.18;
        corners.get(2).moisture = 0;
        corners.get(3).moisture = 0.2;
        corners.get(4).moisture = 1;

        moistureBuilder.assignPolygonMoisture(centers);

        assertEquals(0.73, centers.get(0).moisture, 0.1);
    }

    private void buildDataStructure() {
        Center center = new Center();
        Edge edge = new Edge();
        Corner ocean = new Corner();
        Corner lowElevation = new Corner();
        Corner highElevation = new Corner();
        Corner justRight = new Corner();
        Corner coast = new Corner();

        ocean.ocean = true;

        lowElevation.ocean = false;
        lowElevation.elevation = 0.1;

        highElevation.ocean = false;
        highElevation.elevation = 0.95;

        justRight.ocean = false;
        justRight.water = false;
        justRight.coast = false;
        justRight.river = 0;
        justRight.elevation = 0.3;
        justRight.adjacent.add(coast);
        justRight.adjacent.add(lowElevation);
        justRight.downslope = coast;

        edge.river = 0;
        edge.v0 = justRight;
        edge.v1 = coast;

        justRight.protrudes.add(edge);

        coast.ocean = false;
        coast.water = false;
        coast.coast = true;
        coast.river = 0;
        coast.elevation = 0;

        center.corners.add(ocean);
        center.corners.add(coast);
        center.corners.add(justRight);

        corners.add(ocean);
        corners.add(lowElevation);
        corners.add(highElevation);
        corners.add(justRight);
        corners.add(coast);

        centers.add(center);
    }
}
