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
import com.emergentmud.core.model.Zone;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.RoomRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class MapCommand implements Command {
    private static final int MAP_EXTENT_X = 40;
    private static final int MAP_EXTENT_Y = 20;

    private RoomRepository roomRepository;

    @Inject
    public MapCommand(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String command, String[] tokens, String raw) {
        Room center = entity.getRoom();

        for (long y = center.getY() + MAP_EXTENT_Y, i = 0; y >= center.getY() - MAP_EXTENT_Y; y--, i++) {
            StringBuilder line = new StringBuilder();

            for (long x = center.getX() - MAP_EXTENT_X; x <= center.getX() + MAP_EXTENT_X; x++) {
                if (x == center.getX() && y == center.getY()) {
                    line.append("[cyan][]</span>");
                } else {
                    Room room = roomRepository.findByXAndYAndZ(x, y, center.getZ());

                    if (room != null) {
                        Zone zone = room.getZone();

                        line.append(String.format("<span style='color: #%02x%02x%02x'>[]</span>",
                                zone.getColor()[0],
                                zone.getColor()[1],
                                zone.getColor()[2]));
                    } else {
                        line.append(String.format("<span style='color: #%02x%02x%02x'>&nbsp;&nbsp;</span>", 0, 0, 0));
                    }
                }
            }

            if (i % 10 == 0) {
                line.append(String.format("  [yellow]%d", y));
            }

            output.append(line.toString());
        }

        StringBuilder line = new StringBuilder("[yellow]");
        int offset = 0;

        for (long x = center.getX() - MAP_EXTENT_X, i = 0; x <= center.getX() + MAP_EXTENT_X; x++, i++) {
            if (i % 10 == 0) {
                line.append(x + offset);

                int length = Long.toString(x).length();

                if (length == 1) {
                    line.append("&nbsp;");
                } else {
                    offset += Math.max(0, length - 2);
                }
            } else {
                if (offset >= 2) {
                    offset -= 2;
                } else if (offset == 1) {
                    offset--;
                    line.append("&nbsp;");
                } else {
                    line.append("&nbsp;&nbsp;");
                }
            }
        }

        output.append(line.toString());

        return output;
    }
}
