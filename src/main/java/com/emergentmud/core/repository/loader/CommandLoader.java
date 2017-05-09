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

package com.emergentmud.core.repository.loader;

import com.emergentmud.core.model.Capability;
import com.emergentmud.core.model.CapabilityObject;
import com.emergentmud.core.model.CapabilityScope;
import com.emergentmud.core.model.CommandRole;
import com.emergentmud.core.model.CommandMetadata;
import com.emergentmud.core.repository.CapabilityRepository;
import com.emergentmud.core.repository.CommandMetadataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Component
public class CommandLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandLoader.class);

    private CommandMetadataRepository commandMetadataRepository;
    private CapabilityRepository capabilityRepository;

    @Inject
    public CommandLoader(CommandMetadataRepository commandMetadataRepository,
                         CapabilityRepository capabilityRepository) {
        this.commandMetadataRepository = commandMetadataRepository;
        this.capabilityRepository = capabilityRepository;
    }

    @PostConstruct
    public void loadCommands() {
        if (capabilityRepository.count() == 0) {
            LOGGER.warn("No capabilities found! Loading default capabilities...");

            List<Capability> capabilityList = new ArrayList<>();

            capabilityList.add(new Capability(CommandRole.SUPER.name(), "use every command", CapabilityObject.ENTITY, CapabilityScope.IMPLEMENTOR));
            capabilityList.add(new Capability(CommandRole.TELEPORT.name(), "use teleport commands", CapabilityObject.ENTITY, CapabilityScope.IMPLEMENTOR));
            capabilityList.add(new Capability(CommandRole.CMDEDIT.name(), "use command editor", CapabilityObject.ENTITY, CapabilityScope.IMPLEMENTOR));
            capabilityList.add(new Capability(CommandRole.EMOTEEDIT.name(), "use emote editor", CapabilityObject.ENTITY, CapabilityScope.IMPLEMENTOR));
            capabilityList.add(new Capability(CommandRole.CAPEDIT.name(), "use capability editor", CapabilityObject.ENTITY, CapabilityScope.IMPLEMENTOR));
            capabilityList.add(new Capability(CommandRole.DATA.name(), "use data commands", CapabilityObject.ENTITY, CapabilityScope.IMPLEMENTOR));
            capabilityList.add(new Capability(CommandRole.LOG.name(), "see log messages", CapabilityObject.ENTITY, CapabilityScope.IMPLEMENTOR));

            capabilityList.add(new Capability(CommandRole.CHAR_NEW.name(), "create characters", CapabilityObject.ACCOUNT, CapabilityScope.PLAYER));
            capabilityList.add(new Capability(CommandRole.CHAR_PLAY.name(), "play the game", CapabilityObject.ACCOUNT, CapabilityScope.PLAYER));

            capabilityList.add(new Capability(CommandRole.BASIC.name(), "use basic commands", CapabilityObject.ENTITY, CapabilityScope.PLAYER));
            capabilityList.add(new Capability(CommandRole.MOVE.name(), "use movement commands", CapabilityObject.ENTITY, CapabilityScope.PLAYER));
            capabilityList.add(new Capability(CommandRole.SEE.name(), "use sight commands", CapabilityObject.ENTITY, CapabilityScope.PLAYER));
            capabilityList.add(new Capability(CommandRole.EMOTE.name(), "use emotes", CapabilityObject.ENTITY, CapabilityScope.PLAYER));
            capabilityList.add(new Capability(CommandRole.TALK.name(), "use speech commands", CapabilityObject.ENTITY, CapabilityScope.PLAYER));

            capabilityRepository.save(capabilityList);
        }

        if (commandMetadataRepository.count() == 0) {
            LOGGER.warn("No commands found! Loading default commands...");

            List<CommandMetadata> metadataList = new ArrayList<>();

            metadataList.add(new CommandMetadata("north", "northCommand", 10, capabilityRepository.findByName(CommandRole.MOVE.name())));
            metadataList.add(new CommandMetadata("east", "eastCommand", 10, capabilityRepository.findByName(CommandRole.MOVE.name())));
            metadataList.add(new CommandMetadata("south", "southCommand", 10, capabilityRepository.findByName(CommandRole.MOVE.name())));
            metadataList.add(new CommandMetadata("west", "westCommand", 10, capabilityRepository.findByName(CommandRole.MOVE.name())));
            metadataList.add(new CommandMetadata("goto", "gotoCommand", 15, capabilityRepository.findByName(CommandRole.TELEPORT.name())));
            metadataList.add(new CommandMetadata("look", "lookCommand", 100, capabilityRepository.findByName(CommandRole.SEE.name())));
            metadataList.add(new CommandMetadata("say", "sayCommand", 200, capabilityRepository.findByName(CommandRole.TALK.name())));
            metadataList.add(new CommandMetadata("shout", "shoutCommand", 205, capabilityRepository.findByName(CommandRole.TALK.name())));
            metadataList.add(new CommandMetadata("gossip", "gossipCommand", 210, capabilityRepository.findByName(CommandRole.TALK.name())));
            metadataList.add(new CommandMetadata("tell", "tellCommand", 215, capabilityRepository.findByName(CommandRole.TALK.name())));
            metadataList.add(new CommandMetadata("who", "whoCommand", 220, capabilityRepository.findByName(CommandRole.SEE.name())));
            metadataList.add(new CommandMetadata("emote", "emoteCommand", 250, capabilityRepository.findByName(CommandRole.TALK.name())));
            metadataList.add(new CommandMetadata("info", "infoCommand", 300, capabilityRepository.findByName(CommandRole.DATA.name())));
            metadataList.add(new CommandMetadata("map", "mapCommand", 400, capabilityRepository.findByName(CommandRole.SEE.name())));
            metadataList.add(new CommandMetadata("help", "helpCommand", 500, capabilityRepository.findByName(CommandRole.BASIC.name())));
            metadataList.add(new CommandMetadata("cmdedit", "commandEditCommand", 1000, capabilityRepository.findByName(CommandRole.CMDEDIT.name())));
            metadataList.add(new CommandMetadata("emoteedit", "emoteEditCommand", 1000, capabilityRepository.findByName(CommandRole.EMOTEEDIT.name())));
            metadataList.add(new CommandMetadata("capedit", "capabilityEditCommand", 1000, capabilityRepository.findByName(CommandRole.CAPEDIT.name())));
            metadataList.add(new CommandMetadata("data", "dataCommand", 1000, capabilityRepository.findByName(CommandRole.DATA.name())));
            metadataList.add(new CommandMetadata("quit", "quitCommand", 2000, capabilityRepository.findByName(CommandRole.BASIC.name())));

            commandMetadataRepository.save(metadataList);
        }
    }
}
