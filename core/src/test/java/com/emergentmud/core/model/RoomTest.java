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

package com.emergentmud.core.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RoomTest {
    private Room room = new Room();

    @Test
    public void testId() throws Exception {
        String id = "roomid";

        room.setId(id);

        assertEquals(id, room.getId());
    }

    @Test
    public void testX() throws Exception {
        long x = 0;

        room.setX(x);

        assertEquals(x, room.getX());
    }

    @Test
    public void testY() throws Exception {
        long y = 0;

        room.setY(y);

        assertEquals(y, room.getY());
    }

    @Test
    public void testZ() throws Exception {
        long z = 0;

        room.setZ(z);

        assertEquals(z, room.getZ());
    }

    @Test
    public void testSetContents() throws Exception {
        List<Entity> entityList = new ArrayList<>();

        room.setContents(entityList);

        assertEquals(entityList, room.getContents());
    }

    @Test
    public void testEqualsOperator() throws Exception {
        room.setId("foo");

        //noinspection EqualsWithItself
        assertTrue(room.equals(room));
    }

    @Test
    public void testNotEqualToDifferentClass() throws Exception {
        String fakeAccount = "fakeAccount";

        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(room.equals(fakeAccount));
    }

    @Test
    public void testEquals() throws Exception {
        Room o1 = new Room();
        Room o2 = new Room();

        o1.setId("foo");
        o2.setId("foo");

        assertTrue(o1.equals(o2));
    }

    @Test
    public void testNotEquals() throws Exception {
        Room o1 = new Room();
        Room o2 = new Room();

        o1.setId("foo");
        o2.setId("bar");

        assertFalse(o1.equals(o2));
    }

    @Test
    public void testHashCode() throws Exception {
        String id = "foo";

        room.setId(id);

        assertEquals(id.hashCode(), room.hashCode());
    }
}
