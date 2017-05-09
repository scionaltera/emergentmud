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

package com.emergentmud.core.resource;

import com.emergentmud.core.command.Command;
import com.emergentmud.core.command.Emote;
import com.emergentmud.core.exception.NoAccountException;
import com.emergentmud.core.model.Account;
import com.emergentmud.core.model.Capability;
import com.emergentmud.core.model.CapabilityObject;
import com.emergentmud.core.model.CapabilityScope;
import com.emergentmud.core.model.CommandMetadata;
import com.emergentmud.core.model.CommandRole;
import com.emergentmud.core.model.EmoteMetadata;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.SocialNetwork;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.AccountRepository;
import com.emergentmud.core.repository.CapabilityRepository;
import com.emergentmud.core.repository.CommandMetadataRepository;
import com.emergentmud.core.repository.EmoteMetadataRepository;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.repository.RoomBuilder;
import com.emergentmud.core.repository.WorldManager;
import com.emergentmud.core.resource.model.PlayRequest;
import com.emergentmud.core.util.EntityUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class MainResourceTest {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NETWORK_NAME = "AlteraNet";
    private static final String NETWORK_ID = "alteranet";
    private static final String NETWORK_USER = "007";
    private static final String ACCOUNT_ID = "1234567890";

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private SecurityContextLogoutHandler securityContextLogoutHandler;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private EntityRepository entityRepository;

    @Mock
    private CommandMetadataRepository commandMetadataRepository;

    @Mock
    private EmoteMetadataRepository emoteMetadataRepository;

    @Mock
    private CapabilityRepository capabilityRepository;

    @Mock
    private RoomBuilder roomBuilder;

    @Mock
    private WorldManager worldManager;

    @Mock
    private EntityUtil entityUtil;

    @Mock
    private Emote emote;

    @Mock
    private Capability playCapability;

    @Mock
    private Capability newCharCapability;

    @Mock
    private Capability normalCapability;

    @Mock
    private Capability adminCapability;

    @Mock
    private Capability adminAccountCapability;

    @Mock
    private HttpSession httpSession;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private OAuth2Authentication principal;

    @Mock
    private Model model;

    @Mock
    private PlayRequest playRequest;

    @Captor
    private ArgumentCaptor<Map<String, String>> mapCaptor;

    @Captor
    private ArgumentCaptor<GameOutput> outputCaptor;

    @Captor
    private ArgumentCaptor<Entity> entityCaptor;

    private Account account;
    private Entity entity;
    private List<SocialNetwork> socialNetworks = new ArrayList<>();
    private List<Entity> entities = new ArrayList<>();
    private List<EmoteMetadata> emoteMetadata = new ArrayList<>();

    private MainResource mainResource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        generateSocialNetworks();
        account = generateAccount();
        generateEntities();
        entity = entities.get(0);
        generateEmoteMetadata();

        when(worldManager.test(eq(0L), eq(0L), eq(0L))).thenReturn(true);
        when(httpSession.getAttribute(eq("social"))).thenReturn(NETWORK_ID);
        when(principal.getName()).thenReturn(NETWORK_USER);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account account = (Account)invocation.getArguments()[0];
            account.setId(UUID.randomUUID().toString());
            return account;
        });
        when(entityRepository.save(any(Entity.class))).thenAnswer(invocation -> {
            Entity entity = (Entity)invocation.getArguments()[0];
            entity.setId(UUID.randomUUID().toString());
            return entity;
        });
        when(account.isCapable(eq(playCapability))).thenReturn(true);
        when(accountRepository.count()).thenReturn(100L);
        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq(NETWORK_ID), eq(NETWORK_USER))).thenReturn(account);
        when(entityRepository.findByAccount(eq(account))).thenReturn(entities);
        when(entityRepository.findByAccountAndId(eq(account), eq("entity0"))).thenReturn(entity);
        when(playRequest.getEntityId()).thenReturn("entity0");
        when(capabilityRepository.findByName(eq(CommandRole.SUPER.name()))).thenReturn(adminCapability);
        when(capabilityRepository.findByName(eq(CommandRole.BASIC.name()))).thenReturn(normalCapability);
        when(capabilityRepository.findByName(eq(CommandRole.CHAR_PLAY.name()))).thenReturn(playCapability);
        when(capabilityRepository.findByName(eq(CommandRole.CHAR_NEW.name()))).thenReturn(newCharCapability);
        when(capabilityRepository.findByObjectAndScope(eq(CapabilityObject.ACCOUNT), eq(CapabilityScope.PLAYER))).thenReturn(Collections.singletonList(playCapability));
        when(capabilityRepository.findByObjectAndScope(eq(CapabilityObject.ACCOUNT), eq(CapabilityScope.IMPLEMENTOR))).thenReturn(Collections.singletonList(adminAccountCapability));
        when(capabilityRepository.findByObjectAndScope(eq(CapabilityObject.ENTITY), eq(CapabilityScope.PLAYER))).thenReturn(Collections.singletonList(normalCapability));
        when(capabilityRepository.findByObjectAndScope(eq(CapabilityObject.ENTITY), eq(CapabilityScope.IMPLEMENTOR))).thenReturn(Collections.singletonList(adminCapability));

        mainResource = new MainResource(
                applicationContext,
                socialNetworks,
                securityContextLogoutHandler,
                accountRepository,
                entityRepository,
                commandMetadataRepository,
                emoteMetadataRepository,
                capabilityRepository,
                roomBuilder,
                worldManager,
                entityUtil,
                emote
        );
    }

    @Test
    public void testIndexNotAuthenticated() throws Exception {
        String view = mainResource.index(httpSession, null, model);

        verify(model).addAttribute(eq("networks"), eq(socialNetworks));
        verifyZeroInteractions(accountRepository);
        assertEquals("index", view);
    }

    @Test
    public void testIndexNewAccount() throws Exception {
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq(NETWORK_ID), eq(NETWORK_USER))).thenReturn(null);

        String view = mainResource.index(httpSession, principal, model);

        verify(model).addAttribute(eq("networks"), eq(socialNetworks));
        verify(accountRepository).save(accountCaptor.capture());
        verify(model, never()).addAttribute(eq("account"), any(Account.class));
        verify(model, never()).addAttribute(eq("entities"), eq(entities));
        assertEquals("new-entity", view);

        Account generated = accountCaptor.getValue();

        assertEquals(NETWORK_ID, generated.getSocialNetwork());
        assertEquals(NETWORK_USER, generated.getSocialNetworkId());
        assertTrue(generated.getCapabilities().contains(playCapability));
        assertFalse(generated.getCapabilities().contains(adminAccountCapability));
    }

    @Test
    public void testIndexNewAccountAdmin() throws Exception {
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        when(accountRepository.count()).thenReturn(0L);
        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq(NETWORK_ID), eq(NETWORK_USER))).thenReturn(null);

        String view = mainResource.index(httpSession, principal, model);

        verify(model).addAttribute(eq("networks"), eq(socialNetworks));
        verify(accountRepository).save(accountCaptor.capture());
        verify(model, never()).addAttribute(eq("account"), any(Account.class));
        verify(model, never()).addAttribute(eq("entities"), eq(entities));
        assertEquals("new-entity", view);

        Account generated = accountCaptor.getValue();

        assertEquals(NETWORK_ID, generated.getSocialNetwork());
        assertEquals(NETWORK_USER, generated.getSocialNetworkId());
        assertTrue(generated.getCapabilities().contains(playCapability));
        assertTrue(generated.getCapabilities().contains(adminAccountCapability));
    }

    @Test
    public void testIndexExistingAccount() throws Exception {
        String view = mainResource.index(httpSession, principal, model);

        verify(model).addAttribute(eq("networks"), eq(socialNetworks));
        verify(accountRepository, never()).save(any(Account.class));
        verify(account, never()).setSocialNetwork(anyString());
        verify(account, never()).setSocialNetworkId(anyString());
        verify(model).addAttribute(eq("account"), eq(account));
        verify(model).addAttribute(eq("entities"), eq(entities));
        assertEquals("entity", view);
    }

    @Test
    public void testSocial() throws Exception {
        String view = mainResource.social(NETWORK_ID, httpSession);

        verify(httpSession).setAttribute(eq("social"), eq(NETWORK_ID));
        assertEquals("redirect:/login/" + NETWORK_ID, view);
    }

    @Test
    public void testNewEntity() throws Exception {
        String view = mainResource.newEntity();

        assertEquals("new-entity", view);
    }

    @Test
    public void testSaveFirstNewEntity() throws Exception {
        when(entityRepository.count()).thenReturn(0L);

        String view = mainResource.saveNewEntity(httpSession, principal, entity, model);

        verify(entity).setAccount(eq(account));
        verify(entity).setCreationDate(anyLong());
        verify(entity, times(2)).addCapabilities(anyListOf(Capability.class));

        assertEquals("redirect:/", view);
    }

    @Test
    public void testSaveNewEntity() throws Exception {
        when(entityRepository.count()).thenReturn(100L);

        String view = mainResource.saveNewEntity(httpSession, principal, entity, model);

        verify(entity).setAccount(eq(account));
        verify(entity).setCreationDate(anyLong());
        verify(entity).addCapabilities(anyListOf(Capability.class));

        assertEquals("redirect:/", view);
    }

    @Test
    public void testSaveNewEntityNotAllowed() throws Exception {
        when(entityRepository.count()).thenReturn(100L);
        when(capabilityRepository.findByName(eq(CommandRole.CHAR_NEW.name()))).thenReturn(null);

        String view = mainResource.saveNewEntity(httpSession, principal, entity, model);

        verify(entity, never()).setAccount(eq(account));
        verify(model).addAttribute(eq("entityName"), eq("EntityA"));
        verify(model).addAttribute(eq("errorName"), anyString());
        assertEquals("new-entity", view);
    }

    @Test
    public void testSaveNewEntityNameTooShort() throws Exception {
        when(entityRepository.count()).thenReturn(100L);
        when(entity.getName()).thenReturn("A");

        String view = mainResource.saveNewEntity(httpSession, principal, entity, model);

        verify(entity, never()).setAccount(any(Account.class));
        verify(model).addAttribute(eq("entityName"), eq("A"));
        verify(model).addAttribute(eq("errorName"), anyString());
        assertEquals("new-entity", view);
    }

    @Test
    public void testSaveNewEntityNameTooLong() throws Exception {
        when(entityRepository.count()).thenReturn(100L);
        when(entity.getName()).thenReturn("Supercalifragilisticexpealadocious");

        String view = mainResource.saveNewEntity(httpSession, principal, entity, model);

        verify(entity, never()).setAccount(any(Account.class));
        verify(model).addAttribute(eq("entityName"), eq("Supercalifragilisticexpealadocious"));
        verify(model).addAttribute(eq("errorName"), anyString());
        assertEquals("new-entity", view);
    }

    @Test
    public void testSaveNewEntityNameNotCapitalized() throws Exception {
        when(entityRepository.count()).thenReturn(100L);
        when(entity.getName()).thenReturn("abraham");

        String view = mainResource.saveNewEntity(httpSession, principal, entity, model);

        verify(entity, never()).setAccount(any(Account.class));
        verify(model).addAttribute(eq("entityName"), eq("abraham"));
        verify(model).addAttribute(eq("errorName"), anyString());
        assertEquals("new-entity", view);
    }

    @Test
    public void testSaveNewEntityNameInvalidCharacters() throws Exception {
        when(entityRepository.count()).thenReturn(100L);
        when(entity.getName()).thenReturn("Abra!ham");

        String view = mainResource.saveNewEntity(httpSession, principal, entity, model);

        verify(entity, never()).setAccount(any(Account.class));
        verify(model).addAttribute(eq("entityName"), eq("Abra!ham"));
        verify(model).addAttribute(eq("errorName"), anyString());
        assertEquals("new-entity", view);
    }

    @Test
    public void testSaveNewEntityNameStartsWithHyphen() throws Exception {
        when(entityRepository.count()).thenReturn(100L);
        when(entity.getName()).thenReturn("-Abraham");

        String view = mainResource.saveNewEntity(httpSession, principal, entity, model);

        verify(entity, never()).setAccount(any(Account.class));
        verify(model).addAttribute(eq("entityName"), eq("-Abraham"));
        verify(model).addAttribute(eq("errorName"), anyString());
        assertEquals("new-entity", view);
    }

    @Test
    public void testSaveNewEntityNameStartsWithApostrophe() throws Exception {
        when(entityRepository.count()).thenReturn(100L);
        when(entity.getName()).thenReturn("'Abraham");

        String view = mainResource.saveNewEntity(httpSession, principal, entity, model);

        verify(entity, never()).setAccount(any(Account.class));
        verify(model).addAttribute(eq("entityName"), eq("'Abraham"));
        verify(model).addAttribute(eq("errorName"), anyString());
        assertEquals("new-entity", view);
    }

    @Test
    public void testSaveNewEntityNameEndsWithHyphen() throws Exception {
        when(entityRepository.count()).thenReturn(100L);
        when(entity.getName()).thenReturn("Abraham-");

        String view = mainResource.saveNewEntity(httpSession, principal, entity, model);

        verify(entity, never()).setAccount(any(Account.class));
        verify(model).addAttribute(eq("entityName"), eq("Abraham-"));
        verify(model).addAttribute(eq("errorName"), anyString());
        assertEquals("new-entity", view);
    }

    @Test
    public void testSaveNewEntityNameEndsWithApostrophe() throws Exception {
        when(entityRepository.count()).thenReturn(100L);
        when(entity.getName()).thenReturn("Abraham'");

        String view = mainResource.saveNewEntity(httpSession, principal, entity, model);

        verify(entity, never()).setAccount(any(Account.class));
        verify(model).addAttribute(eq("entityName"), eq("Abraham'"));
        verify(model).addAttribute(eq("errorName"), anyString());
        assertEquals("new-entity", view);
    }

    @Test
    public void testSaveNewEntityNameMultipleSymbols1() throws Exception {
        when(entityRepository.count()).thenReturn(100L);
        when(entity.getName()).thenReturn("Abra--ham");

        String view = mainResource.saveNewEntity(httpSession, principal, entity, model);

        verify(entity, never()).setAccount(any(Account.class));
        verify(model).addAttribute(eq("entityName"), eq("Abra--ham"));
        verify(model).addAttribute(eq("errorName"), anyString());
        assertEquals("new-entity", view);
    }

    @Test
    public void testSaveNewEntityNameMultipleSymbols2() throws Exception {
        when(entityRepository.count()).thenReturn(100L);
        when(entity.getName()).thenReturn("Ab-ra-ham");

        String view = mainResource.saveNewEntity(httpSession, principal, entity, model);

        verify(entity, never()).setAccount(any(Account.class));
        verify(model).addAttribute(eq("entityName"), eq("Ab-ra-ham"));
        verify(model).addAttribute(eq("errorName"), anyString());
        assertEquals("new-entity", view);
    }

    @Test(expected = NoAccountException.class)
    public void testSaveNewEssenceMissingAccount() throws Exception {
        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq(NETWORK_ID), eq(NETWORK_USER))).thenReturn(null);

        mainResource.saveNewEntity(httpSession, principal, entity, model);
    }

    @Test
    public void testPlayGet() throws Exception {
        when(httpSession.getAttribute(eq("entityId"))).thenReturn("entityId");

        String view = mainResource.getPlay(httpSession, model);

        verify(model).addAttribute(eq("entityId"), eq("entityId"));
        verify(httpSession).removeAttribute(eq("entityId"));

        assertEquals("play-post", view);
    }

    @Test
    public void testPlay() throws Exception {
        String view = mainResource.play(playRequest, httpSession, httpServletRequest, principal, model);

        verify(roomBuilder, never()).generateRoom(eq(0L), eq(0L), eq(0L));
        verify(entityUtil).sendMessageToRoom(any(Room.class), any(Entity.class), outputCaptor.capture());
        verify(worldManager).put(eq(entity), eq(0L), eq(0L), eq(0L));
        verify(httpSession).setAttribute(anyString(), mapCaptor.capture());
        verify(model).addAttribute(eq("breadcrumb"), anyString());
        verify(model).addAttribute(eq("account"), eq(account));
        verify(model).addAttribute(eq("entity"), eq(entity));
        verify(entity).setLastLoginDate(anyLong());
        verify(entity).setRemoteAddr(anyString());
        verify(entity).setUserAgent(anyString());
        assertEquals("play", view);

        GameOutput output = outputCaptor.getValue();

        assertTrue(output.getOutput().get(0).startsWith("[yellow]"));

        Map<String, String> sessionMap = mapCaptor.getValue();

        assertEquals(account.getId(), sessionMap.get("account"));
        assertEquals(entity.getId(), sessionMap.get("entity"));
    }

    @Test
    public void testPlayNoWorld() throws Exception {
        when(worldManager.test(eq(0L), eq(0L), eq(0L))).thenReturn(false);

        String view = mainResource.play(playRequest, httpSession, httpServletRequest, principal, model);

        verify(roomBuilder).generateRoom(eq(0L), eq(0L), eq(0L));
        verify(entityUtil).sendMessageToRoom(any(Room.class), any(Entity.class), outputCaptor.capture());
        verify(worldManager).put(eq(entity), eq(0L), eq(0L), eq(0L));
        verify(httpSession).setAttribute(anyString(), mapCaptor.capture());
        verify(model).addAttribute(eq("breadcrumb"), anyString());
        verify(model).addAttribute(eq("account"), eq(account));
        verify(model).addAttribute(eq("entity"), eq(entity));
        assertEquals("play", view);

        Map<String, String> sessionMap = mapCaptor.getValue();

        assertEquals(account.getId(), sessionMap.get("account"));
        assertEquals(entity.getId(), sessionMap.get("entity"));
    }

    @Test
    public void testPlayNoId() throws Exception {
        when(playRequest.getEntityId()).thenReturn(null);

        String view = mainResource.play(playRequest, httpSession, httpServletRequest, principal, model);

        verifyZeroInteractions(model);
        verifyZeroInteractions(httpSession);
        assertEquals("redirect:/", view);
    }

    @Test
    public void testPlayNoAccount() throws Exception {
        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq(NETWORK_ID), eq(NETWORK_USER))).thenReturn(null);

        String view = mainResource.play(playRequest, httpSession, httpServletRequest, principal, model);

        verify(httpSession).getAttribute(eq("social"));
        verifyNoMoreInteractions(httpSession);
        verifyZeroInteractions(model);
        assertEquals("redirect:/", view);
    }

    @Test
    public void testPlayNotAllowed() throws Exception {
        when(account.isCapable(eq(playCapability))).thenReturn(false);

        String view = mainResource.play(playRequest, httpSession, httpServletRequest, principal, model);

        verify(httpSession).getAttribute(eq("social"));
        verifyNoMoreInteractions(httpSession);
        verifyZeroInteractions(model);
        assertEquals("redirect:/", view);
    }

    @Test
    public void testPlayNoEntity() throws Exception {
        when(entityRepository.findByAccountAndId(any(Account.class), anyString())).thenReturn(null);

        String view = mainResource.play(playRequest, httpSession, httpServletRequest, principal, model);

        verify(httpSession).getAttribute(eq("social"));
        verifyNoMoreInteractions(httpSession);
        verifyZeroInteractions(model);
        assertEquals("redirect:/", view);
    }

    @Test
    public void testPlayReconnect() throws Exception {
        Room room = mock(Room.class);
        Entity entity0 = entity;

        when(entity0.getRoom()).thenReturn(room);
        when(entity0.getStompSessionId()).thenReturn("stompSessionId");
        when(entity0.getStompUsername()).thenReturn("stompUsername");

        String view = mainResource.play(playRequest, httpSession, httpServletRequest, principal, model);

        verify(entityUtil).sendMessageToEntity(any(Entity.class), outputCaptor.capture());
        verify(worldManager).put(any(Entity.class), eq(0L), eq(0L), eq(0L));
        verify(httpSession).setAttribute(anyString(), mapCaptor.capture());
        verify(model).addAttribute(eq("breadcrumb"), anyString());
        verify(model).addAttribute(eq("account"), eq(account));
        verify(model).addAttribute(eq("entity"), eq(entity));
        assertEquals("play", view);

        GameOutput output = outputCaptor.getValue();

        assertTrue(output.getOutput().get(0).startsWith("[red]"));
    }

    @Test
    public void testCommandsNotAuthenticated() throws Exception {
        List<CommandMetadata> metadata = generateCommandMetadata(false);

        when(commandMetadataRepository.findAll()).thenReturn(metadata);

        String view = mainResource.commands(model, null, httpSession);

        verifyZeroInteractions(httpSession);
        verify(commandMetadataRepository).findAll();
        verify(applicationContext, times(5)).getBean(startsWith("command"));
        verify(model).addAttribute(eq("metadataList"), anyListOf(CommandMetadata.class));
        verify(model).addAttribute(eq("commandMap"), anyMapOf(String.class, Command.class));

        assertEquals("commands", view);
    }

    @Test
    public void testCommandsAuthenticatedNoAdmins() throws Exception {
        List<CommandMetadata> metadata = generateCommandMetadata(false);

        when(commandMetadataRepository.findAll()).thenReturn(metadata);
        when(httpSession.getAttribute(eq("social"))).thenReturn("alteraBook");
        when(principal.getName()).thenReturn("2928749020");
        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq("alteraBook"), eq("2928749020"))).thenReturn(account);
        when(account.getId()).thenReturn("accountId");
        when(entityRepository.findByAccount(eq(account))).thenReturn(entities);

        String view = mainResource.commands(model, principal, httpSession);

        verify(httpSession).getAttribute(eq("social"));
        verify(commandMetadataRepository).findAll();
        verify(applicationContext, times(5)).getBean(startsWith("command"));
        verify(model).addAttribute(eq("metadataList"), anyListOf(CommandMetadata.class));
        verify(model).addAttribute(eq("commandMap"), anyMapOf(String.class, Command.class));

        assertEquals("commands", view);
    }

    @Test
    public void testCommandsAuthenticatedWithAdmins() throws Exception {
        List<CommandMetadata> metadata = generateCommandMetadata(true);

        when(commandMetadataRepository.findAll()).thenReturn(metadata);
        when(httpSession.getAttribute(eq("social"))).thenReturn("alteraBook");
        when(principal.getName()).thenReturn("2928749020");
        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq("alteraBook"), eq("2928749020"))).thenReturn(account);
        when(account.getId()).thenReturn("accountId");
        when(entities.get(2).isCapable(eq(adminCapability))).thenReturn(true);
        when(entities.get(2).getCapabilities()).thenReturn(Arrays.asList(normalCapability, adminCapability));
        when(entityRepository.findByAccount(eq(account))).thenReturn(entities);

        String view = mainResource.commands(model, principal, httpSession);

        verify(httpSession).getAttribute(eq("social"));
        verify(commandMetadataRepository).findAll();
        verify(applicationContext, times(5)).getBean(startsWith("command"));
        verify(model).addAttribute(eq("metadataList"), anyListOf(CommandMetadata.class));
        verify(model).addAttribute(eq("commandMap"), anyMapOf(String.class, Command.class));

        assertEquals("commands", view);
    }

    @Test
    public void testEmotesNotAuthenticated() throws Exception {
        when(emoteMetadataRepository.findAll()).thenReturn(emoteMetadata);

        String view = mainResource.emotes(model, null, httpSession);

        verify(emoteMetadataRepository).findAll();
        verifyZeroInteractions(httpSession);
        verify(model).addAttribute(eq("self"), entityCaptor.capture());
        verify(model).addAttribute(eq("target"), entityCaptor.capture());
        verify(model).addAttribute(eq("metadataList"), anyListOf(EmoteMetadata.class));
        verify(model).addAttribute(eq("emoteMap"), anyMapOf(String.class, EmoteMetadata.class));
        verifyAllEmoteMetadata(emoteMetadata);

        List<Entity> captures = entityCaptor.getAllValues();

        assertEquals("emotes", view);
        assertEquals("Alice", captures.get(0).getName());
        assertEquals("Bob", captures.get(1).getName());
    }

    @Test
    public void testEmotesAuthenticatedNoEssences() throws Exception {
        when(emoteMetadataRepository.findAll()).thenReturn(emoteMetadata);
        when(httpSession.getAttribute(eq("social"))).thenReturn("social");
        when(principal.getName()).thenReturn("principal");
        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq("social"), eq("principal"))).thenReturn(account);
        when(account.getId()).thenReturn("accountId");
        when(entityRepository.findByAccount(eq(account))).thenReturn(new ArrayList<>());

        String view = mainResource.emotes(model, principal, httpSession);

        verify(emoteMetadataRepository).findAll();
        verify(httpSession).getAttribute(eq("social"));
        verify(model).addAttribute(eq("self"), entityCaptor.capture());
        verify(model).addAttribute(eq("target"), entityCaptor.capture());
        verify(model).addAttribute(eq("metadataList"), anyListOf(EmoteMetadata.class));
        verify(model).addAttribute(eq("emoteMap"), anyMapOf(String.class, EmoteMetadata.class));
        verifyAllEmoteMetadata(emoteMetadata);

        List<Entity> captures = entityCaptor.getAllValues();

        assertEquals("emotes", view);
        assertEquals("Alice", captures.get(0).getName());
        assertEquals("Bob", captures.get(1).getName());
    }

    @Test
    public void testEmotesAuthenticatedOneEssence() throws Exception {
        ArrayList<Entity> oneEntity = new ArrayList<>();

        oneEntity.add(entities.get(0));

        when(emoteMetadataRepository.findAll()).thenReturn(emoteMetadata);
        when(httpSession.getAttribute(eq("social"))).thenReturn("social");
        when(principal.getName()).thenReturn("principal");
        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq("social"), eq("principal"))).thenReturn(account);
        when(account.getId()).thenReturn("accountId");
        when(entityRepository.findByAccount(eq(account))).thenReturn(oneEntity);

        String view = mainResource.emotes(model, principal, httpSession);

        verify(emoteMetadataRepository).findAll();
        verify(httpSession).getAttribute(eq("social"));
        verify(model).addAttribute(eq("self"), entityCaptor.capture());
        verify(model).addAttribute(eq("target"), entityCaptor.capture());
        verify(model).addAttribute(eq("metadataList"), anyListOf(EmoteMetadata.class));
        verify(model).addAttribute(eq("emoteMap"), anyMapOf(String.class, EmoteMetadata.class));
        verifyAllEmoteMetadata(emoteMetadata);

        List<Entity> captures = entityCaptor.getAllValues();

        assertEquals("emotes", view);
        assertEquals("EntityA", captures.get(0).getName());
        assertEquals("Bob", captures.get(1).getName());
    }

    @Test
    public void testEmotesAuthenticatedTwoEssences() throws Exception {
        ArrayList<Entity> twoEntities = new ArrayList<>();

        twoEntities.add(entities.get(0));
        twoEntities.add(entities.get(1));

        when(emoteMetadataRepository.findAll()).thenReturn(emoteMetadata);
        when(httpSession.getAttribute(eq("social"))).thenReturn("social");
        when(principal.getName()).thenReturn("principal");
        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq("social"), eq("principal"))).thenReturn(account);
        when(account.getId()).thenReturn("accountId");
        when(entityRepository.findByAccount(eq(account))).thenReturn(twoEntities);

        String view = mainResource.emotes(model, principal, httpSession);

        verify(emoteMetadataRepository).findAll();
        verify(httpSession).getAttribute(eq("social"));
        verify(model).addAttribute(eq("self"), entityCaptor.capture());
        verify(model).addAttribute(eq("target"), entityCaptor.capture());
        verify(model).addAttribute(eq("metadataList"), anyListOf(EmoteMetadata.class));
        verify(model).addAttribute(eq("emoteMap"), anyMapOf(String.class, EmoteMetadata.class));
        verifyAllEmoteMetadata(emoteMetadata);

        List<Entity> captures = entityCaptor.getAllValues();

        assertEquals("emotes", view);
        assertEquals("EntityA", captures.get(0).getName());
        assertEquals("EntityB", captures.get(1).getName());
    }

    @Test
    public void testLogout() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String view = mainResource.logout(request, response, httpSession, principal);

        verify(securityContextLogoutHandler).logout(eq(request), eq(response), any(Authentication.class));
        assertEquals("redirect:/", view);
    }

    private void generateSocialNetworks() {
        socialNetworks.add(new SocialNetwork(NETWORK_ID, NETWORK_NAME));
    }

    private Account generateAccount() {
        Account account = mock(Account.class);

        when(account.getId()).thenReturn(ACCOUNT_ID);
        when(account.getSocialNetwork()).thenReturn(NETWORK_ID);
        when(account.getSocialNetworkId()).thenReturn(NETWORK_USER);
        when(account.isCapable(eq(playCapability))).thenReturn(true);
        when(account.isCapable(eq(newCharCapability))).thenReturn(true);

        return account;
    }

    private void generateEntities() {
        for (int i = 0; i < 3; i++) {
            Entity entity = mock(Entity.class);

            when(entity.getId()).thenReturn("entity" + i);
            when(entity.getName()).thenReturn("Entity" + ALPHABET.charAt(i));
            when(entity.getAccount()).thenReturn(account);
            when(entity.isCapable(eq(normalCapability))).thenReturn(true);
            when(entity.getCapabilities()).thenReturn(Collections.singletonList(normalCapability));

            entities.add(entity);
        }
    }

    private List<CommandMetadata> generateCommandMetadata(boolean admin) {
        List<CommandMetadata> commandMetadata = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            CommandMetadata metadata = mock(CommandMetadata.class);
            Command command = mock(Command.class);

            when(metadata.getBeanName()).thenReturn("command" + ALPHABET.charAt(i));
            when(metadata.getName()).thenReturn("command" + ALPHABET.charAt(i));

            if (admin && (i % 2 == 0)) {
                when(metadata.getCapability()).thenReturn(adminCapability);
            } else {
                when(metadata.getCapability()).thenReturn(normalCapability);
            }

            when(applicationContext.getBean(eq(metadata.getBeanName()))).thenReturn(command);

            commandMetadata.add(metadata);
        }

        return commandMetadata;
    }

    private void generateEmoteMetadata() {
        for (int i = 0; i < 5; i++) {
            EmoteMetadata metadata = mock(EmoteMetadata.class);

            emoteMetadata.add(metadata);
        }
    }

    private void verifyAllEmoteMetadata(List<EmoteMetadata> metadata) {
        metadata.forEach(m -> {
            verify(m).setToSelfUntargeted(anyString());
            verify(m).setToRoomUntargeted(anyString());
            verify(m).setToSelfWithTarget(anyString());
            verify(m).setToTarget(anyString());
            verify(m).setToRoomWithTarget(anyString());
            verify(m).setToSelfAsTarget(anyString());
            verify(m).setToRoomTargetingSelf(anyString());
        });
    }
}
