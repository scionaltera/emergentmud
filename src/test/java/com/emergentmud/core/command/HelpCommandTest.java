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

package com.emergentmud.core.command;

import com.emergentmud.core.model.CommandMetadata;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.CommandMetadataRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HelpCommandTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private CommandMetadataRepository commandMetadataRepository;

    @Mock
    private GameOutput output;

    @Mock
    private Entity entity;

    private HelpCommand helpCommand;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        helpCommand = new HelpCommand(applicationContext, commandMetadataRepository);
    }

    @Test
    public void testExecuteNoArgs() throws Exception {
        GameOutput result = helpCommand.execute(output, entity, "help", new String[0], "help");

        assertEquals(output, result);
        verify(output, atLeastOnce()).append(anyString());
    }

    @Test
    public void testExecuteMissingCommand() throws Exception {
        GameOutput result = helpCommand.execute(output, entity, "help", new String[] {"foo"}, "help foo");

        assertEquals(output, result);
        verify(commandMetadataRepository).findByName(eq("foo"));
        verifyZeroInteractions(applicationContext);
        verify(output, atLeastOnce()).append(anyString());
    }

    @Test
    public void testExecuteHiddenCommand() throws Exception {
        CommandMetadata metadata = mock(CommandMetadata.class);

        when(entity.isAdmin()).thenReturn(false);
        when(metadata.isAdmin()).thenReturn(true);

        GameOutput result = helpCommand.execute(output, entity, "help", new String[] {"admin"}, "help admin");

        assertEquals(output, result);
        verify(commandMetadataRepository).findByName(eq("admin"));
        verifyZeroInteractions(applicationContext);
        verify(output, atLeastOnce()).append(anyString());
    }

    @Test
    public void testExecuteCommand() throws Exception {
        CommandMetadata metadata = mock(CommandMetadata.class);
        Command adminCommand = mock(Command.class);

        when(commandMetadataRepository.findByName(eq("cmd"))).thenReturn(metadata);
        when(applicationContext.getBean(eq("cmdCommand"))).thenReturn(adminCommand);
        when(entity.isAdmin()).thenReturn(false);
        when(metadata.isAdmin()).thenReturn(false);
        when(metadata.getName()).thenReturn("cmd");
        when(metadata.getBeanName()).thenReturn("cmdCommand");

        GameOutput result = helpCommand.execute(output, entity, "help", new String[] {"cmd"}, "help cmd");

        assertEquals(output, result);
        verify(commandMetadataRepository).findByName(eq("cmd"));
        verify(applicationContext).getBean("cmdCommand");
        verify(adminCommand).usage(eq(output), eq("cmd"));
        verify(adminCommand, never()).execute(any(GameOutput.class), any(Entity.class), anyString(), any(String[].class), anyString());
    }

    @Test
    public void testExecuteAdminCommand() throws Exception {
        CommandMetadata metadata = mock(CommandMetadata.class);
        Command adminCommand = mock(Command.class);

        when(commandMetadataRepository.findByName(eq("admin"))).thenReturn(metadata);
        when(applicationContext.getBean(eq("adminCommand"))).thenReturn(adminCommand);
        when(entity.isAdmin()).thenReturn(true);
        when(metadata.isAdmin()).thenReturn(true);
        when(metadata.getName()).thenReturn("admin");
        when(metadata.getBeanName()).thenReturn("adminCommand");

        GameOutput result = helpCommand.execute(output, entity, "help", new String[] {"admin"}, "help admin");

        assertEquals(output, result);
        verify(commandMetadataRepository).findByName(eq("admin"));
        verify(applicationContext).getBean("adminCommand");
        verify(adminCommand).usage(eq(output), eq("admin"));
        verify(adminCommand, never()).execute(any(GameOutput.class), any(Entity.class), anyString(), any(String[].class), anyString());
    }
}
