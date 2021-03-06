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

import static org.junit.Assert.*;

public class BiomeTest {
    private Biome biome;

    @Before
    public void setUp() throws Exception {
        biome = new Biome("Biome", 0x112233, "cellSelectionStrategy");
    }

    @Test
    public void testId() throws Exception {
        UUID guid2 = UUID.randomUUID();
        UUID guid = UUID.randomUUID();

        biome.setId(guid);
        assertEquals(guid, biome.getId());
        biome.setId(guid2);
        assertEquals(guid2, biome.getId());
    }

    @Test
    public void testName() throws Exception {
        assertEquals("Biome", biome.getName());
    }

    @Test
    public void testColor() throws Exception {
        assertEquals(0x112233, (long)biome.getColor());
    }
}
