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
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.repository.WorldManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MoveCommandTest {
    @Captor
    private ArgumentCaptor<MessageHeaders> messageHeadersArgumentCaptor;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private WorldManager worldManager;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private EntityRepository entityRepository;

    @Mock
    private GameOutput output;

    @Mock
    private Entity entity;

    @Mock
    private Entity observer;

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

        when(entity.getName()).thenReturn("Stu");
        when(entity.getRoom()).thenCallRealMethod();
        when(observer.getStompSessionId()).thenReturn("observerId");
        doCallRealMethod().when(entity).setRoom(any(Room.class));
        when(room.getX()).thenCallRealMethod();
        when(room.getY()).thenCallRealMethod();
        when(room.getZ()).thenCallRealMethod();
        doCallRealMethod().when(room).setX(anyLong());
        doCallRealMethod().when(room).setY(anyLong());
        doCallRealMethod().when(room).setZ(anyLong());

        when(entityRepository.findByRoom(any(Room.class))).thenReturn(Collections.singletonList(observer));

        when(worldManager.test(eq(1L), eq(1L), eq(1L))).thenReturn(true);

        when(applicationContext.getBean(eq("lookCommand"))).thenReturn(lookCommand);

        moveCommand = new MoveCommand(1, 1, 1, "move", "unmove", applicationContext, worldManager, entityRepository, simpMessagingTemplate);
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
        verify(entityRepository, times(2)).findByRoom(any(Room.class));
        verify(simpMessagingTemplate, times(2)).convertAndSendToUser(
                anyString(),
                eq("/queue/output"),
                any(GameOutput.class),
                messageHeadersArgumentCaptor.capture()
        );

        List<MessageHeaders> messageHeaders = messageHeadersArgumentCaptor.getAllValues();

        for (int i = 0; i < 2; i++) {
            SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.getAccessor(messageHeaders.get(i), SimpMessageHeaderAccessor.class);

            assertEquals("observerId", accessor.getSessionId());
            assertTrue(accessor.isMutable());
        }
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
