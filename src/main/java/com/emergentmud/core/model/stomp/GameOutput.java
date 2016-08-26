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

package com.emergentmud.core.model.stomp;

import java.util.ArrayList;
import java.util.List;

public class GameOutput {
    private List<String> output = new ArrayList<>();

    public GameOutput() {}

    public GameOutput(String... messages) {
        for (String message : messages) {
            append(message);
        }
    }

    public GameOutput append(String message) {
        output.add(message);

        return this;
    }

    public List<String> getOutput() {
        return output;
    }
}
