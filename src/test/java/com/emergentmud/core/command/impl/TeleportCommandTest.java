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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class TeleportCommandTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private WorldManager worldManager;

    @Mock
    private EntityService entityService;

    @Mock
    private GameOutput gameOutput;

    @Mock
    private Room room;

    @Mock
    private Room destination;

    @Mock
    private Entity entity;

    @Mock
    private Entity bnarg;

    @Mock
    private LookCommand lookCommand;

    private String command = "teleport";

    private TeleportCommand teleportCommand;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(applicationContext.getBean(eq("lookCommand"))).thenReturn(lookCommand);
        when(worldManager.test(eq(1L), eq(1L), eq(0L))).thenReturn(true);
        when(worldManager.put(any(Entity.class), eq(1L), eq(1L), eq(0L))).thenReturn(destination);
        when(entityService.entitySearchRoom(eq(entity), eq("bnarg"))).thenReturn(Optional.of(bnarg));
        when(entity.getName()).thenReturn("Scion");
        when(entity.getRoom()).thenReturn(room);
        when(bnarg.getName()).thenReturn("Bnarg");
        when(bnarg.getRoom()).thenReturn(room);
        when(gameOutput.append(anyString())).thenReturn(gameOutput);

        teleportCommand = new TeleportCommand(applicationContext, worldManager, entityService);
    }

    @Test
    public void testParameters() throws Exception {
        assertEquals(4, teleportCommand.getParameters().size());
        assertEquals(0, teleportCommand.getSubCommands().size());
    }

    @Test
    public void testSuccessful() throws Exception {
        teleportCommand.execute(gameOutput, entity, command, new String[] { "bnarg", "1", "1" }, "bnarg 1 1");

        verify(entityService).entitySearchRoom(eq(entity), eq("bnarg"));
        verify(worldManager).test(eq(1L), eq(1L), eq(0L));
        verify(entityService).sendMessageToRoom(eq(room), Mockito.anyCollectionOf(Entity.class), any(GameOutput.class));
        verify(worldManager).put(eq(bnarg), eq(1L), eq(1L), eq(0L));
        verify(entityService).sendMessageToRoom(eq(destination), eq(bnarg), any(GameOutput.class));
        verify(applicationContext).getBean(eq("lookCommand"));
        verify(lookCommand).execute(any(GameOutput.class), eq(bnarg), eq("look"), any(String[].class), eq(""));
        verify(entityService).sendMessageToEntity(eq(bnarg), any(GameOutput.class));
    }

    @Test
    public void testTooFewArgs() throws Exception {
        teleportCommand.execute(gameOutput, entity, command, new String[] { "1", "1" }, "1 1");

        verifyZeroInteractions(entityService, worldManager, applicationContext, lookCommand);
    }

    @Test
    public void testTooManyArgs() throws Exception {
        teleportCommand.execute(gameOutput, entity, command, new String[] { "bnarg", "1", "1", "1", "1" }, "bnarg 1 1 1 1");

        verifyZeroInteractions(entityService, worldManager, applicationContext, lookCommand);
    }

    @Test
    public void testNonNumericArgs() throws Exception {
        teleportCommand.execute(gameOutput, entity, command, new String[] { "bnarg", "a", "1", "1" }, "bnarg a 1 1");

        verify(entityService).entitySearchRoom(eq(entity), eq("bnarg"));
        verifyZeroInteractions(worldManager, applicationContext, lookCommand);
    }

    @Test
    public void testMissingTarget() throws Exception {
        when(entityService.entitySearchRoom(eq(entity), eq("morgan"))).thenReturn(Optional.empty());

        teleportCommand.execute(gameOutput, entity, command, new String[] { "morgan", "a", "1", "1" }, "morgan a 1 1");

        verify(entityService).entitySearchRoom(eq(entity), eq("morgan"));
        verifyZeroInteractions(worldManager, applicationContext, lookCommand);
    }

    @Test
    public void teleportOutOfVoid() throws Exception {
        when(bnarg.getRoom()).thenReturn(null);

        teleportCommand.execute(gameOutput, entity, command, new String[] { "bnarg", "1", "1" }, "bnarg 1 1");

        verify(entityService).entitySearchRoom(eq(entity), eq("bnarg"));
        verify(worldManager).test(eq(1L), eq(1L), eq(0L));
        verify(entityService, never()).sendMessageToRoom(eq(room), eq(bnarg), any(GameOutput.class));
        verify(worldManager).put(eq(bnarg), eq(1L), eq(1L), eq(0L));
        verify(entityService).sendMessageToRoom(eq(destination), eq(bnarg), any(GameOutput.class));
        verify(applicationContext).getBean(eq("lookCommand"));
        verify(lookCommand).execute(any(GameOutput.class), eq(bnarg), eq("look"), any(String[].class), eq(""));
        verify(entityService).sendMessageToEntity(eq(bnarg), any(GameOutput.class));
    }

    @Test
    public void teleportToCurrentRoom() throws Exception {
        teleportCommand.execute(gameOutput, entity, command, new String[] { "bnarg", "0", "0" }, "bnarg 0 0");

        verify(entityService).entitySearchRoom(eq(entity), eq("bnarg"));
        verify(worldManager, never()).test(eq(1L), eq(1L), eq(0L));
        verify(entityService, never()).sendMessageToRoom(eq(room), eq(bnarg), any(GameOutput.class));
        verify(worldManager, never()).put(eq(bnarg), eq(1L), eq(1L), eq(0L));
        verify(entityService, never()).sendMessageToRoom(eq(destination), eq(bnarg), any(GameOutput.class));
        verify(applicationContext, never()).getBean(eq("lookCommand"));
        verify(lookCommand, never()).execute(eq(gameOutput), eq(bnarg), eq("look"), any(String[].class), eq(""));
    }

    @Test
    public void teleportToNonexistentRoom() throws Exception {
        when(worldManager.test(eq(1L), eq(1L), eq(0L))).thenReturn(false);

        teleportCommand.execute(gameOutput, entity, command, new String[] { "bnarg", "1", "1" }, "bnarg 1 1");

        verify(entityService).entitySearchRoom(eq(entity), eq("bnarg"));
        verify(worldManager).test(eq(1L), eq(1L), eq(0L));
        verify(entityService, never()).sendMessageToRoom(eq(room), eq(bnarg), any(GameOutput.class));
        verify(worldManager, never()).put(eq(bnarg), eq(1L), eq(1L), eq(0L));
        verify(entityService, never()).sendMessageToRoom(eq(destination), eq(bnarg), any(GameOutput.class));
        verify(applicationContext, never()).getBean(eq("lookCommand"));
        verify(lookCommand, never()).execute(eq(gameOutput), eq(bnarg), eq("look"), any(String[].class), eq(""));
    }
}
