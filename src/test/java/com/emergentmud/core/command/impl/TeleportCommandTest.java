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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
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
    private Room destination;

    @Mock
    private Entity scion;

    @Mock
    private Entity bnarg;

    @Mock
    private Entity spook;

    @Mock
    private LookCommand lookCommand;

    private String command = "teleport";

    private TeleportCommand teleportCommand;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(applicationContext.getBean(eq("lookCommand"))).thenReturn(lookCommand);
        when(worldManager.put(any(Entity.class), anyLong(), anyLong(), anyLong())).thenAnswer(invocation -> {
            Entity entity = (Entity)invocation.getArguments()[0];

            when(entity.getX()).thenReturn((Long)invocation.getArguments()[1]);
            when(entity.getY()).thenReturn((Long)invocation.getArguments()[2]);
            when(entity.getZ()).thenReturn((Long)invocation.getArguments()[3]);

            return entity;
        });
        when(entityService.entitySearchRoom(eq(scion), eq("scion"))).thenReturn(Optional.of(scion));
        when(entityService.entitySearchRoom(eq(scion), eq("bnarg"))).thenReturn(Optional.of(bnarg));
        when(entityService.entitySearchInWorld(eq(scion), eq("spook"))).thenReturn(Optional.of(spook));
        when(entityService.entitySearchInWorld(eq(scion), eq("1"))).thenReturn(Optional.empty());
        when(scion.getName()).thenReturn("Scion");
        when(scion.getX()).thenReturn(0L);
        when(scion.getY()).thenReturn(0L);
        when(scion.getZ()).thenReturn(0L);
        when(bnarg.getName()).thenReturn("Bnarg");
        when(bnarg.getX()).thenReturn(0L);
        when(bnarg.getY()).thenReturn(0L);
        when(bnarg.getZ()).thenReturn(0L);
        when(spook.getName()).thenReturn("Spook");
        when(spook.getX()).thenReturn(1L);
        when(spook.getY()).thenReturn(1L);
        when(spook.getZ()).thenReturn(0L);
        when(destination.getX()).thenReturn(1L);
        when(destination.getY()).thenReturn(1L);
        when(gameOutput.append(anyString())).thenReturn(gameOutput);

        teleportCommand = new TeleportCommand(applicationContext, worldManager, entityService);
    }

    @Test
    public void testDescription() throws Exception {
        assertNotEquals("No description.", teleportCommand.getDescription());
    }

    @Test
    public void testParameters() throws Exception {
        assertEquals(4, teleportCommand.getParameters().size());
        assertEquals(0, teleportCommand.getSubCommands().size());
    }

    @Test
    public void testSuccessful() throws Exception {
        GameOutput output = teleportCommand.execute(gameOutput, scion, command, new String[] { "bnarg", "1", "1" }, "bnarg 1 1");

        assertNotNull(output);

        verify(entityService).entitySearchRoom(eq(scion), eq("bnarg"));
        verify(entityService).sendMessageToRoom(eq(0L), eq(0L), eq(0L), Mockito.anyCollectionOf(Entity.class), any(GameOutput.class));
        verify(worldManager).put(eq(bnarg), eq(1L), eq(1L), eq(0L));
        verify(entityService).sendMessageToRoom(eq(1L), eq(1L), eq(0L), eq(bnarg), any(GameOutput.class));
        verify(applicationContext).getBean(eq("lookCommand"));
        verify(lookCommand).execute(any(GameOutput.class), eq(bnarg), eq("look"), any(String[].class), eq(""));
        verify(entityService).sendMessageToEntity(eq(bnarg), any(GameOutput.class));
    }

    @Test
    public void testTeleportSelf() throws Exception {
        GameOutput output = teleportCommand.execute(gameOutput, scion, command, new String[] { "scion", "1", "1" }, "scion 1 1");

        assertNotNull(output);

        verify(entityService).entitySearchRoom(eq(scion), eq("scion"));
        verify(entityService, never()).sendMessageToRoom(eq(0L), eq(0L), eq(0L), Mockito.anyCollectionOf(Entity.class), any(GameOutput.class));
        verify(worldManager, never()).put(eq(bnarg), eq(1L), eq(1L), eq(0L));
        verify(entityService, never()).sendMessageToRoom(eq(1L), eq(1L), eq(0L), eq(bnarg), any(GameOutput.class));
        verify(applicationContext, never()).getBean(eq("lookCommand"));
        verify(lookCommand, never()).execute(any(GameOutput.class), eq(bnarg), eq("look"), any(String[].class), eq(""));
        verify(entityService, never()).sendMessageToEntity(eq(bnarg), any(GameOutput.class));
    }

    @Test
    public void testTooFewArgs() throws Exception {
        GameOutput output = teleportCommand.execute(gameOutput, scion, command, new String[] { "1" }, "1");

        assertNotNull(output);

        verifyZeroInteractions(entityService, worldManager, applicationContext, lookCommand);
    }

    @Test
    public void testTooManyArgs() throws Exception {
        GameOutput output = teleportCommand.execute(gameOutput, scion, command, new String[] { "bnarg", "", "1", "1", "1" }, "bnarg 1 1 1 1");

        assertNotNull(output);

        verifyZeroInteractions(entityService, worldManager, applicationContext, lookCommand);
    }

    @Test
    public void testTeleportToPerson() throws Exception {
        GameOutput output = teleportCommand.execute(gameOutput, scion, command, new String[] { "bnarg", "spook" }, "bnarg spook");

        assertNotNull(output);

        verify(entityService).entitySearchRoom(eq(scion), eq("bnarg"));
        verify(entityService).entitySearchInWorld(eq(scion), eq("spook"));
        verify(entityService).sendMessageToRoom(eq(0L), eq(0L), eq(0L), Mockito.anyCollectionOf(Entity.class), any(GameOutput.class));
        verify(worldManager).put(eq(bnarg), eq(1L), eq(1L), eq(0L));
        verify(entityService).sendMessageToRoom(eq(1L), eq(1L), eq(0L), eq(bnarg), any(GameOutput.class));
        verify(applicationContext).getBean(eq("lookCommand"));
        verify(lookCommand).execute(any(GameOutput.class), eq(bnarg), eq("look"), any(String[].class), eq(""));
        verify(entityService).sendMessageToEntity(eq(bnarg), any(GameOutput.class));
    }

    @Test
    public void testMismatchedArgs1() throws Exception {
        GameOutput output = teleportCommand.execute(gameOutput, scion, command, new String[] { "bnarg", "spook", "1" }, "bnarg spook 1");

        assertNotNull(output);

        verify(entityService).entitySearchRoom(eq(scion), eq("bnarg"));
        verify(entityService).entitySearchInWorld(eq(scion), eq("spook"));
        verify(entityService).sendMessageToRoom(eq(0L), eq(0L), eq(0L), Mockito.anyCollectionOf(Entity.class), any(GameOutput.class));
        verify(worldManager).put(eq(bnarg), eq(1L), eq(1L), eq(0L));
        verify(entityService).sendMessageToRoom(eq(1L), eq(1L), eq(0L), eq(bnarg), any(GameOutput.class));
        verify(applicationContext).getBean(eq("lookCommand"));
        verify(lookCommand).execute(any(GameOutput.class), eq(bnarg), eq("look"), any(String[].class), eq(""));
        verify(entityService).sendMessageToEntity(eq(bnarg), any(GameOutput.class));
    }

    @Test
    public void testMismatchedArgs2() throws Exception {
        GameOutput output = teleportCommand.execute(gameOutput, scion, command, new String[] { "bnarg", "1", "spook" }, "bnarg 1 spook");

        assertNotNull(output);

        verify(entityService).entitySearchRoom(eq(scion), eq("bnarg"));
        verify(entityService).entitySearchInWorld(eq(scion), eq("1"));
        verify(entityService, never()).sendMessageToRoom(eq(0L), eq(0L), eq(0L), Mockito.anyCollectionOf(Entity.class), any(GameOutput.class));
        verify(worldManager, never()).put(eq(bnarg), eq(1L), eq(1L), eq(0L));
        verify(entityService, never()).sendMessageToRoom(eq(1L), eq(1L), eq(0L), eq(bnarg), any(GameOutput.class));
        verify(applicationContext, never()).getBean(eq("lookCommand"));
        verify(lookCommand, never()).execute(any(GameOutput.class), eq(bnarg), eq("look"), any(String[].class), eq(""));
        verify(entityService, never()).sendMessageToEntity(eq(bnarg), any(GameOutput.class));
    }

    @Test
    public void testMissingTarget() throws Exception {
        when(entityService.entitySearchRoom(eq(scion), eq("morgan"))).thenReturn(Optional.empty());

        GameOutput output = teleportCommand.execute(gameOutput, scion, command, new String[] { "morgan", "a", "1", "1" }, "morgan a 1 1");

        assertNotNull(output);

        verify(entityService).entitySearchRoom(eq(scion), eq("morgan"));
        verifyZeroInteractions(worldManager, applicationContext, lookCommand);
    }

    @Test
    public void teleportOutOfVoid() throws Exception {
        when(bnarg.getX()).thenReturn(null);
        when(bnarg.getY()).thenReturn(null);
        when(bnarg.getZ()).thenReturn(null);

        GameOutput output = teleportCommand.execute(gameOutput, scion, command, new String[] { "bnarg", "1", "1" }, "bnarg 1 1");

        assertNotNull(output);

        verify(entityService).entitySearchRoom(eq(scion), eq("bnarg"));
        verify(entityService, never()).sendMessageToRoom(eq(0L), eq(0L), eq(0L), eq(bnarg), any(GameOutput.class));
        verify(worldManager).put(eq(bnarg), eq(1L), eq(1L), eq(0L));
        verify(entityService).sendMessageToRoom(eq(1L), eq(1L), eq(0L), eq(bnarg), any(GameOutput.class));
        verify(applicationContext).getBean(eq("lookCommand"));
        verify(lookCommand).execute(any(GameOutput.class), eq(bnarg), eq("look"), any(String[].class), eq(""));
        verify(entityService).sendMessageToEntity(eq(bnarg), any(GameOutput.class));
    }

    @Test
    public void teleportToCurrentRoom() throws Exception {
        GameOutput output = teleportCommand.execute(gameOutput, scion, command, new String[] { "bnarg", "0", "0" }, "bnarg 0 0");

        assertNotNull(output);

        verify(entityService).entitySearchRoom(eq(scion), eq("bnarg"));
        verify(entityService, never()).sendMessageToRoom(eq(0L), eq(0L), eq(0L), eq(bnarg), any(GameOutput.class));
        verify(worldManager, never()).put(eq(bnarg), eq(1L), eq(1L), eq(0L));
        verify(entityService, never()).sendMessageToRoom(eq(1L), eq(1L), eq(0L), eq(bnarg), any(GameOutput.class));
        verify(applicationContext, never()).getBean(eq("lookCommand"));
        verify(lookCommand, never()).execute(eq(gameOutput), eq(bnarg), eq("look"), any(String[].class), eq(""));
    }
}
