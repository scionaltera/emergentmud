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
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class DirectionTest {
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testForName() throws Exception {
        assertEquals(Direction.NORTH, Direction.forName("north"));
        assertEquals(Direction.EAST, Direction.forName("east"));
        assertEquals(Direction.SOUTH, Direction.forName("south"));
        assertEquals(Direction.WEST, Direction.forName("west"));
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("north", Direction.NORTH.getName());
        assertEquals("east", Direction.EAST.getName());
        assertEquals("south", Direction.SOUTH.getName());
        assertEquals("west", Direction.WEST.getName());
    }

    @Test
    public void testGetOpposite() throws Exception {
        assertEquals("south", Direction.NORTH.getOpposite());
        assertEquals("west", Direction.EAST.getOpposite());
        assertEquals("north", Direction.SOUTH.getOpposite());
        assertEquals("east", Direction.WEST.getOpposite());
    }

    @Test
    public void testGetX() throws Exception {
        assertEquals(0, Direction.NORTH.getX());
        assertEquals(1, Direction.EAST.getX());
        assertEquals(0, Direction.SOUTH.getX());
        assertEquals(-1, Direction.WEST.getX());
    }

    @Test
    public void testGetY() throws Exception {
        assertEquals(1, Direction.NORTH.getY());
        assertEquals(0, Direction.EAST.getY());
        assertEquals(-1, Direction.SOUTH.getY());
        assertEquals(0, Direction.WEST.getY());
    }

    @Test
    public void testGetZ() throws Exception {
        assertEquals(0, Direction.NORTH.getZ());
        assertEquals(0, Direction.EAST.getZ());
        assertEquals(0, Direction.SOUTH.getZ());
        assertEquals(0, Direction.WEST.getZ());
    }
}
