/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016 Peter Keeler
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
import com.emergentmud.core.model.Zone;
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
    private EntityRepository entityRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ZoneBuilder zoneBuilder;

    @Mock
    private Zone zone;

    @Mock
    private Room room;

    private WorldManager worldManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(entityRepository.save(any(Entity.class))).thenAnswer(invocation -> invocation.getArguments()[0]);
        when(zoneBuilder.build(anyLong(), anyLong(), anyLong())).thenReturn(zone);
        when(zone.getId()).thenReturn("zoneId");

        worldManager = new WorldManager(entityRepository, roomRepository);

    }

    @Test
    public void testTest() throws Exception {
        when(roomRepository.findByXAndYAndZ(eq(0L), eq(0L), eq(0L))).thenReturn(room);

        assertTrue(worldManager.test(0L, 0L, 0L));

        verify(zoneBuilder, never()).build(anyLong(), anyLong(), anyLong());
    }

    @Test
    public void testTestMissing() throws Exception {
        assertFalse(worldManager.test(0L, 0L, 0L));

        verify(zoneBuilder, never()).build(eq(0L), eq(0L), eq(0L));
    }

    @Test
    public void testPutExistingRoom() throws Exception {
        Entity entity = mock(Entity.class);
        List<Entity> contents = new ArrayList<>();

        when(entity.getRoom()).thenCallRealMethod();
        doCallRealMethod().when(entity).setRoom(any(Room.class));
        when(room.getX()).thenReturn(2L);
        when(room.getY()).thenReturn(1L);
        when(room.getZ()).thenReturn(3L);
        when(roomRepository.findByXAndYAndZ(eq(2L), eq(1L), eq(3L))).thenReturn(room);
        when(entityRepository.findByRoom(eq(room))).thenReturn(contents);

        entity.setRoom(mock(Room.class));

        Room result = worldManager.put(entity, 2L, 1L, 3L);

        assertNotNull(result);
        verify(entityRepository).save(eq(entity));
        verify(entity).setRoom(eq(room));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutMissingRoom() throws Exception {
        Entity entity = mock(Entity.class);
        List<Entity> contents = new ArrayList<>();

        when(entity.getRoom()).thenReturn(room);
        when(room.getX()).thenReturn(2L);
        when(room.getY()).thenReturn(1L);
        when(room.getZ()).thenReturn(3L);
        when(entityRepository.findByRoom(eq(room))).thenReturn(contents);

        worldManager.put(entity, 2L, 1L, 3L);

        fail("Required exception was not thrown.");
    }

    @Test
    public void testPutExistingEntity() throws Exception {
        Entity entity = mock(Entity.class);
        List<Entity> contents = new ArrayList<>();

        contents.add(entity);

        when(entity.getRoom()).thenReturn(room);
        when(room.getX()).thenReturn(2L);
        when(room.getY()).thenReturn(1L);
        when(room.getZ()).thenReturn(3L);
        when(entityRepository.findByRoom(eq(room))).thenReturn(contents);
        when(roomRepository.findByXAndYAndZ(eq(2L), eq(1L), eq(3L))).thenReturn(room);

        worldManager.put(entity, 2L, 1L, 3L);

        verify(entityRepository).save(eq(entity));
        verify(entity).setRoom(eq(room));
    }

    @Test
    public void testRemove() throws Exception {
        Entity entity = mock(Entity.class);
        List<Entity> contents = new ArrayList<>();

        contents.add(entity);

        when(entity.getRoom()).thenReturn(room);
        when(room.getX()).thenReturn(2L);
        when(room.getY()).thenReturn(1L);
        when(room.getZ()).thenReturn(3L);
        when(entityRepository.findByRoom(eq(room))).thenReturn(contents);

        worldManager.remove(entity);

        verify(entityRepository).save(eq(entity));
        verify(entity).setRoom(null);
    }

    @Test
    public void testRemoveNoRoom() throws Exception {
        Entity entity = mock(Entity.class);

        worldManager.remove(entity);

        verify(entity).getRoom();
        verifyZeroInteractions(entityRepository);
    }

    @Test
    public void testRemoveNonExistent() throws Exception {
        Entity entity = mock(Entity.class);
        List<Entity> contents = new ArrayList<>();

        when(entity.getRoom()).thenReturn(room);
        when(room.getX()).thenReturn(2L);
        when(room.getY()).thenReturn(1L);
        when(room.getZ()).thenReturn(3L);
        when(entityRepository.findByRoom(eq(room))).thenReturn(contents);

        worldManager.remove(entity);

        verify(entityRepository).save(eq(entity));
        verify(entity).setRoom(null);
    }
}
