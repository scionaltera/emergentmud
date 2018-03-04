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

package com.emergentmud.core.event;

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.service.EntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class GameShutdownListenerTest {
    @Mock
    private EntityRepository entityRepository;

    @Mock
    private EntityService entityService;

    @Mock
    private ContextClosedEvent contextClosedEvent;

    @Mock
    private ContextStoppedEvent contextStoppedEvent;

    @Mock
    private ContextStartedEvent contextStartedEvent;

    private List<Entity> everyone = new ArrayList<>();

    private GameShutdownListener gameShutdownListener;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        for (int i = 0; i < 5; i++) {
            Entity entity = mock(Entity.class);

            everyone.add(entity);
        }

        when(entityRepository.findByLocationIsNotNull()).thenReturn(everyone);

        gameShutdownListener = new GameShutdownListener(entityRepository, entityService);
    }

    @Test
    public void testContextClosed() throws Exception {
        gameShutdownListener.onApplicationEvent(contextClosedEvent);

        verify(entityRepository).findByLocationIsNotNull();
        verify(entityService).sendMessageToListeners(eq(everyone), any(GameOutput.class));
    }

    @Test
    public void testContextStopped() throws Exception {
        gameShutdownListener.onApplicationEvent(contextStoppedEvent);

        verify(entityRepository).findByLocationIsNotNull();
        verify(entityService).sendMessageToListeners(eq(everyone), any(GameOutput.class));
    }

    @Test
    public void testContextStarted() throws Exception {
        gameShutdownListener.onApplicationEvent(contextStartedEvent);

        verifyZeroInteractions(entityRepository);
        verifyZeroInteractions(entityService);
    }
}
