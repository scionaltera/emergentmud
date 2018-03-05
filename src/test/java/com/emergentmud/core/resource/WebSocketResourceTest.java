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

package com.emergentmud.core.resource;

import com.emergentmud.core.command.Command;
import com.emergentmud.core.command.Emote;
import com.emergentmud.core.command.PromptBuilder;
import com.emergentmud.core.model.Capability;
import com.emergentmud.core.model.CommandMetadata;
import com.emergentmud.core.model.CommandRole;
import com.emergentmud.core.model.EmoteMetadata;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.model.stomp.UserInput;
import com.emergentmud.core.repository.CapabilityRepository;
import com.emergentmud.core.repository.CommandMetadataRepository;
import com.emergentmud.core.repository.EmoteMetadataRepository;
import com.emergentmud.core.repository.EntityRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
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
    private static final UUID ACCOUNT_ID = UUID.randomUUID();
    private static final UUID ESSENCE_ID = UUID.randomUUID();
    private static final UUID ENTITY_ID = UUID.randomUUID();

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private EntityRepository entityRepository;

    @Mock
    private CommandMetadataRepository commandMetadataRepository;

    @Mock
    private EmoteMetadataRepository emoteMetadataRepository;

    @Mock
    private CapabilityRepository capabilityRepository;

    @Mock
    private PromptBuilder promptBuilder;

    @Mock
    private Emote emote;

    @Mock
    private OAuth2Authentication principal;

    @Mock
    private OAuth2AuthenticationDetails oauth2Details;

    @Mock
    private Session httpSession;

    @Mock
    private Capability superCapability;

    @Mock
    private Capability emoteCapability;

    @Mock
    private Capability seeCapability;

    @Mock
    private Capability talkCapability;

    @Mock
    private Capability dataCapability;

    @Mock
    private Capability cmdEditCapability;

    @Mock
    private Entity entity;

    @Mock
    private Entity target;

    @Mock
    private Entity observer;

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

        List<Entity> roomContents = new ArrayList<>();

        roomContents.add(entity);
        roomContents.add(target);
        roomContents.add(observer);

        when(principal.getDetails()).thenReturn(oauth2Details);
        when(principal.getName()).thenReturn(PRINCIPAL_USER);
        when(entity.getStompUsername()).thenReturn(PRINCIPAL_USER);
        when(entity.getStompSessionId()).thenReturn("simpSessionId");
        when(entity.getName()).thenReturn("Player");
        when(entity.isCapable(eq(seeCapability))).thenReturn(true);
        when(entity.isCapable(eq(talkCapability))).thenReturn(true);
        when(entity.isCapable(eq(emoteCapability))).thenReturn(true);
        when(target.getName()).thenReturn("Target");
        when(observer.getName()).thenReturn("Observer");
        when(oauth2Details.getSessionId()).thenReturn(httpSessionId);
        when(httpSession.getAttribute(eq(breadcrumb))).thenReturn(sessionMap);
        when(sessionRepository.findById(eq(httpSessionId))).thenReturn(httpSession);
        when(entityRepository.findOne(eq(ENTITY_ID))).thenReturn(entity);
        when(entityRepository.findByLocation(eq(0L), eq(0L), eq(0L))).thenReturn(roomContents);
        when(entityRepository.save(any(Entity.class))).thenAnswer(invocation -> {
            Entity entity = (Entity)invocation.getArguments()[0];

            entity.setId(UUID.randomUUID());

            return entity;
        });
        when(commandMetadataRepository.findAll(any(Sort.class))).thenReturn(commandList);
        when(emoteMetadataRepository.findAll(any(Sort.class))).thenReturn(emoteList);
        when(capabilityRepository.findByName(eq(CommandRole.SUPER.name()))).thenReturn(superCapability);
        when(capabilityRepository.findByName(eq(CommandRole.EMOTE.name()))).thenReturn(emoteCapability);
        when(applicationContext.getBean(anyString())).thenReturn(mockCommand);
        when(mockCommand.execute(any(), any(), any(), any(), any())).thenAnswer(invocation -> {
            GameOutput output = (GameOutput)invocation.getArguments()[0];

            output.append("[green]Test output.");

            return output;
        });
        doAnswer(invocation -> {
            GameOutput message = invocation.getArgumentAt(0, GameOutput.class);
            message.append("").append("[red]UnitTest> ");
            return null;
        }).when(promptBuilder).appendPrompt(any(GameOutput.class));

        webSocketResource = new WebSocketResource(
                APPLICATION_VERSION,
                APPLICATION_BOOT_DATE,
                applicationContext,
                sessionRepository,
                entityRepository,
                commandMetadataRepository,
                emoteMetadataRepository,
                capabilityRepository,
                promptBuilder,
                emote
        );
    }

    @Test
    public void testOnSubscribe() throws Exception {
        GameOutput output = webSocketResource.onSubscribe(principal, breadcrumb, simpSessionId);

        verify(entity).setStompUsername(eq(PRINCIPAL_USER));
        verify(entity).setStompSessionId(eq("simpSessionId"));
        assertEquals(18, output.getOutput().size());
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
        verify(emoteMetadataRepository).findAll(any(Sort.class));
        verify(emote).execute(eq(output), any(EmoteMetadata.class), eq(entity), eq(new String[0]));
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

        when(entity.isCapable(eq(dataCapability))).thenReturn(true);
        when(input.getInput()).thenReturn("info");

        GameOutput output = webSocketResource.onInput(input, principal, breadcrumb, simpSessionId);

        verify(applicationContext).getBean(eq("infoCommand"));
        verify(mockCommand).execute(any(GameOutput.class), eq(entity), eq("info"), eq(new String[] {}), eq(""));
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
                eq("say"),
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
        when(entityRepository.findOne(any(UUID.class))).thenReturn(null);

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

        sessionMap.put("account", ACCOUNT_ID.toString());
        sessionMap.put("essence", ESSENCE_ID.toString());
        sessionMap.put("entity", ENTITY_ID.toString());

        return sessionMap;
    }

    private List<CommandMetadata> generateCommandList() {
        List<CommandMetadata> metadataList = new ArrayList<>();

        metadataList.add(new CommandMetadata("look", "lookCommand", 100, seeCapability));
        metadataList.add(new CommandMetadata("say", "sayCommand", 200, talkCapability));
        metadataList.add(new CommandMetadata("info", "infoCommand", 300, dataCapability));
        metadataList.add(new CommandMetadata("cmdedit", "commandEditCommand", 1000, cmdEditCapability));

        return metadataList;
    }

    private List<EmoteMetadata> generateEmoteList() {
        List<EmoteMetadata> metadataList = new ArrayList<>();

        EmoteMetadata wink = mock(EmoteMetadata.class);

        when(wink.getId()).thenReturn(UUID.randomUUID());
        when(wink.getName()).thenReturn("wink");
        when(wink.getPriority()).thenReturn(100);
        when(wink.getToSelfUntargeted()).thenReturn("You wink.");
        when(wink.getToRoomUntargeted()).thenReturn("%self% winks.");
        when(wink.getToSelfWithTarget()).thenReturn("You wink at %target%.");
        when(wink.getToTarget()).thenReturn("%self% winks at you.");
        when(wink.getToRoomWithTarget()).thenReturn("%self% winks at %target%.");
        when(wink.getToSelfAsTarget()).thenReturn("You scrunch up your face, trying to wink at yourself.");
        when(wink.getToRoomTargetingSelf()).thenReturn("%self% scrunches up %his% face, like %he%'s trying to wink at %himself%.");

        EmoteMetadata nod = mock(EmoteMetadata.class);

        when(nod.getId()).thenReturn(UUID.randomUUID());
        when(nod.getName()).thenReturn("nod");
        when(nod.getPriority()).thenReturn(100);
        when(nod.getToSelfUntargeted()).thenReturn("You nod.");
        when(nod.getToRoomUntargeted()).thenReturn("%self% nods.");
        when(nod.getToSelfWithTarget()).thenReturn("You nod at %target%.");
        when(nod.getToTarget()).thenReturn("%self% nods at you.");
        when(nod.getToRoomWithTarget()).thenReturn("%self% nods at %target%.");

        metadataList.add(nod);
        metadataList.add(wink);
        metadataList.add(new EmoteMetadata("smile", 100));
        metadataList.add(new EmoteMetadata("sneeze", 100));

        return metadataList;
    }
}
