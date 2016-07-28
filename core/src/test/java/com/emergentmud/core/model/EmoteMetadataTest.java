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

package com.emergentmud.core.model;

import org.junit.Test;

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
        emoteMetadata.setId("id");

        assertEquals("id", emoteMetadata.getId());
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
    public void testToSelf() throws Exception {
        emoteMetadata.setToSelf("to self");

        assertEquals("to self", emoteMetadata.getToSelf());
    }

    @Test
    public void testToTarget() throws Exception {
        emoteMetadata.setToTarget("to target");

        assertEquals("to target", emoteMetadata.getToTarget());
    }

    @Test
    public void testToRoom() throws Exception {
        emoteMetadata.setToRoom("to room");

        assertEquals("to room", emoteMetadata.getToRoom());
    }
}
