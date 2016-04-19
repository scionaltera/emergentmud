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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EssenceTest {
    private Essence essence;

    @Before
    public void setUp() throws Exception {
        essence = new Essence();
    }

    @Test
    public void testId() throws Exception {
        essence.setId("id");

        assertEquals("id", essence.getId());
    }

    @Test
    public void testAccountId() throws Exception {
        essence.setAccountId("accountId");

        assertEquals("accountId", essence.getAccountId());
    }

    @Test
    public void testName() throws Exception {
        essence.setName("name");

        assertEquals("name", essence.getName());
    }
}
