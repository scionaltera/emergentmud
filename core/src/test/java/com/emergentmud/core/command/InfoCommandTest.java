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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InfoCommandTest {
    @Mock
    private GameOutput output;

    @Mock
    private Entity entity;

    private String[] tokens = new String[0];
    private String raw = "";

    private InfoCommand infoCommand;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        infoCommand = new InfoCommand();
    }

    @Test
    public void testExecute() throws Exception {
        GameOutput result = infoCommand.execute(output, entity, tokens, raw);

        assertNotNull(result);
        verify(entity).getId();
        verify(entity).getName();
        verify(entity).getStompUsername();
        verify(entity).getStompSessionId();
        verify(output, atLeastOnce()).append(anyString());
    }
}
