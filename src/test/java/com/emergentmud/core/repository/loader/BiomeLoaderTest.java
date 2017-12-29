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

package com.emergentmud.core.repository.loader;

import com.emergentmud.core.model.Biome;
import com.emergentmud.core.repository.BiomeRepository;
import com.emergentmud.core.repository.RoomRepository;
import com.emergentmud.core.repository.WhittakerGridLocationRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BiomeLoaderTest {
    @Mock
    private BiomeRepository biomeRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private WhittakerGridLocationRepository whittakerGridLocationRepository;

    @Captor
    private ArgumentCaptor<List<Biome>> biomeCaptor;

    private BiomeLoader biomeLoader;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        biomeLoader = new BiomeLoader(whittakerGridLocationRepository, biomeRepository);
    }

    @Test
    public void testLoadEmptyWorld() throws Exception {
        when(biomeRepository.count()).thenReturn(0L);
        when(roomRepository.count()).thenReturn(0L);

        biomeLoader.onConstruct();

        verify(biomeRepository).count();
        verify(biomeRepository).save(biomeCaptor.capture());

        List<Biome> biomes = biomeCaptor.getValue();

        biomes.forEach(biome -> {
            assertNotNull(biome.getName());
            assertNotNull(biome.getColor());
        });
    }

    @Test
    public void testPopulatedWorld() throws Exception {
        when(biomeRepository.count()).thenReturn(1000L);
        when(whittakerGridLocationRepository.count()).thenReturn(1000L);

        biomeLoader.onConstruct();

        verify(biomeRepository).count();
        verifyNoMoreInteractions(biomeRepository);

        verify(whittakerGridLocationRepository).count();
        verifyNoMoreInteractions(whittakerGridLocationRepository);
    }
}
