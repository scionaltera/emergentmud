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

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.*;

public class RoomTest {
    private Room room = new Room();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testId() throws Exception {
        UUID uuid = UUID.randomUUID();

        room.setId(uuid);

        assertEquals(uuid, room.getId());
    }

    @Test
    public void testX() throws Exception {
        room.setX(99L);

        assertEquals(99L, (long)room.getX());
    }

    @Test
    public void testY() throws Exception {
        room.setY(98L);

        assertEquals(98L, (long)room.getY());
    }

    @Test
    public void testZ() throws Exception {
        room.setZ(97L);

        assertEquals(97L, (long)room.getZ());
    }
}
