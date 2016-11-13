/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016 Peter Keeler
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

package com.emergentmud.core.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InputUtilTest {
    private InputUtil inputUtil;

    @Before
    public void setUp() throws Exception {
        inputUtil = new InputUtil();
    }

    @Test
    public void testChopSingleWord() throws Exception {
        String in = "able baker charlie dog easy fox";
        String out = inputUtil.chopWords(in);

        assertEquals("baker charlie dog easy fox", out);
    }

    @Test
    public void testChopTwoWords() throws Exception {
        String in = "able baker charlie dog easy fox";
        String out = inputUtil.chopWords(in, 2);

        assertEquals("charlie dog easy fox", out);
    }

    @Test
    public void testChopThreeWords() throws Exception {
        String in = "able baker charlie dog easy fox";
        String out = inputUtil.chopWords(in, 3);

        assertEquals("dog easy fox", out);
    }

    @Test
    public void testWithTabs() throws Exception {
        String in = "able\tbaker\tcharlie\tdog\teasy\tfox";
        String out = inputUtil.chopWords(in);

        assertEquals("baker\tcharlie\tdog\teasy\tfox", out);
    }

    @Test
    public void testWithMultipleSpaces() throws Exception {
        String in = "able   baker   charlie   dog   easy  fox";
        String out = inputUtil.chopWords(in);

        assertEquals("baker   charlie   dog   easy  fox", out);
    }
}
