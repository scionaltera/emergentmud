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

package com.emergentmud.core.command;

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.WorldManager;
import com.emergentmud.core.util.EntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

public class MoveCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(MoveCommand.class);

    private long[] differential;
    private ApplicationContext applicationContext;
    private WorldManager worldManager;
    private EntityUtil entityUtil;
    private String direction;
    private String opposite;

    public MoveCommand(
            long x, long y, long z, String direction, String opposite,
            ApplicationContext applicationContext,
            WorldManager worldManager,
            EntityUtil entityUtil) {

        differential = new long[] {x, y, z};
        this.direction = direction;
        this.opposite = opposite;
        this.applicationContext = applicationContext;
        this.worldManager = worldManager;
        this.entityUtil = entityUtil;
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String command, String[] tokens, String raw) {
        Room room = entity.getRoom();

        if (room == null) {
            output.append("[black]You are floating in a formless void. It is impossible to tell whether or not you are moving.");
        } else {
            long[] location = new long[] {
                    room.getX(),
                    room.getY(),
                    room.getZ()
            };

            LOGGER.trace("Location before: ({}, {}, {})", location[0], location[1], location[2]);

            location[0] += differential[0];
            location[1] += differential[1];
            location[2] += differential[2];

            if (!worldManager.test(location[0], location[1], location[2])) {
                output.append("Alas, you cannot go that way.");
                return output;
            }

            GameOutput exitMessage = new GameOutput(String.format("%s walks %s.", entity.getName(), direction))
                    .append("")
                    .append("> ");

            entityUtil.sendMessageToRoom(room, entity, exitMessage);

            worldManager.remove(entity);

            room = worldManager.put(entity, location[0], location[1], location[2]);
            LOGGER.trace("Location after: ({}, {}, {})", location[0], location[1], location[2]);

            GameOutput enterMessage = new GameOutput(String.format("%s walks in from the %s.", entity.getName(), opposite))
                    .append("")
                    .append("> ");

            entityUtil.sendMessageToRoom(room, entity, enterMessage);

            Command look = (Command)applicationContext.getBean("lookCommand");
            look.execute(output, entity, "look", new String[0], "");
        }

        return output;
    }
}
