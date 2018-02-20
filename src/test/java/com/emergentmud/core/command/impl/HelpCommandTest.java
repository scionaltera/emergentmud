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

package com.emergentmud.core.command.impl;

import com.emergentmud.core.command.Command;
import com.emergentmud.core.model.Capability;
import com.emergentmud.core.model.CommandMetadata;
import com.emergentmud.core.model.CommandRole;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.CapabilityRepository;
import com.emergentmud.core.repository.CommandMetadataRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HelpCommandTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private CommandMetadataRepository commandMetadataRepository;

    @Mock
    private CapabilityRepository capabilityRepository;

    @Mock
    private Capability normalCapability;

    @Mock
    private Capability adminCapability;

    @Mock
    private Capability superCapability;

    @Mock
    private Command normalCommand;

    @Mock
    private Command adminCommand;

    @Mock
    private GameOutput output;

    @Mock
    private Entity entity;

    private HelpCommand command;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        List<CommandMetadata> metadata = new ArrayList<>();
        CommandMetadata normal = mock(CommandMetadata.class);
        CommandMetadata admin = mock(CommandMetadata.class);

        when(normal.getName()).thenReturn("normal");
        when(normal.getBeanName()).thenReturn("normalCommand");
        when(normal.getCapability()).thenReturn(normalCapability);

        when(admin.getName()).thenReturn("admin");
        when(admin.getBeanName()).thenReturn("adminCommand");
        when(admin.getCapability()).thenReturn(adminCapability);

        metadata.add(normal);
        metadata.add(admin);

        when(normalCommand.getDescription()).thenReturn("A normal command.");
        when(adminCommand.getDescription()).thenReturn("An admin command.");
        when(applicationContext.getBean(eq("normalCommand"))).thenReturn(normalCommand);
        when(applicationContext.getBean(eq("adminCommand"))).thenReturn(adminCommand);
        when(commandMetadataRepository.findAll()).thenReturn(metadata);
        when(entity.isCapable(eq(normalCapability))).thenReturn(true);
        when(capabilityRepository.findByName(CommandRole.SUPER.name())).thenReturn(superCapability);

        command = new HelpCommand(applicationContext, commandMetadataRepository, capabilityRepository);
    }

    @Test
    public void testDescription() throws Exception {
        assertNotEquals("No description.", command.getDescription());
    }

    @Test
    public void testExecuteNoArgs() throws Exception {
        GameOutput result = command.execute(output, entity, "help", new String[0], "help");

        assertEquals(output, result);
        verify(output, atLeastOnce()).append(anyString());
        verify(entity).isCapable(eq(normalCapability));
        verify(entity).isCapable(eq(adminCapability));
        verify(normalCommand).getDescription();
        verify(adminCommand, never()).getDescription();
    }

    @Test
    public void testExecuteNoArgsAsAdmin() throws Exception {
        when(entity.isCapable(eq(adminCapability))).thenReturn(true);

        GameOutput result = command.execute(output, entity, "help", new String[0], "help");

        assertEquals(output, result);
        verify(output, atLeastOnce()).append(anyString());
        verify(entity).isCapable(eq(normalCapability));
        verify(entity).isCapable(eq(adminCapability));
        verify(normalCommand).getDescription();
        verify(adminCommand).getDescription();
    }

    @Test
    public void testExecuteNoArgsAsSuper() throws Exception {
        when(entity.isCapable(eq(superCapability))).thenReturn(true);

        GameOutput result = command.execute(output, entity, "help", new String[0], "help");

        assertEquals(output, result);
        verify(output, atLeastOnce()).append(anyString());
        verify(entity).isCapable(eq(normalCapability));
        verify(entity).isCapable(eq(adminCapability));
        verify(normalCommand).getDescription();
        verify(adminCommand).getDescription();
    }

    @Test
    public void testExecuteMissingCommand() throws Exception {
        GameOutput result = command.execute(output, entity, "help", new String[] {"foo"}, "help foo");

        assertEquals(output, result);
        verify(commandMetadataRepository).findByName(eq("foo"));
        verifyZeroInteractions(applicationContext);
        verify(output, atLeastOnce()).append(anyString());
    }

    @Test
    public void testExecuteHiddenCommand() throws Exception {
        CommandMetadata metadata = mock(CommandMetadata.class);

        when(metadata.getCapability()).thenReturn(adminCapability);

        GameOutput result = command.execute(output, entity, "help", new String[] {"admin"}, "help admin");

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
        when(metadata.getCapability()).thenReturn(normalCapability);
        when(metadata.getName()).thenReturn("cmd");
        when(metadata.getBeanName()).thenReturn("cmdCommand");

        GameOutput result = command.execute(output, entity, "help", new String[] {"cmd"}, "help cmd");

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
        when(entity.isCapable(eq(adminCapability))).thenReturn(true);
        when(metadata.getCapability()).thenReturn(adminCapability);
        when(metadata.getName()).thenReturn("admin");
        when(metadata.getBeanName()).thenReturn("adminCommand");

        GameOutput result = command.execute(output, entity, "help", new String[] {"admin"}, "help admin");

        assertEquals(output, result);
        verify(commandMetadataRepository).findByName(eq("admin"));
        verify(applicationContext).getBean("adminCommand");
        verify(adminCommand).usage(eq(output), eq("admin"));
        verify(adminCommand, never()).execute(any(GameOutput.class), any(Entity.class), anyString(), any(String[].class), anyString());
    }

    @Test
    public void testExecuteAdminCommandAsSuper() throws Exception {
        CommandMetadata metadata = mock(CommandMetadata.class);
        Command adminCommand = mock(Command.class);

        when(commandMetadataRepository.findByName(eq("admin"))).thenReturn(metadata);
        when(applicationContext.getBean(eq("adminCommand"))).thenReturn(adminCommand);
        when(entity.isCapable(eq(superCapability))).thenReturn(true);
        when(metadata.getCapability()).thenReturn(adminCapability);
        when(metadata.getName()).thenReturn("admin");
        when(metadata.getBeanName()).thenReturn("adminCommand");

        GameOutput result = command.execute(output, entity, "help", new String[] {"admin"}, "help admin");

        assertEquals(output, result);
        verify(commandMetadataRepository).findByName(eq("admin"));
        verify(applicationContext).getBean("adminCommand");
        verify(adminCommand).usage(eq(output), eq("admin"));
        verify(adminCommand, never()).execute(any(GameOutput.class), any(Entity.class), anyString(), any(String[].class), anyString());
    }
}
