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

import com.emergentmud.core.model.WhittakerGridLocation;
import com.emergentmud.core.repository.BiomeRepository;
import com.emergentmud.core.repository.WhittakerGridLocationRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class WhittakerGridLocationLoaderTest {
    @Mock
    private BiomeRepository biomeRepository;

    @Mock
    private WhittakerGridLocationRepository whittakerGridLocationRepository;

    private WhittakerGridLocationLoader loader;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        loader = new WhittakerGridLocationLoader(biomeRepository, whittakerGridLocationRepository);
    }

    @Test
    public void testEmptyRepository() throws Exception {
        when(whittakerGridLocationRepository.count()).thenReturn(24L);

        loader.loadBiomeMetadata();

        verify(biomeRepository, never()).findByName(anyString());
        verify(whittakerGridLocationRepository, never()).save(anyListOf(WhittakerGridLocation.class));
    }

    @Test
    public void testLoadedRepository() throws Exception {
        when(whittakerGridLocationRepository.count()).thenReturn(0L);

        loader.loadBiomeMetadata();

        verify(biomeRepository, times(24)).findByName(anyString());
        verify(whittakerGridLocationRepository).save(anyListOf(WhittakerGridLocation.class));
    }
}
