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
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RadialIslandShapeTest {
    @Spy
    private Random random = new Random();

    private Rectangle bounds;
    private Point point;

    private RadialIslandShape radialIslandShape;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        random.setSeed(29384);
        bounds = spy(new Rectangle(0, 0, 100, 100));

        radialIslandShape = new RadialIslandShape(random);
    }

    @Test
    public void testInitialParameters() throws Exception {
        assertEquals(5, radialIslandShape.getBumps());
        assertEquals(1.82, radialIslandShape.getStartAngle(), 0.01);
        assertEquals(0.53, radialIslandShape.getDipWidth(), 0.01);
        assertEquals(2.03, radialIslandShape.getDipAngle(), 0.01);
    }

    @Test
    public void testIsWater() throws Exception {
        point = spy(new Point(0, 0));

        // a point in the very bottom left corner should always be water
        assertTrue(radialIslandShape.isWater(bounds, point));
    }

    @Test
    public void testIsLand() throws Exception {
        point = spy(new Point(50, 50));

        // a point in the center should always be land
        assertFalse(radialIslandShape.isWater(bounds, point));
    }
}
