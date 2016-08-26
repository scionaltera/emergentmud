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

package com.emergentmud.core.repository;

import com.emergentmud.core.model.Entity;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EntityBuilderTest {
    @Test
    public void testNoArgConstructor() throws Exception {
        String id = "id";
        String name = "Unit";
        Entity result = new EntityBuilder()
                .withId(id)
                .withName(name)
                .build();

        assertEquals(id, result.getId());
        assertEquals(name, result.getName());
    }

    @Test
    public void testSimple() throws Exception {
        Entity entity = mock(Entity.class);
        Entity result = new EntityBuilder(entity).build();

        verifyZeroInteractions(entity);
        assertNotNull(result);
    }

    @Test
    public void testWithId() throws Exception {
        String id = "id";
        Entity entity = mock(Entity.class);
        Entity result = new EntityBuilder(entity)
                .withId(id)
                .build();

        verify(entity).setId(eq(id));
        verify(entity, never()).setName(anyString());
        assertNotNull(result);
    }

    @Test
    public void testWithName() throws Exception {
        String name = "Unit";
        Entity entity = mock(Entity.class);
        Entity result = new EntityBuilder(entity)
                .withName(name)
                .build();

        verify(entity, never()).setId(anyString());
        verify(entity).setName(eq(name));
        assertNotNull(result);
    }

    @Test
    public void testWithAdmin() throws Exception {
        Entity entity = mock(Entity.class);
        Entity result = new EntityBuilder(entity)
                .withAdmin(true)
                .build();

        verify(entity).setAdmin(eq(true));
        verifyNoMoreInteractions(entity);
        assertNotNull(result);
    }

    @Test
    public void testComplete() throws Exception {
        String id = "id";
        String name = "Unit";
        Entity entity = mock(Entity.class);
        Entity result = new EntityBuilder(entity)
                .withId(id)
                .withName(name)
                .build();

        verify(entity).setId(eq(id));
        verify(entity).setName(eq(name));
        assertNotNull(result);
    }
}
