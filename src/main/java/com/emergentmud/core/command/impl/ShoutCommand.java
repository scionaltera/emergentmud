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

import com.emergentmud.core.command.BaseCommunicationCommand;
import com.emergentmud.core.command.Command;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.repository.RoomRepository;
import com.emergentmud.core.util.EntityUtil;
import com.emergentmud.core.util.RoomUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShoutCommand extends BaseCommunicationCommand implements Command {
    static final long SHOUT_DISTANCE = 7;

    private RoomRepository roomRepository;
    private RoomUtil roomUtil;

    @Inject
    public ShoutCommand(RoomRepository roomRepository,
                        EntityRepository entityRepository,
                        RoomUtil roomUtil,
                        EntityUtil entityUtil) {
        this.roomRepository = roomRepository;
        this.roomUtil = roomUtil;
        this.entityRepository = entityRepository;
        this.entityUtil = entityUtil;

        setDescription("Send a message to those within a few rooms of you.");
        addParameter("message", true);
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String command, String[] tokens, String raw) {
        if (StringUtils.isEmpty(raw)) {
            output.append("What would you like to shout?");

            return output;
        }

        output.append(String.format("[dyellow]You shout '%s[dyellow]'", htmlEscape(raw)));

        GameOutput toZone = new GameOutput(String.format("[dyellow]%s shouts '%s[dyellow]'", entity.getName(), htmlEscape(raw)));

        Room entityRoom = entity.getRoom();
        List<Room> rooms = roomRepository.findByXBetweenAndYBetweenAndZBetween(
                entityRoom.getX() - SHOUT_DISTANCE,
                entityRoom.getX() + SHOUT_DISTANCE,
                entityRoom.getY() - SHOUT_DISTANCE,
                entityRoom.getY() + SHOUT_DISTANCE,
                entityRoom.getZ() - SHOUT_DISTANCE,
                entityRoom.getZ() + SHOUT_DISTANCE);

        rooms = rooms.stream()
                .filter(r -> roomUtil.isWithinDistance(entityRoom, r, SHOUT_DISTANCE))
                .collect(Collectors.toList());

        List<Entity> contents = entityRepository.findByRoomIn(rooms);

        entityUtil.sendMessageToListeners(contents, entity, toZone);

        return output;
    }
}
