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

package com.emergentmud.core.event;

import com.emergentmud.core.model.Coordinate;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.service.MovementService;
import com.emergentmud.core.service.EntityService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import static org.mockito.Mockito.*;

public class StompDisconnectListenerTest {
    private EntityRepository entityRepository;
    private MovementService movementService;
    private EntityService entityService;
    private OAuth2Authentication principal;
    private SessionDisconnectEvent event;
    private Entity entity;
    private Room room;

    private String simpSessionId = "simpSessionId";
    private String socialUserName = "alteranetUser";

    private StompDisconnectListener stompDisconnectListener;

    @Before
    public void setUp() throws Exception {
        entityRepository = mock(EntityRepository.class);
        movementService = mock(MovementService.class);
        entityService = mock(EntityService.class);
        principal = mock(OAuth2Authentication.class);
        event = mock(SessionDisconnectEvent.class);
        entity = mock(Entity.class);
        room = mock(Room.class);

        when(event.getSessionId()).thenReturn(simpSessionId);
        when(event.getUser()).thenReturn(principal);
        when(principal.getName()).thenReturn(socialUserName);
        when(entityRepository.findByStompSessionIdAndStompUsername(
                eq(simpSessionId),
                eq(socialUserName)
        )).thenReturn(entity);
        when(entity.getLocation()).thenReturn(new Coordinate(0, 0, 0));

        stompDisconnectListener = new StompDisconnectListener(
                entityRepository,
                movementService,
                entityService
        );
    }

    @Test
    public void applicationEventTest() {
        stompDisconnectListener.onApplicationEvent(event);

        verify(entityRepository).findByStompSessionIdAndStompUsername(
                eq(simpSessionId),
                eq(socialUserName)
        );
        verify(entityService).sendMessageToRoom(eq(entity), any(GameOutput.class));
        verify(movementService).remove(eq(entity));
    }

    @Test
    public void applicationEventNoRoom() {
        when(entity.getLocation()).thenReturn(null);

        stompDisconnectListener.onApplicationEvent(event);

        verify(entityRepository).findByStompSessionIdAndStompUsername(
                eq(simpSessionId),
                eq(socialUserName)
        );
        verify(entityService, never()).sendMessageToRoom(any(Coordinate.class), any(Entity.class), any(GameOutput.class));
        verify(movementService).remove(eq(entity));
    }

    @Test
    public void applicationEventNoEntity() {
        when(entityRepository.findByStompSessionIdAndStompUsername(anyString(), anyString())).thenReturn(null);

        stompDisconnectListener.onApplicationEvent(event);

        verifyZeroInteractions(movementService);
    }
}
