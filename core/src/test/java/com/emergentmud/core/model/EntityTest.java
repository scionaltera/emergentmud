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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EntityTest {
    private Entity entity = new Entity();

    @Test
    public void testId() throws Exception {
        String id = "id";

        entity.setId(id);

        assertEquals(id, entity.getId());
    }

    @Test
    public void testName() throws Exception {
        String name = "Unit";

        entity.setName(name);

        assertEquals(name, entity.getName());
    }

    @Test
    public void testStompUsername() throws Exception {
        String stompUsername = "stompUsername";

        entity.setStompUsername(stompUsername);

        assertEquals(stompUsername, entity.getStompUsername());
    }

    @Test
    public void testStompSessionId() throws Exception {
        String stompSessionId = "stompSessionId";

        entity.setStompSessionId(stompSessionId);

        assertEquals(stompSessionId, entity.getStompSessionId());
    }

    @Test
    public void testRoom() throws Exception {
        Room room = mock(Room.class);

        entity.setRoom(room);

        verifyZeroInteractions(room);
        assertEquals(room, entity.getRoom());
    }

    @Test
    public void testEqualsOperator() throws Exception {
        entity.setId("foo");

        //noinspection EqualsWithItself
        assertTrue(entity.equals(entity));
    }

    @Test
    public void testNotEqualToDifferentClass() throws Exception {
        String fakeAccount = "fakeAccount";

        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(entity.equals(fakeAccount));
    }

    @Test
    public void testEquals() throws Exception {
        Entity o1 = new Entity();
        Entity o2 = new Entity();

        o1.setId("foo");
        o2.setId("foo");

        assertTrue(o1.equals(o2));
    }

    @Test
    public void testNotEquals() throws Exception {
        Entity o1 = new Entity();
        Entity o2 = new Entity();

        o1.setId("foo");
        o2.setId("bar");

        assertFalse(o1.equals(o2));
    }

    @Test
    public void testHashCode() throws Exception {
        String id = "foo";

        entity.setId(id);

        assertEquals(id.hashCode(), entity.hashCode());
    }
}
