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

import com.emergentmud.core.model.Account;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DataCommandTest {
    @Mock
    private EntityRepository entityRepository;

    @Mock
    private Entity entity;

    @Mock
    private Entity able;

    @Mock
    private Entity baker;

    @Mock
    private Account ableAccount;

    @Mock
    private Account bakerAccount;

    @Spy
    private GameOutput output;

    private List<Entity> entities = new ArrayList<>();

    private DataCommand command;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(able.getName()).thenReturn("Able");
        when(baker.getName()).thenReturn("Baker");

        // purposefully not in alphabetical order
        entities.add(baker);
        entities.add(able);

        when(entityRepository.findByAccountIsNotNull(any(Sort.class))).thenReturn(entities);
        when(able.getAccount()).thenReturn(ableAccount);
        when(baker.getAccount()).thenReturn(bakerAccount);

        command = new DataCommand(entityRepository);
    }

    @Test
    public void testDescription() throws Exception {
        assertNotEquals("No description.", command.getDescription());
    }

    @Test
    public void testExecuteNoArgs() throws Exception {
        GameOutput result = command.execute(output, entity, "data", new String[] {}, "");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Usage")));
    }

    @Test
    public void testExecuteEssence() throws Exception {
        GameOutput result = command.execute(output, entity, "data", new String[] { "entity" }, "entity");

        assertTrue(result.getOutput().get(0).contains("Player Characters in Database"));
        assertTrue(result.getOutput().get(1).contains("Name"));
        assertTrue(result.getOutput().get(1).contains("Able"));
        assertTrue(result.getOutput().get(1).contains("Baker"));
        assertTrue(result.getOutput().get(2).contains("2 Entities listed."));
    }

    @Test
    public void testExecuteSingleEssence() throws Exception {
        entities.remove(baker);

        GameOutput result = command.execute(output, entity, "data", new String[] { "entity" }, "entity");

        assertTrue(result.getOutput().get(0).contains("Player Characters in Database"));
        assertTrue(result.getOutput().get(1).contains("Name"));
        assertTrue(result.getOutput().get(1).contains("Able"));
        assertTrue(result.getOutput().get(2).contains("1 Entity listed."));
    }

    @Test
    public void testExecuteEssenceWrongArg() throws Exception {
        GameOutput result = command.execute(output, entity, "data", new String[] { "farts" }, "farts");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Usage")));
    }
}
