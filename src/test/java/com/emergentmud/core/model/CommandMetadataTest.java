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

import java.util.UUID;

import static org.junit.Assert.*;

public class CommandMetadataTest {
    private CommandMetadata commandMetadata;

    @Before
    public void setUp() throws Exception {
        commandMetadata = new CommandMetadata();
    }

    @Test
    public void testId() throws Exception {
        UUID guid = UUID.randomUUID();

        commandMetadata.setId(guid);

        assertEquals(guid, commandMetadata.getId());
    }

    @Test
    public void testName() throws Exception {
        commandMetadata.setName("name");

        assertEquals("name", commandMetadata.getName());
    }

    @Test
    public void testPriority() throws Exception {
        commandMetadata.setPriority(999);

        assertEquals(Integer.valueOf(999), commandMetadata.getPriority());
    }

    @Test
    public void testBeanName() throws Exception {
        commandMetadata.setBeanName("beanName");

        assertEquals("beanName", commandMetadata.getBeanName());
    }

    @Test
    public void testEqualsOperator() throws Exception {
        commandMetadata.setId(UUID.randomUUID());

        //noinspection EqualsWithItself
        assertTrue(commandMetadata.equals(commandMetadata));
    }

    @Test
    public void testNotEqualToDifferentClass() throws Exception {
        String fakeAccount = "fakeAccount";

        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(commandMetadata.equals(fakeAccount));
    }

    @Test
    public void testEquals() throws Exception {
        CommandMetadata o1 = new CommandMetadata();
        CommandMetadata o2 = new CommandMetadata();
        UUID uuid = UUID.randomUUID();

        o1.setId(uuid);
        o2.setId(uuid);

        assertTrue(o1.equals(o2));
    }

    @Test
    public void testNotEquals() throws Exception {
        CommandMetadata o1 = new CommandMetadata();
        CommandMetadata o2 = new CommandMetadata();
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        o1.setId(uuid1);
        o2.setId(uuid2);

        assertFalse(o1.equals(o2));
    }

    @Test
    public void testHashCode() throws Exception {
        UUID id = UUID.randomUUID();

        commandMetadata.setId(id);

        assertEquals(id.hashCode(), commandMetadata.hashCode());
    }
}
