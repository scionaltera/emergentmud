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

import com.emergentmud.core.model.Account;
import com.emergentmud.core.model.SocialNetwork;
import com.emergentmud.core.repository.AccountRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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
    private AccountRepository accountRepository;

    private List<SocialNetwork> networks = new ArrayList<>();
    private MainResource mainResource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        networks.add(new SocialNetwork("alteranet", "AlteraNet"));
        networks.add(new SocialNetwork("testnet", "TestNet"));

        mainResource = new MainResource(
                networks,
                securityContextLogoutHandler,
                accountRepository
        );
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
        Account account = mock(Account.class);

        HttpSession session = mock(HttpSession.class);
        Principal principal = mock(Principal.class);
        Model model = mock(Model.class);

        when(session.getAttribute(eq("social"))).thenReturn(network);
        when(principal.getName()).thenReturn(networkId);
        when(accountRepository.findBySocialNetworkAndSocialNetworkId(eq(network), eq(networkId))).thenReturn(account);

        String view = mainResource.index(session, principal, model);

        assertEquals("characters", view);
        verify(model).addAttribute(eq("networks"), eq(networks));
        verify(model).addAttribute(eq("account"), eq(account));
        verify(accountRepository).findBySocialNetworkAndSocialNetworkId(eq(network), eq(networkId));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    public void testIndexNewAccount() throws Exception {
        String id = "uniqueobjectid";
        String network = "alteranet";
        String networkId = "123456789";
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);

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

        String view = mainResource.index(session, principal, model);

        assertEquals("characters", view);
        verify(model).addAttribute(eq("networks"), eq(networks));
        verify(model).addAttribute(eq("account"), accountCaptor.capture());
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
    public void testLogout() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        String view = mainResource.logout(request, response);

        assertEquals("redirect:/", view);
        verify(securityContextLogoutHandler).logout(eq(request), eq(response), any(Authentication.class));
    }
}
