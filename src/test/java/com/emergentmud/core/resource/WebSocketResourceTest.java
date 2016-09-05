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

package com.emergentmud.core.resource;

import com.emergentmud.core.command.Command;
import com.emergentmud.core.model.CommandMetadata;
import com.emergentmud.core.model.EmoteMetadata;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Essence;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.model.stomp.UserInput;
import com.emergentmud.core.repository.CommandMetadataRepository;
import com.emergentmud.core.repository.EmoteMetadataRepository;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.repository.EssenceRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class WebSocketResourceTest {
    private static final String APPLICATION_VERSION = "0.1.0-SNAPSHOT";
    private static final Long APPLICATION_BOOT_DATE = System.currentTimeMillis();
    private static final String PRINCIPAL_USER = "124567890";
    private static final String ACCOUNT_ID = UUID.randomUUID().toString();
    private static final String ESSENCE_ID = UUID.randomUUID().toString();
    private static final String ENTITY_ID = UUID.randomUUID().toString();

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private EssenceRepository essenceRepository;

    @Mock
    private EntityRepository entityRepository;

    @Mock
    private CommandMetadataRepository commandMetadataRepository;

    @Mock
    private EmoteMetadataRepository emoteMetadataRepository;

    @Mock
    private OAuth2Authentication principal;

    @Mock
    private OAuth2AuthenticationDetails oauth2Details;

    @Mock
    private Session httpSession;

    @Mock
    private Essence essence;

    @Mock
    private Entity entity;

    @Mock
    private Command mockCommand;

    private String breadcrumb = UUID.randomUUID().toString();
    private String simpSessionId = "simpSessionId";
    private String httpSessionId = "httpSessionId";
    private Map<String, String> sessionMap;
    private List<CommandMetadata> commandList;
    private List<EmoteMetadata> emoteList;

    private WebSocketResource webSocketResource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sessionMap = generateSessionMap();
        commandList = generateCommandList();
        emoteList = generateEmoteList();

        when(principal.getDetails()).thenReturn(oauth2Details);
        when(principal.getName()).thenReturn(PRINCIPAL_USER);
        when(entity.getStompUsername()).thenReturn(PRINCIPAL_USER);
        when(entity.getStompSessionId()).thenReturn("simpSessionId");
        when(oauth2Details.getSessionId()).thenReturn(httpSessionId);
        when(httpSession.getAttribute(eq(breadcrumb))).thenReturn(sessionMap);
        when(sessionRepository.getSession(eq(httpSessionId))).thenReturn(httpSession);
        when(essenceRepository.findOne(eq(ESSENCE_ID))).thenReturn(essence);
        when(entityRepository.save(any(Entity.class))).thenAnswer(invocation -> {
            Entity entity = (Entity)invocation.getArguments()[0];

            entity.setId(UUID.randomUUID().toString());

            return entity;
        });
        when(commandMetadataRepository.findAll(any(Sort.class))).thenReturn(commandList);
        when(emoteMetadataRepository.findAll(any(Sort.class))).thenReturn(emoteList);
        when(applicationContext.getBean(anyString())).thenReturn(mockCommand);
        when(mockCommand.execute(any(), any(), any(), any())).thenAnswer(invocation -> {
            GameOutput output = (GameOutput)invocation.getArguments()[0];

            output.append("[green]Test output.");

            return output;
        });
        when(essence.getEntity()).thenReturn(entity);

        webSocketResource = new WebSocketResource(
                APPLICATION_VERSION,
                APPLICATION_BOOT_DATE,
                applicationContext,
                sessionRepository,
                essenceRepository,
                entityRepository,
                commandMetadataRepository,
                emoteMetadataRepository
        );
    }

    @Test
    public void testOnSubscribe() throws Exception {
        GameOutput output = webSocketResource.onSubscribe(principal, breadcrumb, simpSessionId);

        verify(entity).setStompUsername(eq(PRINCIPAL_USER));
        verify(entity).setStompSessionId(eq("simpSessionId"));
        assertEquals(17, output.getOutput().size());
    }

    @Test
    public void testOnInputBlank() throws Exception {
        UserInput input = mock(UserInput.class);

        when(input.getInput()).thenReturn("");

        GameOutput output = webSocketResource.onInput(input, principal, breadcrumb, simpSessionId);

        verify(applicationContext, never()).getBean(anyString());
        assertEquals("", output.getOutput().get(0));
    }

    @Test
    public void testOnInput() throws Exception {
        UserInput input = mock(UserInput.class);

        when(input.getInput()).thenReturn("look");

        GameOutput output = webSocketResource.onInput(input, principal, breadcrumb, simpSessionId);

        verify(applicationContext).getBean(eq("lookCommand"));
        assertEquals("[green]Test output.", output.getOutput().get(0));
    }

    @Test
    public void testOnEmote() throws Exception {
        UserInput input = mock(UserInput.class);

        when(input.getInput()).thenReturn("wink");

        GameOutput output = webSocketResource.onInput(input, principal, breadcrumb, simpSessionId);

        verify(applicationContext, never()).getBean(anyString());
        assertEquals("[yellow]Emote wink exists.", output.getOutput().get(0));
    }

    @Test
    public void testOnInputNotAnAdmin() throws Exception {
        UserInput input = mock(UserInput.class);

        when(input.getInput()).thenReturn("info");

        GameOutput output = webSocketResource.onInput(input, principal, breadcrumb, simpSessionId);

        verify(applicationContext, never()).getBean(anyString());
        assertEquals("Huh?", output.getOutput().get(0));
    }

    @Test
    public void testOnInputIsAnAdmin() throws Exception {
        UserInput input = mock(UserInput.class);

        when(essence.isAdmin()).thenReturn(true);
        when(input.getInput()).thenReturn("info");

        GameOutput output = webSocketResource.onInput(input, principal, breadcrumb, simpSessionId);

        verify(applicationContext).getBean(eq("infoCommand"));
        verify(mockCommand).execute(any(GameOutput.class), eq(entity), eq(new String[] {}), eq(""));
        assertEquals("[green]Test output.", output.getOutput().get(0));
    }

    @Test
    public void testOnInputMultipleArgs() throws Exception {
        UserInput input = mock(UserInput.class);

        when(input.getInput()).thenReturn("say I love EmergentMUD!");

        GameOutput output = webSocketResource.onInput(input, principal, breadcrumb, simpSessionId);

        verify(applicationContext).getBean(eq("sayCommand"));
        verify(mockCommand).execute(
                any(GameOutput.class),
                any(Entity.class),
                eq(new String[] { "I", "love", "EmergentMUD!" }),
                eq("I love EmergentMUD!")
        );
        assertEquals("[green]Test output.", output.getOutput().get(0));
    }

    @Test
    public void testOnInputUnknownCommand() throws Exception {
        UserInput input = mock(UserInput.class);

        when(applicationContext.getBean(anyString())).thenReturn(null);
        when(input.getInput()).thenReturn("flarg");

        GameOutput output = webSocketResource.onInput(input, principal, breadcrumb, simpSessionId);

        verify(applicationContext, never()).getBean(anyString());
        assertEquals(3, output.getOutput().size());
        assertEquals("Huh?", output.getOutput().get(0));
    }

    @Test
    public void testOnInputNoEntity() throws Exception {
        UserInput input = mock(UserInput.class);

        when(input.getInput()).thenReturn("look");
        when(essence.getEntity()).thenReturn(null);

        GameOutput output = webSocketResource.onInput(input, principal, breadcrumb, simpSessionId);

        verify(applicationContext, never()).getBean(anyString());
        assertEquals(1, output.getOutput().size());
        assertTrue(output.getOutput().get(0).startsWith("[red]"));
    }

    @Test
    public void testOnInputInvalidSession() throws Exception {
        UserInput input = mock(UserInput.class);

        when(input.getInput()).thenReturn("look");
        when(entity.getStompSessionId()).thenReturn(UUID.randomUUID().toString());

        GameOutput output = webSocketResource.onInput(input, principal, breadcrumb, simpSessionId);

        verify(applicationContext, never()).getBean(anyString());
        assertEquals(1, output.getOutput().size());
        assertTrue(output.getOutput().get(0).startsWith("[red]"));
    }

    private Map<String, String> generateSessionMap() {
        Map<String, String> sessionMap = new HashMap<>();

        sessionMap.put("account", ACCOUNT_ID);
        sessionMap.put("essence", ESSENCE_ID);
        sessionMap.put("entity", ENTITY_ID);

        return sessionMap;
    }

    private List<CommandMetadata> generateCommandList() {
        List<CommandMetadata> metadataList = new ArrayList<>();

        metadataList.add(new CommandMetadata("look", "lookCommand", 100, false));
        metadataList.add(new CommandMetadata("say", "sayCommand", 200, false));
        metadataList.add(new CommandMetadata("info", "infoCommand", 300, true));
        metadataList.add(new CommandMetadata("cmdedit", "commandEditCommand", 1000, true));

        return metadataList;
    }

    private List<EmoteMetadata> generateEmoteList() {
        List<EmoteMetadata> metadataList = new ArrayList<>();

        metadataList.add(new EmoteMetadata("nod", 100));
        metadataList.add(new EmoteMetadata("wink", 100));
        metadataList.add(new EmoteMetadata("smile", 100));
        metadataList.add(new EmoteMetadata("sneeze", 100));

        return metadataList;
    }
}
