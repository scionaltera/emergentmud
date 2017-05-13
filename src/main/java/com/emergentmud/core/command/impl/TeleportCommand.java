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

package com.emergentmud.core.command.impl;

import com.emergentmud.core.command.BaseCommand;
import com.emergentmud.core.command.Command;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.WorldManager;
import com.emergentmud.core.service.EntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Optional;

@Component
public class TeleportCommand extends BaseCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeleportCommand.class);

    private ApplicationContext applicationContext;
    private WorldManager worldManager;
    private EntityService entityService;

    @Inject
    public TeleportCommand(ApplicationContext applicationContext,
                           WorldManager worldManager,
                           EntityService entityService) {

        this.applicationContext = applicationContext;
        this.worldManager = worldManager;
        this.entityService = entityService;

        setDescription("Instantly transport someone from here to a room by its coordinate.");
        addParameter("person", true);
        addParameter("x", true);
        addParameter("y", true);
        addParameter("z", false);
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String command, String[] tokens, String raw) {
        Room room;
        long[] location = new long[3];

        if (tokens.length < 3 || tokens.length > 4) {
            usage(output, command);
            return output;
        }

        Optional<Entity> targetOptional = entityService.entitySearchRoom(entity, tokens[0]);

        if (!targetOptional.isPresent()) {
            output.append("[yellow]There is no one by that name here.");
            return output;
        }

        Entity target = targetOptional.get();
        room = target.getRoom();

        try {
            location[0] = Long.parseLong(tokens[1]);
            location[1] = Long.parseLong(tokens[2]);

            if (tokens.length == 4) {
                location[2] = Long.parseLong(tokens[3]);
            } else {
                location[2] = room != null ? room.getZ() : 0L;
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException nfe) {
            usage(output, command);

            return output;
        }

        if (room != null
                && room.getX() == location[0]
                && room.getY() == location[1]
                && room.getZ() == location[2]) {

            output.append("[yellow]You're already there.");
            return output;
        }

        if (worldManager.test(location[0], location[1], location[2])) {
            if (room != null) {
                LOGGER.trace("Location before: ({}, {}, {})", room.getX(), room.getY(), room.getZ());

                String exitMessage = String.format("%s disappears in a puff of smoke!", target.getName());

                entityService.sendMessageToRoom(room, Arrays.asList(entity, target), new GameOutput(exitMessage));

                output
                        .append(String.format("[yellow]You teleport %s.", target.getName()))
                        .append(exitMessage);
            } else {
                LOGGER.warn("TELEPORT from NULL room!");
            }

            room = worldManager.put(target, location[0], location[1], location[2]);
            LOGGER.trace("Location after: ({}, {}, {})", location[0], location[1], location[2]);

            GameOutput enterMessage = new GameOutput(String.format("%s appears in a puff of smoke!", target.getName()));

            entityService.sendMessageToRoom(room, target, enterMessage);

            Command look = (Command) applicationContext.getBean("lookCommand");
            GameOutput lookOutput = new GameOutput();

            lookOutput.append(String.format("[yellow]%s has TELEPORTED you!", entity.getName()))
                .append("");

            look.execute(lookOutput, target, "look", new String[0], "");
            entityService.sendMessageToEntity(target, lookOutput);
        } else {
            output.append("[yellow]No such room exists.");
        }

        return output;
    }
}
