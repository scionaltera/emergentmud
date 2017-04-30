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

import com.emergentmud.core.model.CommandMetadata;
import com.emergentmud.core.repository.CapabilityRepository;
import com.emergentmud.core.repository.CommandMetadataRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class CommandLoaderTest {
    private CommandLoader commandLoader;

    @Mock
    private CapabilityRepository capabilityRepository;

    @Mock
    private CommandMetadataRepository commandMetadataRepository;

    @Captor
    private ArgumentCaptor<List<CommandMetadata>> metadataCaptor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(commandMetadataRepository.save(anyCollectionOf(CommandMetadata.class))).thenAnswer(invocation -> {
            //noinspection unchecked
            List<CommandMetadata> metadataList = (List<CommandMetadata>)invocation.getArguments()[0];

            metadataList.forEach(m -> m.setId(UUID.randomUUID().toString()));

            return metadataList;
        });

        commandLoader = new CommandLoader(commandMetadataRepository, capabilityRepository);
    }

    @Test
    public void testLoadCommandsEmpty() throws Exception {
        when(commandMetadataRepository.count()).thenReturn(0L);

        commandLoader.loadCommands();

        verify(commandMetadataRepository).save(metadataCaptor.capture());

        List<CommandMetadata> metadataList = metadataCaptor.getValue();

        metadataList.forEach(m -> {
                    assertNotNull(m.getId());
                    assertNotNull(m.getName());
                    assertNotNull(m.getBeanName());
                    assertNotNull(m.getPriority());
                });
    }

    @Test
    public void testLoadCommandsNotEmpty() throws Exception {
        when(commandMetadataRepository.count()).thenReturn(60L);

        commandLoader.loadCommands();

        verify(commandMetadataRepository, never()).save(anyCollectionOf(CommandMetadata.class));
    }
}
