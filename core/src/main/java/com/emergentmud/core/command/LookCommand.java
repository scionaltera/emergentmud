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
import java.util.List;

@Component
public class LookCommand implements Command {
    private EntityRepository entityRepository;

    @Inject
    public LookCommand(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String[] tokens, String raw) {
        if (entity.getX() == null || entity.getY() == null || entity.getZ() == null) {
            output.append("[black]You are floating in a formless void.");
        } else {
            output.append(String.format("[yellow]The Featureless Plains [dyellow](%d, %d, %d)", entity.getX(), entity.getY(), entity.getZ()));
            output.append("[default]A bleak, empty landscape stretches beyond the limits of your vision.");
            output.append("[dcyan]Exits: north east south west");

            List<Entity> contents = entityRepository.findByXAndYAndZ(entity.getX(), entity.getY(), entity.getZ());

            contents.stream()
                    .filter(content -> !content.getId().equals(entity.getId()))
                    .forEach(content -> output.append("[green]" + content.getName() + " is here."));
        }

        return output;
    }
}
