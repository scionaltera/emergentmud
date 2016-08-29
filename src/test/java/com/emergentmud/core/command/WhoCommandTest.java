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

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class WhoCommandTest {
    @Mock
    private EntityRepository entityRepository;

    @Mock
    private Entity player1;

    @Mock
    private Entity player2;

    @Mock
    private Entity self;

    @Spy
    private GameOutput output;

    private WhoCommand whoCommand;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        whoCommand = new WhoCommand(entityRepository);
    }

    @Test
    public void testAlone() throws Exception {
        List<Entity> online = Collections.singletonList(self);

        when(entityRepository.findByRoomIsNotNull()).thenReturn(online);

        GameOutput response = whoCommand.execute(output, self, new String[] {}, "");

        verify(entityRepository).findByRoomIsNotNull();
        verify(self).getName();

        assertTrue(response.getOutput().get(2).startsWith("1 player"));
    }

    @Test
    public void testCrowd() throws Exception {
        List<Entity> online = Arrays.asList(self, player1, player2);

        when(entityRepository.findByRoomIsNotNull()).thenReturn(online);

        GameOutput response = whoCommand.execute(output, self, new String[] {}, "");

        verify(entityRepository).findByRoomIsNotNull();
        verify(self).getName();
        verify(player1).getName();
        verify(player2).getName();

        assertTrue(response.getOutput().get(4).startsWith("3 players"));
    }
}
