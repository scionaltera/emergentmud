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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Zone {
    @Id
    @GeneratedValue
    @Type(type = "pg-uuid")
    private UUID id;

    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "top_right_x")),
            @AttributeOverride(name = "y", column = @Column(name = "top_right_y")),
            @AttributeOverride(name = "z", column = @Column(name = "top_right_z"))
    })
    private Coordinate topRight;

    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "bottom_left_x")),
            @AttributeOverride(name = "y", column = @Column(name = "bottom_left_y")),
            @AttributeOverride(name = "z", column = @Column(name = "bottom_left_z"))
    })
    private Coordinate bottomLeft;

    private Integer elevation;
    private Integer moisture;

    @ManyToOne
    private Biome biome;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Coordinate getTopRight() {
        return topRight;
    }

    public void setTopRight(Coordinate topRight) {
        this.topRight = topRight;
    }

    public Coordinate getBottomLeft() {
        return bottomLeft;
    }

    public void setBottomLeft(Coordinate bottomLeft) {
        this.bottomLeft = bottomLeft;
    }

    public Integer getElevation() {
        return elevation;
    }

    public void setElevation(Integer elevation) {
        this.elevation = elevation;
    }

    public Integer getMoisture() {
        return moisture;
    }

    public void setMoisture(Integer moisture) {
        this.moisture = moisture;
    }

    public Biome getBiome() {
        return biome;
    }

    public void setBiome(Biome biome) {
        this.biome = biome;
    }

    public boolean encompasses(Coordinate location) {
        return location.getX() >= getBottomLeft().getX() && location.getX() <= getTopRight().getX() && location.getY() >= getBottomLeft().getY() && location.getY() <= getTopRight().getY();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zone zone = (Zone) o;
        return Objects.equals(getId(), zone.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
