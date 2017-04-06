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
import com.emergentmud.core.command.PromptBuilder;
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
import com.emergentmud.core.util.EntityUtil;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class WebSocketResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketResource.class);
    private static final Sort SORT = new Sort("priority", "name");

    private String applicationVersion;
    private Long applicationBootDate;
    private ApplicationContext applicationContext;
    private SessionRepository sessionRepository;
    private EssenceRepository essenceRepository;
    private EntityRepository entityRepository;
    private CommandMetadataRepository commandMetadataRepository;
    private EmoteMetadataRepository emoteMetadataRepository;
    private PromptBuilder promptBuilder;
    private EntityUtil entityUtil;
    private Emote emote;

    @Inject
    public WebSocketResource(String applicationVersion,
                             Long applicationBootDate,
                             ApplicationContext applicationContext,
                             SessionRepository sessionRepository,
                             EssenceRepository essenceRepository,
                             EntityRepository entityRepository,
                             CommandMetadataRepository commandMetadataRepository,
                             EmoteMetadataRepository emoteMetadataRepository,
                             PromptBuilder promptBuilder,
                             EntityUtil entityUtil,
                             Emote emote) {
        this.applicationVersion = applicationVersion;
        this.applicationBootDate = applicationBootDate;
        this.applicationContext = applicationContext;
        this.sessionRepository = sessionRepository;
        this.essenceRepository = essenceRepository;
        this.entityRepository = entityRepository;
        this.commandMetadataRepository = commandMetadataRepository;
        this.emoteMetadataRepository = emoteMetadataRepository;
        this.promptBuilder = promptBuilder;
        this.entityUtil = entityUtil;
        this.emote = emote;
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

        GameOutput output = new GameOutput();

        output.append("[black]  ___                            _   __  __ _   _ ___  ".replace(" ", "&nbsp;"));
        output.append("[dwhite] | __|_ __  ___ _ _ __ _ ___ _ _| |_|  \\/  | | | |   \\ ".replace(" ", "&nbsp;"));
        output.append("[dwhite] | _|| '  \\/ -_) '_/ _` / -_) ' \\  _| |\\/| | |_| | |) |".replace(" ", "&nbsp;"));
        output.append("[white] |___|_|_|_\\___|_| \\__, \\___|_||_\\__|_|  |_|\\___/|___/ ".replace(" ", "&nbsp;"));
        output.append("[white]                   |___/                               ".replace(" ", "&nbsp;"));
        output.append("[white]Copyright &copy; 2016-2017 Peter Keeler.");
        output.append("[dwhite]EmergentMUD is licensed under the <a class=\"green\" target=\"_blank\" " +
                "href=\"http://www.gnu.org/licenses/agpl-3.0.en.html\">GNU Affero General Public License</a>.");
        output.append("[dwhite]EmergentMUD offers no warranties or guarantees. Play at your own risk.");
        output.append("[red]EmergentMUD is in early stages of development. Reboots and database wipes may occur without warning!");
        output.append("[dwhite]EmergentMUD is <a class=\"green\" target=\"_blank\" " +
                "href=\"https://bitbucket.org/scionaltera/emergentmud/overview\">free, open source software</a> that " +
                "[white]you [dwhite]can contribute to, modify and distribute as you wish.");
        output.append("[dwhite]EmergentMUD server status:");
        output.append("[dwhite]&nbsp;&nbsp;Version: [white]v" + applicationVersion);
        output.append("[dwhite]&nbsp;&nbsp;Up since: [white]" + new DateTime(applicationBootDate));
        output.append(String.format("[yellow]Welcome to the world, %s!", entity.getName()));
        output.append("");

        Command command = (Command)applicationContext.getBean("lookCommand");
        command.execute(output, entity, "look", new String[0], "");

        promptBuilder.appendPrompt(output);

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

        if (!"".equals(input.getInput().trim())) {
            String[] tokens = input.getInput().split(" ");
            String cmd = tokens[0];
            String[] args = new String[tokens.length - 1];
            System.arraycopy(tokens, 1, args, 0, tokens.length - 1);
            String raw = input.getInput().indexOf(' ') == -1 ? "" : input.getInput().substring(input.getInput().indexOf(' ') + 1);
            List<CommandMetadata> commandMetadataList = commandMetadataRepository.findAll(SORT);

            Optional<CommandMetadata> optionalCommandMetadata = commandMetadataList
                    .stream()
                    .filter(cm -> cm.getName().startsWith(cmd.toLowerCase().trim()))
                    .filter(cm -> essence.isAdmin() || !cm.isAdmin())
                    .findFirst();

            if (optionalCommandMetadata.isPresent()) {
                CommandMetadata metadata = optionalCommandMetadata.get();
                Command command = (Command) applicationContext.getBean(metadata.getBeanName());

                command.execute(output, entity, cmd, args, raw);
            } else {
                List<EmoteMetadata> emoteMetadataList = emoteMetadataRepository.findAll(SORT);

                Optional<EmoteMetadata> optionalEmoteMetadata = emoteMetadataList
                        .stream()
                        .filter(emote -> emote.getName().startsWith(cmd.toLowerCase().trim()))
                        .findFirst();

                if (optionalEmoteMetadata.isPresent()) {
                    EmoteMetadata metadata = optionalEmoteMetadata.get();

                    emote.execute(output, metadata, entity, args);
                } else {
                    output.append("Huh?");
                }
            }
        }

        promptBuilder.appendPrompt(output);

        return output;
    }

    private Session getSessionFromPrincipal(Principal principal) {
        OAuth2Authentication oauth2Authentication = (OAuth2Authentication)principal;
        OAuth2AuthenticationDetails oAuth2AuthenticationDetails = (OAuth2AuthenticationDetails)oauth2Authentication.getDetails();
        String sessionId = oAuth2AuthenticationDetails.getSessionId();

        return sessionRepository.getSession(sessionId);
    }
}
