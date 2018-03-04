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

package com.emergentmud.core.command.impl;

import com.emergentmud.core.command.BaseCommand;
import com.emergentmud.core.model.Coordinate;
import com.emergentmud.core.model.Direction;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.service.RoomService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component
public class LookCommand extends BaseCommand {
    private EntityRepository entityRepository;
    private RoomService roomService;

    @Inject
    public LookCommand(EntityRepository entityRepository, RoomService roomService) {
        this.entityRepository = entityRepository;
        this.roomService = roomService;

        setDescription("Describes the things in the world around you.");
        addParameter("target", false);
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String command, String[] tokens, String raw) {
        if (entity.getLocation() == null) {
            output.append("[black]You are floating in a formless void.");

            return output;
        }

        String roomName;
        String roomDescription;
        Room room = roomService.fetchRoom(entity.getLocation().getX(), entity.getLocation().getY(), entity.getLocation().getZ());

        if (room == null) {
            output.append("[black]You are floating in a formless void.");

            return output;
        }

        roomName = "The Great Emptiness";
        roomDescription = "A bleak, empty landscape stretches beyond the limits of your vision.";

        if (room.getZone() != null) {
            if (room.getZone().getBiome() != null) {
                roomName = room.getZone().getBiome().getName();
                roomDescription = room.getZone().getBiome().getDescription(entity.getLocation());
            }
        }

        output.append(String.format("[yellow]%s [dyellow](%d, %d, %d)",
                roomName,
                room.getX(),
                room.getY(),
                room.getZ()));
        output.append(String.format("[default]%s", roomDescription));

        StringBuilder exits = new StringBuilder("[dcyan]Exits:");

        Direction.DIRECTIONS.forEach(d -> {
            long x = entity.getLocation().getX() + d.getX();
            long y = entity.getLocation().getY() + d.getY();
            long z = entity.getLocation().getZ() + d.getZ();

            Room neighbor = roomService.fetchRoom(x, y, z);

            if (neighbor != null) {
                exits.append(" [cyan]");
            } else if (!room.getZone().encompasses(x, y, z)) {
                exits.append(" [black]");
            } else {
                return;
            }

            exits.append(d.getName());
        });

        output.append(exits.toString());

        List<Entity> contents = entityRepository.findByLocation(new Coordinate(room.getX(), room.getY(), room.getZ()));

        contents.stream()
                .filter(content -> !content.getId().equals(entity.getId()))
                .forEach(content -> output.append("[green]" + content.getName() + " is here."));

        return output;
    }
}
