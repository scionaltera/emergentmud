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

package com.emergentmud.core.command.impl;

import com.emergentmud.core.model.Account;
import com.emergentmud.core.model.Capability;
import com.emergentmud.core.model.CapabilityObject;
import com.emergentmud.core.model.CapabilityScope;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.AccountRepository;
import com.emergentmud.core.repository.CapabilityRepository;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.service.EntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CapabilityEditCommandTest {
    @Mock
    private CapabilityRepository capabilityRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private EntityRepository entityRepository;

    @Mock
    private EntityService entityService;

    @Mock
    private GameOutput gameOutput;

    @Mock
    private Entity entity;

    @Mock
    private Entity target;

    @Mock
    private Account account;

    private List<Capability> accountCapabilities;
    private List<Capability> entityCapabilities;

    private CapabilityEditCommand command;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        accountCapabilities = generateAccountCapabilities();
        entityCapabilities = generateEntityCapabilities();

        List<Capability> combinedCapabilities = new ArrayList<>();
        combinedCapabilities.addAll(accountCapabilities);
        combinedCapabilities.addAll(entityCapabilities);

        when(capabilityRepository.findAll()).thenReturn(combinedCapabilities);
        when(capabilityRepository.findByNameIgnoreCase(eq("accountcap"))).thenReturn(accountCapabilities.get(0));
        when(capabilityRepository.findByNameIgnoreCase(eq("entitycap"))).thenReturn(entityCapabilities.get(0));
        when(entityService.entitySearchGlobal(eq(entity), eq("ghan"))).thenReturn(Optional.empty());
        when(entityService.entitySearchGlobal(eq(entity), eq("bnarg"))).thenReturn(Optional.of(target));
        when(target.getCapabilities()).thenReturn(entityCapabilities);
        when(target.getAccount()).thenReturn(account);
        when(account.getCapabilities()).thenReturn(accountCapabilities);

        command = new CapabilityEditCommand(
                capabilityRepository,
                accountRepository,
                entityRepository,
                entityService);
    }

    @Test
    public void testParameters() throws Exception {
        assertNotEquals("No description.", command.getDescription());
        assertEquals(0, command.getParameters().size());
        assertEquals(4, command.getSubCommands().size());
    }

    @Test
    public void testUsage() throws Exception {
        GameOutput result = command.execute(gameOutput, entity, "capedit", new String[] {}, "");

        verify(result).append(contains("Usage"));
    }

    @Test
    public void testWrongSubCommand() throws Exception {
        GameOutput result = command.execute(gameOutput, entity, "capedit", new String[] {"foo"}, "foo");

        verify(result).append(contains("Usage"));
    }

    @Test
    public void testWrongSubCommandWithArgs() throws Exception {
        GameOutput result = command.execute(gameOutput, entity, "capedit", new String[] {"foo", "bnarg", "baz"}, "foo bnarg baz");

        verify(result).append(contains("Usage"));
    }

    @Test
    public void testList() throws Exception {
        GameOutput result = command.execute(gameOutput, entity, "capedit", new String[] {"list"}, "list");

        verify(capabilityRepository).findAll();
        verify(result).append(contains("All Capabilities"));
        verify(result).append(contains("ENTITY"));
        verify(result).append(contains("ACCOUNT"));
    }

    @Test
    public void testNoTarget() throws Exception {
        GameOutput result = command.execute(gameOutput, entity, "capedit", new String[] {"show", "ghan"}, "show ghan");

        verify(entityService).entitySearchGlobal(eq(entity), eq("ghan"));
        verify(result).append(contains("There is nothing"));
    }

    @Test
    public void testShow() throws Exception {
        GameOutput result = command.execute(gameOutput, entity, "capedit", new String[] {"show", "bnarg"}, "show bnarg");

        verify(entityService).entitySearchGlobal(eq(entity), eq("bnarg"));
        verify(result).append(contains("ACCOUNT_CAPABILITY"));
        verify(result).append(contains("ENTITY_CAPABILITY"));
    }

    @Test
    public void testShowWrongArgs() throws Exception {
        GameOutput result = command.execute(gameOutput, entity, "capedit", new String[] {"show", "bnarg", "wuff"}, "show bnarg wuff");

        verify(result).append(contains("Usage"));
    }

    @Test
    public void testAddAccountCapability() throws Exception {
        GameOutput result = command.execute(gameOutput, entity, "capedit", new String[] {"add", "bnarg", "accountcap"}, "add bnarg accountcap");

        verify(entityService).entitySearchGlobal(eq(entity), eq("bnarg"));
        verify(account).addCapabilities(eq(accountCapabilities.get(0)));
        verify(accountRepository).save(eq(account));
        verify(result).append(contains("added"));
    }

    @Test
    public void testAddEntityCapability() throws Exception {
        GameOutput result = command.execute(gameOutput, entity, "capedit", new String[] {"add", "bnarg", "entitycap"}, "add bnarg entitycap");

        verify(entityService).entitySearchGlobal(eq(entity), eq("bnarg"));
        verify(target).addCapabilities(eq(entityCapabilities.get(0)));
        verify(entityRepository).save(eq(target));
        verify(result).append(contains("added"));
    }

    @Test
    public void testAddCapabilityWrongArgs() throws Exception {
        GameOutput result = command.execute(gameOutput, entity, "capedit", new String[] {"add", "bnarg"}, "add bnarg");

        verify(result).append(contains("Usage"));
    }

    @Test
    public void testAddCapabilityWrongCapability() throws Exception {
        GameOutput result = command.execute(gameOutput, entity, "capedit", new String[] {"add", "bnarg", "badcap"}, "add bnarg badcap");

        verify(entityService).entitySearchGlobal(eq(entity), eq("bnarg"));
        verify(result).append(contains("No such capability"));
        verify(accountRepository, never()).save(any(Account.class));
        verify(entityRepository, never()).save(any(Entity.class));
    }

    @Test
    public void testRemoveAccountCapability() throws Exception {
        GameOutput result = command.execute(gameOutput, entity, "capedit", new String[] {"remove", "bnarg", "accountcap"}, "remove bnarg accountcap");

        verify(entityService).entitySearchGlobal(eq(entity), eq("bnarg"));
        verify(account).removeCapabilities(eq(accountCapabilities.get(0)));
        verify(accountRepository).save(eq(account));
        verify(result).append(contains("removed"));
    }

    @Test
    public void testRemoveEntityCapability() throws Exception {
        GameOutput result = command.execute(gameOutput, entity, "capedit", new String[] {"remove", "bnarg", "entitycap"}, "remove bnarg entitycap");

        verify(entityService).entitySearchGlobal(eq(entity), eq("bnarg"));
        verify(target).removeCapabilities(eq(entityCapabilities.get(0)));
        verify(entityRepository).save(eq(target));
        verify(result).append(contains("removed"));
    }

    @Test
    public void testRemoveCapabilityWrongArgs() throws Exception {
        GameOutput result = command.execute(gameOutput, entity, "capedit", new String[] {"remove", "bnarg"}, "remove bnarg");

        verify(result).append(contains("Usage"));
    }

    @Test
    public void testRemoveCapabilityWrongCapability() throws Exception {
        GameOutput result = command.execute(gameOutput, entity, "capedit", new String[] {"remove", "bnarg", "badcap"}, "remove bnarg badcap");

        verify(entityService).entitySearchGlobal(eq(entity), eq("bnarg"));
        verify(result).append(contains("No such capability"));
        verify(accountRepository, never()).save(any(Account.class));
        verify(entityRepository, never()).save(any(Entity.class));
    }

    private List<Capability> generateAccountCapabilities() {
        List<Capability> capabilities = new ArrayList<>();

        Capability capability = mock(Capability.class);

        when(capability.getName()).thenReturn("ACCOUNT_CAPABILITY");
        when(capability.getDescription()).thenReturn("Makes an Account capable.");
        when(capability.getObject()).thenReturn(CapabilityObject.ACCOUNT);
        when(capability.getScope()).thenReturn(CapabilityScope.PLAYER);

        capabilities.add(capability);

        return capabilities;
    }

    private List<Capability> generateEntityCapabilities() {
        List<Capability> capabilities = new ArrayList<>();

        Capability capability = mock(Capability.class);

        when(capability.getName()).thenReturn("ENTITY_CAPABILITY");
        when(capability.getDescription()).thenReturn("Makes an Entity capable.");
        when(capability.getObject()).thenReturn(CapabilityObject.ENTITY);
        when(capability.getScope()).thenReturn(CapabilityScope.PLAYER);

        capabilities.add(capability);

        return capabilities;
    }
}
