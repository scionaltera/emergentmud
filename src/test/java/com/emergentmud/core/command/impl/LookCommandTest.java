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

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Biome;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.service.RoomService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LookCommandTest {
    @Mock
    private EntityRepository entityRepository;

    @Mock
    private RoomService roomService;

    @Mock
    private GameOutput output;

    @Mock
    private Entity entity;

    @Mock
    private Room room;

    @Mock
    private Biome biome;

    private String[] tokens = new String[0];
    private String raw = "";
    private String cmd = "look";

    private LookCommand command;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        command = new LookCommand(entityRepository, roomService);
    }

    @Test
    public void testDescription() throws Exception {
        assertNotEquals("No description.", command.getDescription());
    }

    @Test
    public void testLookVoid() throws Exception {
        when(entity.getX()).thenReturn(null);
        when(entity.getY()).thenReturn(null);
        when(entity.getZ()).thenReturn(null);

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(output).append(anyString());
        verifyZeroInteractions(entityRepository);
    }

    @Test
    public void testLook() throws Exception {
        when(roomService.fetchRoom(eq(0L), eq(0L), eq(0L), anyBoolean())).thenReturn(room);
        when(biome.getName()).thenReturn("Blasted Hellscape");


        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
    }
}
