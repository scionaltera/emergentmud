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
import com.hoten.delaunay.voronoi.nodename.as3delaunay.Voronoi;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LloydsRelaxationTest {
    @Mock
    private Voronoi voronoi;

    List<Point> points = new ArrayList<>();

    private LloydsRelaxation lloydsRelaxation;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Point point1 = new Point(2, 2);
        Point point2 = new Point(2, 8);
        Point point3 = new Point(8, 2);
        Point point4 = new Point(8, 8);

        points.add(point1);
        points.add(point2);
        points.add(point3);
        points.add(point4);

        Rectangle bounds = new Rectangle(0, 0, 10, 10);

        when(voronoi.siteCoords()).thenReturn(points);
        when(voronoi.get_plotBounds()).thenReturn(bounds);
        when(voronoi.region(any(Point.class))).thenReturn(points);

        lloydsRelaxation = new LloydsRelaxation();
    }

    @Test
    public void testRelaxation() throws Exception {
        Voronoi result = lloydsRelaxation.relaxPoints(voronoi);
        List<Point> resultPoints = result.siteCoords();

        assertTrue(resultPoints.size() == 4);

        assertEquals(5.0, resultPoints.get(0).x, 0.1);
        assertEquals(5.0, resultPoints.get(0).y, 0.1);

        assertEquals(6.6, resultPoints.get(1).x, 0.1);
        assertEquals(5.1, resultPoints.get(1).y, 0.1);

        assertEquals(5.7, resultPoints.get(2).x, 0.1);
        assertEquals(5.7, resultPoints.get(2).y, 0.1);

        assertEquals(6.3, resultPoints.get(3).x, 0.1);
        assertEquals(5.9, resultPoints.get(3).y, 0.1);
    }
}
