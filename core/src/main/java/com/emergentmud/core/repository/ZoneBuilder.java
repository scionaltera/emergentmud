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

import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.Zone;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class ZoneBuilder {
    private ZoneRepository zoneRepository;
    private RoomRepository roomRepository;

    @Inject
    public ZoneBuilder(ZoneRepository zoneRepository, RoomRepository roomRepository) {
        this.zoneRepository = zoneRepository;
        this.roomRepository = roomRepository;
    }

    public Zone build(Long x, Long y, Long z) {
        Zone zone = new Zone();
        zone = zoneRepository.save(zone);

        Room room = new Room();
        room.setX(x);
        room.setY(y);
        room.setZ(z);
        room.setZone(zone);
        roomRepository.save(room);

        return zone;
    }
}
