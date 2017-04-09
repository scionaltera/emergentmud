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

import com.emergentmud.core.model.EmoteMetadata;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class EmoteLoaderTest {
    @Mock
    private EmoteMetadataRepository emoteMetadataRepository;

    @Captor
    private ArgumentCaptor<List<EmoteMetadata>> emoteMetadataListCaptor;

    private EmoteLoader emoteLoader;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        emoteLoader = new EmoteLoader(emoteMetadataRepository);
    }

    @Test
    public void testEmptyDatabase() throws Exception {
        when(emoteMetadataRepository.count()).thenReturn(0L);

        emoteLoader.loadEmotes();

        verify(emoteMetadataRepository).count();
        verify(emoteMetadataRepository).save(emoteMetadataListCaptor.capture());

        List<EmoteMetadata> emoteMetadata = emoteMetadataListCaptor.getValue();

        emoteMetadata.forEach(m -> {
            assertTrue(!StringUtils.isEmpty(m.getToSelfUntargeted()));
            assertTrue(!StringUtils.isEmpty(m.getToRoomUntargeted()));
            assertTrue(!StringUtils.isEmpty(m.getToSelfWithTarget()));
            assertTrue(!StringUtils.isEmpty(m.getToTarget()));
            assertTrue(!StringUtils.isEmpty(m.getToRoomWithTarget()));
        });
    }

    @Test
    public void testPopulatedDatabase() throws Exception {
        when(emoteMetadataRepository.count()).thenReturn(1000L);

        emoteLoader.loadEmotes();

        verify(emoteMetadataRepository).count();
        verifyNoMoreInteractions(emoteMetadataRepository);
    }
}
