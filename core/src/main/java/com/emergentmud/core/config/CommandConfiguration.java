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

package com.emergentmud.core.config;

import com.emergentmud.core.command.MoveCommand;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.repository.WorldManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import javax.inject.Inject;

@Configuration
public class CommandConfiguration {
    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private WorldManager worldManager;

    @Inject
    private EntityRepository entityRepository;

    @Inject
    private SimpMessagingTemplate simpMessagingTemplate;

    @Bean(name = "northCommand")
    public MoveCommand northCommand() {
        return new MoveCommand(0, 1, 0, "north", "south", applicationContext, worldManager, entityRepository, simpMessagingTemplate);
    }

    @Bean(name = "eastCommand")
    public MoveCommand eastCommand() {
        return new MoveCommand(1, 0, 0, "east", "west", applicationContext, worldManager, entityRepository, simpMessagingTemplate);
    }

    @Bean(name = "southCommand")
    public MoveCommand southCommand() {
        return new MoveCommand(0, -1, 0, "south", "north", applicationContext, worldManager, entityRepository, simpMessagingTemplate);
    }

    @Bean(name = "westCommand")
    public MoveCommand westCommand() {
        return new MoveCommand(-1, 0, 0, "west", "east", applicationContext, worldManager, entityRepository, simpMessagingTemplate);
    }
}
