/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016-2018 Peter Keeler
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CoordinateTest {
    private Coordinate coordinate;

    @Before
    public void setUp() {
        coordinate = new Coordinate(0L, 0L, 0L);
    }

    @Test
    public void testMutation() {
        coordinate.setX(1L);
        coordinate.setY(2L);
        coordinate.setZ(3L);

        assertEquals(1L, coordinate.getX());
        assertEquals(2L, coordinate.getY());
        assertEquals(3L, coordinate.getZ());
    }

    @Test
    public void testEquality() {
        Coordinate equal = new Coordinate(0L, 0L, 0L);
        Coordinate unequal = new Coordinate(1L, 0L, 0L);

        assertEquals(equal, coordinate);
        assertNotEquals(unequal, coordinate);
    }
}
