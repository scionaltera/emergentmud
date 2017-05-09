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

package com.emergentmud.core.util;

import com.emergentmud.core.model.Room;
import com.emergentmud.core.service.RoomService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RoomServiceTest {
    private RoomService roomService;

    @Mock
    private Room origin;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        roomService = new RoomService();
    }

    @Test
    public void testXSimpleDistance() throws Exception {
        Room near = mock(Room.class);

        when(near.getX()).thenReturn(2L);

        assertTrue(roomService.isWithinDistance(origin, near, 3));
        assertTrue(roomService.isWithinDistance(origin, near, 2));
        assertFalse(roomService.isWithinDistance(origin, near, 1));
    }

    @Test
    public void testYSimpleDistance() throws Exception {
        Room near = mock(Room.class);

        when(near.getY()).thenReturn(2L);

        assertTrue(roomService.isWithinDistance(origin, near, 3));
        assertTrue(roomService.isWithinDistance(origin, near, 2));
        assertFalse(roomService.isWithinDistance(origin, near, 1));
    }

    @Test
    public void testZSimpleDistance() throws Exception {
        Room near = mock(Room.class);

        when(near.getZ()).thenReturn(2L);

        assertTrue(roomService.isWithinDistance(origin, near, 3));
        assertTrue(roomService.isWithinDistance(origin, near, 2));
        assertFalse(roomService.isWithinDistance(origin, near, 1));
    }

    @Test
    public void testXShiftedDistance() throws Exception {
        Room near = mock(Room.class);

        when(origin.getX()).thenReturn(10L);
        when(near.getX()).thenReturn(12L);

        assertTrue(roomService.isWithinDistance(origin, near, 3));
        assertTrue(roomService.isWithinDistance(origin, near, 2));
        assertFalse(roomService.isWithinDistance(origin, near, 1));
    }

    @Test
    public void testYShiftedDistance() throws Exception {
        Room near = mock(Room.class);

        when(origin.getY()).thenReturn(10L);
        when(near.getY()).thenReturn(12L);

        assertTrue(roomService.isWithinDistance(origin, near, 3));
        assertTrue(roomService.isWithinDistance(origin, near, 2));
        assertFalse(roomService.isWithinDistance(origin, near, 1));
    }

    @Test
    public void testZShiftedDistance() throws Exception {
        Room near = mock(Room.class);

        when(origin.getZ()).thenReturn(10L);
        when(near.getZ()).thenReturn(12L);

        assertTrue(roomService.isWithinDistance(origin, near, 3));
        assertTrue(roomService.isWithinDistance(origin, near, 2));
        assertFalse(roomService.isWithinDistance(origin, near, 1));
    }
}
