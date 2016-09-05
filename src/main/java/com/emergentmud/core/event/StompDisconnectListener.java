/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016 Peter Keeler
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

package com.emergentmud.core.event;

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.repository.WorldManager;
import com.emergentmud.core.util.EntityUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import javax.inject.Inject;

@Component
public class StompDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {
    private EntityRepository entityRepository;
    private WorldManager worldManager;
    private EntityUtil entityUtil;

    @Inject
    public StompDisconnectListener(EntityRepository entityRepository,
                                   WorldManager worldManager,
                                   EntityUtil entityUtil) {
        this.entityRepository = entityRepository;
        this.worldManager = worldManager;
        this.entityUtil = entityUtil;
    }

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        Entity entity = entityRepository.findByStompSessionIdAndStompUsername(event.getSessionId(), event.getUser().getName());

        if (entity != null) {
            if (entity.getRoom() != null) {
                GameOutput enterMessage = new GameOutput(String.format("[yellow]%s has left the game.", entity.getName()))
                        .append("")
                        .append("> ");

                entityUtil.sendMessageToRoom(entity.getRoom(), entity, enterMessage);
            }

            worldManager.remove(entity);
        }
    }
}
