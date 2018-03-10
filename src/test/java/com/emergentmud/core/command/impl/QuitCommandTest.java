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

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.service.MovementService;
import com.emergentmud.core.service.EntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class QuitCommandTest {
    @Mock
    private EntityService entityService;

    @Mock
    private MovementService movementService;

    @Spy
    private GameOutput output;

    @Mock
    private Entity entity;

    private String cmd = "quit";

    private QuitCommand command;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        command = new QuitCommand(entityService, movementService);
    }

    @Test
    public void testDescription() {
        assertNotEquals("No description.", command.getDescription());
    }

    @Test
    public void testQuitNoArgs() {
        GameOutput result = command.execute(output, entity, cmd, new String[] {}, "");

        result.getOutput().forEach(line -> assertFalse(line.contains("window.location")));
        verify(entityService, never()).sendMessageToRoom(eq(entity), any(GameOutput.class));
        verify(movementService, never()).remove(any(Entity.class));
    }

    @Test
    public void testQuitNow() {
        GameOutput result = command.execute(output, entity, cmd, new String[] {"now"}, "now");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("window.location")));
        verify(entityService).sendMessageToRoom(eq(entity), any(GameOutput.class));
        verify(movementService).remove(eq(entity));
    }

    @Test
    public void testQuitNowMixedCase() {
        GameOutput result = command.execute(output, entity, cmd, new String[] {"NoW"}, "NoW");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("window.location")));
        verify(entityService).sendMessageToRoom(eq(entity), any(GameOutput.class));
        verify(movementService).remove(eq(entity));
    }

    @Test
    public void testQuitWrongArg() {
        GameOutput result = command.execute(output, entity, cmd, new String[] {"later"}, "later");

        assertFalse(result.getOutput().stream().anyMatch(line -> line.contains("window.location")));
        verify(entityService, never()).sendMessageToRoom(eq(entity), any(GameOutput.class));
        verify(movementService, never()).remove(any(Entity.class));
    }
}
