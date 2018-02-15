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

package com.emergentmud.core.logging;

import com.emergentmud.core.model.Capability;
import com.emergentmud.core.model.CommandRole;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.CapabilityRepository;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.service.EntityService;
import com.emergentmud.core.util.SpringContextSingleton;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

import static org.mockito.Mockito.*;

public class InWorldAppenderTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private EntityService entityService;

    @Mock
    private EntityRepository entityRepository;

    @Mock
    private CapabilityRepository capabilityRepository;

    @Mock
    private Entity admin;

    @Mock
    private Entity player;

    @Mock
    private Entity adminOffline;

    @Mock
    private Entity playerOffline;

    @Mock
    private Room room;

    @Mock
    private Capability capability;

    private String eventObject = "I am a log message!";
    private SpringContextSingleton singleton = new SpringContextSingleton();

    private InWorldAppender<String> inWorldAppender;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        singleton.setup();

        when(capabilityRepository.findByName(eq(CommandRole.LOG.name()))).thenReturn(capability);
        when(admin.isCapable(eq(capability))).thenReturn(true);
        when(adminOffline.isCapable(eq(capability))).thenReturn(true);
        when(admin.getX()).thenReturn(0L);
        when(admin.getY()).thenReturn(0L);
        when(admin.getZ()).thenReturn(0L);
        when(player.getX()).thenReturn(0L);
        when(player.getY()).thenReturn(0L);
        when(player.getZ()).thenReturn(0L);
        when(entityRepository.findByXIsNotNullAndYIsNotNullAndZIsNotNull()).thenReturn(Arrays.asList(admin, player));

        inWorldAppender = new InWorldAppender<>();
    }

    @Test
    public void testEarlyStartup() throws Exception {
        inWorldAppender.append(eventObject);

        verifyZeroInteractions(entityService);
        verifyZeroInteractions(entityRepository);
        verifyZeroInteractions(admin);
        verifyZeroInteractions(adminOffline);
        verifyZeroInteractions(player);
        verifyZeroInteractions(playerOffline);
        verifyZeroInteractions(room);
    }

    @Test
    public void testContextInitialized() throws Exception {
        singleton.setApplicationContext(applicationContext);

        inWorldAppender.append(eventObject);

        verifyZeroInteractions(entityService);
        verifyZeroInteractions(entityRepository);
        verifyZeroInteractions(admin);
        verifyZeroInteractions(adminOffline);
        verifyZeroInteractions(player);
        verifyZeroInteractions(playerOffline);
        verifyZeroInteractions(room);
    }

    @Test
    public void testBeansInitialized() throws Exception {
        singleton.setApplicationContext(applicationContext);

        when(applicationContext.getBean("entityService")).thenReturn(entityService);
        when(applicationContext.getBean("entityRepository")).thenReturn(entityRepository);
        when(applicationContext.getBean("capabilityRepository")).thenReturn(capabilityRepository);

        inWorldAppender.append(eventObject);

        verify(entityRepository).findByXIsNotNullAndYIsNotNullAndZIsNotNull();
        verify(entityService).sendMessageToListeners(anyListOf(Entity.class), any(GameOutput.class));
        verify(admin).isCapable(eq(capability));
        verify(player).isCapable(eq(capability));
        verifyZeroInteractions(adminOffline);
        verifyZeroInteractions(playerOffline);
    }
}
