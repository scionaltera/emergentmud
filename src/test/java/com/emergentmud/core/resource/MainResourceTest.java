/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016 Peter Keeler
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

import com.emergentmud.core.exception.NoAccountException;
import com.emergentmud.core.model.Account;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Essence;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.SocialNetwork;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.AccountRepository;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.repository.EssenceRepository;
import com.emergentmud.core.repository.WorldManager;
import com.emergentmud.core.util.EntityUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class MainResourceTest {
    private static final String NETWORK_NAME = "AlteraNet";
    private static final String NETWORK_ID = "alteranet";
    private static final String NETWORK_USER = "007";
    private static final String ACCOUNT_ID = "1234567890";

    @Mock
    private SecurityContextLogoutHandler securityContextLogoutHandler;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private EssenceRepository essenceRepository;

    @Mock
    private EntityRepository entityRepository;

    @Mock
    private WorldManager worldManager;

    @Mock
    private EntityUtil entityUtil;

    @Mock
    private HttpSession httpSession;

    @Mock
    private OAuth2Authentication principal;

    @Mock
    private Model model;

    @Captor
    private ArgumentCaptor<Map<String, String>> mapCaptor;

    @Captor
    private ArgumentCaptor<GameOutput> outputCaptor;

    private Account account;
    private Essence essence;
    private List<SocialNetwork> socialNetworks = new ArrayList<>();
    private List<Essence> essences = new ArrayList<>();

    private MainResource mainResource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        generateSocialNetworks();
        account = generateAccount();
        generateEssences();
        essence = essences.get(0);

        when(worldManager.test(eq(0L), eq(0L), eq(0L))).thenReturn(true);
        when(httpSession.getAttribute(eq("social"))).thenReturn(NETWORK_ID);
        when(principal.getName()).thenReturn(NETWORK_USER);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account account = (Account)invocation.getArguments()[0];
            account.setId(UUID.randomUUID().toString());
            return account;
        });
        when(essenceRepository.save(any(Essence.class))).thenAnswer(invocation -> {
            Essence essence = (Essence)invocation.getArguments()[0];
            essence.setId(UUID.randomUUID().toString());
            return essence;
        });
        when(entityRepository.save(any(Entity.class))).thenAnswer(invocation -> {
            Entity entity = (Entity)invocation.getArguments()[0];
            entity.setId(UUID.randomUUID().toString());
            return entity;
        });
        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq(NETWORK_ID), eq(NETWORK_USER))).thenReturn(account);
        when(essenceRepository.findByAccountId(anyString())).thenReturn(essences);

        mainResource = new MainResource(
                socialNetworks,
                securityContextLogoutHandler,
                accountRepository,
                essenceRepository,
                entityRepository,
                worldManager,
                entityUtil
        );
    }

    @Test
    public void testIndexNotAuthenticated() throws Exception {
        String view = mainResource.index(httpSession, null, model);

        verify(model).addAttribute(eq("networks"), eq(socialNetworks));
        verifyZeroInteractions(accountRepository);
        verifyZeroInteractions(essenceRepository);
        assertEquals("index", view);
    }

    @Test
    public void testIndexNewAccount() throws Exception {
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq(NETWORK_ID), eq(NETWORK_USER))).thenReturn(null);

        String view = mainResource.index(httpSession, principal, model);

        verify(model).addAttribute(eq("networks"), eq(socialNetworks));
        verify(accountRepository).save(accountCaptor.capture());
        verify(model).addAttribute(eq("account"), any(Account.class));
        verify(model).addAttribute(eq("essences"), eq(essences));
        assertEquals("essence", view);

        Account generated = accountCaptor.getValue();

        assertEquals(NETWORK_ID, generated.getSocialNetwork());
        assertEquals(NETWORK_USER, generated.getSocialNetworkId());
    }

    @Test
    public void testIndexExistingAccount() throws Exception {
        String view = mainResource.index(httpSession, principal, model);

        verify(model).addAttribute(eq("networks"), eq(socialNetworks));
        verify(accountRepository, never()).save(any(Account.class));
        verify(account, never()).setSocialNetwork(anyString());
        verify(account, never()).setSocialNetworkId(anyString());
        verify(model).addAttribute(eq("account"), eq(account));
        verify(model).addAttribute(eq("essences"), eq(essences));
        assertEquals("essence", view);
    }

    @Test
    public void testSocial() throws Exception {
        String view = mainResource.social(NETWORK_ID, httpSession);

        verify(httpSession).setAttribute(eq("social"), eq(NETWORK_ID));
        assertEquals("redirect:/login/" + NETWORK_ID, view);
    }

    @Test
    public void testNewEssence() throws Exception {
        String view = mainResource.newEssence();

        assertEquals("new-essence", view);
    }

    @Test
    public void testSaveFirstNewEssence() throws Exception {
        when(essenceRepository.count()).thenReturn(0L);

        String view = mainResource.saveNewEssence(httpSession, principal, essence);

        verify(essence).setAccountId(eq(ACCOUNT_ID));
        verify(essence).setAdmin(eq(true));
        assertEquals("redirect:/", view);
    }

    @Test
    public void testSaveNewEssence() throws Exception {
        when(essenceRepository.count()).thenReturn(100L);

        String view = mainResource.saveNewEssence(httpSession, principal, essence);

        verify(essence).setAccountId(eq(ACCOUNT_ID));
        verify(essence, never()).setAdmin(anyBoolean());
        assertEquals("redirect:/", view);
    }

    @Test(expected = NoAccountException.class)
    public void testSaveNewEssenceMissingAccount() throws Exception {
        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq(NETWORK_ID), eq(NETWORK_USER))).thenReturn(null);

        mainResource.saveNewEssence(httpSession, principal, essence);
    }

    @Test
    public void testPlayExisting() throws Exception {
        String view = mainResource.play("essence0", httpSession, principal, model);
        Entity entity = essence.getEntity();

        verify(entityUtil).sendMessageToRoom(any(Room.class), any(Entity.class), outputCaptor.capture());
        verify(worldManager).put(eq(entity), eq(0L), eq(0L), eq(0L));
        verify(httpSession).setAttribute(anyString(), mapCaptor.capture());
        verify(model).addAttribute(eq("breadcrumb"), anyString());
        verify(model).addAttribute(eq("account"), eq(account));
        verify(model).addAttribute(eq("essence"), eq(essence));
        assertEquals("play", view);

        GameOutput output = outputCaptor.getValue();

        assertTrue(output.getOutput().get(0).startsWith("[yellow]"));

        Map<String, String> sessionMap = mapCaptor.getValue();

        assertEquals(account.getId(), sessionMap.get("account"));
        assertEquals(essence.getId(), sessionMap.get("essence"));
        assertEquals(entity.getId(), sessionMap.get("entity"));
    }

    @Test
    public void testPlayNoWorld() throws Exception {
        when(worldManager.test(eq(0L), eq(0L), eq(0L))).thenReturn(false);

        String view = mainResource.play("essence0", httpSession, principal, model);
        Entity entity = essence.getEntity();

        verify(entityUtil, never()).sendMessageToRoom(any(Room.class), any(Entity.class), outputCaptor.capture());
        verify(worldManager, never()).put(eq(entity), eq(0L), eq(0L), eq(0L));
        verify(httpSession).setAttribute(anyString(), mapCaptor.capture());
        verify(model).addAttribute(eq("breadcrumb"), anyString());
        verify(model).addAttribute(eq("account"), eq(account));
        verify(model).addAttribute(eq("essence"), eq(essence));
        assertEquals("play", view);

        Map<String, String> sessionMap = mapCaptor.getValue();

        assertEquals(account.getId(), sessionMap.get("account"));
        assertEquals(essence.getId(), sessionMap.get("essence"));
        assertEquals(entity.getId(), sessionMap.get("entity"));
    }

    @Test
    public void testPlayNoId() throws Exception {
        String view = mainResource.play("", httpSession, principal, model);

        verifyZeroInteractions(model);
        verifyZeroInteractions(httpSession);
        assertEquals("redirect:/", view);
    }

    @Test
    public void testPlayNoAccount() throws Exception {
        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq(NETWORK_ID), eq(NETWORK_USER))).thenReturn(null);

        String view = mainResource.play("essence0", httpSession, principal, model);

        verify(httpSession).getAttribute(eq("social"));
        verifyNoMoreInteractions(httpSession);
        verifyZeroInteractions(model);
        assertEquals("redirect:/", view);
    }

    @Test
    public void testPlayNoEssence() throws Exception {
        when(essenceRepository.findByAccountId(anyString())).thenReturn(new ArrayList<>());

        String view = mainResource.play("essence0", httpSession, principal, model);

        verify(httpSession).getAttribute(eq("social"));
        verifyNoMoreInteractions(httpSession);
        verifyZeroInteractions(model);
        assertEquals("redirect:/", view);
    }

    @Test
    public void testPlayNoEntity() throws Exception {
        Essence essence1 = essences.get(1);

        when(essence1.getEntity()).thenReturn(null);

        String view = mainResource.play("essence1", httpSession, principal, model);

        verify(entityUtil).sendMessageToRoom(any(Room.class), any(Entity.class), outputCaptor.capture());
        verify(entityRepository).save(any(Entity.class));
        verify(essence1).setEntity(any(Entity.class));
        verify(essenceRepository).save(eq(essence1));
        verify(worldManager).put(any(Entity.class), eq(0L), eq(0L), eq(0L));
        verify(httpSession).setAttribute(anyString(), mapCaptor.capture());
        verify(model).addAttribute(eq("breadcrumb"), anyString());
        verify(model).addAttribute(eq("account"), eq(account));
        verify(model).addAttribute(eq("essence"), eq(essence1));
        assertEquals("play", view);

        GameOutput output = outputCaptor.getValue();

        assertTrue(output.getOutput().get(0).startsWith("[yellow]"));

        Map<String, String> sessionMap = mapCaptor.getValue();

        assertEquals(account.getId(), sessionMap.get("account"));
        assertEquals(essence1.getId(), sessionMap.get("essence"));
        assertTrue(sessionMap.containsKey("entity"));
    }

    @Test
    public void testPlayReconnect() throws Exception {
        Essence essence0 = essences.get(0);
        Entity entity0 = essence0.getEntity();

        when(entity0.getStompSessionId()).thenReturn("stompSessionId");
        when(entity0.getStompUsername()).thenReturn("stompUsername");

        String view = mainResource.play("essence0", httpSession, principal, model);

        verify(entityUtil).sendMessageToEntity(any(Entity.class), outputCaptor.capture());
        verify(worldManager).put(any(Entity.class), eq(0L), eq(0L), eq(0L));
        verify(httpSession).setAttribute(anyString(), mapCaptor.capture());
        verify(model).addAttribute(eq("breadcrumb"), anyString());
        verify(model).addAttribute(eq("account"), eq(account));
        verify(model).addAttribute(eq("essence"), eq(essence));
        assertEquals("play", view);

        GameOutput output = outputCaptor.getValue();

        assertTrue(output.getOutput().get(0).startsWith("[red]"));
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

        return account;
    }

    private void generateEssences() {
        for (int i = 0; i < 3; i++) {
            Essence essence = mock(Essence.class);
            Entity entity = mock(Entity.class);

            when(essence.getId()).thenReturn("essence" + i);
            when(essence.getName()).thenReturn("Essence" + i);
            when(essence.getAccountId()).thenReturn(ACCOUNT_ID);
            when(essence.getEntity()).thenReturn(entity);

            when(entity.getId()).thenReturn("entity" + i);
            when(entity.getName()).thenReturn("Entity" + i);

            essences.add(essence);
        }
    }
}
