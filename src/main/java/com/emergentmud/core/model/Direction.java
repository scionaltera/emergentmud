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

package com.emergentmud.core.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Document
public class Direction {
    public static final Direction NORTH = new Direction("north", "south", 0, 1, 0);
    public static final Direction EAST = new Direction("east", "west", 1, 0, 0);
    public static final Direction SOUTH = new Direction("south", "north", 0, -1, 0);
    public static final Direction WEST = new Direction("west", "east", -1, 0, 0);
    public static final List<Direction> DIRECTIONS = Collections.unmodifiableList(Arrays.asList(NORTH, EAST, SOUTH, WEST));

    private String name;
    private String opposite;
    private long x;
    private long y;
    private long z;

    public static Direction forName(String name) {
        return DIRECTIONS.stream().filter(d -> d.getName().equals(name)).findFirst().orElse(null);
    }

    private Direction(String name, String opposite, long x, long y, long z) {
        this.name = name;
        this.opposite = opposite;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getName() {
        return name;
    }

    public String getOpposite() {
        return opposite;
    }

    public long getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    public long getZ() {
        return z;
    }
}
