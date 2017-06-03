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

package com.emergentmud.core.util;

import com.emergentmud.core.command.PromptBuilder;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.room.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.service.EntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EntityServiceTest {
    @Mock
    private EntityRepository entityRepository;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private PromptBuilder promptBuilder;

    @Mock
    private Room room;

    @Mock
    private Entity entity;

    @Mock
    private GameOutput output;

    @Captor
    private ArgumentCaptor<MessageHeaders> messageHeadersArgumentCaptor;

    private List<Entity> contents;

    private EntityService entityService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        contents = generateContents();

        when(entityRepository.findByRoom(eq(room))).thenReturn(contents);
        when(entity.getId()).thenReturn("entityId");
        when(entity.getStompSessionId()).thenReturn("stompSessionId");
        when(entity.getStompUsername()).thenReturn("stompUsername");

        entityService = new EntityService(entityRepository, simpMessagingTemplate, promptBuilder);
    }

    @Test
    public void testSendMessageToEntity() throws Exception {
        entityService.sendMessageToEntity(entity, output);

        verify(simpMessagingTemplate).convertAndSendToUser(
                eq("stompUsername"),
                eq("/queue/output"),
                eq(output),
                messageHeadersArgumentCaptor.capture());

        MessageHeaders headers = messageHeadersArgumentCaptor.getValue();
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.getAccessor(headers, SimpMessageHeaderAccessor.class);

        assertEquals("stompSessionId", accessor.getSessionId());
        assertTrue(accessor.isMutable());
    }

    @Test
    public void testSendMessageToRoom() throws Exception {
        entityService.sendMessageToRoom(room, entity, output);

        verifyContents();
    }

    @Test
    public void testSendMessageToListeners() throws Exception {
        entityService.sendMessageToListeners(contents, entity, output);

        verifyContents();
    }

    @Test
    public void testSendMessageToListenersNoSender() throws Exception {
        entityService.sendMessageToListeners(contents, output);

        verifyContents();
    }

    private List<Entity> generateContents() {
        List<Entity> contents = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Entity e = mock(Entity.class);

            when(e.getStompUsername()).thenReturn("stompUsername" + i);
            when(e.getStompSessionId()).thenReturn("stompSessionId" + i);
            when(e.getId()).thenReturn("entityId" + i);

            contents.add(e);
        }

        return contents;
    }

    private void verifyContents() {
        for (int i = 0; i < contents.size(); i++) {
            verify(simpMessagingTemplate).convertAndSendToUser(
                    eq("stompUsername" + i),
                    eq("/queue/output"),
                    eq(output),
                    messageHeadersArgumentCaptor.capture()
            );

            MessageHeaders headers = messageHeadersArgumentCaptor.getValue();
            SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.getAccessor(headers, SimpMessageHeaderAccessor.class);

            assertEquals("stompSessionId" + i, accessor.getSessionId());
            assertTrue(accessor.isMutable());
        }
    }
}
