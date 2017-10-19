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

package com.emergentmud.core.command.impl;

import com.emergentmud.core.model.Account;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.service.EntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InfoCommandTest {
    @Mock
    private GameOutput output;

    @Mock
    private Entity entity;

    @Mock
    private Entity target;

    @Mock
    private Account account;

    @Mock
    private EntityService entityService;

    private String cmd = "info";

    private InfoCommand command;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(entity.getX()).thenReturn(0L);
        when(entity.getY()).thenReturn(0L);
        when(entity.getZ()).thenReturn(0L);
        when(entity.getName()).thenReturn("Scion");
        when(entity.getAccount()).thenReturn(account);
        when(target.getName()).thenReturn("Bnarg");
        when(entityService.entitySearchGlobal(eq(entity), eq("bnarg"))).thenReturn(Optional.of(target));
        when(entityService.entitySearchGlobal(eq(entity), eq("morgan"))).thenReturn(Optional.empty());

        command = new InfoCommand(entityService);
    }

    @Test
    public void testParameter() throws Exception {
        assertTrue(command.getParameters().stream().anyMatch(p -> "target".equals(p.getName())));
    }

    @Test
    public void testDescription() throws Exception {
        assertNotEquals("No description.", command.getDescription());
    }

    @Test
    public void testTooManyArgs() throws Exception {
        GameOutput result = command.execute(output, entity, cmd, new String[] { "unexpected", "args" }, "unexpected args");

        assertNotNull(result);
        verifyZeroInteractions(entity);
    }

    @Test
    public void testExecute() throws Exception {
        GameOutput result = command.execute(output, entity, cmd, new String[] {}, "");

        assertNotNull(result);
        verifyZeroInteractions(target);
        verify(entity, atLeastOnce()).getX();
        verify(entity, atLeastOnce()).getY();
        verify(entity, atLeastOnce()).getZ();
        verify(entity).getId();
        verify(entity).getName();
        verify(entity).getCapabilities();
        verify(entity, atLeastOnce()).getAccount();
        verify(account).getCapabilities();
        verify(account).getSocialNetwork();
        verify(account).getSocialNetworkId();
        verify(entity).getStompSessionId();
        verify(entity).getRemoteAddr();
        verify(entity).getUserAgent();
        verify(output, atLeastOnce()).append(anyString());
    }

    @Test
    public void testExecuteTarget() throws Exception {
        GameOutput result = command.execute(output, entity, cmd, new String[] { "bnarg" }, "bnarg");

        assertNotNull(result);
        verify(entityService).entitySearchGlobal(eq(entity), eq("bnarg"));
        verifyNoMoreInteractions(entity);
        verifyZeroInteractions(account);
        verify(target, atLeastOnce()).getX();
        verify(target, atLeastOnce()).getY();
        verify(target, atLeastOnce()).getZ();
        verify(target).getId();
        verify(target).getName();
        verify(target).getCapabilities();
        verify(target).getAccount();
        verify(output, atLeastOnce()).append(anyString());
    }

    @Test
    public void testExecuteInvalidTarget() throws Exception {
        GameOutput result = command.execute(output, entity, cmd, new String[] { "morgan" }, "morgan");

        assertNotNull(result);
        verify(entityService).entitySearchGlobal(eq(entity), eq("morgan"));
        verifyNoMoreInteractions(target, entity);
        verify(output, atLeastOnce()).append(anyString());
    }
}
