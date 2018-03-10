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

package com.emergentmud.core.service;

import com.emergentmud.core.model.Coordinate;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.repository.RoomRepository;
import com.emergentmud.core.service.maze.ZoneFillStrategy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RoomServiceTest {
    @Mock
    private ZoneService zoneService;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ZoneFillStrategy zoneFillStrategy;

    @Mock
    private Entity originEntity;

    @Mock
    private Coordinate origin;

    private RoomService roomService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(origin.getX()).thenReturn(0L);
        when(origin.getY()).thenReturn(0L);
        when(origin.getZ()).thenReturn(0L);
        when(originEntity.getLocation()).thenReturn(origin);

        roomService = new RoomService(
                zoneService,
                roomRepository,
                zoneFillStrategy);
    }

    @Test
    public void testFetchRoom() {
        roomService.fetchRoom(origin);

        verify(roomRepository).findByLocation(eq(origin));
    }

    @Test
    public void testXSimpleDistance() {
        assertTrue(roomService.isWithinDistance(originEntity, new Coordinate(2, 0, 0), 3));
        assertTrue(roomService.isWithinDistance(originEntity, new Coordinate(2, 0, 0), 2));
        assertFalse(roomService.isWithinDistance(originEntity, new Coordinate(2, 0, 0), 1));
    }

    @Test
    public void testYSimpleDistance() {
        assertTrue(roomService.isWithinDistance(originEntity, new Coordinate(0, 2, 0), 3));
        assertTrue(roomService.isWithinDistance(originEntity, new Coordinate(0, 2, 0), 2));
        assertFalse(roomService.isWithinDistance(originEntity, new Coordinate(0, 2, 0), 1));
    }

    @Test
    public void testZSimpleDistance() {
        assertTrue(roomService.isWithinDistance(originEntity, new Coordinate(0, 0, 2), 3));
        assertTrue(roomService.isWithinDistance(originEntity, new Coordinate(0, 0, 2), 2));
        assertFalse(roomService.isWithinDistance(originEntity, new Coordinate(0, 0, 2), 1));
    }

    @Test
    public void testXShiftedDistance() {
        when(originEntity.getLocation()).thenReturn(new Coordinate(10, 0, 0));

        assertTrue(roomService.isWithinDistance(originEntity, new Coordinate(12, 0, 0), 3));
        assertTrue(roomService.isWithinDistance(originEntity, new Coordinate(12, 0, 0), 2));
        assertFalse(roomService.isWithinDistance(originEntity, new Coordinate(12, 0, 0), 1));
    }

    @Test
    public void testYShiftedDistance() {
        when(originEntity.getLocation()).thenReturn(new Coordinate(0, 10, 0));

        assertTrue(roomService.isWithinDistance(originEntity, new Coordinate(0, 12, 0), 3));
        assertTrue(roomService.isWithinDistance(originEntity, new Coordinate(0, 12, 0), 2));
        assertFalse(roomService.isWithinDistance(originEntity, new Coordinate(0, 12, 0), 1));
    }

    @Test
    public void testZShiftedDistance() {
        when(originEntity.getLocation()).thenReturn(new Coordinate(0, 0, 10));

        assertTrue(roomService.isWithinDistance(originEntity, new Coordinate(0, 0, 12), 3));
        assertTrue(roomService.isWithinDistance(originEntity, new Coordinate(0, 0, 12), 2));
        assertFalse(roomService.isWithinDistance(originEntity, new Coordinate(0, 0, 12), 1));
    }
}
