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

package com.emergentmud.core.model;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class EmoteMetadataTest {
    private EmoteMetadata emoteMetadata;

    public EmoteMetadataTest() {
        emoteMetadata = new EmoteMetadata();
    }

    @Test
    public void testConstructor() throws Exception {
        EmoteMetadata metadata = new EmoteMetadata("wink", 75);

        assertEquals("wink", metadata.getName());
        assertEquals(75, (int)metadata.getPriority());
    }

    @Test
    public void testId() throws Exception {
        UUID uuid = UUID.randomUUID();

        emoteMetadata.setId(uuid);

        assertEquals(uuid, emoteMetadata.getId());
    }

    @Test
    public void testName() throws Exception {
        emoteMetadata.setName("nod");

        assertEquals("nod", emoteMetadata.getName());
    }

    @Test
    public void testPriority() throws Exception {
        emoteMetadata.setPriority(99);

        assertEquals(99, (int)emoteMetadata.getPriority());
    }

    @Test
    public void testToSelfUntargeted() throws Exception {
        emoteMetadata.setToSelfUntargeted("test");

        assertEquals("test", emoteMetadata.getToSelfUntargeted());
    }

    @Test
    public void testToRoomUntargeted() throws Exception {
        emoteMetadata.setToRoomUntargeted("test");

        assertEquals("test", emoteMetadata.getToRoomUntargeted());
    }

    @Test
    public void testToSelfWithTarget() throws Exception {
        emoteMetadata.setToSelfWithTarget("test");

        assertEquals("test", emoteMetadata.getToSelfWithTarget());
    }

    @Test
    public void testToTarget() throws Exception {
        emoteMetadata.setToTarget("test");

        assertEquals("test", emoteMetadata.getToTarget());
    }

    @Test
    public void testToRoomWithTarget() throws Exception {
        emoteMetadata.setToRoomWithTarget("test");

        assertEquals("test", emoteMetadata.getToRoomWithTarget());
    }

    @Test
    public void testToSelfAsTarget() throws Exception {
        emoteMetadata.setToSelfAsTarget("test");

        assertEquals("test", emoteMetadata.getToSelfAsTarget());
    }

    @Test
    public void testToRoomTargetingSelf() throws Exception {
        emoteMetadata.setToRoomTargetingSelf("test");

        assertEquals("test", emoteMetadata.getToRoomTargetingSelf());
    }
}
