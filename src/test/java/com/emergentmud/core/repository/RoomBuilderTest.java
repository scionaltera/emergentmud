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

import com.emergentmud.core.model.WhittakerGridLocation;
import com.emergentmud.core.model.room.Biome;
import com.emergentmud.core.model.room.Room;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RoomBuilderTest {
    @Mock
    private NoiseMap noiseMap;

    @Mock
    private Biome biome;

    @Mock
    private Biome oceanBiome;

    @Mock
    private BiomeRepository biomeRepository;

    @Mock
    private WhittakerGridLocationRepository whittakerGridLocationRepository;

    @Mock
    private WhittakerGridLocation whittakerGridLocation;

    private RoomBuilder roomBuilder;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(noiseMap.getElevation(eq(128L), eq(128L))).thenReturn(3);
        when(noiseMap.getMoisture(eq(128L), eq(128L))).thenReturn(2);
        when(noiseMap.getElevation(eq(1024L), eq(1024L))).thenReturn(0);
        when(noiseMap.getMoisture(eq(1024L), eq(1024L))).thenReturn(1);
        when(whittakerGridLocationRepository.findByElevationAndMoisture(anyInt(), anyInt())).thenReturn(whittakerGridLocation);
        when(biomeRepository.findByName(eq("Ocean"))).thenReturn(oceanBiome);
        when(whittakerGridLocation.getBiome()).thenReturn(biome);
        when(whittakerGridLocation.getElevation()).thenReturn(3);
        when(whittakerGridLocation.getMoisture()).thenReturn(2);

        roomBuilder = new RoomBuilder(noiseMap, whittakerGridLocationRepository, biomeRepository);
    }

    @Test
    public void testGenerateRoom() throws Exception {
        Room room = roomBuilder.generateRoom(128L, 128L, 0L);

        assertEquals(128L, (long)room.getX());
        assertEquals(128L, (long)room.getY());
        assertEquals(0L, (long)room.getZ());
        assertEquals(3, (int)room.getElevation());
        assertEquals(2, (int)room.getMoisture());
        assertEquals(biome, room.getBiome());
    }

    @Test
    public void testGenerateRoomOutOfBounds() throws Exception {
        Room room = roomBuilder.generateRoom(1024L, 1024L, 0L);

        assertEquals(1024L, (long)room.getX());
        assertEquals(1024L, (long)room.getY());
        assertEquals(0L, (long)room.getZ());
        assertEquals(0, (int)room.getElevation());
        assertEquals(1, (int)room.getMoisture());
        assertEquals(biome, room.getBiome());
    }
}
