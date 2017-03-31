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

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.junit.Assert.*;

public class BaseCommandTest {
    @Spy
    private GameOutput output;

    private BaseCommand baseCommand = new BaseCommand() {
        @Override
        public GameOutput execute(GameOutput output, Entity entity, String command, String[] tokens, String raw) {
            return output;
        }
    };

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDescription() throws Exception {
        baseCommand.setDescription("This is the description.");

        assertEquals("This is the description.", baseCommand.getDescription());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testThatParameterListIsUnmodifiable() throws Exception {
        baseCommand.getParameters().remove(0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testThatSubCommandListIsUnmodifiable() throws Exception {
        baseCommand.getSubCommands().remove(0);
    }

    @Test
    public void testCommandWithNoArgs() throws Exception {
        baseCommand.setDescription("This is the description.");

        GameOutput result = baseCommand.usage(output, "test");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Usage")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Description")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("This is the description.")));
    }

    @Test
    public void testCommandWithOneParameter() throws Exception {
        baseCommand.setDescription("This is the description.");
        baseCommand.addParameter("foo", true);

        GameOutput result = baseCommand.usage(output, "test");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Usage")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Description")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("This is the description.")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("foo")));
    }

    @Test
    public void testCommandWithTwoParameters() throws Exception {
        baseCommand.setDescription("This is the description.");
        baseCommand.addParameter("foo", true);
        baseCommand.addParameter("bar", false);

        GameOutput result = baseCommand.usage(output, "test");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Usage")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Description")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("This is the description.")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("foo")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("bar")));
    }

    @Test
    public void testCommandWithOneSubcommand() throws Exception {
        baseCommand.setDescription("This is the description.");
        baseCommand.addSubcommand("foo", "Foos a bar.");

        GameOutput result = baseCommand.usage(output, "test");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Usage")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Description")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("This is the description.")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("foo")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Foos a bar.")));
    }

    @Test
    public void testCommandWithTwoSubcommands() throws Exception {
        baseCommand.setDescription("This is the description.");
        baseCommand.addSubcommand("foo", "Foos a bar.");
        baseCommand.addSubcommand("bam", "Bams a baz.");

        GameOutput result = baseCommand.usage(output, "test");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Usage")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Description")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("This is the description.")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("foo")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Foos a bar.")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("bam")));
        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Bams a baz.")));
    }
}
