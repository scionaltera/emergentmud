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

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InfoCommandTest {
    @Mock
    private GameOutput output;

    @Mock
    private Entity entity;

    @Mock
    private Entity target;

    @Mock
    private Room room;

    @Mock
    private EntityRepository entityRepository;

    private InfoCommand infoCommand;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(entity.getRoom()).thenReturn(room);
        when(entity.getName()).thenReturn("Scion");
        when(target.getName()).thenReturn("Bnarg");

        infoCommand = new InfoCommand(entityRepository);
    }

    @Test
    public void testTooManyArgs() throws Exception {
        GameOutput result = infoCommand.execute(output, entity, new String[] { "unexpected", "args" }, "unexpected args");

        assertNotNull(result);
        verifyZeroInteractions(entity);
    }

    @Test
    public void testExecute() throws Exception {
        when(entityRepository.findByRoom(room)).thenReturn(Arrays.asList(entity, target));

        GameOutput result = infoCommand.execute(output, entity, new String[] {}, "");

        assertNotNull(result);
        verify(entity).getId();
        verify(entity).getName();
        verify(entity).getStompUsername();
        verify(entity).getStompSessionId();
        verify(output, atLeastOnce()).append(anyString());
    }

    @Test
    public void testExecuteTarget() throws Exception {
        when(entityRepository.findByRoom(room)).thenReturn(Arrays.asList(entity, target));

        GameOutput result = infoCommand.execute(output, entity, new String[] { "bnarg" }, "bnarg");

        assertNotNull(result);
        verify(target).getId();
        verify(target, times(2)).getName();
        verify(target).getStompUsername();
        verify(target).getStompSessionId();
        verify(output, atLeastOnce()).append(anyString());
    }

    @Test
    public void testExecuteInvalidTarget() throws Exception {
        when(entityRepository.findByRoom(room)).thenReturn(Arrays.asList(entity, target));

        GameOutput result = infoCommand.execute(output, entity, new String[] { "morgan" }, "morgan");

        assertNotNull(result);
        verify(target).getName();
        verify(entity).getName();
        verify(entity).getRoom();
        verifyNoMoreInteractions(target, entity);
        verify(output, atLeastOnce()).append(anyString());
    }
}
