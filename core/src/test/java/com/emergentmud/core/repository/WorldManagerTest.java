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

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WorldManagerTest {
    @Mock
    private RoomRepository roomRepository;

    @Mock
    private EntityRepository entityRepository;

    private WorldManager worldManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(entityRepository.save(any(Entity.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        worldManager = new WorldManager(roomRepository, entityRepository);
    }

    @Test
    public void testPut() throws Exception {
        Entity entity = mock(Entity.class);
        Room room = mock(Room.class);
        List<Entity> contents = new ArrayList<>();

        when(roomRepository.findByXAndYAndZ(eq(2L), eq(1L), eq(3L))).thenReturn(room);
        when(room.getContents()).thenReturn(contents);

        boolean result = worldManager.put(entity, 2L, 1L, 3L);

        verify(room, times(2)).getContents();
        verify(entityRepository).save(eq(entity));
        verify(roomRepository).save(eq(room));
        verify(entity).setRoom(eq(room));
        assertTrue(contents.contains(entity));
        assertTrue(result);
    }

    @Test
    public void testPutExistingEntity() throws Exception {
        Entity entity = mock(Entity.class);
        Room room = mock(Room.class);
        List<Entity> contents = new ArrayList<>();

        contents.add(entity);

        when(roomRepository.findByXAndYAndZ(eq(2L), eq(1L), eq(3L))).thenReturn(room);
        when(room.getContents()).thenReturn(contents);

        boolean result = worldManager.put(entity, 2L, 1L, 3L);

        verify(room).getContents();
        verify(entityRepository).save(eq(entity));
        verify(roomRepository, never()).save(eq(room));
        verify(entity).setRoom(eq(room));
        assertTrue(contents.contains(entity));
        assertTrue(result);
    }

    @Test
    public void testRemove() throws Exception {
        Entity entity = mock(Entity.class);
        Room room = mock(Room.class);
        List<Entity> contents = new ArrayList<>();

        contents.add(entity);

        when(roomRepository.findByXAndYAndZ(eq(2L), eq(1L), eq(3L))).thenReturn(room);
        when(room.getContents()).thenReturn(contents);

        boolean result = worldManager.remove(entity, 2L, 1L, 3L);

        verify(room, times(2)).getContents();
        verify(entityRepository).save(eq(entity));
        verify(roomRepository).save(eq(room));
        verify(entity).setRoom(eq(null));
        assertFalse(contents.contains(entity));
        assertTrue(result);
    }

    @Test
    public void testRemoveNonExistent() throws Exception {
        Entity entity = mock(Entity.class);
        Room room = mock(Room.class);
        List<Entity> contents = new ArrayList<>();

        when(roomRepository.findByXAndYAndZ(eq(2L), eq(1L), eq(3L))).thenReturn(room);
        when(room.getContents()).thenReturn(contents);

        boolean result = worldManager.remove(entity, 2L, 1L, 3L);

        verify(room).getContents();
        verify(entityRepository).save(eq(entity));
        verify(roomRepository, never()).save(eq(room));
        verify(entity).setRoom(eq(null));
        assertFalse(contents.contains(entity));
        assertFalse(result);
    }

    @Test
    public void testGetExistingRoom() throws Exception {
        Room existing = mock(Room.class);

        when(roomRepository.findByXAndYAndZ(eq(2L), eq(1L), eq(3L))).thenReturn(existing);

        Room result = worldManager.getRoom(2L, 1L, 3L);

        verifyZeroInteractions(existing);
        assertNotNull(result);
    }

    @Test
    public void testGetNewRoom() throws Exception {
        Room room = worldManager.getRoom(2L, 1L, 3L);

        verify(roomRepository).findByXAndYAndZ(eq(2L), eq(1L), eq(3L));
        verify(roomRepository).save(eq(room));
        assertEquals(2L, room.getX());
        assertEquals(1L, room.getY());
        assertEquals(3L, room.getZ());
    }
}
