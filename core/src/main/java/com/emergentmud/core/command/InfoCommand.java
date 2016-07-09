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
public class InfoCommand implements Command {

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String[] tokens, String raw) {
        output.append("[cyan][ [dcyan]Entity ([cyan]" + entity.getId() + "[dcyan]) [cyan]]");
        output.append("[dcyan]Name: [cyan]" + entity.getName());
        output.append("[dcyan]Admin: " + entity.isAdmin());
        output.append("[dcyan]Social Username: [cyan]" + entity.getStompUsername());
        output.append("[dcyan]STOMP Session ID: [cyan]" + entity.getStompSessionId());

        return output;
    }
}
