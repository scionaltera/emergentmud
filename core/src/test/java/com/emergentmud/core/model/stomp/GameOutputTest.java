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

package com.emergentmud.core.model.stomp;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class GameOutputTest {
    @Test
    public void testBareConstructor() throws Exception {
        GameOutput output = new GameOutput();
        List<String> outputList = output.getOutput();

        assertTrue(outputList.isEmpty());
    }

    @Test
    public void testSingleArgConstructor() throws Exception {
        String text = "This text is a test.";
        GameOutput output = new GameOutput(text);
        List<String> outputList = output.getOutput();

        assertEquals(1, outputList.size());
        assertEquals(text, outputList.get(0));
    }

    @Test
    public void testMultipleArgConstructor() throws Exception {
        String text1 = "Test 1";
        String text2 = "Test 2";
        GameOutput output = new GameOutput(text1, text2);
        List<String> outputList = output.getOutput();

        assertEquals(2, outputList.size());
        assertEquals(text1, outputList.get(0));
        assertEquals(text2, outputList.get(1));
    }

    @Test
    public void testFluentAppend() throws Exception {
        String text1 = "Test 1";
        String text2 = "Test 2";
        String text3 = "Test 3";
        GameOutput output = new GameOutput(text1)
                .append(text2)
                .append(text3);

        List<String> outputList = output.getOutput();

        assertEquals(3, outputList.size());
        assertEquals(text1, outputList.get(0));
        assertEquals(text2, outputList.get(1));
        assertEquals(text3, outputList.get(2));
    }

    @Test
    public void testAppend() throws Exception {
        String text1 = "Test 1";
        String text2 = "Test 2";
        GameOutput output = new GameOutput();

        output.append(text1);
        output.append(text2);

        List<String> outputList = output.getOutput();

        assertEquals(2, outputList.size());
        assertEquals(text1, outputList.get(0));
        assertEquals(text2, outputList.get(1));
    }

    @Test
    public void testAppendToExistingMessage() throws Exception {
        String text1 = "Test 1";
        String text2 = "Test 2";
        GameOutput output = new GameOutput(text1);

        output.append(text2);

        List<String> outputList = output.getOutput();

        assertEquals(2, outputList.size());
        assertEquals(text1, outputList.get(0));
        assertEquals(text2, outputList.get(1));
    }
}
