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

import com.emergentmud.core.model.Direction;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.room.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.WorldManager;
import com.emergentmud.core.service.EntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MoveCommandTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private WorldManager worldManager;

    @Mock
    private EntityService entityService;

    @Mock
    private GameOutput output;

    @Mock
    private Entity entity;

    @Mock
    private Entity observer;

    @Mock
    private Room room;

    @Mock
    private Room room2;

    @Mock
    private LookCommand lookCommand;

    private String[] tokens = new String[] {};
    private String raw = "";
    private String cmd = "e";

    private MoveCommand command;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(entity.getName()).thenReturn("Stu");
        when(entity.getX()).thenCallRealMethod();
        when(entity.getY()).thenCallRealMethod();
        when(entity.getZ()).thenCallRealMethod();
        when(observer.getStompSessionId()).thenReturn("observerId");
        doCallRealMethod().when(entity).setX(anyLong());
        doCallRealMethod().when(entity).setY(anyLong());
        doCallRealMethod().when(entity).setZ(anyLong());

        Stream.of(room, room2)
                .forEach(r -> {
                    when(r.getX()).thenCallRealMethod();
                    when(r.getY()).thenCallRealMethod();
                    when(r.getZ()).thenCallRealMethod();
                    doCallRealMethod().when(r).setX(anyLong());
                    doCallRealMethod().when(r).setY(anyLong());
                    doCallRealMethod().when(r).setZ(anyLong());
                });

        when(worldManager.put(eq(entity), eq(0L), eq(1L), eq(0L))).thenReturn(entity);
        when(worldManager.put(eq(observer), eq(0L), eq(1L), eq(0L))).thenReturn(observer);

        when(applicationContext.getBean(eq("lookCommand"))).thenReturn(lookCommand);

        command = new MoveCommand(Direction.NORTH, applicationContext, worldManager, entityService);
    }

    @Test
    public void testDescription() throws Exception {
        assertNotEquals("No description.", command.getDescription());
    }

    @Test
    public void testMove() throws Exception {
        room.setX(0L);
        room.setY(0L);
        room.setZ(0L);
        entity.setX(0L);
        entity.setY(0L);
        entity.setZ(0L);

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(worldManager).remove(eq(entity));
        verify(worldManager).put(eq(entity), eq(0L), eq(1L), eq(0L));
        verify(applicationContext).getBean(eq("lookCommand"));
        verify(lookCommand).execute(eq(output), eq(entity), eq("look"), eq(new String[0]), eq(""));
        verify(entityService).sendMessageToRoom(eq(0L), eq(0L), eq(0L), eq(entity), any(GameOutput.class));
        verify(entityService).sendMessageToRoom(eq(0L), eq(1L), eq(0L), eq(entity), any(GameOutput.class));
    }

    @Test
    public void testMoveNoRoom() throws Exception {
        room.setX(0L);
        room.setY(0L);
        room.setZ(0L);
        entity.setX(0L);
        entity.setY(0L);
        entity.setZ(0L);

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(worldManager, never()).remove(eq(entity));
        verify(worldManager, never()).put(eq(entity), eq(0L), eq(1L), eq(0L));
    }

    @Test
    public void testMoveInVoid() throws Exception {
        command.execute(output, entity, cmd, tokens, raw);

        verifyZeroInteractions(worldManager);
        verifyZeroInteractions(applicationContext);
    }
}
