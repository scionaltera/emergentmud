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

package com.emergentmud.core.config;

import com.emergentmud.core.command.MoveCommand;
import com.emergentmud.core.model.Direction;
import com.emergentmud.core.repository.RoomBuilder;
import com.emergentmud.core.repository.WorldManager;
import com.emergentmud.core.util.EntityUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
public class CommandConfiguration {
    @Inject
    private ApplicationContext applicationContext;

    @Inject
    private RoomBuilder roomBuilder;

    @Inject
    private WorldManager worldManager;

    @Inject
    private EntityUtil entityUtil;

    @Bean(name = "northCommand")
    public MoveCommand northCommand() {
        return new MoveCommand(Direction.NORTH, applicationContext, worldManager, roomBuilder, entityUtil);
    }

    @Bean(name = "eastCommand")
    public MoveCommand eastCommand() {
        return new MoveCommand(Direction.EAST, applicationContext, worldManager, roomBuilder, entityUtil);
    }

    @Bean(name = "southCommand")
    public MoveCommand southCommand() {
        return new MoveCommand(Direction.SOUTH, applicationContext, worldManager, roomBuilder, entityUtil);
    }

    @Bean(name = "westCommand")
    public MoveCommand westCommand() {
        return new MoveCommand(Direction.WEST, applicationContext, worldManager, roomBuilder, entityUtil);
    }
}
