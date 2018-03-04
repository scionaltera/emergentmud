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

package com.emergentmud.core.event;

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.service.EntityService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component
public class GameShutdownListener implements ApplicationListener<ApplicationContextEvent> {
    private EntityRepository entityRepository;
    private EntityService entityService;

    @Inject
    public GameShutdownListener(EntityRepository entityRepository, EntityService entityService) {
        this.entityRepository = entityRepository;
        this.entityService = entityService;
    }

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        if (event instanceof ContextClosedEvent || event instanceof ContextStoppedEvent) {
            List<Entity> everyone = entityRepository.findByLocationIsNotNull();
            GameOutput output = new GameOutput("[red]EmergentMUD is shutting down. Please check back later!");

            entityService.sendMessageToListeners(everyone, output);
        }
    }
}
