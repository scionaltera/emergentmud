/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016-2017 Peter Keeler
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

import static org.junit.Assert.*;

public class CapabilityTest {

    private Capability capability;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        capability = new Capability("CAP", "capable", CapabilityObject.ENTITY, CapabilityScope.PLAYER);
    }

    @Test
    public void testId() throws Exception {
        String id = "id";

        capability.setId(id);

        assertEquals(id, capability.getId());
    }

    @Test
    public void testName() throws Exception {
        String name = "name";

        capability.setName(name);

        assertEquals(name, capability.getName());
    }

    @Test
    public void testDescription() throws Exception {
        String description = "description";

        capability.setDescription(description);

        assertEquals(description, capability.getDescription());
        assertEquals(description, capability.toString());
    }

    @Test
    public void testObject() throws Exception {
        capability.setObject(CapabilityObject.ENTITY);

        assertEquals(CapabilityObject.ENTITY, capability.getObject());
    }

    @Test
    public void testScope() throws Exception {
        capability.setScope(CapabilityScope.PLAYER);

        assertEquals(CapabilityScope.PLAYER, capability.getScope());
    }

    @Test
    public void testEquals() throws Exception {
        Capability capabilityB = new Capability("B", "alternate", CapabilityObject.ENTITY, CapabilityScope.PLAYER);

        capability.setId("capA");
        capabilityB.setId("capB");

        assertEquals(capability, capability);
        assertNotEquals(capability, this);
        assertNotEquals(capability, capabilityB);

        capabilityB.setId("capA");

        assertEquals(capability, capabilityB);
    }
}
