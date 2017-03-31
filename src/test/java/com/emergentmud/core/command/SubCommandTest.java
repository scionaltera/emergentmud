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

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SubCommandTest {
    private List<Parameter> parameters = new ArrayList<>();

    private SubCommand subCommand;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        for (int i = 0; i < 3; i++) {
            Parameter parameter = mock(Parameter.class);

            when(parameter.getName()).thenReturn("parameter" + i);
            when(parameter.isRequired()).thenReturn(true);

            parameters.add(parameter);
        }

        subCommand = new SubCommand("test", "Tests things.", parameters);
    }

    @Test
    public void testGetters() throws Exception {
        assertEquals("test", subCommand.getName());
        assertEquals("Tests things.", subCommand.getDescription());

        subCommand.getParameters().forEach(p -> {
            assertTrue(p.getName().startsWith("parameter"));
            assertTrue(p.isRequired());
        });
    }
}
