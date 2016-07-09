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
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

public abstract class BaseCommunicationCommand {
    protected SimpMessagingTemplate simpMessagingTemplate;
    protected EntityRepository entityRepository;

    protected void sendMessageToListeners(List<Entity> targets, Entity source, GameOutput message) {
        targets.stream()
                .filter(e -> !source.getId().equals(e.getId()))
                .forEach(e -> {
                    SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
                    headerAccessor.setSessionId(e.getStompSessionId());
                    headerAccessor.setLeaveMutable(true);

                    simpMessagingTemplate.convertAndSendToUser(e.getStompUsername(), "/queue/output", message, headerAccessor.getMessageHeaders());
                });
    }

    protected String htmlEscape(String input) {
        return input
                .replace("&", "&amp;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\\", "&#x2F;");
    }
}
