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

package com.emergentmud.core.repository;

import com.emergentmud.core.model.Account;
import com.emergentmud.core.model.Entity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntityRepository extends MongoRepository<Entity, String> {
    Entity findByAccountAndId(Account account, String id);
    Entity findByNameStartingWithIgnoreCase(String name);
    Entity findByNameStartingWithIgnoreCaseAndXIsNotNullAndYIsNotNullAndZIsNotNull(String name);
    Entity findByStompSessionIdAndStompUsername(String stompSessionId, String stompUsername);
    List<Entity> findByXAndYAndZ(Long x, Long y, Long z);
    List<Entity> findByXIsNotNullAndYIsNotNullAndZIsNotNull();
    List<Entity> findByXBetweenAndYBetweenAndZBetween(Long xFrom, Long xTo, Long yFrom, Long yTo, Long zFrom, Long zTo);
    List<Entity> findByAccount(Account account);
    List<Entity> findByAccountIsNotNull();
}
