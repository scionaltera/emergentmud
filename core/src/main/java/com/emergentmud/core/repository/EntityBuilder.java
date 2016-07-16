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

package com.emergentmud.core.repository;

import com.emergentmud.core.model.Entity;

public class EntityBuilder {
    private Entity entity = new Entity();

    public EntityBuilder() {
        entity = new Entity();
    }

    EntityBuilder(Entity entity) {
        this.entity = entity;
    }

    public EntityBuilder withId(String id) {
        entity.setId(id);
        return this;
    }

    public EntityBuilder withName(String name) {
        entity.setName(name);
        return this;
    }

    public EntityBuilder withAdmin(boolean admin) {
        entity.setAdmin(admin);
        return this;
    }

    public Entity build() {
        return entity;
    }
}
