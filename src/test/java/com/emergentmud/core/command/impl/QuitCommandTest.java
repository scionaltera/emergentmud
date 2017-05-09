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

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.WorldManager;
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
    private WorldManager worldManager;

    @Spy
    private GameOutput output;

    @Mock
    private Entity entity;

    @Mock
    private Room room;

    private String cmd = "quit";

    private QuitCommand command;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(entity.getRoom()).thenReturn(room);

        command = new QuitCommand(entityService, worldManager);
    }

    @Test
    public void testDescription() throws Exception {
        assertNotEquals("No description.", command.getDescription());
    }

    @Test
    public void testQuitNoArgs() throws Exception {
        GameOutput result = command.execute(output, entity, cmd, new String[] {}, "");

        result.getOutput().forEach(line -> assertFalse(line.contains("window.location")));
        verify(entityService, never()).sendMessageToRoom(eq(room), eq(entity), any(GameOutput.class));
        verify(worldManager, never()).remove(any(Entity.class));
    }

    @Test
    public void testQuitNow() throws Exception {
        GameOutput result = command.execute(output, entity, cmd, new String[] {"now"}, "now");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("window.location")));
        verify(entityService).sendMessageToRoom(eq(room), eq(entity), any(GameOutput.class));
        verify(worldManager).remove(eq(entity));
    }

    @Test
    public void testQuitNowMixedCase() throws Exception {
        GameOutput result = command.execute(output, entity, cmd, new String[] {"NoW"}, "NoW");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("window.location")));
        verify(entityService).sendMessageToRoom(eq(room), eq(entity), any(GameOutput.class));
        verify(worldManager).remove(eq(entity));
    }

    @Test
    public void testQuitWrongArg() throws Exception {
        GameOutput result = command.execute(output, entity, cmd, new String[] {"later"}, "later");

        assertFalse(result.getOutput().stream().anyMatch(line -> line.contains("window.location")));
        verify(entityService, never()).sendMessageToRoom(eq(room), eq(entity), any(GameOutput.class));
        verify(worldManager, never()).remove(any(Entity.class));
    }
}
