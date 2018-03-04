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

import com.emergentmud.core.model.EmoteMetadata;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Pronoun;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import com.emergentmud.core.service.EntityService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EmoteTest {
    @Mock
    private EntityRepository entityRepository;

    @Mock
    private EntityService entityService;

    @Mock
    private GameOutput output;

    @Mock
    private EmoteMetadata metadata;

    @Mock
    private Entity entity;

    @Mock
    private Entity target;

    @Mock
    private Entity observer;

    @Mock
    private Pronoun malePronoun;

    @Captor
    private ArgumentCaptor<GameOutput> outputCaptor;

    private Emote emote;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(malePronoun.getSubject()).thenReturn("he");
        when(malePronoun.getObject()).thenReturn("him");
        when(malePronoun.getPossessive()).thenReturn("his");
        when(malePronoun.getPossessivePronoun()).thenReturn("his");
        when(malePronoun.getReflexive()).thenReturn("himself");

        when(metadata.getToSelfUntargeted()).thenReturn("You grin.");
        when(metadata.getToRoomUntargeted()).thenReturn("%self% grins.");
        when(metadata.getToSelfWithTarget()).thenReturn("You grin at %target%.");
        when(metadata.getToTarget()).thenReturn("%self% grins at you.");
        when(metadata.getToRoomWithTarget()).thenReturn("%self% grins at %target%.");
        when(metadata.getToSelfAsTarget()).thenReturn("You grin to yourself.");
        when(metadata.getToRoomTargetingSelf()).thenReturn("%self% grins to %himself%.");

        when(entity.getName()).thenReturn("Scion");
        when(entity.getGender()).thenReturn(malePronoun);
        when(entity.getX()).thenReturn(0L);
        when(entity.getY()).thenReturn(0L);
        when(entity.getZ()).thenReturn(0L);

        when(target.getName()).thenReturn("Bnarg");
        when(target.getGender()).thenReturn(malePronoun);
        when(target.getX()).thenReturn(0L);
        when(target.getY()).thenReturn(0L);
        when(target.getZ()).thenReturn(0L);

        when(observer.getName()).thenReturn("Ghan");
        when(observer.getGender()).thenReturn(malePronoun);
        when(observer.getX()).thenReturn(0L);
        when(observer.getY()).thenReturn(0L);
        when(observer.getZ()).thenReturn(0L);

        List<Entity> entities = new ArrayList<>();

        entities.add(entity);
        entities.add(target);
        entities.add(observer);

        when(entityRepository.findByLocation(eq(0L), eq(0L), eq(0L))).thenReturn(entities);

        emote = new Emote(entityRepository, entityService);
    }

    @Test
    public void testMissingRequiredFields() throws Exception {
        when(metadata.getToRoomUntargeted()).thenReturn(null);
        when(metadata.getToSelfWithTarget()).thenReturn(null);
        when(metadata.getToTarget()).thenReturn(null);
        when(metadata.getToRoomWithTarget()).thenReturn(null);

        emote.execute(output, metadata, entity, new String[0]);

        verify(output).append(eq("Huh?"));
        verifyZeroInteractions(entityRepository);
        verifyZeroInteractions(entityService);
    }

    @Test
    public void testEmote() throws Exception {
        emote.execute(output, metadata, entity, new String[0]);

        verify(output).append("You grin.");
        verify(entityService).sendMessageToRoom(eq(0L), eq(0L), eq(0L), eq(entity), outputCaptor.capture());

        GameOutput observerOutput = outputCaptor.getValue();

        assertTrue(observerOutput.getOutput().stream().anyMatch("Scion grins."::equals));
    }

    @Test
    public void testTargetNotFound() throws Exception {
        emote.execute(output, metadata, entity, new String[] {"Xander"});

        verify(output).append("You do not see anyone by that name here.");
    }

    @Test
    public void testTargetedEmote() throws Exception {
        emote.execute(output, metadata, entity, new String[]{"Bnarg"});

        verify(output).append("You grin at Bnarg.");
        verify(entityService).sendMessageToEntity(eq(target), outputCaptor.capture());
        verify(entityService).sendMessageToListeners(anyListOf(Entity.class), outputCaptor.capture());

        List<GameOutput> outputs = outputCaptor.getAllValues();
        GameOutput targetOutput = outputs.get(0);
        GameOutput observerOutput = outputs.get(1);

        assertTrue(targetOutput.getOutput().stream().anyMatch("Scion grins at you."::equals));
        assertTrue(observerOutput.getOutput().stream().anyMatch("Scion grins at Bnarg."::equals));
    }

    @Test
    public void testMe() throws Exception {
        emote.execute(output, metadata, entity, new String[] {"me"});

        verify(output).append("You grin to yourself.");
        verify(entityService).sendMessageToListeners(anyListOf(Entity.class), outputCaptor.capture());

        GameOutput observerOutput = outputCaptor.getValue();

        assertTrue(observerOutput.getOutput().stream().anyMatch("Scion grins to himself."::equals));
    }

    @Test
    public void testSelf() throws Exception {
        emote.execute(output, metadata, entity, new String[] {"self"});

        verify(output).append("You grin to yourself.");
        verify(entityService).sendMessageToListeners(anyListOf(Entity.class), outputCaptor.capture());

        GameOutput observerOutput = outputCaptor.getValue();

        assertTrue(observerOutput.getOutput().stream().anyMatch("Scion grins to himself."::equals));
    }

    @Test
    public void testSelfUnsupported() throws Exception {
        when(metadata.getToSelfAsTarget()).thenReturn(null);
        when(metadata.getToRoomTargetingSelf()).thenReturn(null);

        emote.execute(output, metadata, entity, new String[] {"self"});

        verify(output).append("Sorry, this emote doesn't support targeting yourself.");
        verifyZeroInteractions(entityService);
    }
}
