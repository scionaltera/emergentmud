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
import org.springframework.stereotype.Component;

@Component
public class MapCommand implements Command {
    private static final int MAP_EXTENT = 20;

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String[] tokens, String raw) {
        for (long y = entity.getY() + MAP_EXTENT; y > entity.getY() - MAP_EXTENT; y--) {
            StringBuilder line = new StringBuilder();

            for (long x = entity.getX() - MAP_EXTENT; x < entity.getY() + MAP_EXTENT; x++) {
                if (x == entity.getX() && y == entity.getY()) {
                    line.append("[cyan]");
                } else {
                    line.append("[dwhite]");
                }

                line.append("[]");
            }

            line.append(String.format("  [yellow]Y: %d", y));

            output.append(line.toString());
        }

        output.append(String.format("[yellow]X: %d - %d", entity.getX() - MAP_EXTENT, entity.getX() + MAP_EXTENT));

        return output;
    }
}
