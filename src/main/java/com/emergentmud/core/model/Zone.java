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

    private Long topRightX;
    private Long topRightY;

    private Long bottomLeftX;
    private Long bottomLeftY;

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

    public Long getTopRightX() {
        return topRightX;
    }

    public void setTopRightX(Long topRightX) {
        this.topRightX = topRightX;
    }

    public Long getTopRightY() {
        return topRightY;
    }

    public void setTopRightY(Long topRightY) {
        this.topRightY = topRightY;
    }

    public Long getBottomLeftX() {
        return bottomLeftX;
    }

    public void setBottomLeftX(Long bottomLeftX) {
        this.bottomLeftX = bottomLeftX;
    }

    public Long getBottomLeftY() {
        return bottomLeftY;
    }

    public void setBottomLeftY(Long bottomLeftY) {
        this.bottomLeftY = bottomLeftY;
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

    public boolean encompasses(long x, long y, long z) {
        return x >= getBottomLeftX() && x <= getTopRightX() && y >= getBottomLeftY() && y <= getTopRightY();
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
