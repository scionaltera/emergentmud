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
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.repository.RoomRepository;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.util.List;

@Component
public class ShoutCommand implements Command {
    private SimpMessagingTemplate simpMessagingTemplate;
    private RoomRepository roomRepository;
    private EntityRepository entityRepository;

    @Inject
    public ShoutCommand(SimpMessagingTemplate simpMessagingTemplate,
                        RoomRepository roomRepository,
                        EntityRepository entityRepository) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.roomRepository = roomRepository;
        this.entityRepository = entityRepository;
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String[] tokens, String raw) {
        if (StringUtils.isEmpty(raw)) {
            output.append("What would you like to shout?");

            return output;
        }

        output.append(String.format("[dyellow]You shout '%s[dyellow]'", htmlEscape(raw)));

        GameOutput toZone = new GameOutput(String.format("[dyellow]%s shouts '%s[dyellow]'", entity.getName(), htmlEscape(raw)))
                .append("")
                .append("> ");

        Room entityRoom = entity.getRoom();
        List<Room> rooms = roomRepository.findByZone(entityRoom.getZone());
        List<Entity> contents = entityRepository.findByRoomIn(rooms);

        contents.stream()
                .filter(e -> !entity.getId().equals(e.getId()))
                .forEach(e -> {
                    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
                    headerAccessor.setSessionId(e.getStompSessionId());
                    headerAccessor.setLeaveMutable(true);

                    simpMessagingTemplate.convertAndSendToUser(e.getStompUsername(), "/queue/output", toZone, headerAccessor.getMessageHeaders());
                });

        return output;
    }

    private String htmlEscape(String input) {
        return input
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\\", "&#x2F;");
    }
}
