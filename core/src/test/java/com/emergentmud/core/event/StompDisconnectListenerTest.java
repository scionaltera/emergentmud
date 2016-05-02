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

package com.emergentmud.core.event;

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.repository.WorldManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(StompHeaderAccessor.class)
public class StompDisconnectListenerTest {
    private SessionRepository sessionRepository;
    private EntityRepository entityRepository;
    private WorldManager worldManager;

    private StompDisconnectListener stompDisconnectListener;

    @Before
    public void setUp() throws Exception {
        sessionRepository = mock(SessionRepository.class);
        entityRepository = mock(EntityRepository.class);
        worldManager = mock(WorldManager.class);

        stompDisconnectListener = new StompDisconnectListener(
                sessionRepository,
                entityRepository,
                worldManager
        );
    }

    @Test
    public void applicationEventTest() throws Exception {
        SessionDisconnectEvent event = mock(SessionDisconnectEvent.class);
        Message message = mock(Message.class);
        StompHeaderAccessor sha = mock(StompHeaderAccessor.class);
        Map<String, Object> sessionAttributes = new HashMap<>();
        String springSessionId = UUID.randomUUID().toString();
        String entityId = "entityId";
        Session session = mock(Session.class);
        Entity entity = mock(Entity.class);
        Room room = mock(Room.class);

        sessionAttributes.put("SPRING.SESSION.ID", springSessionId);

        PowerMockito.mockStatic(StompHeaderAccessor.class);
        PowerMockito.when(StompHeaderAccessor.wrap(any(Message.class))).thenReturn(sha);

        when(event.getMessage()).thenReturn(message);
        when(sha.getSessionAttributes()).thenReturn(sessionAttributes);
        when(sessionRepository.getSession(eq(springSessionId))).thenReturn(session);
        when(entityRepository.findOne(eq(entityId))).thenReturn(entity);
        when(session.getAttribute("entity")).thenReturn(entityId);
        when(entity.getRoom()).thenReturn(room);

        stompDisconnectListener.onApplicationEvent(event);

        verify(sessionRepository).getSession(eq(springSessionId));
        verify(entityRepository).findOne(eq(entityId));
        verify(worldManager).remove(eq(entity), eq(0L), eq(0L), eq(0L));
    }

    @Test
    public void applicationEventTestNoRoom() throws Exception {
        SessionDisconnectEvent event = mock(SessionDisconnectEvent.class);
        Message message = mock(Message.class);
        StompHeaderAccessor sha = mock(StompHeaderAccessor.class);
        Map<String, Object> sessionAttributes = new HashMap<>();
        String springSessionId = UUID.randomUUID().toString();
        String entityId = "entityId";
        Session session = mock(Session.class);
        Entity entity = mock(Entity.class);

        sessionAttributes.put("SPRING.SESSION.ID", springSessionId);

        PowerMockito.mockStatic(StompHeaderAccessor.class);
        PowerMockito.when(StompHeaderAccessor.wrap(any(Message.class))).thenReturn(sha);

        when(event.getMessage()).thenReturn(message);
        when(sha.getSessionAttributes()).thenReturn(sessionAttributes);
        when(sessionRepository.getSession(eq(springSessionId))).thenReturn(session);
        when(entityRepository.findOne(eq(entityId))).thenReturn(entity);
        when(session.getAttribute("entity")).thenReturn(entityId);

        stompDisconnectListener.onApplicationEvent(event);

        verify(sessionRepository).getSession(eq(springSessionId));
        verify(entityRepository).findOne(eq(entityId));
        verify(worldManager, never()).remove(eq(entity), eq(0L), eq(0L), eq(0L));
    }

    @Test
    public void applicationEventTestNoEntity() throws Exception {
        SessionDisconnectEvent event = mock(SessionDisconnectEvent.class);
        Message message = mock(Message.class);
        StompHeaderAccessor sha = mock(StompHeaderAccessor.class);
        Map<String, Object> sessionAttributes = new HashMap<>();
        String springSessionId = UUID.randomUUID().toString();
        String entityId = "entityId";
        Session session = mock(Session.class);
        Entity entity = mock(Entity.class);

        sessionAttributes.put("SPRING.SESSION.ID", springSessionId);

        PowerMockito.mockStatic(StompHeaderAccessor.class);
        PowerMockito.when(StompHeaderAccessor.wrap(any(Message.class))).thenReturn(sha);

        when(event.getMessage()).thenReturn(message);
        when(sha.getSessionAttributes()).thenReturn(sessionAttributes);
        when(sessionRepository.getSession(eq(springSessionId))).thenReturn(session);

        stompDisconnectListener.onApplicationEvent(event);

        verify(sessionRepository).getSession(eq(springSessionId));
        verify(entityRepository).findOne(null);
        verify(worldManager, never()).remove(eq(entity), eq(0L), eq(0L), eq(0L));
    }

    @Test
    public void applicationEventTestNoSession() throws Exception {
        SessionDisconnectEvent event = mock(SessionDisconnectEvent.class);
        Message message = mock(Message.class);
        StompHeaderAccessor sha = mock(StompHeaderAccessor.class);
        Map<String, Object> sessionAttributes = new HashMap<>();
        String springSessionId = UUID.randomUUID().toString();
        Entity entity = mock(Entity.class);

        sessionAttributes.put("SPRING.SESSION.ID", springSessionId);

        PowerMockito.mockStatic(StompHeaderAccessor.class);
        PowerMockito.when(StompHeaderAccessor.wrap(any(Message.class))).thenReturn(sha);

        when(event.getMessage()).thenReturn(message);
        when(sha.getSessionAttributes()).thenReturn(sessionAttributes);

        stompDisconnectListener.onApplicationEvent(event);

        verify(sessionRepository).getSession(eq(springSessionId));
        verify(entityRepository, never()).findOne(anyString());
        verify(worldManager, never()).remove(eq(entity), eq(0L), eq(0L), eq(0L));
    }
}
