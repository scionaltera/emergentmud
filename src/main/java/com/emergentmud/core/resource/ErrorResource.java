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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class ErrorResource extends AbstractErrorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorResource.class);

    private ErrorAttributes errorAttributes;

    @Inject
    public ErrorResource(ErrorAttributes errorAttributes) {
        super(errorAttributes);

        this.errorAttributes = errorAttributes;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping("/error")
    public String error(HttpServletRequest request, Model model) {
        Map<String, Object> attributes = getErrorAttributes(request, false);

        LOGGER.warn("{} {}: {} -> {}",
                request.getRemoteAddr(),
                attributes.get("status"),
                attributes.get("message"),
                attributes.get("path"));

        model.addAttribute("title", getStatus(request).getReasonPhrase());
        model.addAttribute("path", request.getContextPath());
        model.addAttribute("message", getStatus(request).getReasonPhrase());
        model.addAttribute("httpStatus", getStatus(request).value());

        return "error";
    }
}
