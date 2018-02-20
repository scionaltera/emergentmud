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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ZoneTest {
    private Zone zone;

    @Before
    public void setUp() {
        zone = new Zone();
        zone.setBottomLeftX(0L);
        zone.setBottomLeftY(0L);
        zone.setTopRightX(10L);
        zone.setTopRightY(10L);
    }

    @Test
    public void testEncompasses() {
        assertTrue(zone.encompasses(5L, 5L, 0L));
        assertTrue(zone.encompasses(0L, 0L, 0L));
        assertTrue(zone.encompasses(5L, 5L, 150L));
        assertFalse(zone.encompasses(-5L, -5L, 0L));
        assertFalse(zone.encompasses(15L, 15L, 0L));
    }
}
