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

package com.emergentmud.core.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@CompoundIndexes({
        @CompoundIndex(name = "room_idx", def = "{'x': 1, 'y': 1, 'z': 1}")
})
public class Room {
    private static final Logger LOGGER = LoggerFactory.getLogger(Room.class);

    @Id
    private String id;

    @DBRef
    private Zone zone;

    private Long x;
    private Long y;
    private Long z;
    private Integer[] color;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Zone getZone() {
        return zone;
    }

    public void setZone(Zone zone) {
        this.zone = zone;
    }

    public Long getX() {
        return x;
    }

    public void setX(Long x) {
        this.x = x;
    }

    public Long getY() {
        return y;
    }

    public void setY(Long y) {
        this.y = y;
    }

    public Long getZ() {
        return z;
    }

    public void setZ(Long z) {
        this.z = z;
    }

    public Integer[] getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = new Integer[4];

        // read an ARGB color and store it as RGBA
        this.color[0] = (color >>> 16) & 0xFF; // 0x00FF0000 is red
        this.color[1] = (color >>> 8) & 0xFF;  // 0x0000FF00 is green
        this.color[2] = color & 0xFF;          // 0x000000FF is blue
        this.color[3] = (color >>> 24) & 0xFF; // 0xFF000000 is alpha

//        LOGGER.info("{} -> {} {} {} {}",
//                Integer.toHexString(color),
//                Integer.toHexString(this.color[0]),
//                Integer.toHexString(this.color[1]),
//                Integer.toHexString(this.color[2]),
//                Integer.toHexString(this.color[3]));
    }

    public void setColor(Integer[] color) {
        this.color = color;
    }
}
