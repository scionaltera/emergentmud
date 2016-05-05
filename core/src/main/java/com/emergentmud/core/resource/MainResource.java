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
import com.emergentmud.core.exception.NoAccountException;
import com.emergentmud.core.model.Account;
import com.emergentmud.core.model.CommandMetadata;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Essence;
import com.emergentmud.core.model.SocialNetwork;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.model.stomp.UserInput;
import com.emergentmud.core.repository.AccountRepository;
import com.emergentmud.core.repository.CommandMetadataRepository;
import com.emergentmud.core.repository.EntityBuilder;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.repository.EssenceRepository;
import com.emergentmud.core.repository.WorldManager;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
public class MainResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainResource.class);
    private static final Sort SORT = new Sort("priority", "name");

    private String applicationVersion;
    private Long applicationBootDate;
    private ApplicationContext applicationContext;
    private List<SocialNetwork> networks;
    private SecurityContextLogoutHandler securityContextLogoutHandler;
    private SessionRepository sessionRepository;
    private AccountRepository accountRepository;
    private EssenceRepository essenceRepository;
    private EntityRepository entityRepository;
    private CommandMetadataRepository commandMetadataRepository;
    private WorldManager worldManager;
    private SimpMessagingTemplate simpMessagingTemplate;

    @Inject
    public MainResource(String applicationVersion,
                        Long applicationBootDate,
                        ApplicationContext applicationContext,
                        List<SocialNetwork> networks,
                        SecurityContextLogoutHandler securityContextLogoutHandler,
                        SessionRepository sessionRepository,
                        AccountRepository accountRepository,
                        EssenceRepository essenceRepository,
                        EntityRepository entityRepository,
                        CommandMetadataRepository commandMetadataRepository,
                        WorldManager worldManager,
                        SimpMessagingTemplate simpMessagingTemplate) {
        this.applicationVersion = applicationVersion;
        this.applicationBootDate = applicationBootDate;
        this.applicationContext = applicationContext;
        this.networks = networks;
        this.securityContextLogoutHandler = securityContextLogoutHandler;
        this.sessionRepository = sessionRepository;
        this.accountRepository = accountRepository;
        this.essenceRepository = essenceRepository;
        this.entityRepository = entityRepository;
        this.commandMetadataRepository = commandMetadataRepository;
        this.worldManager = worldManager;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @SubscribeMapping("/queue/output")
    public GameOutput onSubscribe(Principal principal,
                                  @Header("breadcrumb") String breadcrumb,
                                  @Header("simpSessionId") String simpSessionId) {
        Session session = getSessionFromPrincipal(principal);
        Map<String, String> sessionMap = session.getAttribute(breadcrumb);
        Essence essence = essenceRepository.findOne(sessionMap.get("essence"));
        Entity entity = essence.getEntity();

        entity.setStompUsername(principal.getName());
        entity.setStompSessionId(simpSessionId);
        entity = entityRepository.save(entity);

        GameOutput output = new GameOutput("[green]Connected to server.");

        output.append("[black]  ___                            _   __  __ _   _ ___  ".replace(" ", "&nbsp;"));
        output.append("[dwhite] | __|_ __  ___ _ _ __ _ ___ _ _| |_|  \\/  | | | |   \\ ".replace(" ", "&nbsp;"));
        output.append("[dwhite] | _|| '  \\/ -_) '_/ _` / -_) ' \\  _| |\\/| | |_| | |) |".replace(" ", "&nbsp;"));
        output.append("[white] |___|_|_|_\\___|_| \\__, \\___|_||_\\__|_|  |_|\\___/|___/ ".replace(" ", "&nbsp;"));
        output.append("[white]                   |___/                               ".replace(" ", "&nbsp;"));
        output.append("[white]Copyright &copy; 2016 BoneVM, LLC.");
        output.append("[dwhite]EmergentMUD is licensed under the <a class=\"green\" target=\"_blank\" " +
                "href=\"http://www.gnu.org/licenses/agpl-3.0.en.html\">GNU Affero General Public License</a>.");
        output.append("[dwhite]EmergentMUD offers no warranties or guarantees. Play at your own risk.");
        output.append("[dwhite]EmergentMUD is <a class=\"green\" target=\"_blank\" " +
                "href=\"https://bitbucket.org/scionaltera/emergentmud/overview\">free, open source software</a> that " +
                "[white]you [dwhite]can contribute to, modify and distribute as you wish.");
        output.append("[dwhite]EmergentMUD server status:");
        output.append("[dwhite]&nbsp;&nbsp;Version: [white]v" + applicationVersion);
        output.append("[dwhite]&nbsp;&nbsp;Up since: [white]" + new DateTime(applicationBootDate));
        output.append(String.format("[yellow]Welcome to the world, %s!", entity.getName()));
        output.append("");

        Command command = (Command)applicationContext.getBean("lookCommand");
        command.execute(output, entity, new String[0], "");

        output.append("");
        output.append("> ");

        return output;
    }

    @MessageMapping("/input")
    @SendToUser(value = "/queue/output", broadcast = false)
    public GameOutput onInput(UserInput input,
                              Principal principal,
                              @Header("breadcrumb") String breadcrumb,
                              @Header("simpSessionId") String simpSessionId) {
        Session session = getSessionFromPrincipal(principal);
        Map<String, String> sessionMap = session.getAttribute(breadcrumb);
        Essence essence = essenceRepository.findOne(sessionMap.get("essence"));
        Entity entity = essence.getEntity();

        GameOutput output = new GameOutput();

        if (entity == null) {
            LOGGER.error("Entity was null for user {}", principal.getName());
            output.append("[red]There was an internal error. The administrators have been notified.");
            return output;
        }

        if (!principal.getName().equals(entity.getStompUsername()) || !simpSessionId.equals(entity.getStompSessionId())) {
            output.append("[red]This session is no longer valid.");
            return output;
        }

        String[] tokens = input.getInput().split(" ");
        String cmd = tokens[0];
        String[] args = new String[tokens.length - 1];
        System.arraycopy(tokens, 1, args, 0, tokens.length - 1);
        String raw = input.getInput().indexOf(' ') == -1 ? "" : input.getInput().substring(input.getInput().indexOf(' ') + 1);
        List<CommandMetadata> commandMetadataList = commandMetadataRepository.findAll(SORT);

        Optional<CommandMetadata> optionalCommandMetadata = commandMetadataList
                .stream()
                .filter(cm -> cm.getName().startsWith(cmd.toLowerCase().trim()))
                .findFirst();

        if (optionalCommandMetadata.isPresent()) {
            CommandMetadata metadata = optionalCommandMetadata.get();
            Command command = (Command)applicationContext.getBean(metadata.getBeanName());

            command.execute(output, entity, args, raw);
        } else {
            output.append("Huh?");
        }

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
            LOGGER.info("No such essence: {}", id);
            return "redirect:/";
        }

        Essence essence = eOptional.get();
        Entity entity = essence.getEntity();

        if (entity == null) {
            LOGGER.info("Creating a new entity for {}", essence.getName());
            entity = new EntityBuilder()
                    .withName(essence.getName())
                    .build();

            entity = entityRepository.save(entity);

            essence.setEntity(entity);
            essence = essenceRepository.save(essence);
        }

        if (entity.getRoom() != null && entity.getStompSessionId() != null && entity.getStompUsername() != null) {
            LOGGER.info("Reconnecting: {}@{}", entity.getStompSessionId(), entity.getStompUsername());

            SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
            headerAccessor.setSessionId(entity.getStompSessionId());
            headerAccessor.setLeaveMutable(true);

            GameOutput out = new GameOutput("[red]This session has been reconnected in another browser.");

            simpMessagingTemplate.convertAndSendToUser(entity.getStompUsername(), "/queue/output", out, headerAccessor.getMessageHeaders());
        }

        worldManager.put(entity, 0L, 0L, 0L);

        String breadcrumb = UUID.randomUUID().toString();
        Map<String, String> sessionMap = new HashMap<>();

        sessionMap.put("account", account.getId());
        sessionMap.put("essence", essence.getId());
        sessionMap.put("entity", entity.getId());

        session.setAttribute(breadcrumb, sessionMap);

        model.addAttribute("breadcrumb", breadcrumb);
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

    private Session getSessionFromPrincipal(Principal principal) {
        OAuth2Authentication oauth2Authentication = (OAuth2Authentication)principal;
        OAuth2AuthenticationDetails oAuth2AuthenticationDetails = (OAuth2AuthenticationDetails)oauth2Authentication.getDetails();
        String sessionId = oAuth2AuthenticationDetails.getSessionId();

        return sessionRepository.getSession(sessionId);
    }
}
