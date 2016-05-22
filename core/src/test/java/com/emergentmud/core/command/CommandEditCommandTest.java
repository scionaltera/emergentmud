/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016 BoneVM, LLC
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

import com.emergentmud.core.model.CommandMetadata;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.CommandMetadataRepository;
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

public class CommandEditCommandTest {
    private static final int USAGE_LENGTH = 4;

    @Captor
    private ArgumentCaptor<CommandMetadata> commandMetadataArgumentCaptor;

    @Mock
    private CommandMetadataRepository commandMetadataRepository;

    @Mock
    private GameOutput output;

    @Mock
    private Entity entity;

    @Mock
    private CommandMetadata command;

    private List<CommandMetadata> commands = new ArrayList<>();

    private CommandEditCommand commandEditCommand;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        for (int i = 0; i < 3; i++) {
            commands.add(mock(CommandMetadata.class));
        }

        when(commandMetadataRepository.findAll(eq(CommandEditCommand.SORT))).thenReturn(commands);
        when(commandMetadataRepository.findByName(eq("test"))).thenReturn(command);

        commandEditCommand = new CommandEditCommand(commandMetadataRepository);
    }

    @Test
    public void testNoArgs() throws Exception {
        String[] tokens = new String[0];
        String raw = "";

        GameOutput result = commandEditCommand.execute(output, entity, tokens, raw);

        assertNotNull(result);
        verifyZeroInteractions(commandMetadataRepository);
        verify(output, times(USAGE_LENGTH)).append(anyString());
    }

    @Test
    public void testList() throws Exception {
        String[] tokens = new String[] { "list" };
        String raw = "list";

        GameOutput result = commandEditCommand.execute(output, entity, tokens, raw);

        assertNotNull(result);
        verify(output, atLeast(2)).append(anyString());
        verify(commandMetadataRepository).findAll(eq(CommandEditCommand.SORT));

        commands.stream()
                .forEach(command -> {
                    verify(command).getPriority();
                    verify(command).getName();
                });
    }

    @Test
    public void testAddWrongArgs() throws Exception {
        String[] tokens = new String[] { "add" };
        String raw = "add";

        GameOutput result = commandEditCommand.execute(output, entity, tokens, raw);

        assertNotNull(result);
        verifyZeroInteractions(commandMetadataRepository);
        verify(output, times(USAGE_LENGTH)).append(anyString());
    }

    @Test
    public void testAddNonIntegerPriority() throws Exception {
        String[] tokens = new String[] { "add", "test", "testCommand", "important" };
        String raw = "add test testCommand important";

        GameOutput result = commandEditCommand.execute(output, entity, tokens, raw);

        assertNotNull(result);
        verifyZeroInteractions(commandMetadataRepository);
        verify(output).append(anyString());
    }

    @Test
    public void testAddValid() throws Exception {
        String[] tokens = new String[] { "add", "test", "testCommand", "42" };
        String raw = "add test testCommand 42";

        GameOutput result = commandEditCommand.execute(output, entity, tokens, raw);

        assertNotNull(result);
        verify(commandMetadataRepository).save(commandMetadataArgumentCaptor.capture());
        verify(output).append(anyString());

        CommandMetadata commandMetadata = commandMetadataArgumentCaptor.getValue();

        assertNotNull(commandMetadata);
        assertEquals("test", commandMetadata.getName());
        assertEquals("testCommand", commandMetadata.getBeanName());
        assertEquals((Integer)42, commandMetadata.getPriority());
    }

    @Test
    public void testPriorityWrongArgs() throws Exception {
        String[] tokens = new String[] { "priority", "test" };
        String raw = "priority test";

        GameOutput result = commandEditCommand.execute(output, entity, tokens, raw);

        assertNotNull(result);
        verify(output, times(USAGE_LENGTH)).append(anyString());
    }

    @Test
    public void testPriorityNonIntegerPriority() throws Exception {
        String[] tokens = new String[] { "priority", "test", "important" };
        String raw = "priority test important";

        GameOutput result = commandEditCommand.execute(output, entity, tokens, raw);

        assertNotNull(result);
        verify(commandMetadataRepository).findByName(eq("test"));
        verify(command, never()).setPriority(anyInt());
        verify(commandMetadataRepository, never()).save(any(CommandMetadata.class));
    }

    @Test
    public void testPriorityValid() throws Exception {
        String[] tokens = new String[] { "priority", "test", "42" };
        String raw = "priority test 42";

        GameOutput result = commandEditCommand.execute(output, entity, tokens, raw);

        assertNotNull(result);
        verify(commandMetadataRepository).findByName(eq("test"));
        verify(command).setPriority(eq(42));
        verify(commandMetadataRepository).save(any(CommandMetadata.class));
    }

    @Test
    public void testInvalidSubcommand() throws Exception {
        String[] tokens = new String[] { "foo" };
        String raw = "foo";

        GameOutput result = commandEditCommand.execute(output, entity, tokens, raw);

        assertNotNull(result);
        verify(output, times(USAGE_LENGTH)).append(anyString());
    }
}
