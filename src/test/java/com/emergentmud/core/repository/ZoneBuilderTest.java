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

import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.Zone;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ZoneBuilderTest {
    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private Room origin;

    @Captor
    private ArgumentCaptor<List<Room>> roomListCaptor;

    private DrunkWalkZoneBuilder zoneBuilder;

    public ZoneBuilderTest() {
        MockitoAnnotations.initMocks(this);

        when(origin.getX()).thenReturn(0L);
        when(origin.getY()).thenReturn(0L);
        when(origin.getZ()).thenReturn(0L);
        when(roomRepository.findByXAndYAndZ(eq(0L), eq(0L), eq(0L))).thenReturn(origin);
        when(roomRepository.save(anyCollectionOf(Room.class))).thenAnswer(new Answer<Iterable<Room>>() {
            @Override
            public Iterable<Room> answer(InvocationOnMock invocation) throws Throwable {
                //noinspection unchecked
                Iterable<Room> iterable = (Iterable<Room>)invocation.getArguments()[0];

                iterable.forEach(room -> room.setId("roomId"));

                return iterable;
            }
        });
        when(zoneRepository.save(any(Zone.class))).thenAnswer(new Answer<Zone>() {
            @Override
            public Zone answer(InvocationOnMock invocation) throws Throwable {
                Zone zone = (Zone)invocation.getArguments()[0];

                zone.setId("zoneId");

                return zone;
            }
        });

        zoneBuilder = new DrunkWalkZoneBuilder(zoneRepository, roomRepository);
    }

    @Test
    public void testBuild() throws Exception {
        Zone zone = zoneBuilder.build(0L, 0L, 0L);

        assertTrue(3 == zone.getColor().length);

        verify(zoneRepository).save(eq(zone));
        verify(roomRepository).save(roomListCaptor.capture());

        List<Room> roomList = roomListCaptor.getValue();

        assertEquals(DrunkWalkZoneBuilder.ZONE_SIZE, roomList.size());
        roomList.stream().forEach(room -> assertEquals(zone, room.getZone()));
    }

    @Test
    public void testCreateRoom() throws Exception {
        Map<String, Room> rooms = new HashMap<>();
        Room room = zoneBuilder.createRoom(rooms, 0, 0, 0);

        assertNotNull(room);
        assertTrue(rooms.containsKey("0-0-0"));
        assertEquals(0L, (long)room.getX());
        assertEquals(0L, (long)room.getY());
        assertEquals(0L, (long)room.getZ());
    }

    @Test
    public void testCachedRoom() throws Exception {
        Map<String, Room> rooms = new HashMap<>();
        Room existing = mock(Room.class);

        rooms.put("0-0-0", existing);

        Room room = zoneBuilder.createRoom(rooms, 0, 0, 0);

        assertEquals(existing, room);
        assertTrue(rooms.size() == 1);
        assertEquals(0L, (long)room.getX());
        assertEquals(0L, (long)room.getY());
        assertEquals(0L, (long)room.getZ());
    }

    @Test
    public void testCountNeighborsNoNeighbors() throws Exception {
        assertEquals(0, zoneBuilder.countNeighbors(origin));
    }

    @Test
    public void testCountNeighborsNorthNeighbor() throws Exception {
        when(roomRepository.findByXAndYAndZ(eq(0L), eq(1L), eq(0L))).thenReturn(mock(Room.class));

        assertEquals(1, zoneBuilder.countNeighbors(origin));
    }

    @Test
    public void testCountNeighborsEastNeighbor() throws Exception {
        when(roomRepository.findByXAndYAndZ(eq(1L), eq(0L), eq(0L))).thenReturn(mock(Room.class));

        assertEquals(1, zoneBuilder.countNeighbors(origin));
    }

    @Test
    public void testCountNeighborsSouthNeighbor() throws Exception {
        when(roomRepository.findByXAndYAndZ(eq(0L), eq(-1L), eq(0L))).thenReturn(mock(Room.class));

        assertEquals(1, zoneBuilder.countNeighbors(origin));
    }

    @Test
    public void testCountNeighborsWestNeighbor() throws Exception {
        when(roomRepository.findByXAndYAndZ(eq(-1L), eq(0L), eq(0L))).thenReturn(mock(Room.class));

        assertEquals(1, zoneBuilder.countNeighbors(origin));
    }

    @Test
    public void testCountNeighborsAllNeighbors() throws Exception {
        when(roomRepository.findByXAndYAndZ(eq(1L), eq(0L), eq(0L))).thenReturn(mock(Room.class));
        when(roomRepository.findByXAndYAndZ(eq(0L), eq(1L), eq(0L))).thenReturn(mock(Room.class));
        when(roomRepository.findByXAndYAndZ(eq(-1L), eq(0L), eq(0L))).thenReturn(mock(Room.class));
        when(roomRepository.findByXAndYAndZ(eq(0L), eq(-1L), eq(0L))).thenReturn(mock(Room.class));

        assertEquals(4, zoneBuilder.countNeighbors(origin));
    }
}
