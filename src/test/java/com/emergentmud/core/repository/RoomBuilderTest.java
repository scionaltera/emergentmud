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

package com.emergentmud.core.repository;

import com.emergentmud.core.model.room.Biome;
import com.emergentmud.core.model.room.FlowType;
import com.emergentmud.core.model.room.Room;
import com.emergentmud.core.model.WhittakerGridLocation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class RoomBuilderTest {
    @Mock
    private RoomRepository roomRepository;

    @Mock
    private WhittakerGridLocationRepository whittakerGridLocationRepository;

    @Spy
    private Random random;

    private List<WhittakerGridLocation> whittakerGridLocations = new ArrayList<>();
    private List<Room> neighbors = new ArrayList<>();

    private RoomBuilder roomBuilder;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        roomBuilder = new RoomBuilder(roomRepository, whittakerGridLocationRepository, random);

        doReturn(whittakerGridLocations).when(whittakerGridLocationRepository).findAll();
        doReturn(neighbors).when(roomRepository).findByXBetweenAndYBetweenAndZ(anyLong(), anyLong(), anyLong(), anyLong(), anyLong());
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgumentAt(0, Room.class));
    }

    @Test
    public void testRoomAlreadyExists() throws Exception {
        Room existing = mock(Room.class);

        when(roomRepository.findByXAndYAndZ(anyLong(), anyLong(), anyLong())).thenReturn(existing);

        Room room = roomBuilder.generateRoom(0L, 0L, 0L);

        assertEquals(existing, room);
    }

    @Test
    public void testGenerateFirstRoom() throws Exception {
        generateGridLocations(24);
        generateNeighbors(0);

        Room room = roomBuilder.generateRoom(0L, 0L, 0L);

        verify(roomRepository).findByXAndYAndZ(eq(0L), eq(0L), eq(0L));
        verify(whittakerGridLocationRepository).findAll();
        verify(roomRepository).findByXBetweenAndYBetweenAndZ(-2L, 2L, -2L, 2L, 0L);
        verify(roomRepository).save(any(Room.class));

        assertEquals(0L, (long)room.getX());
        assertEquals(0L, (long)room.getY());
        assertEquals(0L, (long)room.getZ());
        assertNotNull(room.getBiome());
        assertNotNull(room.getElevation());
        assertNotNull(room.getMoisture());
    }

    @Test
    public void testGenerateWithLegalNeighbor() throws Exception {
        generateGridLocations(2);
        generateNeighbors(1);

        Room room = roomBuilder.generateRoom(0L, 0L, 0L);

        verify(roomRepository).findByXAndYAndZ(eq(0L), eq(0L), eq(0L));
        verify(whittakerGridLocationRepository).findAll();
        verify(roomRepository).findByXBetweenAndYBetweenAndZ(-2L, 2L, -2L, 2L, 0L);
        verify(roomRepository).save(any(Room.class));

        assertEquals(0L, (long)room.getX());
        assertEquals(0L, (long)room.getY());
        assertEquals(0L, (long)room.getZ());
        assertNotNull(room.getBiome());
        assertNotNull(room.getElevation());
        assertNotNull(room.getMoisture());

        boolean result = neighbors.stream()
                .allMatch(n -> Math.abs(n.getElevation() - room.getElevation()) <= 1
                        && Math.abs(n.getMoisture() - room.getMoisture()) <= 1);

        assertTrue(result);
    }

    @Test
    public void testGenerateWithIllegalNeighbor() throws Exception {
        generateGridLocations(2);
        generateNeighbors(1);

        when(neighbors.get(0).getElevation()).thenReturn(5); // this neighbor is too different for our biomes

        Room room = roomBuilder.generateRoom(0L, 0L, 0L);

        verify(roomRepository).findByXAndYAndZ(eq(0L), eq(0L), eq(0L));
        verify(whittakerGridLocationRepository).findAll();
        verify(roomRepository).findByXBetweenAndYBetweenAndZ(-2L, 2L, -2L, 2L, 0L);
        verify(roomRepository, never()).save(any(Room.class));

        assertNull(room);
    }

    @Test
    public void testNeighborHigher() throws Exception {
        generateGridLocations(1);

        Room neighbor = mock(Room.class);

        when(neighbor.getElevation()).thenReturn(2);
        when(neighbor.getMoisture()).thenReturn(1);

        neighbors.add(neighbor);

        Room room = roomBuilder.generateRoom(0L, 0L, 0L);

        assertNotNull(room);
    }

    @Test
    public void testNeighborWetter() throws Exception {
        generateGridLocations(1);

        Room neighbor = mock(Room.class);

        when(neighbor.getElevation()).thenReturn(1);
        when(neighbor.getMoisture()).thenReturn(2);

        neighbors.add(neighbor);

        Room room = roomBuilder.generateRoom(0L, 0L, 0L);

        assertNotNull(room);
    }

    @Test
    public void testNeighborSame() throws Exception {
        generateGridLocations(1);

        Room neighbor = mock(Room.class);

        when(neighbor.getElevation()).thenReturn(1);
        when(neighbor.getMoisture()).thenReturn(1);

        neighbors.add(neighbor);

        Room room = roomBuilder.generateRoom(0L, 0L, 0L);

        assertNotNull(room);
    }

    @Test
    public void testNeighborTooWet() throws Exception {
        generateGridLocations(1);

        Room neighbor = mock(Room.class);

        when(neighbor.getElevation()).thenReturn(1);
        when(neighbor.getMoisture()).thenReturn(3);

        neighbors.add(neighbor);

        Room room = roomBuilder.generateRoom(0L, 0L, 0L);

        assertNull(room);
    }

    @Test
    public void testNeighborTooHigh() throws Exception {
        generateGridLocations(1);

        Room neighbor = mock(Room.class);

        when(neighbor.getElevation()).thenReturn(3);
        when(neighbor.getMoisture()).thenReturn(1);

        neighbors.add(neighbor);

        Room room = roomBuilder.generateRoom(0L, 0L, 0L);

        assertNull(room);
    }

    @Test
    public void testNeighborTooWetAndHigh() throws Exception {
        generateGridLocations(1);

        Room neighbor = mock(Room.class);

        when(neighbor.getElevation()).thenReturn(2);
        when(neighbor.getMoisture()).thenReturn(2);

        neighbors.add(neighbor);

        Room room = roomBuilder.generateRoom(0L, 0L, 0L);

        assertNull(room);
    }

    @Test
    public void testSpringGeneration() throws Exception {
        Biome biome = mock(Biome.class);
        WhittakerGridLocation whittakerGridLocation = mock(WhittakerGridLocation.class);

        when(biome.getName()).thenReturn("Snow");
        when(whittakerGridLocation.getBiome()).thenReturn(biome);
        when(whittakerGridLocation.getElevation()).thenReturn(WhittakerGridLocation.MAX_ELEVATION);
        when(whittakerGridLocation.getMoisture()).thenReturn(3);

        whittakerGridLocations.clear();
        whittakerGridLocations.add(whittakerGridLocation);

        when(random.nextDouble()).thenReturn(0.0, 0.01);

        Room room = roomBuilder.generateRoom(0L, 0L, 0L);

        assertEquals(FlowType.SPRING, room.getWater().getFlowType());

        room = roomBuilder.generateRoom(0L, 0L, 0L);

        assertNotNull(room);
        assertNull(room.getWater());
    }

    private void generateGridLocations(int count) {
        whittakerGridLocations.clear();

        for (int i = 1; i <= count; i++) {
            Biome biome = mock(Biome.class);

            when(biome.getName()).thenReturn("Biome " + i);

            WhittakerGridLocation whittakerGridLocation = mock(WhittakerGridLocation.class);

            when(whittakerGridLocation.getBiome()).thenReturn(biome);
            when(whittakerGridLocation.getElevation()).thenReturn(i);
            when(whittakerGridLocation.getMoisture()).thenReturn(i);

            whittakerGridLocations.add(whittakerGridLocation);
        }
    }

    private void generateNeighbors(int count) {
        neighbors.clear();

        for (int i = 1; i <= count; i++) {
            Room room = mock(Room.class);

            when(room.getElevation()).thenReturn(i);
            when(room.getMoisture()).thenReturn(i);

            neighbors.add(room);
        }
    }
}
