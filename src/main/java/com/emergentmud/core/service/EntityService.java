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

package com.emergentmud.core.service;

import com.emergentmud.core.command.PromptBuilder;
import com.emergentmud.core.model.Coordinate;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.repository.RoomRepository;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class EntityService {
    private EntityRepository entityRepository;
    private RoomRepository roomRepository;
    private SimpMessagingTemplate simpMessagingTemplate;
    private PromptBuilder promptBuilder;

    @Inject
    public EntityService(EntityRepository entityRepository,
                         RoomRepository roomRepository,
                         SimpMessagingTemplate simpMessagingTemplate,
                         PromptBuilder promptBuilder) {
        this.entityRepository = entityRepository;
        this.roomRepository = roomRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.promptBuilder = promptBuilder;
    }

    public void sendMessageToEntity(Entity entity, GameOutput message) {
        promptBuilder.appendPrompt(message);

        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        headerAccessor.setSessionId(entity.getStompSessionId());
        headerAccessor.setLeaveMutable(true);

        simpMessagingTemplate.convertAndSendToUser(entity.getStompUsername(), "/queue/output", message, headerAccessor.getMessageHeaders());
    }

    public void sendMessageToRoom(Entity entity, GameOutput message) {
        sendMessageToRoom(entity.getLocation(), entity, message);
    }

    public void sendMessageToRoom(Coordinate location, Entity entity, GameOutput message) {
        Room room = roomRepository.findByXAndYAndZ(location.getX(), location.getY(), location.getZ());

        sendMessageToRoom(room, entity, message);
    }

    public void sendMessageToRoom(Room room, Entity entity, GameOutput message) {
        promptBuilder.appendPrompt(message);

        entityRepository.findByLocation(new Coordinate(room.getX(), room.getY(), room.getZ()))
                .stream()
                .filter(e -> !e.equals(entity))
                .forEach(e -> {
                    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
                    headerAccessor.setSessionId(e.getStompSessionId());
                    headerAccessor.setLeaveMutable(true);

                    simpMessagingTemplate.convertAndSendToUser(e.getStompUsername(), "/queue/output", message, headerAccessor.getMessageHeaders());
                });
    }

    public void sendMessageToRoom(Coordinate location, Collection<Entity> exclude, GameOutput message) {
        Room room = roomRepository.findByXAndYAndZ(location.getX(), location.getY(), location.getZ());

        sendMessageToRoom(room, exclude, message);
    }

    public void sendMessageToRoom(Room room, Collection<Entity> exclude, GameOutput message) {
        promptBuilder.appendPrompt(message);

        entityRepository.findByLocation(new Coordinate(room.getX(), room.getY(), room.getZ()))
                .stream()
                .filter(e -> !exclude.contains(e))
                .forEach(e -> {
                    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
                    headerAccessor.setSessionId(e.getStompSessionId());
                    headerAccessor.setLeaveMutable(true);

                    simpMessagingTemplate.convertAndSendToUser(e.getStompUsername(), "/queue/output", message, headerAccessor.getMessageHeaders());
                });
    }


    public void sendMessageToListeners(List<Entity> targets, GameOutput message) {
        promptBuilder.appendPrompt(message);

        targets.forEach(e -> {
                    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
                    headerAccessor.setSessionId(e.getStompSessionId());
                    headerAccessor.setLeaveMutable(true);

                    simpMessagingTemplate.convertAndSendToUser(e.getStompUsername(), "/queue/output", message, headerAccessor.getMessageHeaders());
                });
    }

    public void sendMessageToListeners(List<Entity> targets, Entity source, GameOutput message) {
        promptBuilder.appendPrompt(message);

        targets.stream()
                .filter(e -> !source.getId().equals(e.getId()))
                .forEach(e -> {
                    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
                    headerAccessor.setSessionId(e.getStompSessionId());
                    headerAccessor.setLeaveMutable(true);

                    simpMessagingTemplate.convertAndSendToUser(e.getStompUsername(), "/queue/output", message, headerAccessor.getMessageHeaders());
                });
    }

    public Optional<Entity> entitySearchRoom(Entity entity, String name) {
        return entityRepository.findByLocation(entity.getLocation())
                .stream()
                .filter(t -> t.getName().toLowerCase().startsWith(name.toLowerCase()))
                .findFirst();
    }

    public Optional<Entity> entitySearchInWorld(Entity entity, String name) {
        Optional<Entity> entityOptional = entitySearchRoom(entity, name);

        if (entityOptional.isPresent()) {
            return entityOptional;
        }

        return Optional.ofNullable(entityRepository.findByNameStartingWithIgnoreCaseAndLocationIsNotNull(name));
    }

    public Optional<Entity> entitySearchGlobal(Entity entity, String name) {
        Optional<Entity> entityOptional = entitySearchInWorld(entity, name);

        if (entityOptional.isPresent()) {
            return entityOptional;
        }

        return Optional.ofNullable(entityRepository.findByNameStartingWithIgnoreCase(name));
    }
}
