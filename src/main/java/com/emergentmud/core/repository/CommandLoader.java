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

package com.emergentmud.core.repository;

import com.emergentmud.core.model.CommandMetadata;
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

    @Inject
    public CommandLoader(CommandMetadataRepository commandMetadataRepository) {
        this.commandMetadataRepository = commandMetadataRepository;
    }

    @PostConstruct
    public void loadCommands() {
        if (commandMetadataRepository.count() == 0) {
            LOGGER.warn("No commands found! Bootstrapping default command set into the database...");

            List<CommandMetadata> metadataList = new ArrayList<>();

            metadataList.add(new CommandMetadata("north", "northCommand", 10, false));
            metadataList.add(new CommandMetadata("east", "eastCommand", 10, false));
            metadataList.add(new CommandMetadata("south", "southCommand", 10, false));
            metadataList.add(new CommandMetadata("west", "westCommand", 10, false));
            metadataList.add(new CommandMetadata("goto", "gotoCommand", 15, true));
            metadataList.add(new CommandMetadata("look", "lookCommand", 100, false));
            metadataList.add(new CommandMetadata("say", "sayCommand", 200, false));
            metadataList.add(new CommandMetadata("shout", "shoutCommand", 205, false));
            metadataList.add(new CommandMetadata("gossip", "gossipCommand", 210, false));
            metadataList.add(new CommandMetadata("tell", "tellCommand", 215, false));
            metadataList.add(new CommandMetadata("who", "whoCommand", 220, false));
            metadataList.add(new CommandMetadata("info", "infoCommand", 300, true));
            metadataList.add(new CommandMetadata("map", "mapCommand", 400, false));
            metadataList.add(new CommandMetadata("cmdedit", "commandEditCommand", 1000, true));
            metadataList.add(new CommandMetadata("emoteedit", "emoteEditCommand", 1000, true));
            metadataList.add(new CommandMetadata("data", "dataCommand", 1000, true));
            metadataList.add(new CommandMetadata("quit", "quitCommand", 2000, false));

            commandMetadataRepository.save(metadataList);
        }
    }
}
