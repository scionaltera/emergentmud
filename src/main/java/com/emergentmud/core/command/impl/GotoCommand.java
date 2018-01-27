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

package com.emergentmud.core.command.impl;

import com.emergentmud.core.command.BaseCommand;
import com.emergentmud.core.command.Command;
import com.emergentmud.core.exception.NoSuchRoomException;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.service.MovementService;
import com.emergentmud.core.service.EntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

@Component
public class GotoCommand extends BaseCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(GotoCommand.class);

    private ApplicationContext applicationContext;
    private MovementService movementService;
    private EntityService entityService;

    @Inject
    public GotoCommand(ApplicationContext applicationContext,
                       MovementService movementService,
                       EntityService entityService) {

        this.applicationContext = applicationContext;
        this.movementService = movementService;
        this.entityService = entityService;

        setDescription("Instantly transport to another person, or a room by its coordinate.");
        addParameter("x|person", true);
        addParameter("y", false);
        addParameter("z", false);
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String command, String[] tokens, String raw) {
        long[] location = new long[3];

        if (tokens.length == 0) {
            usage(output, command);
            return output;
        } else if (tokens.length == 1) {
            Optional<Entity> targetOptional = entityService.entitySearchInWorld(entity, tokens[0]);

            if (targetOptional.isPresent()) {
                Entity target = targetOptional.get();

                location[0] = target.getX();
                location[1] = target.getY();
                location[2] = target.getZ();
            } else {
                output.append("[yellow]There is no one by that name to go to.");
                return output;
            }
        } else if (tokens.length == 2 || tokens.length == 3) {
            try {
                location[0] = Long.parseLong(tokens[0]);
                location[1] = Long.parseLong(tokens[1]);

                if (tokens.length == 3) {
                    location[2] = Long.parseLong(tokens[2]);
                }
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException nfe) {
                usage(output, command);

                return output;
            }
        }

        if (((Long)location[0]).equals(entity.getX())
                && ((Long)location[1]).equals(entity.getY())
                && ((Long)location[2]).equals(entity.getZ())) {

            output.append("[yellow]You're already there.");
            return output;
        }

        LOGGER.trace("Location before: ({}, {}, {})", entity.getX(), entity.getY(), entity.getZ());

        GameOutput exitMessage = new GameOutput(String.format("%s disappears in a puff of smoke!", entity.getName()));

        entityService.sendMessageToRoom(entity.getX(), entity.getY(), entity.getZ(), entity, exitMessage);

        try {
            entity = movementService.put(entity, location[0], location[1], location[2]);
        } catch (NoSuchRoomException ex) {
            output.append(ex.getMessage());
            return output;
        }

        LOGGER.trace("Location after: ({}, {}, {})", entity.getX(), entity.getY(), entity.getZ());

        GameOutput enterMessage = new GameOutput(String.format("%s appears in a puff of smoke!", entity.getName()));

        entityService.sendMessageToRoom(entity.getX(), entity.getY(), entity.getZ(), entity, enterMessage);

        Command look = (Command) applicationContext.getBean("lookCommand");
        look.execute(output, entity, "look", new String[0], "");

        return output;
    }
}
