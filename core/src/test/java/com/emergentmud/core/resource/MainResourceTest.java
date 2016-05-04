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
import com.emergentmud.core.model.Room;
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
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private OAuth2Authentication oAuth2Authentication;

    @Mock
    private OAuth2AuthenticationDetails oAuth2AuthenticationDetails;

    @Mock
    private Model model;

    @Mock
    private HttpSession httpSession;

    @Mock
    private Session session;

    @Mock
    private Account account;

    @Mock
    private Essence essence;

    @Mock
    private Entity entity;

    @Mock
    private Room room;

    private String applicationVersion = "9.8.7-SNAPSHOT";
    private long applicationBootDate = System.currentTimeMillis();
    private List<SocialNetwork> networks = new ArrayList<>();
    private String network = "alteranet";
    private String accountId = "accountid";
    private String sessionId = "sessionid";
    private String simpSessionId = "simpSessionId";
    private String networkUsername = "alteranetUser007";
    private String essenceId = "essenceid";
    private String entityId = "entityid";
    private String breadcrumb = UUID.randomUUID().toString();

    private MainResource mainResource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        Map<String, String> sessionMap = new HashMap<>();

        sessionMap.put("essence", essenceId);

        when(oAuth2Authentication.getName()).thenReturn(networkUsername);
        when(oAuth2Authentication.getDetails()).thenReturn(oAuth2AuthenticationDetails);
        when(oAuth2AuthenticationDetails.getSessionId()).thenReturn(sessionId);
        when(sessionRepository.getSession(eq(sessionId))).thenReturn(session);
        when(essenceRepository.findOne(eq(essenceId))).thenReturn(essence);
        when(entityRepository.save(any(Entity.class))).thenAnswer((Answer<Entity>) invocation -> {
            Entity entity = (Entity)invocation.getArguments()[0];
            entity.setId(entityId);
            return entity;
        });
        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq(network), eq(networkUsername))).thenReturn(account);
        when(account.getId()).thenReturn(accountId);
        when(session.getAttribute(eq(breadcrumb))).thenReturn(sessionMap);
        when(httpSession.getAttribute(eq("social"))).thenReturn(network);
        when(essence.getId()).thenReturn(essenceId);
        when(essence.getEntity()).thenReturn(entity);
        when(entity.getStompSessionId()).thenReturn(simpSessionId);
        when(entity.getStompUsername()).thenReturn(networkUsername);

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
                worldManager,
                simpMessagingTemplate
        );
    }

    @Test
    public void testOnSubscribe() throws Exception {
        GameOutput greeting = mainResource.onSubscribe(oAuth2Authentication, breadcrumb, simpSessionId);
        List<String> lines = greeting.getOutput();

        verify(entity).setStompUsername(eq(networkUsername));
        verify(entity).setStompSessionId(eq(simpSessionId));
        assertEquals(16, lines.size());
    }

    @Test
    public void testOnInputInfo() throws Exception {
        String text = "info";
        UserInput input = mock(UserInput.class);

        when(input.getInput()).thenReturn(text);

        GameOutput output = mainResource.onInput(input, oAuth2Authentication, breadcrumb, simpSessionId);

        List<String> lines = output.getOutput();

        assertEquals(13, lines.size());
    }

    @Test
    public void testOnInputInvalidSession() throws Exception {
        String text = "info";
        UserInput input = mock(UserInput.class);

        when(input.getInput()).thenReturn(text);
        when(entity.getStompUsername()).thenReturn("jello");
        when(entity.getStompSessionId()).thenReturn("biafra");

        GameOutput output = mainResource.onInput(input, oAuth2Authentication, breadcrumb, simpSessionId);
        List<String> lines = output.getOutput();

        assertEquals(1, lines.size());
    }

    @Test
    public void testOnInputInfoNoEntity() throws Exception {
        String text = "info";
        UserInput input = mock(UserInput.class);

        when(essence.getEntity()).thenReturn(null);
        when(input.getInput()).thenReturn(text);

        GameOutput output = mainResource.onInput(input, oAuth2Authentication, breadcrumb, simpSessionId);

        List<String> lines = output.getOutput();

        assertEquals(1, lines.size());
    }

    @Test
    public void testOnInputSay() throws Exception {
        String text = "say I'm a banana!";
        UserInput input = mock(UserInput.class);
        List<Entity> roomContents = new ArrayList<>();

        roomContents.add(entity);

        when(entity.getId()).thenReturn(entityId);
        when(entity.getRoom()).thenReturn(room);
        when(room.getContents()).thenReturn(roomContents);
        when(input.getInput()).thenReturn(text);

        GameOutput output = mainResource.onInput(input, oAuth2Authentication, breadcrumb, simpSessionId);

        List<String> lines = output.getOutput();

        assertEquals("[cyan]You say 'I&#39;m a banana![cyan]'", lines.get(0));
        assertEquals("", lines.get(1));
        assertEquals("> ", lines.get(2));
        assertEquals(3, lines.size());
    }

    @Test
    public void testOnInputBadCommand() throws Exception {
        String text = "I'm a banana!";
        UserInput input = mock(UserInput.class);
        List<Entity> roomContents = new ArrayList<>();

        roomContents.add(entity);

        when(entity.getId()).thenReturn(entityId);
        when(entity.getRoom()).thenReturn(room);
        when(room.getContents()).thenReturn(roomContents);
        when(input.getInput()).thenReturn(text);

        GameOutput output = mainResource.onInput(input, oAuth2Authentication, breadcrumb, simpSessionId);

        List<String> lines = output.getOutput();

        assertEquals("Huh?", lines.get(0));
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
        HttpSession session = mock(HttpSession.class);
        Principal principal = mock(Principal.class);
        Model model = mock(Model.class);
        Account account = mock(Account.class);
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
        verify(session).setAttribute(anyString(), any(Map.class));
        verify(model).addAttribute(eq("breadcrumb"), anyString());
        verify(model).addAttribute(eq("account"), eq(account));
        verify(model).addAttribute(eq("essence"), eq(essence));

        assertEquals("play", view);
    }

    @Test
    public void testPlayNoEntity() throws Exception {
        String network = "alteranet";
        String networkId = "123456789";
        String accountId = "accountid";
        HttpSession session = mock(HttpSession.class);
        Principal principal = mock(Principal.class);
        Model model = mock(Model.class);
        Account account = mock(Account.class);
        List<Essence> essences = new ArrayList<>();

        essences.add(essence);

        addMoreEssences(essences);

        when(session.getAttribute(eq("social"))).thenReturn(network);
        when(principal.getName()).thenReturn(networkId);
        when(essenceRepository.findByAccountId(eq(accountId))).thenReturn(essences);
        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq(network), eq(networkId))).thenReturn(account);
        when(essenceRepository.save(any(Essence.class))).thenAnswer((Answer<Essence>) invocation -> (Essence)invocation.getArguments()[0]);
        when(account.getId()).thenReturn(accountId);
        when(essence.getEntity()).thenReturn(null);

        String view = mainResource.play(essenceId, session, principal, model);

        verify(accountRepository).findBySocialNetworkAndSocialNetworkId(eq(network), eq(networkId));
        verify(essenceRepository).findByAccountId(eq(accountId));
        verify(entityRepository).save(any(Entity.class));
        verify(essence).setEntity(any(Entity.class));
        verify(essenceRepository).save(any(Essence.class));
        verify(worldManager).put(any(Entity.class), eq(0L), eq(0L), eq(0L));
        verify(session).setAttribute(anyString(), any(Map.class));
        verify(model).addAttribute(eq("breadcrumb"), anyString());
        verify(model).addAttribute(eq("account"), eq(account));
        verify(model).addAttribute(eq("essence"), eq(essence));

        assertEquals("play", view);
    }

    @Test
    public void testPlayAlreadyInGame() throws Exception {
        List<Essence> essences = new ArrayList<>();

        when(essence.getEntity()).thenReturn(entity);
        when(entity.getId()).thenReturn(entityId);
        when(entity.getRoom()).thenReturn(room);

        essences.add(essence);

        addMoreEssences(essences);

        when(essenceRepository.findByAccountId(eq(accountId))).thenReturn(essences);

        String view = mainResource.play(essenceId, httpSession, oAuth2Authentication, model);

        verify(accountRepository).findBySocialNetworkAndSocialNetworkId(eq(network), eq(networkUsername));
        verify(essenceRepository).findByAccountId(eq(accountId));
        verify(worldManager).put(any(Entity.class), anyLong(), anyLong(), anyLong());
        verify(httpSession).setAttribute(anyString(), any(Map.class));
        verify(model).addAttribute(eq("breadcrumb"), anyString());
        verify(model).addAttribute(eq("account"), eq(account));
        verify(model).addAttribute(eq("essence"), eq(essence));

        assertEquals("play", view);
    }

    @Test
    public void testPlayReconnect() throws Exception {
        List<Essence> essences = new ArrayList<>();

        when(essence.getEntity()).thenReturn(entity);
        when(entity.getId()).thenReturn(entityId);
        when(entity.getRoom()).thenReturn(room);

        essences.add(essence);

        addMoreEssences(essences);

        when(essenceRepository.findByAccountId(eq(accountId))).thenReturn(essences);

        ArgumentCaptor<MessageHeaders> headersCaptor = ArgumentCaptor.forClass(MessageHeaders.class);

        String view = mainResource.play(essenceId, httpSession, oAuth2Authentication, model);

        assertEquals("play", view);

        verify(simpMessagingTemplate).convertAndSendToUser(anyString(), eq("/queue/output"), any(GameOutput.class), headersCaptor.capture());
        verify(worldManager).put(any(Entity.class), anyLong(), anyLong(), anyLong());
        verify(httpSession).setAttribute(anyString(), any(Map.class));
        verify(model).addAttribute(eq("breadcrumb"), anyString());
        verify(model).addAttribute(eq("account"), eq(account));
        verify(model).addAttribute(eq("essence"), eq(essence));

        MessageHeaders headers = headersCaptor.getValue();
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.getAccessor(headers, SimpMessageHeaderAccessor.class);

        assertTrue(headers.get("simpSessionId").equals(simpSessionId));
        assertTrue(accessor.isMutable());
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
