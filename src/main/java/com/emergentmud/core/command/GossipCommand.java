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
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.util.EntityUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.util.List;

@Component
public class GossipCommand extends BaseCommunicationCommand implements Command {
    @Inject
    public GossipCommand(EntityRepository entityRepository,
                         EntityUtil entityUtil) {
        this.entityRepository = entityRepository;
        this.entityUtil = entityUtil;

        setDescription("Send a message to all other players.");
        addParameter("message", true);
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String command, String[] tokens, String raw) {
        if (StringUtils.isEmpty(raw)) {
            output.append("What would you like to gossip?");

            return output;
        }

        output.append(String.format("[green]You gossip '%s[green]'", htmlEscape(raw)));

        GameOutput toRoom = new GameOutput(String.format("[green]%s gossips '%s[green]'", entity.getName(), htmlEscape(raw)));

        List<Entity> contents = entityRepository.findByRoomIsNotNull();

        entityUtil.sendMessageToListeners(contents, entity, toRoom);

        return output;
    }
}
