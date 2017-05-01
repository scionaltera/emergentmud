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
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

@Component
public class InfoCommand extends BaseCommand {
    private EntityRepository entityRepository;

    @Inject
    public InfoCommand(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;

        setDescription("Display information about a thing in the game.");
        addParameter("target", false);
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String command, String[] tokens, String raw) {
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
                target = entityRepository.findByNameStartingWithIgnoreCase(tokens[0]);

                if (target == null) {
                    output.append("[yellow]There is nothing by that name here.");

                    return output;
                }
            }
        } else {
            usage(output, command);

            return output;
        }

        String location = "[black]Void";

        if (target.getRoom() != null) {
            location = String.format("[dcyan]([cyan]%d[dcyan], [cyan]%d[dcyan], [cyan]%d[dcyan])",
                    target.getRoom().getX(),
                    target.getRoom().getY(),
                    target.getRoom().getZ());
        }

        output.append("[cyan][ [dcyan]Entity ([cyan]" + target.getId() + "[dcyan]) [cyan]]");
        output.append("[dcyan]Name: [cyan]" + target.getName());
        output.append("[dcyan]Location: " + location);

        if (entity.getAccount() != null) {
            output.append("[dcyan]Account Capabilities: [cyan]" + target.getAccount().getCapabilities());
        }

        output.append("[dcyan]Entity Capabilities: [cyan]" + target.getCapabilities());
        output.append("[dcyan]Social Username: [cyan]" + target.getStompUsername());
        output.append("[dcyan]STOMP Session ID: [cyan]" + target.getStompSessionId());
        output.append("[dcyan]Remote Address: [cyan]" + target.getRemoteAddr());
        output.append("[dcyan]User Agent: [cyan]" + target.getUserAgent());

        return output;
    }
}
