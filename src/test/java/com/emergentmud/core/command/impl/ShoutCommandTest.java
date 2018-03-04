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

import com.emergentmud.core.command.BaseCommunicationCommandTest;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.service.EntityService;
import com.emergentmud.core.service.RoomService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.emergentmud.core.command.impl.ShoutCommand.SHOUT_DISTANCE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ShoutCommandTest extends BaseCommunicationCommandTest {
    @Mock
    private EntityRepository entityRepository;

    @Mock
    private EntityService entityService;

    @Mock
    private RoomService roomService;

    @Mock
    private GameOutput output;

    @Mock
    private Entity entity;

    @Captor
    private ArgumentCaptor<GameOutput> outputCaptor;

    @Captor
    private ArgumentCaptor<List<Entity>> entityListCaptor;

    private String cmd = "shout";

    private ShoutCommand command;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(entity.getId()).thenReturn(UUID.randomUUID());
        when(entity.getName()).thenReturn("Testy");
        when(entity.getX()).thenReturn(0L);
        when(entity.getY()).thenReturn(0L);
        when(entity.getZ()).thenReturn(0L);

        when(roomService.isWithinDistance(any(Entity.class), anyLong(), anyLong(), anyLong(), anyDouble())).thenCallRealMethod();

        command = new ShoutCommand(entityRepository, roomService, entityService);
    }

    @Test
    public void testDescription() throws Exception {
        assertNotEquals("No description.", command.getDescription());
    }

    @Test
    public void testShoutSomething() throws Exception {
        GameOutput response = command.execute(output, entity, cmd,
                new String[] { "Feed", "me", "a", "stray", "cat." },
                "Feed me a stray cat.");

        verify(response).append(eq("[dyellow]You shout 'Feed me a stray cat.[dyellow]'"));
        verify(entityService).sendMessageToListeners(anyListOf(Entity.class), eq(entity), outputCaptor.capture());

        GameOutput output = outputCaptor.getValue();

        assertTrue(output.getOutput().get(0).equals("[dyellow]Testy shouts 'Feed me a stray cat.[dyellow]'"));
    }

    @Test
    public void testShoutRadius() throws Exception {
        List<Entity> entities = new ArrayList<>();

        for (long y = -SHOUT_DISTANCE; y < SHOUT_DISTANCE; y++) {
            for (long x = -SHOUT_DISTANCE; x < SHOUT_DISTANCE; x++) {
                Entity entity = mock(Entity.class);

                when(entity.getX()).thenReturn(x);
                when(entity.getY()).thenReturn(y);
                when(entity.getZ()).thenReturn(0L);

                entities.add(entity);
            }
        }

        when(entityRepository.findByLocationBetween(
                eq(-7L),
                eq(7L),
                eq(-7L),
                eq(7L),
                eq(-7L),
                eq(7L)
        )).thenReturn(entities);

        doNothing().when(entityService).sendMessageToListeners(
                entityListCaptor.capture(),
                any(Entity.class),
                any(GameOutput.class));

        GameOutput response = command.execute(output, entity, cmd,
                new String[] { "Feed", "me", "a", "stray", "cat." },
                "Feed me a stray cat.");

        List<Entity> filteredEntities = entityListCaptor.getValue();

        assertNotNull(response);

        // 196 would be the full square
        // 147 is the circle
        assertEquals(147, filteredEntities.size());
    }

    @Test
    public void testShoutSomethingWithSymbols() throws Exception {
        GameOutput response = command.execute(output, entity, cmd,
                new String[] { "<script", "type=\"text/javascript\">var", "evil", "=", "\"stuff\";</script>" },
                "<script type=\"text/javascript\">var evil = \"stuff\";</script>");

        verify(response).append(eq("[dyellow]You shout '&lt;script type=&quot;text/javascript&quot;&gt;var evil = &quot;stuff&quot;;&lt;/script&gt;[dyellow]'"));
        verify(entityService).sendMessageToListeners(anyListOf(Entity.class), eq(entity), outputCaptor.capture());

        GameOutput output = outputCaptor.getValue();

        assertTrue(output.getOutput().get(0).equals("[dyellow]Testy shouts '&lt;script type=&quot;text/javascript&quot;&gt;var evil = &quot;stuff&quot;;&lt;/script&gt;[dyellow]'"));
    }

    @Test
    public void testShoutNothing() throws Exception {
        GameOutput response = command.execute(output, entity, cmd, new String[] {}, "");

        verify(response).append(eq("What would you like to shout?"));
    }
}
