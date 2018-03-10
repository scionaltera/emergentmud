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
import com.emergentmud.core.model.Coordinate;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.service.MovementService;
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
    private MovementService movementService;
    private EntityService entityService;

    @Inject
    public TeleportCommand(ApplicationContext applicationContext,
                           MovementService movementService,
                           EntityService entityService) {

        this.applicationContext = applicationContext;
        this.movementService = movementService;
        this.entityService = entityService;

        setDescription("Instantly transport someone from here to a room by its coordinate.");
        addParameter("person", true);
        addParameter("x|target", true);
        addParameter("y", false);
        addParameter("z", false);
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String command, String[] tokens, String raw) {
        long[] location = new long[3];

        if (tokens.length < 2 || tokens.length > 4) {
            usage(output, command);
            return output;
        }

        Optional<Entity> targetOptional = entityService.entitySearchRoom(entity, tokens[0]);

        if (!targetOptional.isPresent()) {
            output.append("[yellow]There is no one by that name here.");
            return output;
        }

        Entity target = targetOptional.get();

        if (target.equals(entity)) {
            output.append("[yellow]You can't teleport yourself. Use GOTO instead.");
            return output;
        }

        try {
            location[0] = Long.parseLong(tokens[1]);
            location[1] = Long.parseLong(tokens[2]);

            if (tokens.length == 4) {
                location[2] = Long.parseLong(tokens[3]);
            } else {
                location[2] = entity.getLocation() != null ? entity.getLocation().getZ() : 0L;
            }
        } catch (NumberFormatException e) {
            Optional<Entity> destOptional = entityService.entitySearchInWorld(entity, tokens[1]);

            if (!destOptional.isPresent()) {
                output.append("[yellow]Unable to determine destination.");
                return output;
            }

            Entity dest = destOptional.get();

            location[0] = dest.getLocation().getX();
            location[1] = dest.getLocation().getY();
            location[2] = dest.getLocation().getZ();
        } catch (ArrayIndexOutOfBoundsException e) {
            usage(output, command);

            return output;
        }

        if (entity.getLocation().getX() == location[0]
                && entity.getLocation().getY() == location[1]
                && entity.getLocation().getZ() == location[2]) {

            output.append("[yellow]You're already there.");
            return output;
        }

        LOGGER.trace("Location before: {}", entity.getLocation());

        String exitMessage = String.format("%s disappears in a puff of smoke!", target.getName());

        entityService.sendMessageToRoom(entity.getLocation(), Arrays.asList(entity, target), new GameOutput(exitMessage));

        output
                .append(String.format("[yellow]You teleport %s.", target.getName()))
                .append(exitMessage);

        try {
            entity = movementService.put(target, new Coordinate(location[0], location[1], location[2]));
        } catch (NoSuchRoomException ex) {
            output.append(ex.getMessage());
            return output;
        }

        LOGGER.trace("Location after: ({}, {}, {})", entity.getLocation());

        GameOutput enterMessage = new GameOutput(String.format("%s appears in a puff of smoke!", target.getName()));

        entityService.sendMessageToRoom(entity.getLocation(), target, enterMessage);

        Command look = (Command) applicationContext.getBean("lookCommand");
        GameOutput lookOutput = new GameOutput();

        lookOutput.append(String.format("[yellow]%s TELEPORTS you!", entity.getName()));

        look.execute(lookOutput, target, "look", new String[0], "");
        entityService.sendMessageToEntity(target, lookOutput);

        return output;
    }
}
