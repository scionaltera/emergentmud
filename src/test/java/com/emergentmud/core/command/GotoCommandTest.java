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
import com.emergentmud.core.repository.WorldManager;
import com.emergentmud.core.util.EntityUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GotoCommandTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private WorldManager worldManager;

    @Mock
    private EntityUtil entityUtil;

    @Spy
    private GameOutput output;

    @Mock
    private Entity entity;

    @Mock
    private Room room;

    @Mock
    private Room destination;

    @Mock
    private LookCommand lookCommand;

    private String cmd = "goto";

    private GotoCommand gotoCommand;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(entity.getRoom()).thenReturn(room);
        when(worldManager.put(eq(entity), eq(1000L), eq(1000L), eq(0L))).thenReturn(destination);
        when(applicationContext.getBean(eq("lookCommand"))).thenReturn(lookCommand);

        gotoCommand = new GotoCommand(applicationContext, worldManager, entityUtil);
    }

    @Test
    public void testGotoNoArgs() throws Exception {
        GameOutput result = gotoCommand.execute(output, entity, cmd, new String[] {}, "");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Usage: ")));

        verifyZeroInteractions(applicationContext);
        verifyZeroInteractions(entityUtil);
        verifyZeroInteractions(worldManager);
    }

    @Test
    public void testGotoOneArg() throws Exception {
        GameOutput result = gotoCommand.execute(output, entity, cmd, new String[] { "1000" }, "1000");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Usage: ")));

        verifyZeroInteractions(applicationContext);
        verifyZeroInteractions(entityUtil);
        verifyZeroInteractions(worldManager);
    }

    @Test
    public void testGotoTwoArgs() throws Exception {
        GameOutput result = gotoCommand.execute(output, entity, cmd, new String[] {"1000", "1000"}, "1000 1000");

        assertFalse(result.getOutput().stream().anyMatch(line -> line.contains("Usage: ")));

        verify(entityUtil).sendMessageToRoom(eq(room), eq(entity), any(GameOutput.class));
        verify(worldManager).put(eq(entity), eq(1000L), eq(1000L), eq(0L));
        verify(entityUtil).sendMessageToRoom(eq(destination), eq(entity), any(GameOutput.class));
        verify(applicationContext).getBean(eq("lookCommand"));
        verify(lookCommand).execute(eq(output), eq(entity), eq("look"), any(), eq(""));
    }

    @Test
    public void testGotoThreeArgs() throws Exception {
        GameOutput result = gotoCommand.execute(output, entity, cmd, new String[] {"1000", "1000", "0"}, "1000 1000 0");

        assertFalse(result.getOutput().stream().anyMatch(line -> line.contains("Usage: ")));

        verify(entityUtil).sendMessageToRoom(eq(room), eq(entity), any(GameOutput.class));
        verify(worldManager).put(eq(entity), eq(1000L), eq(1000L), eq(0L));
        verify(entityUtil).sendMessageToRoom(eq(destination), eq(entity), any(GameOutput.class));
        verify(applicationContext).getBean(eq("lookCommand"));
        verify(lookCommand).execute(eq(output), eq(entity), eq("look"), any(), eq(""));
    }

    @Test
    public void testGotoTwoArgsBad() throws Exception {
        GameOutput result = gotoCommand.execute(output, entity, cmd, new String[] { "1000", "bad" }, "1000 bad");

        assertTrue(result.getOutput().stream().anyMatch(line -> line.contains("Usage: ")));

        verifyZeroInteractions(applicationContext);
        verifyZeroInteractions(entityUtil);
        verifyZeroInteractions(worldManager);
    }

    @Test
    public void testGotoTwoArgsNullOrigin() throws Exception {
        when(entity.getRoom()).thenReturn(null);

        GameOutput result = gotoCommand.execute(output, entity, cmd, new String[] {"1000", "1000"}, "1000 1000");

        assertFalse(result.getOutput().stream().anyMatch(line -> line.contains("Usage: ")));

        verify(entityUtil, never()).sendMessageToRoom(eq(room), eq(entity), any(GameOutput.class));
        verify(worldManager).put(eq(entity), eq(1000L), eq(1000L), eq(0L));
        verify(entityUtil).sendMessageToRoom(eq(destination), eq(entity), any(GameOutput.class));
        verify(applicationContext).getBean(eq("lookCommand"));
        verify(lookCommand).execute(eq(output), eq(entity), eq("look"), any(), eq(""));
    }
}
