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

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class EntityTest {
    @Mock
    private Room room;

    @Mock
    private Account account;

    @Mock
    private Capability capabilityA;

    @Mock
    private Capability capabilityB;

    @Mock
    private Capability capabilityC;

    private Entity entity = new Entity();

    public EntityTest() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testId() throws Exception {
        UUID id = UUID.randomUUID();

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
    public void testAccount() throws Exception {
        entity.setAccount(account);

        assertEquals(account, entity.getAccount());
    }

    @Test
    public void testCreationDate() throws Exception {
        entity.setCreationDate(1L);

        assertEquals(1L, (long)entity.getCreationDate());
    }

    @Test
    public void testLastLoginDate() throws Exception {
        entity.setLastLoginDate(1L);

        assertEquals(1L, (long)entity.getLastLoginDate());
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
    public void testRemoteAddr() throws Exception {
        String remoteAddr = "remote-address";

        entity.setRemoteAddr(remoteAddr);

        assertEquals(remoteAddr, entity.getRemoteAddr());
    }

    @Test
    public void testUserAgent() throws Exception {
        String userAgent = "user-agent";

        entity.setUserAgent(userAgent);

        assertEquals(userAgent, entity.getUserAgent());
    }

    @Test
    public void testCapabilityVararg() throws Exception {
        entity.addCapabilities(capabilityA, capabilityB);

        assertTrue(entity.isCapable(capabilityA));
        assertTrue(entity.isCapable(capabilityB));
        assertFalse(entity.isCapable(capabilityC));

        entity.removeCapabilities(capabilityB);

        assertTrue(entity.isCapable(capabilityA));
        assertFalse(entity.isCapable(capabilityB));
        assertFalse(entity.isCapable(capabilityC));
    }

    @Test
    public void testCapabilityCollection() throws Exception {
        entity.addCapabilities(Arrays.asList(capabilityA, capabilityB));

        assertTrue(entity.isCapable(capabilityA));
        assertTrue(entity.isCapable(capabilityB));
        assertFalse(entity.isCapable(capabilityC));

        entity.removeCapabilities(Collections.singletonList(capabilityB));

        assertTrue(entity.isCapable(capabilityA));
        assertFalse(entity.isCapable(capabilityB));
        assertFalse(entity.isCapable(capabilityC));
    }

    @Test
    public void testGetCapabilities() throws Exception {
        entity.addCapabilities(Arrays.asList(capabilityA, capabilityB));

        List<Capability> capabilities = entity.getCapabilities();

        assertTrue(capabilities.contains(capabilityA));
        assertTrue(capabilities.contains(capabilityB));
        assertFalse(capabilities.contains(capabilityC));
    }

    @Test
    public void testEqualsOperator() throws Exception {
        entity.setId(UUID.randomUUID());

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
        UUID uuid = UUID.randomUUID();

        o1.setId(uuid);
        o2.setId(uuid);

        assertTrue(o1.equals(o2));
    }

    @Test
    public void testNotEquals() throws Exception {
        Entity o1 = new Entity();
        Entity o2 = new Entity();
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();

        o1.setId(uuid1);
        o2.setId(uuid2);

        assertFalse(o1.equals(o2));
    }

    @Test
    public void testEqualsWithoutId() throws Exception {
        Entity o1 = new Entity();
        Entity o2 = new Entity();

        o1.setName("Alice");
        o2.setName("Alice");

        assertFalse(o1.equals(o2));
    }

    @Test
    public void testNotEqualsWithoutId() throws Exception {
        Entity o1 = new Entity();
        Entity o2 = new Entity();

        o1.setName("Alice");
        o2.setName("Bob");

        assertFalse(o1.equals(o2));
    }


    @Test
    public void testHashCode() throws Exception {
        UUID id = UUID.randomUUID();

        entity.setId(id);

        assertEquals(id.hashCode(), entity.hashCode());
    }
}
