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

        worldManager = new WorldManager(entityRepository, zoneBuilder, roomRepository);

    }

    @Test
    public void testTest() throws Exception {
        when(roomRepository.findByXAndYAndZ(eq(0L), eq(0L), eq(0L))).thenReturn(room);

        assertTrue(worldManager.test(0L, 0L, 0L));

        verify(zoneBuilder, never()).build(anyLong(), anyLong(), anyLong());
    }

    @Test
    public void testTestMissing() throws Exception {
        assertTrue(worldManager.test(0L, 0L, 0L));

        verify(zoneBuilder).build(eq(0L), eq(0L), eq(0L));
    }

    @Test
    public void testTestNoZone() throws Exception {
        when(zoneBuilder.build(eq(0L), eq(0L), eq(0L))).thenReturn(null);

        assertFalse(worldManager.test(0L, 0L, 0L));

        verify(zoneBuilder).build(eq(0L), eq(0L), eq(0L));
    }

    @Test
    public void testPutExistingRoom() throws Exception {
        Entity entity = mock(Entity.class);
        List<Entity> contents = new ArrayList<>();

        when(entity.getX()).thenReturn(2L);
        when(entity.getY()).thenReturn(1L);
        when(entity.getZ()).thenReturn(3L);
        when(roomRepository.findByXAndYAndZ(eq(2L), eq(1L), eq(3L))).thenReturn(mock(Room.class));
        when(entityRepository.findByXAndYAndZ(eq(2L), eq(1L), eq(3L))).thenReturn(contents);

        worldManager.put(entity, 2L, 1L, 3L);

        verify(entityRepository).save(eq(entity));
        verify(entity).setX(eq(2L));
        verify(entity).setY(eq(1L));
        verify(entity).setZ(eq(3L));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutMissingRoom() throws Exception {
        Entity entity = mock(Entity.class);
        List<Entity> contents = new ArrayList<>();

        when(entity.getX()).thenReturn(2L);
        when(entity.getY()).thenReturn(1L);
        when(entity.getZ()).thenReturn(3L);
        when(entityRepository.findByXAndYAndZ(eq(2L), eq(1L), eq(3L))).thenReturn(contents);

        worldManager.put(entity, 2L, 1L, 3L);

        fail("Required exception was not thrown.");
    }

    @Test
    public void testPutExistingEntity() throws Exception {
        Entity entity = mock(Entity.class);
        List<Entity> contents = new ArrayList<>();

        contents.add(entity);

        when(entity.getX()).thenReturn(2L);
        when(entity.getY()).thenReturn(1L);
        when(entity.getZ()).thenReturn(3L);
        when(entityRepository.findByXAndYAndZ(eq(2L), eq(1L), eq(3L))).thenReturn(contents);
        when(roomRepository.findByXAndYAndZ(eq(2L), eq(1L), eq(3L))).thenReturn(room);

        worldManager.put(entity, 2L, 1L, 3L);

        verify(entityRepository).save(eq(entity));
        verify(entity).setX(eq(2L));
        verify(entity).setY(eq(1L));
        verify(entity).setZ(eq(3L));
    }

    @Test
    public void testRemove() throws Exception {
        Entity entity = mock(Entity.class);
        List<Entity> contents = new ArrayList<>();

        contents.add(entity);

        when(entity.getX()).thenReturn(2L);
        when(entity.getY()).thenReturn(1L);
        when(entity.getZ()).thenReturn(3L);
        when(entityRepository.findByXAndYAndZ(eq(2L), eq(1L), eq(3L))).thenReturn(contents);

        worldManager.remove(entity);

        verify(entityRepository).save(eq(entity));
        verify(entity).setX(null);
        verify(entity).setY(null);
        verify(entity).setZ(null);
    }

    @Test
    public void testRemoveNonExistent() throws Exception {
        Entity entity = mock(Entity.class);
        List<Entity> contents = new ArrayList<>();

        when(entity.getX()).thenReturn(2L);
        when(entity.getY()).thenReturn(1L);
        when(entity.getZ()).thenReturn(3L);
        when(entityRepository.findByXAndYAndZ(eq(2L), eq(1L), eq(3L))).thenReturn(contents);

        worldManager.remove(entity);

        verify(entityRepository).save(eq(entity));
        verify(entity).setX(null);
        verify(entity).setY(null);
        verify(entity).setZ(null);
    }
}
