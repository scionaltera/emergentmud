/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016-2018 Peter Keeler
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

import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class PronounTest {
    private Pronoun pronoun;

    @Before
    public void setUp() {
        pronoun = new Pronoun();
    }

    @Test
    public void testId() {
        UUID id = UUID.randomUUID();

        pronoun.setId(id);

        assertEquals(id, pronoun.getId());
    }

    @Test
    public void testName() {
        String name = "male";

        pronoun.setName(name);

        assertEquals(name, pronoun.getName());
    }

    @Test
    public void testSubject() {
        String subject = "he";

        pronoun.setSubject(subject);

        assertEquals(subject, pronoun.getSubject());
    }

    @Test
    public void testObject() {
        String object = "him";

        pronoun.setObject(object);

        assertEquals(object, pronoun.getObject());
    }

    @Test
    public void testPossessive() {
        String possessive = "his";

        pronoun.setPossessive(possessive);

        assertEquals(possessive, pronoun.getPossessive());
    }

    @Test
    public void testPossessivePronoun() {
        String possessive = "his";

        pronoun.setPossessivePronoun(possessive);

        assertEquals(possessive, pronoun.getPossessivePronoun());
    }

    @Test
    public void testReflexive() {
        String reflexive = "himself";

        pronoun.setReflexive(reflexive);

        assertEquals(reflexive, pronoun.getReflexive());
    }
}
