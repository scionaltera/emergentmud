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
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.service.EntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TellCommandTest extends BaseCommunicationCommandTest {
    @Mock
    private EntityRepository entityRepository;

    @Mock
    private GameOutput output;

    @Mock
    private Room room;

    @Mock
    private Entity entity;

    @Mock
    private EntityService entityService;

    @Captor
    private ArgumentCaptor<GameOutput> outputCaptor;

    private String cmd = "tell";

    private TellCommand command;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        stu = generateRoomContents().get(0);

        when(entity.getId()).thenReturn(UUID.randomUUID());
        when(entity.getName()).thenReturn("Testy");
        when(entity.getX()).thenReturn(0L);
        when(entity.getY()).thenReturn(0L);
        when(entity.getZ()).thenReturn(0L);
        when(entityRepository.findByNameStartingWithIgnoreCase(contains("stu"))).thenReturn(stu);
        when(entityRepository.findByNameStartingWithIgnoreCase(contains("testy"))).thenReturn(entity);

        command = new TellCommand(entityRepository, entityService);
    }

    @Test
    public void testDescription() throws Exception {
        assertNotEquals("No description.", command.getDescription());
    }

    @Test
    public void testTellNoArgs() throws Exception {
        GameOutput response = command.execute(output, entity, cmd,
                new String[] { },
                "");

        verify(response).append(eq("Usage: TELL &lt;target&gt; &lt;message&gt;"));
        verifyZeroInteractions(entityService);
    }

    @Test
    public void testTellWithoutMessage() throws Exception {
        GameOutput response = command.execute(output, entity, cmd,
                new String[] { "stu" },
                "stu");

        verify(response).append(eq("Usage: TELL &lt;target&gt; &lt;message&gt;"));
        verifyZeroInteractions(entityService);
    }


    @Test
    public void testTellWithNonexistentTarget() throws Exception {
        GameOutput response = command.execute(output, entity, cmd,
                new String[] { "frodo", "Feed", "me", "a", "stray", "cat." },
                "frodo Feed me a stray cat.");

        verify(response).append(eq("You don't know of anyone by that name."));
        verifyZeroInteractions(entityService);
    }

    @Test
    public void testTellSelf() throws Exception {
        GameOutput response = command.execute(output, entity, cmd,
                new String[] { "testy", "Feed", "me", "a", "stray", "cat." },
                "testy Feed me a stray cat.");

        verify(response).append(eq("You murmur quietly to yourself."));
        verifyZeroInteractions(entityService);
    }

    @Test
    public void testTellWithSingleWordMessage() throws Exception {
        GameOutput response = command.execute(output, entity, cmd,
                new String[] { "stu", "Ahoy!" },
                "stu Ahoy!");

        verify(response).append(eq("[red]You tell Stu 'Ahoy![red]'"));
        verify(entityService).sendMessageToEntity(eq(stu), outputCaptor.capture());

        GameOutput output = outputCaptor.getValue();

        assertTrue(output.getOutput().get(0).equals("[red]Testy tells you 'Ahoy![red]'"));
    }

    @Test
    public void testTellSomething() throws Exception {
        GameOutput response = command.execute(output, entity, cmd,
                new String[] { "stu", "Feed", "me", "a", "stray", "cat." },
                "stu Feed me a stray cat.");

        verify(response).append(eq("[red]You tell Stu 'Feed me a stray cat.[red]'"));
        verify(entityService).sendMessageToEntity(eq(stu), outputCaptor.capture());

        GameOutput output = outputCaptor.getValue();

        assertTrue(output.getOutput().get(0).equals("[red]Testy tells you 'Feed me a stray cat.[red]'"));
    }

    @Test
    public void testTellSomethingWithSymbols() throws Exception {
        GameOutput response = command.execute(output, entity, cmd,
                new String[] { "stu", "<script", "type=\"text/javascript\">var", "evil", "=", "\"stuff\";</script>" },
                "stu <script type=\"text/javascript\">var evil = \"stuff\";</script>");

        verify(response).append(eq("[red]You tell Stu '&lt;script type=&quot;text/javascript&quot;&gt;var evil = &quot;stuff&quot;;&lt;/script&gt;[red]'"));
        verify(entityService).sendMessageToEntity(eq(stu), outputCaptor.capture());

        GameOutput output = outputCaptor.getValue();

        assertTrue(output.getOutput().get(0).equals("[red]Testy tells you '&lt;script type=&quot;text/javascript&quot;&gt;var evil = &quot;stuff&quot;;&lt;/script&gt;[red]'"));
    }

    @Test
    public void testSayNothing() throws Exception {
        GameOutput response = command.execute(output, entity, cmd, new String[] {}, "");

        verify(response).append(eq("Usage: TELL &lt;target&gt; &lt;message&gt;"));
    }
}
