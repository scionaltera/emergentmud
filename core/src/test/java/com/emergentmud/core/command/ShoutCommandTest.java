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

import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.Zone;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.RoomRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ShoutCommandTest extends CommunicationCommandTestBase {
    @Mock
    private RoomRepository roomRepository;

    @Mock
    private Zone zone;

    private ShoutCommand command;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        List<Room> roomList = generateRoomList();

        roomContents = generateRoomContents();

        when(entity.getId()).thenReturn("id");
        when(entity.getName()).thenReturn("Testy");
        when(entity.getRoom()).thenReturn(room);
        when(room.getZone()).thenReturn(zone);
        when(room.getX()).thenReturn(0L);
        when(room.getY()).thenReturn(0L);
        when(room.getZ()).thenReturn(0L);
        when(roomRepository.findByZone(eq(zone))).thenReturn(roomList);
        when(entityRepository.findByRoomIn(anyListOf(Room.class))).thenReturn(roomContents);

        command = new ShoutCommand(simpMessagingTemplate, roomRepository, entityRepository);
    }

    @Test
    public void testShoutSomething() throws Exception {
        GameOutput response = command.execute(output, entity,
                new String[] { "Feed", "me", "a", "stray", "cat." },
                "Feed me a stray cat.");

        verify(response).append(eq("[dyellow]You shout 'Feed me a stray cat.[dyellow]'"));
        verify(simpMessagingTemplate).convertAndSendToUser(
                eq("stuSimpUsername"),
                eq("/queue/output"),
                any(GameOutput.class),
                headerCaptor.capture()
        );

        MessageHeaders headers = headerCaptor.getValue();
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.getAccessor(headers, SimpMessageHeaderAccessor.class);

        assertEquals("stuSimpSessionId", accessor.getSessionId());
        assertTrue(accessor.isMutable());
    }

    @Test
    public void testShoutSomethingWithSymbols() throws Exception {
        GameOutput response = command.execute(output, entity,
                new String[] { "<script", "type=\"text/javascript\">var", "evil", "=", "\"stuff\";</script>" },
                "<script type=\"text/javascript\">var evil = \"stuff\";</script>");

        verify(response).append(eq("[dyellow]You shout '&lt;script type=&quot;text/javascript&quot;&gt;var evil = &quot;stuff&quot;;&lt;/script&gt;[dyellow]'"));
        verify(simpMessagingTemplate).convertAndSendToUser(
                eq("stuSimpUsername"),
                eq("/queue/output"),
                any(GameOutput.class),
                headerCaptor.capture()
        );

        MessageHeaders headers = headerCaptor.getValue();
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.getAccessor(headers, SimpMessageHeaderAccessor.class);

        assertEquals("stuSimpSessionId", accessor.getSessionId());
        assertTrue(accessor.isMutable());
    }

    @Test
    public void testShoutNothing() throws Exception {
        GameOutput response = command.execute(output, entity, new String[] {}, "");

        verify(response).append(eq("What would you like to shout?"));
    }

    private List<Room> generateRoomList() {
        List<Room> rooms = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Room room = mock(Room.class);

            when(room.getId()).thenReturn("room-" + i);
            when(room.getX()).thenReturn((long)i);
            when(room.getY()).thenReturn(0L);
            when(room.getZ()).thenReturn(0L);
            when(room.getZone()).thenReturn(zone);

            rooms.add(room);
        }

        return rooms;
    }
}
