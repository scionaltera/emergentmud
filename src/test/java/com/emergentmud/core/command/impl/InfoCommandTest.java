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
import com.emergentmud.core.model.Coordinate;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Pronoun;
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
    private Pronoun gender;

    @Mock
    private EntityService entityService;

    private Coordinate origin = new Coordinate(0, 0, 0);
    private String cmd = "info";

    private InfoCommand command;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(gender.toString()).thenReturn("Test/Test");
        when(entity.getLocation()).thenReturn(origin);
        when(entity.getName()).thenReturn("Scion");
        when(entity.getGender()).thenReturn(gender);
        when(entity.getAccount()).thenReturn(account);
        when(target.getName()).thenReturn("Bnarg");
        when(target.getGender()).thenReturn(gender);
        when(target.getLocation()).thenReturn(origin);
        when(entityService.entitySearchGlobal(eq(entity), eq("bnarg"))).thenReturn(Optional.of(target));
        when(entityService.entitySearchGlobal(eq(entity), eq("morgan"))).thenReturn(Optional.empty());

        command = new InfoCommand(entityService);
    }

    @Test
    public void testParameter() {
        assertTrue(command.getParameters().stream().anyMatch(p -> "target".equals(p.getName())));
    }

    @Test
    public void testDescription() {
        assertNotEquals("No description.", command.getDescription());
    }

    @Test
    public void testTooManyArgs(){
        GameOutput result = command.execute(output, entity, cmd, new String[] { "unexpected", "args" }, "unexpected args");

        assertNotNull(result);
        verifyZeroInteractions(entity);
    }

    @Test
    public void testExecute() {
        GameOutput result = command.execute(output, entity, cmd, new String[] {}, "");

        assertNotNull(result);
        verifyZeroInteractions(target);
        verify(entity, atLeastOnce()).getLocation();
        verify(entity).getId();
        verify(entity).getName();
        verify(entity).getGender();
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
    public void testExecuteTarget() {
        GameOutput result = command.execute(output, entity, cmd, new String[] { "bnarg" }, "bnarg");

        assertNotNull(result);
        verify(entityService).entitySearchGlobal(eq(entity), eq("bnarg"));
        verifyNoMoreInteractions(entity);
        verifyZeroInteractions(account);
        verify(target, atLeastOnce()).getLocation();
        verify(target).getId();
        verify(target).getName();
        verify(target).getGender();
        verify(target).getCapabilities();
        verify(target).getAccount();
        verify(output, atLeastOnce()).append(anyString());
    }

    @Test
    public void testExecuteInvalidTarget() {
        GameOutput result = command.execute(output, entity, cmd, new String[] { "morgan" }, "morgan");

        assertNotNull(result);
        verify(entityService).entitySearchGlobal(eq(entity), eq("morgan"));
        verifyNoMoreInteractions(target, entity);
        verify(output, atLeastOnce()).append(anyString());
    }
}
