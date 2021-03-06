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

import com.emergentmud.core.command.Command;
import com.emergentmud.core.exception.NoSuchRoomException;
import com.emergentmud.core.model.Coordinate;
import com.emergentmud.core.model.Direction;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.service.MovementService;
import com.emergentmud.core.service.EntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MoveCommandTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MovementService movementService;

    @Mock
    private EntityService entityService;

    @Mock
    private GameOutput output;

    @Mock
    private Entity entity;

    @Mock
    private Command look;

    private String[] tokens = new String[] {};
    private String raw = "";
    private String cmd = "e";

    private MoveCommand command;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(applicationContext.getBean(eq("lookCommand"))).thenReturn(look);
        when(entity.getLocation()).thenReturn(new Coordinate(0L, 0L, 0L));

        doAnswer(i -> {
            Entity entity = i.getArgumentAt(0, Entity.class);
            long x = i.getArgumentAt(1, Coordinate.class).getX();
            long y = i.getArgumentAt(1, Coordinate.class).getY();
            long z = i.getArgumentAt(1, Coordinate.class).getZ();

            Coordinate location = mock(Coordinate.class);

            when(location.getX()).thenReturn(x);
            when(location.getY()).thenReturn(y);
            when(location.getZ()).thenReturn(z);
            when(entity.getLocation()).thenReturn(location);

            return entity;
        }).when(movementService).put(any(Entity.class), any(Coordinate.class));

        command = new MoveCommand(Direction.NORTH, applicationContext, movementService, entityService);
    }

    @Test
    public void testDescription() {
        assertNotEquals("No description.", command.getDescription());
    }

    @Test
    public void testMoveInVoid() {
        when(entity.getLocation()).thenReturn(null);

        command.execute(output, entity, cmd, tokens, raw);

        verifyZeroInteractions(movementService);
        verifyZeroInteractions(applicationContext);
    }

    @Test
    public void testExitMessageOnBlockedMovement() {
        try {
            doThrow(new NoSuchRoomException("Alas!")).when(movementService).put(any(Entity.class), any(Coordinate.class));

            command.execute(output, entity, cmd, tokens, raw);
        } catch (NoSuchRoomException e) {
            fail("Exception should not have bubbled up this far.");
        }

        verify(entityService, never()).sendMessageToRoom(any(Coordinate.class), any(Entity.class), any(GameOutput.class));
    }

    @Test
    public void testExitMessageOnMovement() {
        command.execute(output, entity, cmd, tokens, raw);

        verify(entityService).sendMessageToRoom(eq(entity), any(GameOutput.class));
    }
}
