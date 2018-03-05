/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016-2018 Peter Keeler
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

package com.emergentmud.core.command.impl;

import com.emergentmud.core.model.EmoteMetadata;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EmoteMetadataRepository;
import com.emergentmud.core.service.InputService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EmoteEditCommandTest {
    private static final int USAGE_LENGTH = 9;

    @Captor
    private ArgumentCaptor<EmoteMetadata> emoteMetadataArgumentCaptor;

    @Mock
    private EmoteMetadataRepository emoteMetadataRepository;

    @Mock
    private GameOutput output;

    @Mock
    private Entity entity;

    @Mock
    private EmoteMetadata emote;

    @Spy
    private InputService inputService;

    private List<EmoteMetadata> emotes = new ArrayList<>();
    private String cmd = "emoteedit";

    private EmoteEditCommand command;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(emoteMetadataRepository.findAll(any(Sort.class))).thenReturn(emotes);
        when(emoteMetadataRepository.findByName(eq("nod"))).thenReturn(emote);

        for (int i = 0; i < 3; i++) {
            EmoteMetadata mock = mock(EmoteMetadata.class);

            when(mock.getPriority()).thenReturn(i + 10);
            when(mock.getName()).thenReturn("test" + i);

            emotes.add(mock);
        }

        command = new EmoteEditCommand(emoteMetadataRepository, inputService);
    }

    @Test
    public void testDescription() throws Exception {
        assertNotEquals("No description.", command.getDescription());
    }

    @Test
    public void testNoArgs() throws Exception {
        String[] tokens = new String[0];
        String raw = "";

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verifyZeroInteractions(emoteMetadataRepository);
        verify(output, times(USAGE_LENGTH)).append(anyString());
    }

    @Test
    public void testList() throws Exception {
        String[] tokens = new String[] { "list" };
        String raw = "list";

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(output, atLeast(2)).append(anyString());
        verify(emoteMetadataRepository).findAll(any(Sort.class));

        emotes.forEach(emote -> {
                    verify(emote, atLeastOnce()).getPriority();
                    verify(emote, atLeastOnce()).getName();
                });
    }

    @Test
    public void testShow() throws Exception {
        String[] tokens = new String[] { "show", "nod" };
        String raw = "show nod";

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(output).append(eq("[yellow](0) null"));
        verify(output, times(7)).append(contains("[Empty]"));
        verify(emoteMetadataRepository).findByName(eq("nod"));
    }

    @Test
    public void testShowNotFound() throws Exception {
        String[] tokens = new String[] { "show", "waffle" };
        String raw = "show waffle";

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(output).append(anyString());
        verify(emoteMetadataRepository).findByName(eq("waffle"));
    }

    @Test
    public void testShowWrongArgs() throws Exception {
        String[] tokens = new String[] { "show" };
        String raw = "show";

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(output, times(USAGE_LENGTH)).append(anyString());
        verify(emoteMetadataRepository, never()).findByName(anyString());
    }

    @Test
    public void testAddWrongArgs() throws Exception {
        String[] tokens = new String[] { "add" };
        String raw = "add";

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verifyZeroInteractions(emoteMetadataRepository);
        verify(output, times(USAGE_LENGTH)).append(anyString());
    }

    @Test
    public void testAddValid() throws Exception {
        String[] tokens = new String[] { "add", "nod" };
        String raw = "add nod";

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository).save(emoteMetadataArgumentCaptor.capture());
        verify(output).append(anyString());

        EmoteMetadata commandMetadata = emoteMetadataArgumentCaptor.getValue();

        assertNotNull(commandMetadata);
        assertEquals("nod", commandMetadata.getName());
        assertEquals((Integer)100, commandMetadata.getPriority());
    }

    @Test
    public void testSetWrongArgs() throws Exception {
        String[] tokens = new String[] { "set" };
        String raw = "set";

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository, never()).findByName(anyString());
        verify(emoteMetadataRepository, never()).save(any(EmoteMetadata.class));
        verify(output, times(USAGE_LENGTH)).append(anyString());
    }

    @Test
    public void testSetWrongEmote() throws Exception {
        String[] tokens = new String[] { "set", "waffle", "self", "foo" };
        String raw = "set waffle self foo";

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository).findByName(eq("waffle"));
        verify(emoteMetadataRepository, never()).save(any(EmoteMetadata.class));
        verify(output).append(anyString());
    }

    @Test
    public void testSetEmptyEmote() throws Exception {
        String[] tokens = new String[] { "set", "nod", "self" };
        String raw = "set nod self";

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository, never()).findByName(anyString());
        verify(emoteMetadataRepository, never()).save(any(EmoteMetadata.class));
        verify(output, times(USAGE_LENGTH)).append(anyString());
    }

    @Test
    public void testSetWrongTarget() throws Exception {
        String[] tokens = new String[] { "set", "nod", "flarp", "You", "flarp." };
        String raw = "set nod flarp You flarp.";

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository).findByName(eq("nod"));
        verify(emoteMetadataRepository, never()).save(any(EmoteMetadata.class));
        verify(output, times(USAGE_LENGTH)).append(anyString());
    }

    @Test
    public void testSetSelf() throws Exception {
        String[] tokens = new String[] { "set", "nod", "1", "You", "nod." };
        String raw = "set nod 1 You nod.";

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository).findByName(eq("nod"));
        verify(emoteMetadataRepository).save(any(EmoteMetadata.class));
        verify(emote).setToSelfUntargeted(eq("You nod."));
        verifyNoMoreInteractions(emote);
        verify(output).append(anyString());
    }

    @Test
    public void testSetTarget() throws Exception {
        String[] tokens = new String[] { "set", "nod", "4", "%self%", "nods", "to", "you." };
        String raw = "set nod 4 %self% nods to you.";

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository).findByName(eq("nod"));
        verify(emoteMetadataRepository).save(any(EmoteMetadata.class));
        verify(emote).setToTarget(eq("%self% nods to you."));
        verifyNoMoreInteractions(emote);
        verify(output).append(anyString());
    }

    @Test
    public void testSetRoom() throws Exception {
        String[] tokens = new String[] { "set", "nod", "2", "%self%", "nods." };
        String raw = "set nod 2 %self% nods.";

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository).findByName(eq("nod"));
        verify(emoteMetadataRepository).save(any(EmoteMetadata.class));
        verify(emote).setToRoomUntargeted(eq("%self% nods."));
        verifyNoMoreInteractions(emote);
        verify(output).append(anyString());
    }

    @Test
    public void testDelete() throws Exception {
        String[] tokens = new String[] { "delete", "nod" };
        String raw = "delete nod";

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository).findByName(eq("nod"));
        verify(emoteMetadataRepository).delete(eq(emote));
        verify(output).append(anyString());
    }

    @Test
    public void testDeleteNoName() throws Exception {
        String[] tokens = new String[] { "delete" };
        String raw = "delete";

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository, never()).findByName(anyString());
        verify(emoteMetadataRepository, never()).delete(any(EmoteMetadata.class));
        verify(output, times(USAGE_LENGTH)).append(anyString());
    }

    @Test
    public void testDeleteMissing() throws Exception {
        String[] tokens = new String[] { "delete", "waffle" };
        String raw = "delete waffle";

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository).findByName(eq("waffle"));
        verify(emoteMetadataRepository, never()).delete(any(EmoteMetadata.class));
        verify(output).append(anyString());
    }

    @Test
    public void testPriorityWrongArgs() throws Exception {
        String[] tokens = new String[] { "priority", "nod" };
        String raw = "priority nod";

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(output, times(USAGE_LENGTH)).append(anyString());
    }

    @Test
    public void testPriorityNonIntegerPriority() throws Exception {
        String[] tokens = new String[] { "priority", "nod", "important" };
        String raw = "priority nod important";

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository).findByName(eq("nod"));
        verify(emote, never()).setPriority(anyInt());
        verify(emoteMetadataRepository, never()).save(any(EmoteMetadata.class));
    }

    @Test
    public void testPriorityValid() throws Exception {
        String[] tokens = new String[] { "priority", "nod", "42" };
        String raw = "priority nod 42";

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository).findByName(eq("nod"));
        verify(emote).setPriority(eq(42));
        verify(emoteMetadataRepository).save(any(EmoteMetadata.class));
    }
}
