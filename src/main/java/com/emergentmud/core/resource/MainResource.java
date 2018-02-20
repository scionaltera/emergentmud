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
import com.emergentmud.core.exception.NoAccountException;
import com.emergentmud.core.exception.NoSuchRoomException;
import com.emergentmud.core.model.Account;
import com.emergentmud.core.model.Capability;
import com.emergentmud.core.model.CapabilityObject;
import com.emergentmud.core.model.CapabilityScope;
import com.emergentmud.core.model.CommandRole;
import com.emergentmud.core.model.CommandMetadata;
import com.emergentmud.core.model.EmoteMetadata;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.SocialNetwork;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.AccountRepository;
import com.emergentmud.core.repository.CapabilityRepository;
import com.emergentmud.core.repository.CommandMetadataRepository;
import com.emergentmud.core.repository.EmoteMetadataRepository;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.service.MovementService;
import com.emergentmud.core.resource.model.PlayRequest;
import com.emergentmud.core.service.EntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class MainResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainResource.class);

    private ApplicationContext applicationContext;
    private List<SocialNetwork> networks;
    private SecurityContextLogoutHandler securityContextLogoutHandler;
    private AccountRepository accountRepository;
    private EntityRepository entityRepository;
    private CommandMetadataRepository commandMetadataRepository;
    private EmoteMetadataRepository emoteMetadataRepository;
    private CapabilityRepository capabilityRepository;
    private MovementService movementService;
    private EntityService entityService;
    private Emote emote;

    @Inject
    public MainResource(ApplicationContext applicationContext,
                        List<SocialNetwork> networks,
                        SecurityContextLogoutHandler securityContextLogoutHandler,
                        AccountRepository accountRepository,
                        EntityRepository entityRepository,
                        CommandMetadataRepository commandMetadataRepository,
                        EmoteMetadataRepository emoteMetadataRepository,
                        CapabilityRepository capabilityRepository,
                        MovementService movementService,
                        EntityService entityService,
                        Emote emote) {

        this.applicationContext = applicationContext;
        this.networks = networks;
        this.securityContextLogoutHandler = securityContextLogoutHandler;
        this.accountRepository = accountRepository;
        this.entityRepository = entityRepository;
        this.commandMetadataRepository = commandMetadataRepository;
        this.emoteMetadataRepository = emoteMetadataRepository;
        this.capabilityRepository = capabilityRepository;
        this.movementService = movementService;
        this.entityService = entityService;
        this.emote = emote;
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
            account.addCapabilities(capabilityRepository.findByObjectAndScope(CapabilityObject.ACCOUNT, CapabilityScope.PLAYER));

            if (accountRepository.count() == 0) {
                LOGGER.info("Making {}:{} into an administrator", account.getSocialNetwork(), account.getSocialNetworkId());

                account.addCapabilities(capabilityRepository.findByObjectAndScope(CapabilityObject.ACCOUNT, CapabilityScope.ADMINISTRATOR));
            }

            account = accountRepository.save(account);

            LOGGER.info("Created new account {}:{} -> {}", network, networkId, account.getId());
        }

        LOGGER.info("Successful login for: {}:{}", network, networkId);

        List<Entity> entities = entityRepository.findByAccount(account);

        if (entities.isEmpty()) {
            httpSession.setAttribute("firstLogin", "true");
            return "new-entity";
        }

        model.addAttribute("account", account);
        model.addAttribute("entities", entities);

        return "entity";
    }

    @RequestMapping("/social/{network}")
    public String social(@PathVariable String network, HttpSession session) {
        session.setAttribute("social", network);

        return "redirect:/login/" + network;
    }

    @RequestMapping("/entity")
    public String newEntity() {
        return "new-entity";
    }

    @RequestMapping(method=RequestMethod.POST, value="/entity")
    public String saveNewEntity(HttpSession session, Principal principal, Entity entity, Model model) {
        String network = (String)session.getAttribute("social");
        String networkId = principal.getName();
        Account account = accountRepository.findBySocialNetworkAndSocialNetworkId(network, networkId);

        if (account == null) {
            throw new NoAccountException(network, networkId);
        }

        if (!account.isCapable(capabilityRepository.findByName(CommandRole.CHAR_NEW.name()))) {
            model.addAttribute("entityName", entity.getName());
            model.addAttribute("errorName", "You are not allowed to create new characters at this time.");
            return "new-entity";
        }

        if (entity.getName().length() < 3) {
            model.addAttribute("entityName", entity.getName());
            model.addAttribute("errorName", "Names must be at least 3 letters long.");
            return "new-entity";
        }

        if (entity.getName().length() > 30) {
            model.addAttribute("entityName", entity.getName());
            model.addAttribute("errorName", "Names must be less than 30 letters long.");
            return "new-entity";
        }

        if (!entity.getName().matches("^[A-Z].+$")) {
            model.addAttribute("entityName", entity.getName());
            model.addAttribute("errorName", "Names must begin with a capital letter.");
            return "new-entity";
        }

        if (!entity.getName().matches("^[A-Za-z'-]+$")) {
            model.addAttribute("entityName", entity.getName());
            model.addAttribute("errorName", "Names may only contain letters, hyphens and apostrophes.");
            return "new-entity";
        }

        if (entity.getName().matches("^[A-Za-z'-]\\w+['-]$")) {
            model.addAttribute("entityName", entity.getName());
            model.addAttribute("errorName", "Names may not end with a hyphen or apostrophe.");
            return "new-entity";
        }

        if (entity.getName().matches("^\\w+['-].*['-]\\w+$")) {
            model.addAttribute("entityName", entity.getName());
            model.addAttribute("errorName", "Names may only contain one hyphen or apostrophe.");
            return "new-entity";
        }

        if (entityRepository.count() == 0) {
            LOGGER.info("Making {} into an administrator", entity.getName());

            entity.addCapabilities(capabilityRepository.findByObjectAndScope(CapabilityObject.ENTITY, CapabilityScope.ADMINISTRATOR));
        }

        entity.addCapabilities(capabilityRepository.findByObjectAndScope(CapabilityObject.ENTITY, CapabilityScope.PLAYER));
        entity.setCreationDate(System.currentTimeMillis());
        entity.setAccount(account);
        entity = entityRepository.save(entity);

        LOGGER.info("Saved new Entity: {} -> {}", entity.getName(), entity.getId());

        if ("true".equals(session.getAttribute("firstLogin"))) {
            session.removeAttribute("firstLogin");
            session.setAttribute("entityId", entity.getId());
            return "redirect:/play";
        }

        return "redirect:/";
    }

    @RequestMapping(method=RequestMethod.GET, value="/play")
    public String getPlay(HttpSession session, Model model) {
        UUID entityId = (UUID) session.getAttribute("entityId");

        model.addAttribute("entityId", entityId);
        session.removeAttribute("entityId");

        return "play-post";
    }

    @RequestMapping(method=RequestMethod.POST, value="/play")
    public String play(PlayRequest playRequest, HttpSession session, HttpServletRequest httpServletRequest, Principal principal, Model model) {
        if (StringUtils.isEmpty(playRequest.getEntityId())) {
            LOGGER.info("No ID provided.");
            return "redirect:/";
        }

        UUID entityId = UUID.fromString(playRequest.getEntityId());
        String network = (String)session.getAttribute("social");
        String networkId = principal.getName();
        Account account = accountRepository.findBySocialNetworkAndSocialNetworkId(network, networkId);

        if (account == null) {
            LOGGER.info("No such account: {}:{}", network, networkId);
            return "redirect:/";
        }

        if (!account.isCapable(capabilityRepository.findByName(CommandRole.CHAR_PLAY.name()))) {
            LOGGER.info("Account {} is not allowed to play.", account.getId());
            return "redirect:/";
        }

        // It's important to verify that the Entity has the right ID -and- belongs to the account
        // because we can't trust that the client didn't change the ID in the request.
        Entity entity = entityRepository.findByAccountAndId(account, entityId);

        if (entity == null) {
            LOGGER.info("No such entity: {}", entityId);
            return "redirect:/";
        }

        entity.setLastLoginDate(System.currentTimeMillis());
        entity.setRemoteAddr(extractRemoteIp(httpServletRequest));
        entity.setUserAgent(httpServletRequest.getHeader("User-Agent"));

        entity = entityRepository.save(entity);

        if (entity.getX() != null && entity.getY() != null && entity.getZ() != null && entity.getStompSessionId() != null && entity.getStompUsername() != null) {
            LOGGER.info("Reconnecting: {}@{}", entity.getStompSessionId(), entity.getStompUsername());

            GameOutput out = new GameOutput("[red]This session has been reconnected in another browser.");
            entityService.sendMessageToEntity(entity, out);
        }

        try {
            entity = movementService.put(entity, 0L, 0L, 0L);
        } catch (NoSuchRoomException ex) {
            GameOutput errorOut = new GameOutput("[red]No starting room could be found! The administrators have been notified!");
            entityService.sendMessageToEntity(entity, errorOut);

            LOGGER.error("Start room doesn't exist! Unable to place new player!");

            return "redirect:/";
        }

        GameOutput enterMessage = new GameOutput(String.format("[yellow]%s has entered the game.", entity.getName()));

        entityService.sendMessageToRoom(entity.getX(), entity.getY(), entity.getZ(), entity, enterMessage);

        LOGGER.info("{} has entered the game from {}", entity.getName(), entity.getRemoteAddr());

        String breadcrumb = UUID.randomUUID().toString();
        Map<String, String> sessionMap = new HashMap<>();

        sessionMap.put("account", account.getId().toString());
        sessionMap.put("entity", entity.getId().toString());

        session.setAttribute(breadcrumb, sessionMap);

        model.addAttribute("breadcrumb", breadcrumb);
        model.addAttribute("account", account);
        model.addAttribute("entity", entity);

        return "play";
    }

    @RequestMapping("/public/commands")
    public String commands(Model model, Principal principal, HttpSession httpSession) {
        Capability superCapability = capabilityRepository.findByName(CommandRole.SUPER.name());
        Set<Capability> capabilities = new HashSet<>();

        if (principal != null) {
            String network = (String)httpSession.getAttribute("social");
            String networkId = principal.getName();
            Account account = accountRepository.findBySocialNetworkAndSocialNetworkId(network, networkId);

            if (account != null) {
                List<Entity> entities = entityRepository.findByAccount(account);

                capabilities.addAll(account.getCapabilities());
                entities.forEach(e -> capabilities.addAll(e.getCapabilities()));
            }
        } else {
            capabilities.add(capabilityRepository.findByName(CommandRole.EMOTE.name()));
            capabilities.add(capabilityRepository.findByName(CommandRole.BASIC.name()));
            capabilities.add(capabilityRepository.findByName(CommandRole.MOVE.name()));
            capabilities.add(capabilityRepository.findByName(CommandRole.SEE.name()));
            capabilities.add(capabilityRepository.findByName(CommandRole.TALK.name()));
        }

        Map<String, Command> commandMap = new HashMap<>();
        List<CommandMetadata> allMetadata = new ArrayList<>();

        commandMetadataRepository.findAll().forEach(allMetadata::add);

        List<CommandMetadata> metadata = allMetadata.stream()
                .filter(m -> capabilities.contains(m.getCapability()) || capabilities.contains(superCapability))
                .collect(Collectors.toList());

        metadata.forEach(m -> {
                Command command = (Command) applicationContext.getBean(m.getBeanName());
                commandMap.put(m.getName(), command);
        });

        model.addAttribute("metadataList", metadata);
        model.addAttribute("commandMap", commandMap);

        return "commands";
    }

    @RequestMapping("/public/emotes")
    public String emotes(Model model, Principal principal, HttpSession httpSession) {
        List<EmoteMetadata> metadata = new ArrayList<>();

        emoteMetadataRepository.findAll().forEach(metadata::add);

        Map<String, EmoteMetadata> emoteMap = new HashMap<>();
        Entity self;
        Entity target;

        if (principal != null) {
            String network = (String)httpSession.getAttribute("social");
            String networkId = principal.getName();
            Account account = accountRepository.findBySocialNetworkAndSocialNetworkId(network, networkId);
            List<Entity> entities = entityRepository.findByAccount(account);

            if (entities.size() > 0) {
                self = new Entity();
                self.setName(entities.get(0).getName());
            } else {
                self = new Entity();
                self.setName("Alice");
            }

            if (entities.size() > 1) {
                target = new Entity();
                target.setName(entities.get(1).getName());
            } else {
                target = new Entity();
                target.setName("Bob");
            }
        } else {
            self = new Entity();
            self.setName("Alice");

            target = new Entity();
            target.setName("Bob");
        }

        metadata.forEach(m -> {
            m.setToSelfUntargeted(emote.replaceVariables(m.getToSelfUntargeted(), self, null));
            m.setToRoomUntargeted(emote.replaceVariables(m.getToRoomUntargeted(), self, null));
            m.setToSelfWithTarget(emote.replaceVariables(m.getToSelfWithTarget(), self, target));
            m.setToTarget(emote.replaceVariables(m.getToTarget(), self, target));
            m.setToRoomWithTarget(emote.replaceVariables(m.getToRoomWithTarget(), self, target));
            m.setToSelfAsTarget(emote.replaceVariables(m.getToSelfAsTarget(), self, self));
            m.setToRoomTargetingSelf(emote.replaceVariables(m.getToRoomTargetingSelf(), self, self));

            emoteMap.put(m.getName(), m);
        });

        model.addAttribute("self", self);
        model.addAttribute("target", target);
        model.addAttribute("metadataList", metadata);
        model.addAttribute("emoteMap", emoteMap);

        return "emotes";
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

    private String extractRemoteIp(HttpServletRequest request) {
        String forwardedHeader = request.getHeader("x-forwarded-for");

        if (forwardedHeader != null) {
            String[] addresses = forwardedHeader.split("[,]");

            for (String address : addresses) {
                try {
                    InetAddress inetAddress = InetAddress.getByName(address);

                    if (!inetAddress.isSiteLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                } catch (UnknownHostException e) {
                    LOGGER.debug("Failed to resolve IP for address: {}", address);
                }
            }
        }

        return request.getRemoteAddr();
    }
}
