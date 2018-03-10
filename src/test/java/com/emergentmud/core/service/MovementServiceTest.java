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
import com.emergentmud.core.model.Room;
import com.emergentmud.core.repository.EntityRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MovementServiceTest {
    @Mock
    private EntityRepository entityRepository;

    @Mock
    private RoomService roomService;

    @Mock
    private Room room;

    private MovementService movementService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(entityRepository.save(any(Entity.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        movementService = new MovementService(entityRepository, roomService);
    }

    @Test
    public void testPutExistingRoom() throws Exception {
        Entity entity = mock(Entity.class);
        List<Entity> contents = new ArrayList<>();

        when(entity.getLocation()).thenCallRealMethod();
        doCallRealMethod().when(entity).setLocation(any(Coordinate.class));
        when(entityRepository.findByLocation(eq(new Coordinate(2L, 1L, 3L)))).thenReturn(contents);
        when(roomService.createRoom(new Coordinate(2L, 1L, 3L))).thenReturn(room);

        entity.setLocation(new Coordinate(0L, 0L, 0L));

        Entity result = movementService.put(entity, new Coordinate(2L, 1L, 3L));

        assertNotNull(result);
        verify(entityRepository).save(eq(entity));
        verify(entity, times(2)).setLocation(any(Coordinate.class));
    }

    @Test
    public void testPutExistingEntity() throws Exception {
        when(roomService.createRoom(new Coordinate(2L, 1L, 3L))).thenReturn(room);

        Entity entity = mock(Entity.class);

        movementService.put(entity, new Coordinate(2L, 1L, 3L));

        verify(entityRepository).save(eq(entity));
        verify(entity).setLocation(any(Coordinate.class));
    }

    @Test
    public void testRemove() {
        Entity entity = mock(Entity.class);

        movementService.remove(entity);

        verify(entityRepository).save(eq(entity));
        verify(entity).setLocation(eq(null));
    }

    @Test
    public void testRemoveNonExistent() {
        Entity entity = mock(Entity.class);
        List<Entity> contents = new ArrayList<>();

        when(room.getLocation()).thenReturn(new Coordinate(2L, 1L, 3L));
        when(entityRepository.findByLocation(new Coordinate(2L, 1L, 3L))).thenReturn(contents);

        assertFalse(contents.contains(entity));

        movementService.remove(entity);

        verify(entityRepository).save(eq(entity));
        verify(entity).setLocation(eq(null));
        assertFalse(contents.contains(entity));
    }
}
