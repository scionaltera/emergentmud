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
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Zone {
    @Id
    private String id;

    private Long topRightX;
    private Long topRightY;

    private Long bottomLeftX;
    private Long bottomLeftY;

    @DBRef
    private Biome biome;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Biome getBiome() {
        return biome;
    }

    public void setBiome(Biome biome) {
        this.biome = biome;
    }
}
