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

package com.emergentmud.core.command;

import com.emergentmud.core.model.EmoteMetadata;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.service.EntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Component
public class Emote {
    private static final Logger LOGGER = LoggerFactory.getLogger(Emote.class);

    private EntityRepository entityRepository;
    private EntityService entityService;

    @Inject
    public Emote(EntityRepository entityRepository,
                 EntityService entityService) {
        this.entityRepository = entityRepository;
        this.entityService = entityService;
    }

    public void execute(GameOutput output, EmoteMetadata metadata, Entity entity, String[] args) {
        Entity target = null;

        if (metadata.getToSelfUntargeted() == null
                || metadata.getToRoomUntargeted() == null
                || metadata.getToSelfWithTarget() == null
                || metadata.getToTarget() == null
                || metadata.getToRoomWithTarget() == null) {

            LOGGER.info("Emote '{}' is missing some fields and cannot be used.", metadata.getName());
            output.append("Huh?");
        } else {
            if (args.length > 0) {
                Optional<Entity> optionalTarget = entityRepository.findByXAndYAndZ(entity.getX(), entity.getY(), entity.getZ())
                        .stream()
                        .filter(e -> ("self".equals(args[0]) && e.getName().equals(entity.getName()))
                                || ("me".equals(args[0]) && e.getName().equals(entity.getName()))
                                || e.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                        .findFirst();

                if (optionalTarget.isPresent()) {
                    target = optionalTarget.get();
                } else {
                    output.append("You do not see anyone by that name here.");
                    return;
                }
            }

            if (target == null) {
                entityService.sendMessageToRoom(entity.getX(), entity.getY(), entity.getZ(), entity, new GameOutput(replaceVariables(metadata.getToRoomUntargeted(), entity, null)));
                output.append(replaceVariables(metadata.getToSelfUntargeted(), entity, null));
            } else if (entity.equals(target)) {
                if (metadata.getToSelfAsTarget() != null && metadata.getToRoomTargetingSelf() != null) {
                    List<Entity> others = entityRepository.findByXAndYAndZ(entity.getX(), entity.getY(), entity.getZ());

                    others.remove(entity);
                    others.remove(target);

                    entityService.sendMessageToListeners(others, new GameOutput(replaceVariables(metadata.getToRoomTargetingSelf(), entity, target)));
                    output.append(replaceVariables(metadata.getToSelfAsTarget(), entity, target));
                } else {
                    output.append("Sorry, this emote doesn't support targeting yourself.");
                }
            } else {
                List<Entity> others = entityRepository.findByXAndYAndZ(entity.getX(), entity.getY(), entity.getZ());

                others.remove(entity);
                others.remove(target);

                entityService.sendMessageToEntity(target, new GameOutput(replaceVariables(metadata.getToTarget(), entity, target)));
                entityService.sendMessageToListeners(others, new GameOutput(replaceVariables(metadata.getToRoomWithTarget(), entity, target)));
                output.append(replaceVariables(metadata.getToSelfWithTarget(), entity, target));
            }
        }
    }

    public String replaceVariables(String message, Entity self, Entity target) {
        if (message == null) {
            return null;
        }

        if (self.equals(target)) {
            message = message.replace("%self%", self.getName());
            message = message.replace("%target%", target.getName());
        } else {
            message = message.replace("%self%", self.getName());
            message = message.replace("%target%", (target == null ? "NULL" : target.getName()));
        }

        message = message.replace("%him%", self.getGender().getObject());
        message = message.replace("%selfpos%", self.getName() + "'s");
        message = message.replace("%targetpos%", (target == null ? "NULL" : target.getName()) + "'s");
        message = message.replace("%his%", self.getGender().getPossessive());
        message = message.replace("%he%", self.getGender().getSubject());
        message = message.replace("%himself%", self.getGender().getReflexive());
        message = message.replace("%hispos%", self.getGender().getPossessivePronoun());

        return StringUtils.capitalize(message);
    }
}
