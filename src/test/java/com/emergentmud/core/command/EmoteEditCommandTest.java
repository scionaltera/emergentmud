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

package com.emergentmud.core.command;

import com.emergentmud.core.model.EmoteMetadata;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EmoteMetadataRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EmoteEditCommandTest {
    private static final int USAGE_LENGTH = 7;

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

    private List<EmoteMetadata> emotes = new ArrayList<>();
    private String cmd = "emoteedit";

    private EmoteEditCommand emoteEditCommand;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(emoteMetadataRepository.findAll(eq(EmoteEditCommand.SORT))).thenReturn(emotes);
        when(emoteMetadataRepository.findByName(eq("nod"))).thenReturn(emote);

        for (int i = 0; i < 3; i++) {
            emotes.add(mock(EmoteMetadata.class));
        }

        emoteEditCommand = new EmoteEditCommand(emoteMetadataRepository);
    }

    @Test
    public void testNoArgs() throws Exception {
        String[] tokens = new String[0];
        String raw = "";

        GameOutput result = emoteEditCommand.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verifyZeroInteractions(emoteMetadataRepository);
        verify(output, times(USAGE_LENGTH)).append(anyString());
    }

    @Test
    public void testList() throws Exception {
        String[] tokens = new String[] { "list" };
        String raw = "list";

        GameOutput result = emoteEditCommand.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(output, atLeast(2)).append(anyString());
        verify(emoteMetadataRepository).findAll(eq(CommandEditCommand.SORT));

        emotes.forEach(emote -> {
                    verify(emote).getPriority();
                    verify(emote).getName();
                });
    }

    @Test
    public void testShow() throws Exception {
        String[] tokens = new String[] { "show", "nod" };
        String raw = "show nod";

        GameOutput result = emoteEditCommand.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(output, times(4)).append(anyString());
        verify(emoteMetadataRepository).findByName(eq("nod"));
    }

    @Test
    public void testShowNotFound() throws Exception {
        String[] tokens = new String[] { "show", "waffle" };
        String raw = "show waffle";

        GameOutput result = emoteEditCommand.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(output).append(anyString());
        verify(emoteMetadataRepository).findByName(eq("waffle"));
    }

    @Test
    public void testShowWrongArgs() throws Exception {
        String[] tokens = new String[] { "show" };
        String raw = "show";

        GameOutput result = emoteEditCommand.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(output, times(USAGE_LENGTH)).append(anyString());
        verify(emoteMetadataRepository, never()).findByName(anyString());
    }

    @Test
    public void testAddWrongArgs() throws Exception {
        String[] tokens = new String[] { "add" };
        String raw = "add";

        GameOutput result = emoteEditCommand.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verifyZeroInteractions(emoteMetadataRepository);
        verify(output, times(USAGE_LENGTH)).append(anyString());
    }

    @Test
    public void testAddValid() throws Exception {
        String[] tokens = new String[] { "add", "nod" };
        String raw = "add nod";

        GameOutput result = emoteEditCommand.execute(output, entity, cmd, tokens, raw);

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

        GameOutput result = emoteEditCommand.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository, never()).findByName(anyString());
        verify(emoteMetadataRepository, never()).save(any(EmoteMetadata.class));
        verify(output, times(USAGE_LENGTH)).append(anyString());
    }

    @Test
    public void testSetWrongEmote() throws Exception {
        String[] tokens = new String[] { "set", "waffle", "self", "foo" };
        String raw = "set waffle self foo";

        GameOutput result = emoteEditCommand.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository).findByName(eq("waffle"));
        verify(emoteMetadataRepository, never()).save(any(EmoteMetadata.class));
        verify(output).append(anyString());
    }

    @Test
    public void testSetEmptyEmote() throws Exception {
        String[] tokens = new String[] { "set", "nod", "self" };
        String raw = "set nod self";

        GameOutput result = emoteEditCommand.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository, never()).findByName(anyString());
        verify(emoteMetadataRepository, never()).save(any(EmoteMetadata.class));
        verify(output, times(USAGE_LENGTH)).append(anyString());
    }

    @Test
    public void testSetWrongTarget() throws Exception {
        String[] tokens = new String[] { "set", "nod", "flarp", "You", "flarp." };
        String raw = "set nod flarp You flarp.";

        GameOutput result = emoteEditCommand.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository).findByName(eq("nod"));
        verify(emoteMetadataRepository, never()).save(any(EmoteMetadata.class));
        verify(output, times(USAGE_LENGTH)).append(anyString());
    }

    @Test
    public void testSetSelf() throws Exception {
        String[] tokens = new String[] { "set", "nod", "self", "You", "nod." };
        String raw = "set nod self You nod.";

        GameOutput result = emoteEditCommand.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository).findByName(eq("nod"));
        verify(emoteMetadataRepository).save(any(EmoteMetadata.class));
        verify(emote).setToSelf(eq("You nod."));
        verify(emote, never()).setToTarget(anyString());
        verify(emote, never()).setToRoom(anyString());
        verify(output).append(anyString());
    }

    @Test
    public void testSetTarget() throws Exception {
        String[] tokens = new String[] { "set", "nod", "target", "nods." };
        String raw = "set nod target nods.";

        GameOutput result = emoteEditCommand.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository).findByName(eq("nod"));
        verify(emoteMetadataRepository).save(any(EmoteMetadata.class));
        verify(emote, never()).setToSelf(anyString());
        verify(emote).setToTarget(eq("nods."));
        verify(emote, never()).setToRoom(anyString());
        verify(output).append(anyString());
    }

    @Test
    public void testSetRoom() throws Exception {
        String[] tokens = new String[] { "set", "nod", "room", "nods." };
        String raw = "set nod room nods.";

        GameOutput result = emoteEditCommand.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository).findByName(eq("nod"));
        verify(emoteMetadataRepository).save(any(EmoteMetadata.class));
        verify(emote, never()).setToSelf(anyString());
        verify(emote, never()).setToTarget(anyString());
        verify(emote).setToRoom(eq("nods."));
        verify(output).append(anyString());
    }

    @Test
    public void testDelete() throws Exception {
        String[] tokens = new String[] { "delete", "nod" };
        String raw = "delete nod";

        GameOutput result = emoteEditCommand.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository).findByName(eq("nod"));
        verify(emoteMetadataRepository).delete(eq(emote));
        verify(output).append(anyString());
    }

    @Test
    public void testDeleteNoName() throws Exception {
        String[] tokens = new String[] { "delete" };
        String raw = "delete";

        GameOutput result = emoteEditCommand.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository, never()).findByName(anyString());
        verify(emoteMetadataRepository, never()).delete(any(EmoteMetadata.class));
        verify(output, times(USAGE_LENGTH)).append(anyString());
    }

    @Test
    public void testDeleteMissing() throws Exception {
        String[] tokens = new String[] { "delete", "waffle" };
        String raw = "delete waffle";

        GameOutput result = emoteEditCommand.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository).findByName(eq("waffle"));
        verify(emoteMetadataRepository, never()).delete(any(EmoteMetadata.class));
        verify(output).append(anyString());
    }

    @Test
    public void testPriorityWrongArgs() throws Exception {
        String[] tokens = new String[] { "priority", "nod" };
        String raw = "priority nod";

        GameOutput result = emoteEditCommand.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(output, times(USAGE_LENGTH)).append(anyString());
    }

    @Test
    public void testPriorityNonIntegerPriority() throws Exception {
        String[] tokens = new String[] { "priority", "nod", "important" };
        String raw = "priority nod important";

        GameOutput result = emoteEditCommand.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository).findByName(eq("nod"));
        verify(emote, never()).setPriority(anyInt());
        verify(emoteMetadataRepository, never()).save(any(EmoteMetadata.class));
    }

    @Test
    public void testPriorityValid() throws Exception {
        String[] tokens = new String[] { "priority", "nod", "42" };
        String raw = "priority nod 42";

        GameOutput result = emoteEditCommand.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(emoteMetadataRepository).findByName(eq("nod"));
        verify(emote).setPriority(eq(42));
        verify(emoteMetadataRepository).save(any(EmoteMetadata.class));
    }
}
