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

package com.emergentmud.core.command;

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.WorldManager;
import com.emergentmud.core.util.EntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class GotoCommand extends BaseCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(GotoCommand.class);

    private ApplicationContext applicationContext;
    private WorldManager worldManager;
    private EntityUtil entityUtil;

    @Inject
    public GotoCommand(ApplicationContext applicationContext,
                       WorldManager worldManager,
                       EntityUtil entityUtil) {

        this.applicationContext = applicationContext;
        this.worldManager = worldManager;
        this.entityUtil = entityUtil;

        addParameter("x", true);
        addParameter("y", true);
        addParameter("z", false);
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String command, String[] tokens, String raw) {
        Room room = entity.getRoom();
        long[] location = new long[3];

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


        if (room != null) {
            LOGGER.trace("Location before: ({}, {}, {})", room.getX(), room.getY(), room.getZ());

            GameOutput exitMessage = new GameOutput(String.format("%s disappears in a puff of smoke!", entity.getName()));

            entityUtil.sendMessageToRoom(room, entity, exitMessage);
        } else {
            LOGGER.warn("GOTO from NULL room!");
        }

        room = worldManager.put(entity, location[0], location[1], location[2]);
        LOGGER.trace("Location after: ({}, {}, {})", location[0], location[1], location[2]);

        GameOutput enterMessage = new GameOutput(String.format("%s appears in a puff of smoke!", entity.getName()));

        entityUtil.sendMessageToRoom(room, entity, enterMessage);

        Command look = (Command)applicationContext.getBean("lookCommand");
        look.execute(output, entity, "look", new String[0], "");

        return output;
    }
}
