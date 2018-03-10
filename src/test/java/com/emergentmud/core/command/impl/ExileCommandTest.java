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
import com.emergentmud.core.model.CommandRole;
import com.emergentmud.core.model.Coordinate;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.AccountRepository;
import com.emergentmud.core.repository.CapabilityRepository;
import com.emergentmud.core.service.MovementService;
import com.emergentmud.core.service.EntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class ExileCommandTest {
    @Mock
    private EntityService entityService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CapabilityRepository capabilityRepository;

    @Mock
    private MovementService movementService;

    @Mock
    private GameOutput output;

    @Mock
    private Entity entity;

    @Mock
    private Entity victim;

    @Mock
    private Account account;

    @Mock
    private Capability playCapability;

    @Mock
    private Capability newCharCapability;

    private ExileCommand command;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(output.append(anyString())).thenReturn(output);
        when(entity.getName()).thenReturn("Admin");
        when(victim.getAccount()).thenReturn(account);
        when(victim.getLocation()).thenReturn(new Coordinate(0L, 0L, 0L));
        when(victim.getName()).thenReturn("Victim");
        when(capabilityRepository.findByName(eq(CommandRole.CHAR_PLAY.name()))).thenReturn(playCapability);
        when(capabilityRepository.findByName(eq(CommandRole.CHAR_NEW.name()))).thenReturn(newCharCapability);
        when(entityService.entitySearchGlobal(eq(entity), eq("victim"))).thenReturn(Optional.of(victim));

        command = new ExileCommand(entityService, accountRepository, capabilityRepository, movementService);
    }

    @Test
    public void testDescription() {
        assertNotEquals("No description.", command.getDescription());
    }

    @Test
    public void testSyntax() {
        assertTrue(command.getParameters().size() == 2);
        assertTrue(command.getSubCommands().isEmpty());
    }

    @Test
    public void testTooFewArgs() {
        GameOutput result = command.execute(output, entity, "exile", new String[] { "victim" }, "victim");

        assertNotNull(result);

        verifyZeroInteractions(capabilityRepository, entityService, accountRepository, movementService);
    }

    @Test
    public void testTooManyArgs() {
        GameOutput result = command.execute(output, entity, "exile", new String[] { "add victim now" }, "add victim now");

        assertNotNull(result);

        verifyZeroInteractions(capabilityRepository, entityService, accountRepository, movementService);
    }

    @Test
    public void testWrongOperation() {
        GameOutput result = command.execute(output, entity, "exile", new String[] { "kick", "victim" }, "kick victim");

        assertNotNull(result);

        verify(capabilityRepository).findByName(eq(CommandRole.CHAR_PLAY.name()));
        verify(capabilityRepository).findByName(eq(CommandRole.CHAR_NEW.name()));
        verify(entityService).entitySearchGlobal(eq(entity), eq("victim"));
        verifyNoMoreInteractions(capabilityRepository, entityService);
        verifyZeroInteractions(accountRepository, movementService);
    }

    @Test
    public void testVictimNotFound() {
        when(entityService.entitySearchGlobal(eq(entity), eq("victim"))).thenReturn(Optional.empty());

        GameOutput result = command.execute(output, entity, "exile", new String[] { "add", "victim" }, "add victim");

        assertNotNull(result);

        verify(capabilityRepository).findByName(eq(CommandRole.CHAR_PLAY.name()));
        verify(capabilityRepository).findByName(eq(CommandRole.CHAR_NEW.name()));
        verify(entityService).entitySearchGlobal(eq(entity), eq("victim"));
        verify(accountRepository, never()).save(eq(account));
        verify(entityService, never()).sendMessageToEntity(eq(victim), any(GameOutput.class));
        verify(entityService, never()).sendMessageToRoom(eq(new Coordinate(0L, 0L, 0L)), anyCollectionOf(Entity.class), any(GameOutput.class));
        verify(movementService, never()).remove(victim);
    }

    @Test
    public void testVictimIsSelf() {
        when(entityService.entitySearchGlobal(eq(entity), eq("admin"))).thenReturn(Optional.of(entity));

        GameOutput result = command.execute(output, entity, "exile", new String[] { "add", "admin" }, "add admin");

        assertNotNull(result);

        verify(capabilityRepository).findByName(eq(CommandRole.CHAR_PLAY.name()));
        verify(capabilityRepository).findByName(eq(CommandRole.CHAR_NEW.name()));
        verify(entityService).entitySearchGlobal(eq(entity), eq("admin"));
        verify(accountRepository, never()).save(eq(account));
        verify(entityService, never()).sendMessageToEntity(eq(victim), any(GameOutput.class));
        verify(entityService, never()).sendMessageToRoom(eq(new Coordinate(0L, 0L, 0L)), anyCollectionOf(Entity.class), any(GameOutput.class));
        verify(movementService, never()).remove(victim);
    }

    @Test
    public void testAddExile() {
        when(account.isCapable(eq(playCapability))).thenReturn(true);
        when(account.isCapable(eq(newCharCapability))).thenReturn(true);

        GameOutput result = command.execute(output, entity, "exile", new String[] { "add", "victim" }, "add victim");

        assertNotNull(result);

        verify(capabilityRepository).findByName(eq(CommandRole.CHAR_PLAY.name()));
        verify(capabilityRepository).findByName(eq(CommandRole.CHAR_NEW.name()));
        verify(entityService).entitySearchGlobal(eq(entity), eq("victim"));
        verify(account).removeCapabilities(playCapability, newCharCapability);
        verify(accountRepository).save(eq(account));
        verify(entityService).sendMessageToEntity(eq(victim), any(GameOutput.class));
        verify(entityService).sendMessageToRoom(eq(new Coordinate(0L, 0L, 0L)), anyCollectionOf(Entity.class), any(GameOutput.class));
        verify(movementService).remove(victim);
    }

    @Test
    public void testAddAlreadyExiled() {
        when(account.isCapable(eq(playCapability))).thenReturn(false);
        when(account.isCapable(eq(newCharCapability))).thenReturn(false);

        GameOutput result = command.execute(output, entity, "exile", new String[] { "add", "victim" }, "add victim");

        assertNotNull(result);

        verify(capabilityRepository).findByName(eq(CommandRole.CHAR_PLAY.name()));
        verify(capabilityRepository).findByName(eq(CommandRole.CHAR_NEW.name()));
        verify(entityService).entitySearchGlobal(eq(entity), eq("victim"));
        verify(accountRepository, never()).save(eq(account));
        verify(entityService, never()).sendMessageToEntity(eq(victim), any(GameOutput.class));
        verify(entityService, never()).sendMessageToRoom(eq(new Coordinate(0L, 0L, 0)), anyCollectionOf(Entity.class), any(GameOutput.class));
        verify(movementService, never()).remove(victim);
    }

    @Test
    public void testRemoveExile() {
        when(account.isCapable(eq(playCapability))).thenReturn(false);
        when(account.isCapable(eq(newCharCapability))).thenReturn(false);

        GameOutput result = command.execute(output, entity, "exile", new String[] { "remove", "victim" }, "remove victim");

        assertNotNull(result);

        verify(capabilityRepository).findByName(eq(CommandRole.CHAR_PLAY.name()));
        verify(capabilityRepository).findByName(eq(CommandRole.CHAR_NEW.name()));
        verify(entityService).entitySearchGlobal(eq(entity), eq("victim"));
        verify(account).addCapabilities(playCapability, newCharCapability);
        verify(accountRepository).save(eq(account));
    }

    @Test
    public void testAlreadyRemovedExile() {
        when(account.isCapable(eq(playCapability))).thenReturn(true);
        when(account.isCapable(eq(newCharCapability))).thenReturn(true);

        GameOutput result = command.execute(output, entity, "exile", new String[] { "remove", "victim" }, "remove victim");

        assertNotNull(result);

        verify(capabilityRepository).findByName(eq(CommandRole.CHAR_PLAY.name()));
        verify(capabilityRepository).findByName(eq(CommandRole.CHAR_NEW.name()));
        verify(entityService).entitySearchGlobal(eq(entity), eq("victim"));
        verify(accountRepository, never()).save(eq(account));
    }
}
