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

package com.emergentmud.core.util;

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component
public class EntityUtil {
    private EntityRepository entityRepository;
    private SimpMessagingTemplate simpMessagingTemplate;

    @Inject
    public EntityUtil(EntityRepository entityRepository,
                      SimpMessagingTemplate simpMessagingTemplate) {
        this.entityRepository = entityRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    public void sendMessageToEntity(Entity entity, GameOutput message) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        headerAccessor.setSessionId(entity.getStompSessionId());
        headerAccessor.setLeaveMutable(true);

        simpMessagingTemplate.convertAndSendToUser(entity.getStompUsername(), "/queue/output", message, headerAccessor.getMessageHeaders());
    }

    public void sendMessageToRoom(Room room, Entity entity, GameOutput message) {
        entityRepository.findByRoom(room)
                .stream()
                .filter(e -> !e.equals(entity))
                .forEach(e -> {
                    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
                    headerAccessor.setSessionId(e.getStompSessionId());
                    headerAccessor.setLeaveMutable(true);

                    simpMessagingTemplate.convertAndSendToUser(e.getStompUsername(), "/queue/output", message, headerAccessor.getMessageHeaders());
                });
    }


    public void sendMessageToListeners(List<Entity> targets, GameOutput message) {
        targets.forEach(e -> {
                    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
                    headerAccessor.setSessionId(e.getStompSessionId());
                    headerAccessor.setLeaveMutable(true);

                    simpMessagingTemplate.convertAndSendToUser(e.getStompUsername(), "/queue/output", message, headerAccessor.getMessageHeaders());
                });
    }

    public void sendMessageToListeners(List<Entity> targets, Entity source, GameOutput message) {
        targets.stream()
                .filter(e -> !source.getId().equals(e.getId()))
                .forEach(e -> {
                    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
                    headerAccessor.setSessionId(e.getStompSessionId());
                    headerAccessor.setLeaveMutable(true);

                    simpMessagingTemplate.convertAndSendToUser(e.getStompUsername(), "/queue/output", message, headerAccessor.getMessageHeaders());
                });
    }
}
