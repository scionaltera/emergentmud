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

package com.emergentmud.core.command;

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.repository.RoomRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

@Component
public class LookCommand implements Command {
    private EntityRepository entityRepository;
    private RoomRepository roomRepository;

    @Inject
    public LookCommand(EntityRepository entityRepository, RoomRepository roomRepository) {
        this.entityRepository = entityRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String command, String[] tokens, String raw) {
        if (entity.getRoom() == null) {
            output.append("[black]You are floating in a formless void.");
        } else {
            String roomName;
            String roomDescription;

            roomName = "The Featureless Plains";
            roomDescription = "A bleak, empty landscape stretches beyond the limits of your vision.";

            output.append(String.format("[yellow]%s [dyellow](%d, %d, %d)",
                    roomName,
                    entity.getRoom().getX(),
                    entity.getRoom().getY(),
                    entity.getRoom().getZ()));
            output.append(String.format("[default]%s", roomDescription));

            StringBuilder exits = new StringBuilder("[dcyan]Exits:");
            Room room = entity.getRoom();

            if (roomRepository.findByXAndYAndZ(room.getX(), room.getY() + 1, room.getZ()) != null) {
                exits.append(" north");
            }

            if (roomRepository.findByXAndYAndZ(room.getX() + 1, room.getY(), room.getZ()) != null) {
                exits.append(" east");
            }

            if (roomRepository.findByXAndYAndZ(room.getX(), room.getY() - 1, room.getZ()) != null) {
                exits.append(" south");
            }

            if (roomRepository.findByXAndYAndZ(room.getX() - 1, room.getY(), room.getZ()) != null) {
                exits.append(" west");
            }

            output.append(exits.toString());

            List<Entity> contents = entityRepository.findByRoom(room);

            contents.stream()
                    .filter(content -> !content.getId().equals(entity.getId()))
                    .forEach(content -> output.append("[green]" + content.getName() + " is here."));
        }

        return output;
    }
}
