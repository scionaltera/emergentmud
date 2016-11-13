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
import com.hoten.delaunay.voronoi.Center;
import com.hoten.delaunay.voronoi.Corner;
import com.hoten.delaunay.voronoi.Edge;
import com.hoten.delaunay.voronoi.nodename.as3delaunay.Voronoi;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class VoronoiGraphBuilderTest {
    private Random random = new Random();

    private VoronoiGraphBuilder voronoiGraphBuilder;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        random.setSeed(2342);

        voronoiGraphBuilder = new VoronoiGraphBuilder();
    }

    @Test
    public void testBuildGraph() throws Exception {
        Voronoi voronoi = new Voronoi(10, 25, 25, random, null);
        List<Edge> edges = new ArrayList<>();
        List<Center> centers = new ArrayList<>();
        List<Corner> corners = new ArrayList<>();

        voronoiGraphBuilder.buildGraph(voronoi, edges, centers, corners);

        assertFalse(edges.isEmpty());
        assertFalse(centers.isEmpty());
        assertFalse(corners.isEmpty());

        edges.stream()
                .filter(e -> e.d0 != null)
                .forEach(e -> assertTrue(e.d0.borders.contains(e)));

        edges.stream()
                .filter(e -> e.d1 != null)
                .forEach(e -> assertTrue(e.d1.borders.contains(e)));

        edges.stream()
                .filter(e -> e.v0 != null)
                .forEach(e -> assertTrue(e.v0.protrudes.contains(e)));

        edges.stream()
                .filter(e -> e.v1 != null)
                .forEach(e -> assertTrue(e.v1.protrudes.contains(e)));

        edges.stream()
                .filter(e -> e.d0 != null && e.d1 != null)
                .forEach(e -> {
                    assertTrue(e.d0.neighbors.contains(e.d1));
                    assertTrue(e.d1.neighbors.contains(e.d0));
                });

        edges.stream()
                .filter(e -> e.v0 != null && e.v1 != null)
                .forEach(e -> {
                    assertTrue(e.v0.adjacent.contains(e.v1));
                    assertTrue(e.v1.adjacent.contains(e.v0));
                });

        edges.stream()
                .filter(e -> e.d0 != null)
                .forEach(e -> {
                    assertTrue(e.d0.corners.contains(e.v0));
                    assertTrue(e.d0.corners.contains(e.v1));
                });

        edges.stream()
                .filter(e -> e.d1 != null)
                .forEach(e -> {
                    assertTrue(e.d1.corners.contains(e.v0));
                    assertTrue(e.d1.corners.contains(e.v1));
                });

        edges.stream()
                .filter(e -> e.v0 != null)
                .forEach(e -> {
                    assertTrue(e.v0.touches.contains(e.d0));
                    assertTrue(e.v0.touches.contains(e.d1));
                });

        edges.stream()
                .filter(e -> e.v1 != null)
                .forEach(e -> {
                    assertTrue(e.v1.touches.contains(e.d0));
                    assertTrue(e.v1.touches.contains(e.d1));
                });
    }

    @Test
    public void testImproveCorners() throws Exception {
        List<Corner> corners = new ArrayList<>();

        Corner corner1 = new Corner();
        Corner corner2 = new Corner();
        Corner corner3 = new Corner();

        corner1.loc = new Point(0, 0);
        corner2.loc = new Point(0, 1);
        corner3.loc = new Point(1, 1);

        corner1.border = true;

        Center center1 = new Center();

        center1.loc = new Point(0.3, 0.5);

        corner1.touches.add(center1);
        corner2.touches.add(center1);
        corner3.touches.add(center1);

        corners.add(corner1);
        corners.add(corner2);
        corners.add(corner3);

        voronoiGraphBuilder.improveCorners(corners);

        assertEquals(0.3, corner1.loc.x, 0.01);
        assertEquals(0.5, corner1.loc.y, 0.01);
        assertEquals(0.3, corner2.loc.x, 0.01);
        assertEquals(0.5, corner2.loc.y, 0.01);
        assertEquals(0.3, corner3.loc.x, 0.01);
        assertEquals(0.5, corner3.loc.y, 0.01);
    }

    @Test
    public void testComputeEdgeMidpoints() throws Exception {
        List<Edge> edges = new ArrayList<>();

        Edge edge1 = spy(new Edge());
        Edge edge2 = spy(new Edge());

        Corner corner1 = new Corner();
        Corner corner2 = new Corner();

        corner1.loc = new Point(0, 0);
        corner2.loc = new Point(0, 2);

        edge2.v0 = corner1;
        edge2.v1 = corner2;

        edges.add(edge1);
        edges.add(edge2);

        voronoiGraphBuilder.computeEdgeMidpoints(edges);

        verifyZeroInteractions(edge1);
        assertEquals(0, edge2.midpoint.x, 0.1);
        assertEquals(1, edge2.midpoint.y, 0.1);
    }
}
