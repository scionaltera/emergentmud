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
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.service.EntityService;
import com.emergentmud.core.service.RoomService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShoutCommand extends BaseCommunicationCommand implements Command {
    static final long SHOUT_DISTANCE = 7;

    private RoomService roomService;

    @Inject
    public ShoutCommand(EntityRepository entityRepository,
                        RoomService roomService,
                        EntityService entityService) {
        this.roomService = roomService;
        this.entityRepository = entityRepository;
        this.entityService = entityService;

        setDescription("Send a message to those within a few rooms of you.");
        addParameter("message", true);
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String command, String[] tokens, String raw) {
        if (StringUtils.isEmpty(raw)) {
            output.append("What would you like to shout?");

            return output;
        }

        output.append(String.format("[dyellow]You shout '%s[dyellow]'", HtmlUtils.htmlEscape(raw)));

        GameOutput toZone = new GameOutput(String.format("[dyellow]%s shouts '%s[dyellow]'", entity.getName(), HtmlUtils.htmlEscape(raw)));
        List<Entity> contents = entityRepository.findByXBetweenAndYBetweenAndZBetween(
                entity.getX() - SHOUT_DISTANCE, entity.getX() + SHOUT_DISTANCE,
                entity.getY() - SHOUT_DISTANCE, entity.getY() + SHOUT_DISTANCE,
                entity.getZ() - SHOUT_DISTANCE, entity.getZ() + SHOUT_DISTANCE
        );

        contents = contents.stream()
                .filter(r -> roomService.isWithinDistance(entity, r.getX(), r.getY(), r.getZ(), SHOUT_DISTANCE))
                .collect(Collectors.toList());

        entityService.sendMessageToListeners(contents, entity, toZone);

        return output;
    }
}
