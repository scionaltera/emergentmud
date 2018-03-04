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

package com.emergentmud.core.repository;

import com.emergentmud.core.model.Account;
import com.emergentmud.core.model.Coordinate;
import com.emergentmud.core.model.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EntityRepository extends JpaRepository<Entity, UUID> {
    Entity findByAccountAndId(Account account, UUID id);
    Entity findByNameStartingWithIgnoreCase(String name);
    Entity findByNameStartingWithIgnoreCaseAndLocationIsNotNull(String name);
    Entity findByStompSessionIdAndStompUsername(String stompSessionId, String stompUsername);
    List<Entity> findByLocation(Coordinate location);
    List<Entity> findByLocationIsNotNull();
    List<Entity> findByLocationBetween(Coordinate from, Coordinate to);
    List<Entity> findByAccount(Account account);
    List<Entity> findByAccountIsNotNull();
}
