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
import com.emergentmud.core.model.room.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.WorldManager;
import com.emergentmud.core.service.EntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GotoCommandTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private WorldManager worldManager;

    @Mock
    private EntityService entityService;

    @Spy
    private GameOutput output;

    @Mock
    private Entity entity;

    @Mock
    private Entity morgan;

    @Mock
    private Room room;

    @Mock
    private Room destination;

    @Mock
    private LookCommand lookCommand;

    private String cmd = "goto";

    private GotoCommand command;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(entity.getX()).thenReturn(0L);
        when(entity.getY()).thenReturn(0L);
        when(entity.getZ()).thenReturn(0L);
        when(morgan.getX()).thenReturn(1000L);
        when(morgan.getY()).thenReturn(1000L);
        when(morgan.getZ()).thenReturn(0L);
        when(destination.getX()).thenReturn(1000L);
        when(destination.getY()).thenReturn(1000L);
        when(destination.getZ()).thenReturn(0L);
        when(entityService.entitySearchInWorld(eq(entity), eq("morgan"))).thenReturn(Optional.of(morgan));
        when(worldManager.put(eq(entity), eq(1000L), eq(1000L), eq(0L))).thenReturn(entity);
        when(applicationContext.getBean(eq("lookCommand"))).thenReturn(lookCommand);

        command = new GotoCommand(applicationContext, worldManager, entityService);
    }

    @Test
    public void testDescription() throws Exception {
        assertNotEquals("No description.", command.getDescription());
    }

    @Test
    public void testSyntax() throws Exception {
        assertTrue(command.getParameters().size() == 3);
        assertTrue(command.getSubCommands().isEmpty());
    }

    @Test
    public void testGotoNoArgs() throws Exception {
        GameOutput result = command.execute(output, entity, cmd, new String[] {}, "");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Usage")));

        verifyZeroInteractions(applicationContext);
        verifyZeroInteractions(entityService);
        verifyZeroInteractions(worldManager);
    }

    @Test
    public void testGotoOneArg() throws Exception {
        GameOutput result = command.execute(output, entity, cmd, new String[] { "morgan" }, "morgan");

        assertFalse(result.getOutput().stream().anyMatch(line -> line.contains("Usage: ")));

        verify(entityService).sendMessageToRoom(eq(0L), eq(0L), eq(0L), eq(entity), any(GameOutput.class));
        verify(worldManager).put(eq(entity), eq(1000L), eq(1000L), eq(0L));
        verify(entityService).sendMessageToRoom(eq(1000L), eq(1000L), eq(0L), eq(entity), any(GameOutput.class));
        verify(applicationContext).getBean(eq("lookCommand"));
        verify(lookCommand).execute(eq(output), eq(entity), eq("look"), any(), eq(""));
    }

    @Test
    public void testGotoUnknownPlayer() throws Exception {
        when(entityService.entitySearchInWorld(eq(entity), eq("stu"))).thenReturn(Optional.empty());

        GameOutput result = command.execute(output, entity, cmd, new String[] { "stu" }, "stu");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("no one by that name")));

        verify(entityService, never()).sendMessageToRoom(eq(0L), eq(0L), eq(0L), eq(entity), any(GameOutput.class));
        verify(worldManager, never()).put(eq(entity), eq(1000L), eq(1000L), eq(0L));
        verify(entityService, never()).sendMessageToRoom(eq(1000L), eq(1000L), eq(0L), eq(entity), any(GameOutput.class));
        verify(applicationContext, never()).getBean(eq("lookCommand"));
        verify(lookCommand, never()).execute(eq(output), eq(entity), eq("look"), any(), eq(""));
    }

    @Test
    public void testGotoSameRoom() throws Exception {
        when(destination.getX()).thenReturn(0L);
        when(destination.getY()).thenReturn(0L);
        when(destination.getZ()).thenReturn(0L);

        GameOutput result = command.execute(output, entity, cmd, new String[] { "morgan" }, "morgan");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("already there")));

        verify(entityService, never()).sendMessageToRoom(eq(0L), eq(0L), eq(0L), eq(entity), any(GameOutput.class));
        verify(worldManager, never()).put(eq(entity), eq(1000L), eq(1000L), eq(0L));
        verify(entityService, never()).sendMessageToRoom(eq(1000L), eq(1000L), eq(0L), eq(entity), any(GameOutput.class));
        verify(applicationContext, never()).getBean(eq("lookCommand"));
        verify(lookCommand, never()).execute(eq(output), eq(entity), eq("look"), any(), eq(""));
    }

    @Test
    public void testGotoTwoArgs() throws Exception {
        GameOutput result = command.execute(output, entity, cmd, new String[] {"1000", "1000"}, "1000 1000");

        assertFalse(result.getOutput().stream().anyMatch(line -> line.contains("Usage: ")));

        verify(entityService).sendMessageToRoom(eq(0L), eq(0L), eq(0L), eq(entity), any(GameOutput.class));
        verify(worldManager).put(eq(entity), eq(1000L), eq(1000L), eq(0L));
        verify(entityService).sendMessageToRoom(eq(1000L), eq(1000L), eq(0L), eq(entity), any(GameOutput.class));
        verify(applicationContext).getBean(eq("lookCommand"));
        verify(lookCommand).execute(eq(output), eq(entity), eq("look"), any(), eq(""));
    }

    @Test
    public void testGotoNonexistentRoom() throws Exception {
        GameOutput result = command.execute(output, entity, cmd, new String[] {"100", "100"}, "100 100");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("No such room")));

        verify(entityService, never()).sendMessageToRoom(eq(0L), eq(0L), eq(0L), eq(entity), any(GameOutput.class));
        verify(worldManager, never()).put(eq(entity), eq(1000L), eq(1000L), eq(0L));
        verify(entityService, never()).sendMessageToRoom(eq(1000L), eq(1000L), eq(0L), eq(entity), any(GameOutput.class));
        verify(applicationContext, never()).getBean(eq("lookCommand"));
        verify(lookCommand, never()).execute(eq(output), eq(entity), eq("look"), any(), eq(""));
    }

    @Test
    public void testGotoThreeArgs() throws Exception {
        GameOutput result = command.execute(output, entity, cmd, new String[] {"1000", "1000", "0"}, "1000 1000 0");

        assertFalse(result.getOutput().stream().anyMatch(line -> line.contains("Usage: ")));

        verify(entityService).sendMessageToRoom(eq(0L), eq(0L), eq(0L), eq(entity), any(GameOutput.class));
        verify(worldManager).put(eq(entity), eq(1000L), eq(1000L), eq(0L));
        verify(entityService).sendMessageToRoom(eq(1000L), eq(1000L), eq(0L), eq(entity), any(GameOutput.class));
        verify(applicationContext).getBean(eq("lookCommand"));
        verify(lookCommand).execute(eq(output), eq(entity), eq("look"), any(), eq(""));
    }

    @Test
    public void testGotoTwoArgsBad() throws Exception {
        GameOutput result = command.execute(output, entity, cmd, new String[] { "1000", "bad" }, "1000 bad");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Usage")));

        verifyZeroInteractions(applicationContext);
        verifyZeroInteractions(entityService);
        verifyZeroInteractions(worldManager);
    }

    @Test
    public void testGotoTwoArgsNullOrigin() throws Exception {
        when(entity.getX()).thenReturn(null);
        when(entity.getY()).thenReturn(null);
        when(entity.getZ()).thenReturn(null);

        GameOutput result = command.execute(output, entity, cmd, new String[] {"1000", "1000"}, "1000 1000");

        assertFalse(result.getOutput().stream().anyMatch(line -> line.contains("Usage: ")));

        verify(entityService, never()).sendMessageToRoom(eq(0L), eq(0L), eq(0L), eq(entity), any(GameOutput.class));
        verify(worldManager).put(eq(entity), eq(1000L), eq(1000L), eq(0L));
        verify(entityService).sendMessageToRoom(eq(1000L), eq(1000L), eq(0L), eq(entity), any(GameOutput.class));
        verify(applicationContext).getBean(eq("lookCommand"));
        verify(lookCommand).execute(eq(output), eq(entity), eq("look"), any(), eq(""));
    }
}
