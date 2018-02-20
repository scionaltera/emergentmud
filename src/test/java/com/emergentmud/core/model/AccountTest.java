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

package com.emergentmud.core.model;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class AccountTest {
    @Mock
    private Capability capabilityA;

    @Mock
    private Capability capabilityB;

    @Mock
    private Capability capabilityC;

    private Account account;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        account = new Account();
    }

    @Test
    public void testId() throws Exception {
        UUID id = UUID.randomUUID();

        account.setId(id);

        assertEquals(id, account.getId());
    }

    @Test
    public void testSocialNetwork() throws Exception {
        String socialNetwork = "alteranet";

        account.setSocialNetwork(socialNetwork);

        assertEquals(socialNetwork, account.getSocialNetwork());
    }

    @Test
    public void testSocialNetworkId() throws Exception {
        String socialNetworkId = "123456789";

        account.setSocialNetworkId(socialNetworkId);

        assertEquals(socialNetworkId, account.getSocialNetworkId());
    }

    @Test
    public void testCapabilityVararg() throws Exception {
        account.addCapabilities(capabilityA, capabilityB);

        assertTrue(account.isCapable(capabilityA));
        assertTrue(account.isCapable(capabilityB));
        assertFalse(account.isCapable(capabilityC));

        account.removeCapabilities(capabilityB);

        assertTrue(account.isCapable(capabilityA));
        assertFalse(account.isCapable(capabilityB));
        assertFalse(account.isCapable(capabilityC));
    }

    @Test
    public void testCapabilityCollection() throws Exception {
        account.addCapabilities(Arrays.asList(capabilityA, capabilityB));

        assertTrue(account.isCapable(capabilityA));
        assertTrue(account.isCapable(capabilityB));
        assertFalse(account.isCapable(capabilityC));

        account.removeCapabilities(Collections.singletonList(capabilityB));

        assertTrue(account.isCapable(capabilityA));
        assertFalse(account.isCapable(capabilityB));
        assertFalse(account.isCapable(capabilityC));
    }

    @Test
    public void testGetCapabilities() throws Exception {
        account.addCapabilities(Arrays.asList(capabilityA, capabilityB));

        List<Capability> capabilities = account.getCapabilities();

        assertTrue(capabilities.contains(capabilityA));
        assertTrue(capabilities.contains(capabilityB));
        assertFalse(capabilities.contains(capabilityC));
    }

    @Test
    public void testEqualsOperator() throws Exception {
        account.setId(UUID.randomUUID());

        //noinspection EqualsWithItself
        assertTrue(account.equals(account));
    }

    @Test
    public void testNotEqualToDifferentClass() throws Exception {
        String fakeAccount = "fakeAccount";

        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(account.equals(fakeAccount));
    }

    @Test
    public void testEquals() throws Exception {
        Account o1 = new Account();
        Account o2 = new Account();
        UUID guid = UUID.randomUUID();

        o1.setId(guid);
        o2.setId(guid);

        assertTrue(o1.equals(o2));
    }

    @Test
    public void testNotEquals() throws Exception {
        Account o1 = new Account();
        Account o2 = new Account();
        UUID guid1 = UUID.randomUUID();
        UUID guid2 = UUID.randomUUID();

        o1.setId(guid1);
        o2.setId(guid2);

        assertFalse(o1.equals(o2));
    }

    @Test
    public void testHashCode() throws Exception {
        UUID id = UUID.randomUUID();

        account.setId(id);

        assertEquals(id.hashCode(), account.hashCode());
    }
}
