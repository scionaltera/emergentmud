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

package com.emergentmud.core.command.impl;

import com.emergentmud.core.command.BaseCommand;
import com.emergentmud.core.model.Direction;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.room.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.repository.RoomBuilder;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component
public class LookCommand extends BaseCommand {
    private EntityRepository entityRepository;
    private RoomBuilder roomBuilder;

    @Inject
    public LookCommand(EntityRepository entityRepository, RoomBuilder roomBuilder) {
        this.entityRepository = entityRepository;
        this.roomBuilder = roomBuilder;

        setDescription("Describes the things in the world around you.");
        addParameter("target", false);
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String command, String[] tokens, String raw) {
        String roomName;
        String roomDescription;
        Room room = roomBuilder.generateRoom(entity.getX(), entity.getY(), entity.getZ());

        if (room.getBiome() == null) {
            roomName = "No Biome";
        } else {
            roomName = room.getBiome().getName();
        }

        roomDescription = "A bleak, empty landscape stretches beyond the limits of your vision.";
        roomDescription += String.format("<br/>elevation=%d moisture=%d", room.getElevation(), room.getMoisture());

        if (room.getWater() != null) {
            roomDescription += String.format("<br/>[cyan]The water here is: %s", room.getWater().getFlowType());
        }

        output.append(String.format("[yellow]%s [dyellow](%d, %d, %d)",
                roomName,
                room.getX(),
                room.getY(),
                room.getZ()));
        output.append(String.format("[default]%s", roomDescription));

        StringBuilder exits = new StringBuilder("[dcyan]Exits:");

        Direction.DIRECTIONS.forEach(d -> {
            exits.append(" [cyan]");
            exits.append(d.getName());
        });

        output.append(exits.toString());

        List<Entity> contents = entityRepository.findByXAndYAndZ(room.getX(), room.getY(), room.getZ());

        contents.stream()
                .filter(content -> !content.getId().equals(entity.getId()))
                .forEach(content -> output.append("[green]" + content.getName() + " is here."));

        return output;
    }
}
