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
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.util.EntityUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class GossipCommandTest extends BaseCommunicationCommandTest {
    @Mock
    private EntityRepository entityRepository;

    @Mock
    private GameOutput output;

    @Mock
    private Entity entity;

    @Mock
    private EntityUtil entityUtil;

    @Captor
    private ArgumentCaptor<GameOutput> outputCaptor;

    private GossipCommand command;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        List<Entity> worldContents = generateRoomContents();

        when(entity.getId()).thenReturn("id");
        when(entity.getName()).thenReturn("Testy");
        when(entityRepository.findByRoomIsNotNull()).thenReturn(worldContents);

        command = new GossipCommand(entityRepository, entityUtil);
    }

    @Test
    public void testGossipSomething() throws Exception {
        GameOutput response = command.execute(output, entity,
                new String[] { "Feed", "me", "a", "stray", "cat." },
                "Feed me a stray cat.");

        verify(response).append(eq("[green]You gossip 'Feed me a stray cat.[green]'"));
        verify(entityUtil).sendMessageToListeners(anyListOf(Entity.class), eq(entity), outputCaptor.capture());

        GameOutput output = outputCaptor.getValue();

        assertTrue(output.getOutput().get(0).equals("[green]Testy gossips 'Feed me a stray cat.[green]'"));
    }

    @Test
    public void testGossipSomethingWithSymbols() throws Exception {
        GameOutput response = command.execute(output, entity,
                new String[] { "<script", "type=\"text/javascript\">var", "evil", "=", "\"stuff\";</script>" },
                "<script type=\"text/javascript\">var evil = \"stuff\";</script>");

        verify(response).append(eq("[green]You gossip '&lt;script type=&quot;text/javascript&quot;&gt;var evil = &quot;stuff&quot;;&lt;/script&gt;[green]'"));
        verify(entityUtil).sendMessageToListeners(anyListOf(Entity.class), eq(entity), outputCaptor.capture());

        GameOutput output = outputCaptor.getValue();

        assertTrue(output.getOutput().get(0).equals("[green]Testy gossips '&lt;script type=&quot;text/javascript&quot;&gt;var evil = &quot;stuff&quot;;&lt;/script&gt;[green]'"));
    }

    @Test
    public void testSayNothing() throws Exception {
        GameOutput response = command.execute(output, entity, new String[] {}, "");

        verify(response).append(eq("What would you like to gossip?"));
    }
}
