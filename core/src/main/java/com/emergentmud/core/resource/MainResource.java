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
import com.emergentmud.core.model.Essence;
import com.emergentmud.core.model.SocialNetwork;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.model.stomp.UserInput;
import com.emergentmud.core.repository.AccountRepository;
import com.emergentmud.core.repository.EssenceRepository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class MainResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainResource.class);

    private List<SocialNetwork> networks;
    private SecurityContextLogoutHandler securityContextLogoutHandler;
    private AccountRepository accountRepository;
    private EssenceRepository essenceRepository;

    @Inject
    public MainResource(List<SocialNetwork> networks,
                        SecurityContextLogoutHandler securityContextLogoutHandler,
                        AccountRepository accountRepository,
                        EssenceRepository essenceRepository) {
        this.networks = networks;
        this.securityContextLogoutHandler = securityContextLogoutHandler;
        this.accountRepository = accountRepository;
        this.essenceRepository = essenceRepository;
    }

    @SubscribeMapping("/user/queue/output")
    @SendToUser("/queue/output")
    public GameOutput onSubscribe() {
        GameOutput output = new GameOutput("[green]Connected to server.");

        output.append("[dcyan]  ___                            _   __  __ _   _ ___  ".replace(" ", "&nbsp;"));
        output.append("[dcyan] | __|_ __  ___ _ _ __ _ ___ _ _| |_|  \\/  | | | |   \\ ".replace(" ", "&nbsp;"));
        output.append("[dcyan] | _|| '  \\/ -_) '_/ _` / -_) ' \\  _| |\\/| | |_| | |) |".replace(" ", "&nbsp;"));
        output.append("[cyan] |___|_|_|_\\___|_| \\__, \\___|_||_\\__|_|  |_|\\___/|___/ ".replace(" ", "&nbsp;"));
        output.append("[cyan]                   |___/                               ".replace(" ", "&nbsp;"));
        output.append(String.format("[white]Copyright &copy; %d BoneVM, LLC.", DateTime.now().getYear()));
        output.append("[white]EmergentMUD is licensed under the <a target=\"_blank\" " +
                "href=\"http://www.gnu.org/licenses/agpl-3.0.en.html\">GNU Affero General Public License</a>.");
        output.append("[white]EmergentMUD offers no warranties or guarantees. Play at your own risk.");
        output.append("[white]EmergentMUD is <a target=\"_blank\" " +
                "href=\"https://bitbucket.org/scionaltera/emergentmud/overview\">free, open source software</a> that " +
                "[green]you [white]can contribute to, modify and distribute as you wish.");
        output.append("[yellow]Welcome to the world!");
        output.append("");
        output.append("> ");

        return output;
    }

    @MessageMapping("/input")
    @SendToUser("/queue/output")
    public GameOutput onInput(UserInput input) {
        GameOutput output = new GameOutput();

        output.append(String.format("[cyan]You say '%s[cyan]'", htmlEscape(input.getInput())));
        output.append("");
        output.append("> ");

        return output;
    }

    @RequestMapping("/")
    public String index(HttpSession httpSession, Principal principal, Model model) {
        model.addAttribute("networks", networks);

        if (principal == null) {
            return "index";
        }

        String network = (String)httpSession.getAttribute("social");
        String networkId = principal.getName();
        Account account = accountRepository.findBySocialNetworkAndSocialNetworkId(network, networkId);

        if (account == null) {
            account = new Account();
            account.setSocialNetwork(network);
            account.setSocialNetworkId(networkId);
            account = accountRepository.save(account);

            LOGGER.info("Created new account {}:{} -> {}", network, networkId, account.getId());
        }

        LOGGER.info("Successful login for: {}:{}", network, networkId);

        List<Essence> essences = essenceRepository.findByAccountId(account.getId());

        model.addAttribute("account", account);
        model.addAttribute("essences", essences);

        return "essence";
    }

    @RequestMapping("/social/{network}")
    public String social(@PathVariable String network, HttpSession session) {
        session.setAttribute("social", network);

        LOGGER.info("Logging in via social network: {}", network);

        return "redirect:/login/" + network;
    }

    @RequestMapping("/new-essence")
    public String newEssence() {
        return "new-essence";
    }

    @RequestMapping(method=RequestMethod.POST, value="/new-essence")
    public String saveNewEssence(HttpSession session, Principal principal, Essence essence) {
        String network = (String)session.getAttribute("social");
        String networkId = principal.getName();
        Account account = accountRepository.findBySocialNetworkAndSocialNetworkId(network, networkId);

        if (account == null) {
            throw new NoAccountException(network, networkId);
        }

        essence.setAccountId(account.getId());
        essence = essenceRepository.save(essence);
        LOGGER.info("Saved new Essence: {} -> {}", essence.getName(), essence.getId());

        return "redirect:/";
    }

    @RequestMapping("/play/{id}")
    public String play(@PathVariable("id") String id, HttpSession session, Principal principal, Model model) {
        if (StringUtils.isEmpty(id)) {
            LOGGER.info("No ID provided.");
            return "redirect:/";
        }

        String network = (String)session.getAttribute("social");
        String networkId = principal.getName();
        Account account = accountRepository.findBySocialNetworkAndSocialNetworkId(network, networkId);

        if (account == null) {
            LOGGER.info("No such account: {}:{}", network, networkId);
            return "redirect:/";
        }

        List<Essence> essences = essenceRepository.findByAccountId(account.getId());
        Optional<Essence> eOptional = essences.stream()
                .filter(e -> id.equals(e.getId()))
                .findFirst();

        if (!eOptional.isPresent()) {
            LOGGER.info("No such character: {}", id);
            return "redirect:/";
        }

        Essence essence = eOptional.get();

        model.addAttribute("account", account);
        model.addAttribute("essence", essence);

        return "play";
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response,
                         HttpSession httpSession,
                         Principal principal) {

        String network = (String)httpSession.getAttribute("social");
        String networkId = principal.getName();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        LOGGER.info("Logged out: {}:{}", network, networkId);

        securityContextLogoutHandler.logout(request, response, authentication);

        return "redirect:/";
    }

    private String htmlEscape(String input) {
        return input
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\\", "&#x2F;");
    }
}
