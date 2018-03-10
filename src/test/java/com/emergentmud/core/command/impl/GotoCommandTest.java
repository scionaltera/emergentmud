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

import com.emergentmud.core.model.Coordinate;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.service.MovementService;
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
    private MovementService movementService;

    @Mock
    private EntityService entityService;

    @Spy
    private GameOutput output;

    @Mock
    private Entity entity;

    @Mock
    private Entity morgan;

    @Mock
    private Coordinate startLocation;

    @Mock
    private Coordinate destLocation;

    @Mock
    private Room destination;

    @Mock
    private LookCommand lookCommand;

    private String cmd = "goto";

    private GotoCommand command;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(entity.getLocation()).thenReturn(startLocation);
        when(startLocation.getX()).thenReturn(0L);
        when(startLocation.getY()).thenReturn(0L);
        when(startLocation.getZ()).thenReturn(0L);
        when(morgan.getLocation()).thenReturn(destLocation);
        when(destLocation.getX()).thenReturn(1000L);
        when(destLocation.getY()).thenReturn(1000L);
        when(destLocation.getZ()).thenReturn(0L);
        when(destination.getLocation()).thenReturn(destLocation);
        when(entityService.entitySearchInWorld(eq(entity), eq("morgan"))).thenReturn(Optional.of(morgan));
        when(applicationContext.getBean(eq("lookCommand"))).thenReturn(lookCommand);
        when(movementService.put(eq(entity), any(Coordinate.class))).thenAnswer(invocation -> {
            doReturn(invocation.getArgumentAt(1, Coordinate.class).getX()).when(startLocation).getX();
            doReturn(invocation.getArgumentAt(1, Coordinate.class).getY()).when(startLocation).getY();
            doReturn(invocation.getArgumentAt(1, Coordinate.class).getZ()).when(startLocation).getZ();

            return entity;
        });

        command = new GotoCommand(applicationContext, movementService, entityService);
    }

    @Test
    public void testDescription() {
        assertNotEquals("No description.", command.getDescription());
    }

    @Test
    public void testSyntax() {
        assertTrue(command.getParameters().size() == 3);
        assertTrue(command.getSubCommands().isEmpty());
    }

    @Test
    public void testGotoNoArgs() {
        GameOutput result = command.execute(output, entity, cmd, new String[] {}, "");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Usage")));

        verifyZeroInteractions(applicationContext);
        verifyZeroInteractions(entityService);
        verifyZeroInteractions(movementService);
    }

    @Test
    public void testGotoOneArg() throws Exception {
        GameOutput result = command.execute(output, entity, cmd, new String[] { "morgan" }, "morgan");

        assertFalse(result.getOutput().stream().anyMatch(line -> line.contains("Usage: ")));

        verify(entityService, times(2)).sendMessageToRoom(eq(entity), any(GameOutput.class));
        verify(movementService).put(eq(entity), any(Coordinate.class));
        verify(applicationContext).getBean(eq("lookCommand"));
        verify(lookCommand).execute(eq(output), eq(entity), eq("look"), any(), eq(""));
    }

    @Test
    public void testGotoUnknownPlayer() throws Exception {
        when(entityService.entitySearchInWorld(eq(entity), eq("stu"))).thenReturn(Optional.empty());

        GameOutput result = command.execute(output, entity, cmd, new String[] { "stu" }, "stu");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("no one by that name")));

        verify(entityService, never()).sendMessageToRoom(eq(startLocation), eq(entity), any(GameOutput.class));
        verify(movementService, never()).put(eq(entity), eq(destLocation));
        verify(entityService, never()).sendMessageToRoom(eq(destLocation), eq(entity), any(GameOutput.class));
        verify(applicationContext, never()).getBean(eq("lookCommand"));
        verify(lookCommand, never()).execute(eq(output), eq(entity), eq("look"), any(), eq(""));
    }

    @Test
    public void testGotoSameRoom() throws Exception {
        when(entity.getLocation()).thenReturn(destLocation);

        GameOutput result = command.execute(output, entity, cmd, new String[] { "morgan" }, "morgan");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("already there")));

        verify(entityService, never()).sendMessageToRoom(eq(startLocation), eq(entity), any(GameOutput.class));
        verify(movementService, never()).put(eq(entity), eq(destLocation));
        verify(entityService, never()).sendMessageToRoom(eq(destLocation), eq(entity), any(GameOutput.class));
        verify(applicationContext, never()).getBean(eq("lookCommand"));
        verify(lookCommand, never()).execute(eq(output), eq(entity), eq("look"), any(), eq(""));
    }

    @Test
    public void testGotoTwoArgs() throws Exception {
        GameOutput result = command.execute(output, entity, cmd, new String[] {"1000", "1000"}, "1000 1000");

        assertFalse(result.getOutput().stream().anyMatch(line -> line.contains("Usage: ")));

        verify(entityService, times(2)).sendMessageToRoom(eq(entity), any(GameOutput.class));
        verify(movementService).put(eq(entity), any(Coordinate.class));
        verify(applicationContext).getBean(eq("lookCommand"));
        verify(lookCommand).execute(eq(output), eq(entity), eq("look"), any(), eq(""));
    }

    @Test
    public void testGotoThreeArgs() throws Exception {
        GameOutput result = command.execute(output, entity, cmd, new String[] {"1000", "1000", "0"}, "1000 1000 0");

        assertFalse(result.getOutput().stream().anyMatch(line -> line.contains("Usage: ")));

        verify(entityService, times(2)).sendMessageToRoom(eq(entity), any(GameOutput.class));
        verify(movementService).put(eq(entity), any(Coordinate.class));
        verify(applicationContext).getBean(eq("lookCommand"));
        verify(lookCommand).execute(eq(output), eq(entity), eq("look"), any(), eq(""));
    }

    @Test
    public void testGotoTwoArgsBad() {
        GameOutput result = command.execute(output, entity, cmd, new String[] { "1000", "bad" }, "1000 bad");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Usage")));

        verifyZeroInteractions(applicationContext);
        verifyZeroInteractions(entityService);
        verifyZeroInteractions(movementService);
    }

    @Test
    public void testGotoTwoArgsNullOrigin() throws Exception {
        when(entity.getLocation()).thenReturn(null);

        GameOutput result = command.execute(output, entity, cmd, new String[] {"1000", "1000"}, "1000 1000");

        assertFalse(result.getOutput().stream().anyMatch(line -> line.contains("Usage: ")));

        verify(entityService, never()).sendMessageToRoom(eq(startLocation), eq(entity), any(GameOutput.class));
        verify(movementService).put(eq(entity), any(Coordinate.class));
        verify(entityService, times(2)).sendMessageToRoom(eq(entity), any(GameOutput.class));
        verify(applicationContext).getBean(eq("lookCommand"));
        verify(lookCommand).execute(eq(output), eq(entity), eq("look"), any(), eq(""));
    }
}
