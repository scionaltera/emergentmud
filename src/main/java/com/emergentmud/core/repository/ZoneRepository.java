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

import com.emergentmud.core.model.Zone;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ZoneRepository extends MongoRepository<Zone, String> {
    Zone findZoneByBottomLeftXLessThanEqualAndTopRightXGreaterThanEqualAndBottomLeftYLessThanEqualAndTopRightYGreaterThanEqual(Long x1, Long x2, Long y1, Long y2);
    List<Zone> findZonesByBottomLeftXLessThanEqualAndTopRightXGreaterThanEqualAndBottomLeftYLessThanEqualAndTopRightYGreaterThanEqual(Long x1, Long x2, Long y1, Long y2);
}
