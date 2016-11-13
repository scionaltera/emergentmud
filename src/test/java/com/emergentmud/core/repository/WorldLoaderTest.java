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

import com.emergentmud.core.model.Biome;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WorldLoaderTest {
    @Mock
    private ZoneBuilder zoneBuilder;

    @Mock
    private BiomeRepository biomeRepository;

    @Mock
    private RoomRepository roomRepository;

    @Captor
    private ArgumentCaptor<List<Biome>> biomeCaptor;

    private WorldLoader worldLoader;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        worldLoader = new WorldLoader(zoneBuilder, biomeRepository, roomRepository);
    }

    @Test
    public void testLoadEmptyWorld() throws Exception {
        when(biomeRepository.count()).thenReturn(0L);
        when(roomRepository.count()).thenReturn(0L);

        worldLoader.loadWorld();

        verify(biomeRepository).count();
        verify(biomeRepository).save(biomeCaptor.capture());

        List<Biome> biomes = biomeCaptor.getValue();

        biomes.forEach(biome -> {
            assertNotNull(biome.getName());
            assertNotNull(biome.getColor());
        });

        verify(roomRepository).count();
        verify(zoneBuilder).build(0L, 0L, 0L);
    }

    @Test
    public void testPopulatedWorld() throws Exception {
        when(biomeRepository.count()).thenReturn(1000L);
        when(roomRepository.count()).thenReturn(1000L);

        worldLoader.loadWorld();

        verify(biomeRepository).count();
        verify(roomRepository).count();
        verifyNoMoreInteractions(biomeRepository);
        verifyNoMoreInteractions(roomRepository);
        verifyZeroInteractions(zoneBuilder);
    }
}
