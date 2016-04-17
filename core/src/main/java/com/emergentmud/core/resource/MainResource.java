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
package com.emergentmud.core.resource;

import com.emergentmud.core.model.SocialNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

@Controller
public class MainResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainResource.class);

    private List<SocialNetwork> networks;
    private SecurityContextLogoutHandler securityContextLogoutHandler;

    @Inject
    public MainResource(List<SocialNetwork> networks,
                        SecurityContextLogoutHandler securityContextLogoutHandler) {
        this.networks = networks;
        this.securityContextLogoutHandler = securityContextLogoutHandler;
    }

    @RequestMapping("/")
    public String index(HttpSession httpSession, Principal principal, Model model) {
        model.addAttribute("networks", networks);

        if (principal == null) {
            return "index";
        }

        return "characters";
    }

    @RequestMapping("/social/{network}")
    public String social(@PathVariable String network, HttpSession session) {
        session.setAttribute("social", network);

        LOGGER.info("Logging in via social network: {}", network);

        return "redirect:/login/" + network;
    }

    @RequestMapping("/play/{id}")
    public String play(@PathVariable("id") String id, HttpSession session, Principal principal, Model model) {
        return "play";
    }

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        securityContextLogoutHandler.logout(request, response, authentication);

        return "redirect:/";
    }
}
