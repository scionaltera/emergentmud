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

import com.emergentmud.core.exception.NoAccountException;
import com.emergentmud.core.model.Account;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Essence;
import com.emergentmud.core.model.SocialNetwork;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.model.stomp.UserInput;
import com.emergentmud.core.repository.AccountRepository;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.repository.EssenceRepository;
import com.emergentmud.core.repository.WorldManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class MainResourceTest {
    @Mock
    private SecurityContextLogoutHandler securityContextLogoutHandler;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private EssenceRepository essenceRepository;

    @Mock
    private EntityRepository entityRepository;

    @Mock
    private WorldManager worldManager;

    @Mock
    private OAuth2Authentication oAuth2Authentication;

    @Mock
    private OAuth2AuthenticationDetails oAuth2AuthenticationDetails;

    @Mock
    private Session session;

    private String applicationVersion = "9.8.7-SNAPSHOT";
    private long applicationBootDate = System.currentTimeMillis();
    private List<SocialNetwork> networks = new ArrayList<>();
    private String sessionId = "sessionid";

    private MainResource mainResource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(oAuth2Authentication.getDetails()).thenReturn(oAuth2AuthenticationDetails);
        when(oAuth2AuthenticationDetails.getSessionId()).thenReturn(sessionId);
        when(sessionRepository.getSession(eq(sessionId))).thenReturn(session);

        networks.add(new SocialNetwork("alteranet", "AlteraNet"));
        networks.add(new SocialNetwork("testnet", "TestNet"));

        mainResource = new MainResource(
                applicationVersion,
                applicationBootDate,
                networks,
                securityContextLogoutHandler,
                sessionRepository,
                accountRepository,
                essenceRepository,
                entityRepository,
                worldManager
        );
    }

    @Test
    public void testOnSubscribe() throws Exception {
        String essenceId = "essenceid";
        Essence essence = mock(Essence.class);
        Entity entity = mock(Entity.class);

        when(session.getAttribute(eq("essence"))).thenReturn(essenceId);
        when(essenceRepository.findOne(eq(essenceId))).thenReturn(essence);
        when(essence.getEntity()).thenReturn(entity);

        GameOutput greeting = mainResource.onSubscribe(oAuth2Authentication);

        List<String> lines = greeting.getOutput();

        assertEquals(16, lines.size());
    }

    @Test
    public void testOnInputInfo() throws Exception {
        String text = "info";
        String essenceId = "essenceid";
        UserInput input = mock(UserInput.class);
        Essence essence = mock(Essence.class);
        Entity entity = mock(Entity.class);

        when(session.getAttribute(eq("essence"))).thenReturn(essenceId);
        when(essenceRepository.findOne(eq(essenceId))).thenReturn(essence);
        when(essence.getEntity()).thenReturn(entity);
        when(input.getInput()).thenReturn(text);

        GameOutput output = mainResource.onInput(input, oAuth2Authentication);

        List<String> lines = output.getOutput();

        assertEquals(9, lines.size());
    }

    @Test
    public void testOnInputInfoNoEntity() throws Exception {
        String text = "info";
        String essenceId = "essenceid";
        UserInput input = mock(UserInput.class);
        Essence essence = mock(Essence.class);

        when(session.getAttribute(eq("essence"))).thenReturn(essenceId);
        when(essenceRepository.findOne(eq(essenceId))).thenReturn(essence);
        when(input.getInput()).thenReturn(text);

        GameOutput output = mainResource.onInput(input, oAuth2Authentication);

        List<String> lines = output.getOutput();

        assertEquals(5, lines.size());
    }

    @Test
    public void testOnInputSay() throws Exception {
        String text = "I'm a banana!";
        String essenceId = "essenceid";
        UserInput input = mock(UserInput.class);
        Essence essence = mock(Essence.class);
        Entity entity = mock(Entity.class);

        when(session.getAttribute(eq("essence"))).thenReturn(essenceId);
        when(essenceRepository.findOne(eq(essenceId))).thenReturn(essence);
        when(essence.getEntity()).thenReturn(entity);
        when(input.getInput()).thenReturn(text);

        GameOutput output = mainResource.onInput(input, oAuth2Authentication);

        List<String> lines = output.getOutput();

        assertEquals("[cyan]You say 'I&#39;m a banana![cyan]'", lines.get(0));
        assertEquals("", lines.get(1));
        assertEquals("> ", lines.get(2));
        assertEquals(3, lines.size());
    }

    @Test
    public void testIndexNotLoggedIn() throws Exception {
        HttpSession session = mock(HttpSession.class);
        Model model = mock(Model.class);

        String view = mainResource.index(session, null, model);

        assertEquals("index", view);
        verify(model).addAttribute(eq("networks"), eq(networks));
        verify(model, never()).addAttribute(eq("account"), any(Account.class));
        verifyZeroInteractions(session);
        verifyZeroInteractions(accountRepository);
    }

    @Test
    public void testIndexExistingAccount() throws Exception {
        String network = "alteranet";
        String networkId = "123456789";
        String accountId = "accountId";
        Account account = mock(Account.class);
        List<Essence> essences = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Essence essence = mock(Essence.class);

            essences.add(essence);
        }

        HttpSession session = mock(HttpSession.class);
        Principal principal = mock(Principal.class);
        Model model = mock(Model.class);

        when(session.getAttribute(eq("social"))).thenReturn(network);
        when(principal.getName()).thenReturn(networkId);
        when(account.getId()).thenReturn(accountId);
        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq(network), eq(networkId))).thenReturn(account);
        when(essenceRepository.findByAccountId(eq(accountId))).thenReturn(essences);

        String view = mainResource.index(session, principal, model);

        assertEquals("essence", view);
        verify(model).addAttribute(eq("networks"), eq(networks));
        verify(model).addAttribute(eq("account"), eq(account));
        verify(model).addAttribute(eq("essences"), eq(essences));
        verify(accountRepository).findBySocialNetworkAndSocialNetworkId(eq(network), eq(networkId));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void testIndexNewAccount() throws Exception {
        String id = "uniqueobjectid";
        String network = "alteranet";
        String networkId = "123456789";
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        List<Essence> essences = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Essence essence = mock(Essence.class);

            essences.add(essence);
        }

        HttpSession session = mock(HttpSession.class);
        Principal principal = mock(Principal.class);
        Model model = mock(Model.class);

        when(session.getAttribute(eq("social"))).thenReturn(network);
        when(principal.getName()).thenReturn(networkId);
        when(accountRepository.save(any(Account.class))).thenAnswer((Answer<Account>) invocation -> {
            Object[] args = invocation.getArguments();
            Account account = (Account)args[0];

            account.setId(id);

            return account;
        });
        when(essenceRepository.findByAccountId(anyString())).thenReturn(essences);

        String view = mainResource.index(session, principal, model);

        assertEquals("essence", view);
        verify(model).addAttribute(eq("networks"), eq(networks));
        verify(model).addAttribute(eq("account"), accountCaptor.capture());
        verify(model).addAttribute(eq("essences"), eq(essences));
        verify(accountRepository).findBySocialNetworkAndSocialNetworkId(eq(network), eq(networkId));
        verify(accountRepository).save(any(Account.class));

        Account account = accountCaptor.getValue();

        assertEquals(id, account.getId());
        assertEquals(network, account.getSocialNetwork());
        assertEquals(networkId, account.getSocialNetworkId());
    }

    @Test
    public void testSocial() throws Exception {
        String network = "alteranet";
        HttpSession session = mock(HttpSession.class);

        String view = mainResource.social(network, session);

        assertEquals("redirect:/login/alteranet", view);
        verify(session).setAttribute(eq("social"), eq(network));
    }

    @Test
    public void testNewEssence() throws Exception {
        String view = mainResource.newEssence();

        assertEquals("new-essence", view);
    }

    @Test
    public void testSaveEssenceExistingAccount() throws Exception {
        String accountId = "accountId";
        String network = "alteranet";
        String networkId = "123456789";

        HttpSession session = mock(HttpSession.class);
        Principal principal = mock(Principal.class);
        Essence essence = mock(Essence.class);
        Account account = mock(Account.class);

        when(essence.getName()).thenReturn("Testy");
        when(essence.getId()).thenReturn("essenceId");
        when(account.getId()).thenReturn(accountId);
        when(session.getAttribute(eq("social"))).thenReturn(network);
        when(principal.getName()).thenReturn(networkId);
        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq(network), eq(networkId))).thenReturn(account);
        when(essenceRepository.save(any(Essence.class))).thenReturn(essence);

        String view = mainResource.saveNewEssence(session, principal, essence);

        verify(essence).setAccountId(eq(accountId));
        verify(essenceRepository).save(eq(essence));
        assertEquals("redirect:/", view);
    }

    @Test(expected = NoAccountException.class)
    public void testSaveEssenceWithoutAccount() throws Exception {
        String network = "alteranet";
        String networkId = "123456789";

        HttpSession session = mock(HttpSession.class);
        Principal principal = mock(Principal.class);
        Essence essence = mock(Essence.class);

        when(session.getAttribute(eq("social"))).thenReturn(network);
        when(principal.getName()).thenReturn(networkId);
        when(essence.getName()).thenReturn("Testy");
        when(essence.getId()).thenReturn("essenceId");

        mainResource.saveNewEssence(session, principal, essence);

        verify(essenceRepository, never()).save(any(Essence.class));
    }

    @Test
    public void testPlayNoId() throws Exception {
        String id = "";
        HttpSession session = mock(HttpSession.class);
        Principal principal = mock(Principal.class);
        Model model = mock(Model.class);

        String view = mainResource.play(id, session, principal, model);

        verifyZeroInteractions(accountRepository);
        verifyZeroInteractions(essenceRepository);

        assertEquals("redirect:/", view);
    }

    @Test
    public void testPlayNoAccount() throws Exception {
        String id = "id";
        String network = "alteranet";
        String networkId = "123456789";
        HttpSession session = mock(HttpSession.class);
        Principal principal = mock(Principal.class);
        Model model = mock(Model.class);

        when(session.getAttribute(eq("social"))).thenReturn(network);
        when(principal.getName()).thenReturn(networkId);

        String view = mainResource.play(id, session, principal, model);

        verify(accountRepository).findBySocialNetworkAndSocialNetworkId(eq(network), eq(networkId));
        verifyZeroInteractions(essenceRepository);

        assertEquals("redirect:/", view);
    }

    @Test
    public void testPlayNoEssence() throws Exception {
        String id = "id";
        String network = "alteranet";
        String networkId = "123456789";
        String accountId = "accountId";
        HttpSession session = mock(HttpSession.class);
        Principal principal = mock(Principal.class);
        Model model = mock(Model.class);
        Account account = mock(Account.class);
        List<Essence> essences = new ArrayList<>();

        when(session.getAttribute(eq("social"))).thenReturn(network);
        when(principal.getName()).thenReturn(networkId);
        when(essenceRepository.findByAccountId(eq(accountId))).thenReturn(essences);
        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq(network), eq(networkId))).thenReturn(account);
        when(account.getId()).thenReturn(accountId);

        String view = mainResource.play(id, session, principal, model);

        verify(accountRepository).findBySocialNetworkAndSocialNetworkId(eq(network), eq(networkId));
        verify(essenceRepository).findByAccountId(eq(accountId));

        assertEquals("redirect:/", view);
    }

    @Test
    public void testPlay() throws Exception {
        String network = "alteranet";
        String networkId = "123456789";
        String accountId = "accountid";
        String essenceId = "essenceid";
        String entityId = "entityid";
        HttpSession session = mock(HttpSession.class);
        Principal principal = mock(Principal.class);
        Model model = mock(Model.class);
        Account account = mock(Account.class);
        Essence essence = mock(Essence.class);
        Entity entity = mock(Entity.class);
        List<Essence> essences = new ArrayList<>();

        when(essence.getId()).thenReturn(essenceId);
        when(essence.getEntity()).thenReturn(entity);
        when(entity.getId()).thenReturn(entityId);

        essences.add(essence);

        addMoreEssences(essences);

        when(session.getAttribute(eq("social"))).thenReturn(network);
        when(principal.getName()).thenReturn(networkId);
        when(essenceRepository.findByAccountId(eq(accountId))).thenReturn(essences);
        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq(network), eq(networkId))).thenReturn(account);
        when(account.getId()).thenReturn(accountId);

        String view = mainResource.play(essenceId, session, principal, model);

        verify(accountRepository).findBySocialNetworkAndSocialNetworkId(eq(network), eq(networkId));
        verify(essenceRepository).findByAccountId(eq(accountId));
        verify(worldManager).put(eq(entity), eq(0L), eq(0L), eq(0L));
        verify(session).setAttribute(eq("account"), eq(accountId));
        verify(session).setAttribute(eq("essence"), eq(essenceId));
        verify(session).setAttribute(eq("entity"), eq(entityId));
        verify(model).addAttribute(eq("account"), eq(account));
        verify(model).addAttribute(eq("essence"), eq(essence));

        assertEquals("play", view);
    }

    @Test
    public void testPlayNoEntity() throws Exception {
        String network = "alteranet";
        String networkId = "123456789";
        String accountId = "accountid";
        String essenceId = "essenceid";
        String entityId = "entityid";
        HttpSession session = mock(HttpSession.class);
        Principal principal = mock(Principal.class);
        Model model = mock(Model.class);
        Account account = mock(Account.class);
        Essence essence = mock(Essence.class);
        List<Essence> essences = new ArrayList<>();

        when(essence.getId()).thenReturn(essenceId);
        essences.add(essence);

        addMoreEssences(essences);

        when(session.getAttribute(eq("social"))).thenReturn(network);
        when(principal.getName()).thenReturn(networkId);
        when(essenceRepository.findByAccountId(eq(accountId))).thenReturn(essences);
        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq(network), eq(networkId))).thenReturn(account);
        when(entityRepository.save(any(Entity.class))).thenAnswer((Answer<Entity>) invocation -> {
            Entity entity = (Entity)invocation.getArguments()[0];
            entity.setId(entityId);
            return entity;
        });
        when(essenceRepository.save(any(Essence.class))).thenAnswer((Answer<Essence>) invocation -> (Essence)invocation.getArguments()[0]);
        when(account.getId()).thenReturn(accountId);

        String view = mainResource.play(essenceId, session, principal, model);

        verify(accountRepository).findBySocialNetworkAndSocialNetworkId(eq(network), eq(networkId));
        verify(essenceRepository).findByAccountId(eq(accountId));
        verify(entityRepository).save(any(Entity.class));
        verify(essence).setEntity(any(Entity.class));
        verify(essenceRepository).save(any(Essence.class));
        verify(worldManager).put(any(Entity.class), eq(0L), eq(0L), eq(0L));
        verify(session).setAttribute(eq("account"), eq(accountId));
        verify(session).setAttribute(eq("essence"), eq(essenceId));
        verify(session).setAttribute(eq("entity"), eq(entityId));
        verify(model).addAttribute(eq("account"), eq(account));
        verify(model).addAttribute(eq("essence"), eq(essence));

        assertEquals("play", view);
    }

    @Test
    public void testLogout() throws Exception {
        String network = "alteranet";
        String networkId = "123456789";

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession httpSession = mock(HttpSession.class);
        Principal principal = mock(Principal.class);

        when(httpSession.getAttribute(eq("session"))).thenReturn(network);
        when(principal.getName()).thenReturn(networkId);

        String view = mainResource.logout(request, response, httpSession, principal);

        verify(securityContextLogoutHandler).logout(eq(request), eq(response), any(Authentication.class));
        assertEquals("redirect:/", view);
    }

    private void addMoreEssences(List<Essence> essences) {
        for (int i = 0; i < 3; i++) {
            Essence mockEssence = mock(Essence.class);
            Entity mockEntity = mock(Entity.class);

            when(mockEssence.getId()).thenReturn("essence" + i);
            when(mockEssence.getEntity()).thenReturn(mockEntity);
            when(mockEntity.getId()).thenReturn("entity" + i);

            essences.add(mockEssence);
        }
    }
}
