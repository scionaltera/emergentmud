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

import org.hibernate.annotations.Type;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;
import java.util.UUID;

@Entity
public class Biome {
    @Id
    @GeneratedValue
    @Type(type = "pg-uuid")
    private UUID id;

    private String name;
    private Integer color;
    private String cellSelectionStrategy;

    // This collection gets populated by Hibernate and is not intended to be changed at runtime.
    // Maybe at some point in the future there will be a command to edit descriptions from inside the game.
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> description;

    public Biome() {
        // this method intentionally left blank
    }

    public Biome(String name, Integer color, String cellSelectionStrategy) {
        this.name = name;
        this.color = color;
        this.cellSelectionStrategy = cellSelectionStrategy;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription(Coordinate location) {
        return description.get((int) Math.abs((location.getX() + location.getY() + location.getZ())) % description.size());
    }

    public Integer getColor() {
        return color;
    }

    public String getCellSelectionStrategy() {
        return cellSelectionStrategy;
    }
}
