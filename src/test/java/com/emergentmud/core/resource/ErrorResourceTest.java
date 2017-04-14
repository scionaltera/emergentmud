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

package com.emergentmud.core.resource;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ErrorResourceTest {
    @Mock
    private ErrorAttributes errorAttributes;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private Model model;

    private ErrorResource errorResource;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        errorResource = new ErrorResource(errorAttributes);
    }

    @Test
    public void testErrorPath() throws Exception {
        assertEquals("/error", errorResource.getErrorPath());
    }

    @Test
    public void testError() throws Exception {
        String view = errorResource.error(httpServletRequest, model);

        assertEquals("error", view);

        verify(model).addAttribute(eq("message"), anyString());
        verify(model).addAttribute(eq("httpStatus"), anyInt());
    }
}
