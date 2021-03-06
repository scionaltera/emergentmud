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
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.service.MovementService;
import com.emergentmud.core.service.EntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class TransferCommandTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MovementService movementService;

    @Mock
    private EntityService entityService;

    @Mock
    private GameOutput gameOutput;

    @Mock
    private Entity scion;

    @Mock
    private Entity spook;

    @Mock
    private LookCommand lookCommand;

    private String command = "transfer";

    private TransferCommand transferCommand;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(applicationContext.getBean(eq("lookCommand"))).thenReturn(lookCommand);
        when(movementService.put(any(Entity.class), any(Coordinate.class))).thenAnswer(invocation -> {
            Entity entity = invocation.getArgumentAt(0, Entity.class);

            when(entity.getLocation()).thenReturn(new Coordinate(
                    invocation.getArgumentAt(1, Coordinate.class).getX(),
                    invocation.getArgumentAt(1, Coordinate.class).getY(),
                    invocation.getArgumentAt(1, Coordinate.class).getZ()
            ));

            return entity;
        });
        when(entityService.entitySearchRoom(eq(scion), eq("scion"))).thenReturn(Optional.of(scion));
        when(entityService.entitySearchInWorld(eq(scion), eq("scion"))).thenReturn(Optional.of(scion));
        when(entityService.entitySearchInWorld(eq(scion), eq("spook"))).thenReturn(Optional.of(spook));
        when(entityService.entitySearchInWorld(eq(scion), eq("morgan"))).thenReturn(Optional.empty());
        when(entityService.entitySearchInWorld(eq(scion), eq("1"))).thenReturn(Optional.empty());
        when(scion.getName()).thenReturn("Scion");
        when(scion.getLocation()).thenReturn(new Coordinate(0L, 0L, 0L));
        when(spook.getName()).thenReturn("Spook");
        when(spook.getLocation()).thenReturn(new Coordinate(1L, 1L, 0L));
        when(gameOutput.append(anyString())).thenReturn(gameOutput);

        transferCommand = new TransferCommand(applicationContext, movementService, entityService);
    }

    @Test
    public void testDescription() {
        assertNotEquals("No description.", transferCommand.getDescription());
    }

    @Test
    public void testParameters() {
        assertEquals(1, transferCommand.getParameters().size());
        assertEquals(0, transferCommand.getSubCommands().size());
    }

    @Test
    public void testTooFewArgs() {
        GameOutput output = transferCommand.execute(gameOutput, scion, command, new String[] {}, "");

        assertNotNull(output);

        verifyZeroInteractions(entityService, movementService, applicationContext, lookCommand);
    }

    @Test
    public void testTooManyArgs() {
        GameOutput output = transferCommand.execute(gameOutput, scion, command, new String[] { "spook", "morgan" }, "spook morgan");

        assertNotNull(output);

        verifyZeroInteractions(entityService, movementService, applicationContext, lookCommand);
    }

    @Test
    public void testTransferMissingTarget() {
        GameOutput output = transferCommand.execute(gameOutput, scion, command, new String[] { "morgan" }, "morgan");

        assertNotNull(output);

        verify(entityService).entitySearchInWorld(eq(scion), eq("morgan"));
        verifyNoMoreInteractions(entityService);
        verifyZeroInteractions(movementService, applicationContext, lookCommand);
    }

    @Test
    public void testTransferSelf() {
        GameOutput output = transferCommand.execute(gameOutput, scion, command, new String[] { "scion" }, "scion");

        assertNotNull(output);

        verify(entityService).entitySearchInWorld(eq(scion), eq("scion"));
        verifyNoMoreInteractions(entityService);
        verifyZeroInteractions(movementService, applicationContext, lookCommand);
    }

    @Test
    public void testTransferToSameRoom() {
        when(spook.getLocation()).thenReturn(new Coordinate(0L, 0L, 0L));

        GameOutput output = transferCommand.execute(gameOutput, scion, command, new String[] { "spook" }, "spook");

        assertNotNull(output);

        verify(entityService).entitySearchInWorld(eq(scion), eq("spook"));
        verifyNoMoreInteractions(entityService);
        verifyZeroInteractions(movementService, applicationContext, lookCommand);
    }

    @Test
    public void testSuccessful() throws Exception {
        GameOutput output = transferCommand.execute(gameOutput, scion, command, new String[] { "spook" }, "spook");

        assertNotNull(output);

        verify(entityService).entitySearchInWorld(eq(scion), eq("spook"));
        verify(entityService).sendMessageToRoom(eq(spook), any(GameOutput.class));
        verify(movementService).put(eq(spook), eq(new Coordinate(0L, 0L, 0L)));
        verify(entityService).sendMessageToRoom(eq(new Coordinate(0L, 0L, 0L)), Mockito.anyCollectionOf(Entity.class), any(GameOutput.class));
        verify(applicationContext).getBean(eq("lookCommand"));
        verify(lookCommand).execute(any(GameOutput.class), eq(spook), eq("look"), any(String[].class), eq(""));
        verify(entityService).sendMessageToEntity(eq(spook), any(GameOutput.class));
    }
}
