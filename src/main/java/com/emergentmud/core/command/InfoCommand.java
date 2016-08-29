/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016 BoneVM, LLC
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
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

@Component
public class InfoCommand implements Command {
    private EntityRepository entityRepository;

    @Inject
    public InfoCommand(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String[] tokens, String raw) {
        Entity target;

        if (tokens.length == 0) {
            target = entity;
        } else if (tokens.length == 1) {
            Optional<Entity> optional = entityRepository.findByRoom(entity.getRoom())
                    .stream()
                    .filter(t -> t.getName().toLowerCase().startsWith(tokens[0]))
                    .findFirst();

            if (optional.isPresent()) {
                target = optional.get();
            } else {
                output.append("[yellow]There is nothing by that name here.");

                return output;
            }
        } else {
            output.append("Usage: INFO [target]");
            output.append("If target is omitted, the command will display information about you.");

            return output;
        }

        output.append("[cyan][ [dcyan]Entity ([cyan]" + target.getId() + "[dcyan]) [cyan]]");
        output.append("[dcyan]Name: [cyan]" + target.getName());
        output.append("[dcyan]Admin: " + target.isAdmin());
        output.append("[dcyan]Social Username: [cyan]" + target.getStompUsername());
        output.append("[dcyan]STOMP Session ID: [cyan]" + target.getStompSessionId());

        return output;
    }
}
