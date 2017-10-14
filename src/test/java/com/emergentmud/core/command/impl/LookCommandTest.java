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

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.room.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.repository.RoomBuilder;
import com.emergentmud.core.repository.RoomRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LookCommandTest {
    @Mock
    private EntityRepository entityRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomBuilder roomBuilder;

    @Mock
    private GameOutput output;

    @Mock
    private Entity entity;

    @Mock
    private Room room;

    private String[] tokens = new String[0];
    private String raw = "";
    private List<Entity> contents = new ArrayList<>();
    private String cmd = "look";

    private LookCommand command;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(entity.getId()).thenReturn("Tester1");
        when(entity.getX()).thenCallRealMethod();
        when(entity.getY()).thenCallRealMethod();
        when(entity.getZ()).thenCallRealMethod();
        doCallRealMethod().when(entity).setX(anyLong());
        doCallRealMethod().when(entity).setY(anyLong());
        doCallRealMethod().when(entity).setZ(anyLong());
        when(room.getX()).thenCallRealMethod();
        when(room.getY()).thenCallRealMethod();
        when(room.getZ()).thenCallRealMethod();
        doCallRealMethod().when(room).setX(anyLong());
        doCallRealMethod().when(room).setY(anyLong());
        doCallRealMethod().when(room).setZ(anyLong());
        when(entityRepository.findByXAndYAndZ(eq(0L), eq(0L), eq(0L))).thenReturn(contents);

        for (int i = 0; i < 3; i++) {
            Entity entity = mock(Entity.class);

            when(entity.getId()).thenReturn(Integer.toString(i));
            when(entity.getName()).thenReturn("Tester" + i);

            contents.add(entity);
        }

        command = new LookCommand(entityRepository, roomBuilder);
    }

    @Test
    public void testDescription() throws Exception {
        assertNotEquals("No description.", command.getDescription());
    }

    @Test
    public void testLookVoid() throws Exception {
        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(output).append(anyString());
        verifyZeroInteractions(entityRepository);
    }

    @Test
    public void testLook() throws Exception {
        when(roomRepository.findByXAndYAndZ(eq(0L), eq(1L), eq(0L))).thenReturn(mock(Room.class));
        when(roomRepository.findByXAndYAndZ(eq(1L), eq(0L), eq(0L))).thenReturn(mock(Room.class));
        when(roomRepository.findByXAndYAndZ(eq(0L), eq(-1L), eq(0L))).thenReturn(mock(Room.class));
        when(roomRepository.findByXAndYAndZ(eq(-1L), eq(0L), eq(0L))).thenReturn(mock(Room.class));

        room.setX(0L);
        room.setY(0L);
        room.setZ(0L);
        entity.setX(0L);
        entity.setY(0L);
        entity.setZ(0L);

        GameOutput result = command.execute(output, entity, cmd, tokens, raw);

        assertNotNull(result);
        verify(output, atLeast(3)).append(anyString());
        verify(output).append(startsWith("[dcyan]Exits:"));
        verify(entityRepository).findByXAndYAndZ(eq(0L), eq(0L), eq(0L));

        contents.forEach(e -> {
                    if (!"Tester1".equals(e.getId())) {
                        verify(e, atLeastOnce()).getId();
                        verify(e).getName();
                    } else {
                        verify(e, never()).getName();
                    }
                });
    }
}
