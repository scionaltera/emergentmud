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

    private String toSelfUntargeted;
    private String toRoomUntargeted;
    private String toSelfWithTarget;
    private String toTarget;
    private String toRoomWithTarget;
    private String toSelfAsTarget;
    private String toRoomTargetingSelf;

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

    public String getToSelfUntargeted() {
        return toSelfUntargeted;
    }

    public void setToSelfUntargeted(String toSelfUntargeted) {
        this.toSelfUntargeted = toSelfUntargeted;
    }

    public String getToRoomUntargeted() {
        return toRoomUntargeted;
    }

    public void setToRoomUntargeted(String toRoomUntargeted) {
        this.toRoomUntargeted = toRoomUntargeted;
    }

    public String getToSelfWithTarget() {
        return toSelfWithTarget;
    }

    public void setToSelfWithTarget(String toSelfWithTarget) {
        this.toSelfWithTarget = toSelfWithTarget;
    }

    public String getToTarget() {
        return toTarget;
    }

    public void setToTarget(String toTarget) {
        this.toTarget = toTarget;
    }

    public String getToRoomWithTarget() {
        return toRoomWithTarget;
    }

    public void setToRoomWithTarget(String toRoomWithTarget) {
        this.toRoomWithTarget = toRoomWithTarget;
    }

    public String getToSelfAsTarget() {
        return toSelfAsTarget;
    }

    public void setToSelfAsTarget(String toSelfAsTarget) {
        this.toSelfAsTarget = toSelfAsTarget;
    }

    public String getToRoomTargetingSelf() {
        return toRoomTargetingSelf;
    }

    public void setToRoomTargetingSelf(String toRoomTargetingSelf) {
        this.toRoomTargetingSelf = toRoomTargetingSelf;
    }
}
