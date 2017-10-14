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

import com.emergentmud.core.model.Entity;
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
    private Entity origin;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(origin.getX()).thenReturn(0L);
        when(origin.getY()).thenReturn(0L);
        when(origin.getZ()).thenReturn(0L);

        roomService = new RoomService();
    }

    @Test
    public void testXSimpleDistance() throws Exception {
        assertTrue(roomService.isWithinDistance(origin, 2L, 0L, 0L, 3));
        assertTrue(roomService.isWithinDistance(origin, 2L, 0L, 0L, 2));
        assertFalse(roomService.isWithinDistance(origin, 2L, 0L, 0L, 1));
    }

    @Test
    public void testYSimpleDistance() throws Exception {
        assertTrue(roomService.isWithinDistance(origin, 0L, 2L, 0L, 3));
        assertTrue(roomService.isWithinDistance(origin, 0L, 2L, 0L, 2));
        assertFalse(roomService.isWithinDistance(origin, 0L, 2L, 0L, 1));
    }

    @Test
    public void testZSimpleDistance() throws Exception {
        assertTrue(roomService.isWithinDistance(origin, 0L, 0L, 2L, 3));
        assertTrue(roomService.isWithinDistance(origin, 0L, 0L, 2L, 2));
        assertFalse(roomService.isWithinDistance(origin, 0L, 0L, 2L, 1));
    }

    @Test
    public void testXShiftedDistance() throws Exception {
        when(origin.getX()).thenReturn(10L);

        assertTrue(roomService.isWithinDistance(origin, 12L, 0L, 0L, 3));
        assertTrue(roomService.isWithinDistance(origin, 12L, 0L, 0L, 2));
        assertFalse(roomService.isWithinDistance(origin, 12L, 0L, 0L, 1));
    }

    @Test
    public void testYShiftedDistance() throws Exception {
        when(origin.getY()).thenReturn(10L);

        assertTrue(roomService.isWithinDistance(origin, 0L, 12L, 0L, 3));
        assertTrue(roomService.isWithinDistance(origin, 0L, 12L, 0L, 2));
        assertFalse(roomService.isWithinDistance(origin, 0L, 12L, 0L, 1));
    }

    @Test
    public void testZShiftedDistance() throws Exception {
        when(origin.getZ()).thenReturn(10L);

        assertTrue(roomService.isWithinDistance(origin, 0L, 0L, 12L, 3));
        assertTrue(roomService.isWithinDistance(origin, 0L, 0L, 12L, 2));
        assertFalse(roomService.isWithinDistance(origin, 0L, 0L, 12L, 1));
    }
}
