/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016 BoneVM, LLC
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

package com.emergentmud.core.command;

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.RoomRepository;
import com.emergentmud.core.repository.WorldManager;
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
    private WorldManager worldManager;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private GameOutput output;

    @Mock
    private Entity entity;

    @Mock
    private Room room;

    @Mock
    private LookCommand lookCommand;

    private String[] tokens = new String[] { "e" };
    private String raw = "e";

    private MoveCommand moveCommand;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(entity.getRoom()).thenCallRealMethod();
        doCallRealMethod().when(entity).setRoom(any(Room.class));
        when(room.getX()).thenCallRealMethod();
        when(room.getY()).thenCallRealMethod();
        when(room.getZ()).thenCallRealMethod();
        doCallRealMethod().when(room).setX(anyLong());
        doCallRealMethod().when(room).setY(anyLong());
        doCallRealMethod().when(room).setZ(anyLong());

        when(worldManager.test(eq(1L), eq(1L), eq(1L))).thenReturn(true);

        when(applicationContext.getBean(eq("lookCommand"))).thenReturn(lookCommand);

        moveCommand = new MoveCommand(1, 1, 1, applicationContext, worldManager, roomRepository);
    }

    @Test
    public void testMove() throws Exception {
        room.setX(0L);
        room.setY(0L);
        room.setZ(0L);
        entity.setRoom(room);

        GameOutput result = moveCommand.execute(output, entity, tokens, raw);

        assertNotNull(result);
        verify(worldManager).remove(eq(entity));
        verify(worldManager).put(eq(entity), eq(1L), eq(1L), eq(1L));
        verify(applicationContext).getBean(eq("lookCommand"));
        verify(lookCommand).execute(eq(output), eq(entity), eq(new String[0]), eq(""));
    }

    @Test
    public void testMoveNoRoom() throws Exception {
        room.setX(0L);
        room.setY(0L);
        room.setZ(0L);
        entity.setRoom(room);

        when(worldManager.test(eq(1L), eq(1L), eq(1L))).thenReturn(false);

        GameOutput result = moveCommand.execute(output, entity, tokens, raw);

        assertNotNull(result);
        verify(worldManager, never()).remove(eq(entity));
        verify(worldManager, never()).put(eq(entity), eq(1L), eq(1L), eq(1L));
    }

    @Test
    public void testMoveInVoid() throws Exception {
        moveCommand.execute(output, entity, tokens, raw);

        verifyZeroInteractions(worldManager);
        verifyZeroInteractions(applicationContext);
    }
}
