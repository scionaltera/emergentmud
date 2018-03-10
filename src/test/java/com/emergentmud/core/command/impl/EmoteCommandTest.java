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
import com.emergentmud.core.model.Coordinate;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.service.EntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class EmoteCommandTest extends BaseCommunicationCommandTest {
    @Mock
    protected GameOutput output;

    @Mock
    protected Room room;

    @Mock
    protected Entity entity;

    @Mock
    private EntityService entityService;

    @Captor
    private ArgumentCaptor<GameOutput> outputCaptor;

    private String cmd = "emote";

    private EmoteCommand command;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(entity.getId()).thenReturn(UUID.randomUUID());
        when(entity.getName()).thenReturn("Testy");
        when(entity.getLocation()).thenReturn(new Coordinate(0L, 0L, 0L));
        when(room.getX()).thenReturn(0L);
        when(room.getY()).thenReturn(0L);
        when(room.getZ()).thenReturn(0L);

        command = new EmoteCommand(entityService);
    }

    @Test
    public void testDescription() {
        assertNotEquals("No description.", command.getDescription());
    }

    @Test
    public void testEmoteSomething() {
        GameOutput response = command.execute(output, entity, cmd,
                new String[] { "dies", "a", "little", "on", "the", "inside." },
                "dies a little on the inside.");

        verify(response).append(eq("Testy dies a little on the inside."));
        verify(entityService).sendMessageToRoom(eq(entity), outputCaptor.capture());

        GameOutput output = outputCaptor.getValue();

        assertTrue(output.getOutput().get(0).equals("Testy dies a little on the inside."));
    }

    @Test
    public void testEmoteSomethingWithSymbols() {
        GameOutput response = command.execute(output, entity, cmd,
                new String[] { "<script", "type=\"text/javascript\">var", "evil", "=", "\"stuff\";</script>" },
                "<script type=\"text/javascript\">var evil = \"stuff\";</script>");

        verify(response).append(eq("Testy &lt;script type=&quot;text/javascript&quot;&gt;var evil = &quot;stuff&quot;;&lt;/script&gt;"));
        verify(entityService).sendMessageToRoom(eq(entity), outputCaptor.capture());

        GameOutput output = outputCaptor.getValue();

        assertTrue(output.getOutput().get(0).equals("Testy &lt;script type=&quot;text/javascript&quot;&gt;var evil = &quot;stuff&quot;;&lt;/script&gt;"));
    }

    @Test
    public void testEmoteNothing() {
        GameOutput response = command.execute(output, entity, cmd, new String[] {}, "");

        verify(response).append(eq("What would you like to emote?"));
    }
}
