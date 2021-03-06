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

import com.emergentmud.core.model.Zone;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ZoneRepository extends CrudRepository<Zone, UUID> {
    @Query("select zone from Zone zone where zone.topRight.x >= :x and zone.topRight.y >= :y and zone.bottomLeft.x <= :x and zone.bottomLeft.y <= :y")
    Zone findZoneAtPoint(@Param("x") Long x, @Param("y") Long y);

    @Query("select zone from Zone zone where zone.bottomLeft.x <= :topRightX and zone.topRight.x >= :bottomLeftX and zone.topRight.y >= :bottomLeftY and zone.bottomLeft.y <= :topRightY")
    List<Zone> findZonesWithin(
            @Param("topRightX") Long topRightX,
            @Param("topRightY") Long topRightY,
            @Param("bottomLeftX") Long bottomLeftX,
            @Param("bottomLeftY") Long bottomLeftY);
}
