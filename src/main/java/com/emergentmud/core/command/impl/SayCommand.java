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
import com.emergentmud.core.service.EntityService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

import javax.inject.Inject;

@Component
public class SayCommand extends BaseCommunicationCommand implements Command {
    @Inject
    public SayCommand(EntityService entityService) {
        this.entityService = entityService;

        setDescription("Send a message to others in the same room.");
        addParameter("message", true);
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String command, String[] tokens, String raw) {
        if (StringUtils.isEmpty(raw)) {
            output.append("What would you like to say?");

            return output;
        }

        output.append(String.format("[cyan]You say '%s[cyan]'", HtmlUtils.htmlEscape(raw)));

        GameOutput toRoom = new GameOutput(String.format("[cyan]%s says '%s[cyan]'", entity.getName(), HtmlUtils.htmlEscape(raw)));

        entityService.sendMessageToRoom(entity.getX(), entity.getY(), entity.getZ(), entity, toRoom);

        return output;
    }
}
