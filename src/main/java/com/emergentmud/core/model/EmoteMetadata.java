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

package com.emergentmud.core.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class EmoteMetadata {
    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    @Indexed
    private Integer priority;

    private String toSelf;
    private String toTarget;
    private String toRoom;

    public EmoteMetadata() {
        // this method intentionally left blank
    }

    public EmoteMetadata(String name, Integer priority) {
        setName(name);
        setPriority(priority);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getToSelf() {
        return toSelf;
    }

    public void setToSelf(String toSelf) {
        this.toSelf = toSelf;
    }

    public String getToTarget() {
        return toTarget;
    }

    public void setToTarget(String toTarget) {
        this.toTarget = toTarget;
    }

    public String getToRoom() {
        return toRoom;
    }

    public void setToRoom(String toRoom) {
        this.toRoom = toRoom;
    }
}
