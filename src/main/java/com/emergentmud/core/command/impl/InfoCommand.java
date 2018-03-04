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

package com.emergentmud.core.command.impl;

import com.emergentmud.core.command.BaseCommand;
import com.emergentmud.core.command.TableFormatter;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.service.EntityService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Optional;

@Component
public class InfoCommand extends BaseCommand {
    private EntityService entityService;

    @Inject
    public InfoCommand(EntityService entityService) {
        this.entityService = entityService;

        setDescription("Display information about a thing in the game.");
        addParameter("target", false);
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String command, String[] tokens, String raw) {
        Entity target;

        if (tokens.length == 0) {
            target = entity;
        } else if (tokens.length == 1) {
            Optional<Entity> entityOptional = entityService.entitySearchGlobal(entity, tokens[0]);

            if (!entityOptional.isPresent()) {
                output.append("[yellow]There is nothing by that name here.");

                return output;
            }

            target = entityOptional.get();
        } else {
            usage(output, command);

            return output;
        }

        final String COLOR = "yellow";
        TableFormatter tableFormatter = new TableFormatter(
                String.format("Entity [d%s]([%s]%s[d%s])[%s]", COLOR, COLOR, target.getId(), COLOR, COLOR),
                Arrays.asList("Attribute", "Value"),
                "Attribute",
                "Attributes"
        );

        String location = String.format("[d%s]([%s]%d[d%s], [%s]%d[d%s], [%s]%d[d%s])",
                COLOR, COLOR,
                target.getLocation().getX(),
                COLOR, COLOR,
                target.getLocation().getY(),
                COLOR, COLOR,
                target.getLocation().getZ(),
                COLOR);

        tableFormatter.addRow(Arrays.asList("Name", target.getName()));
        tableFormatter.addRow(Arrays.asList("Gender", target.getGender().toString()));
        tableFormatter.addRow(Arrays.asList("Location", location));
        tableFormatter.addRow(Arrays.asList("Entity Capabilities", target.getCapabilities().toString()));

        if (target.getAccount() != null) {
            tableFormatter.addRow(Arrays.asList("Account Capabilities", target.getAccount().getCapabilities().toString()));
            tableFormatter.addRow(Arrays.asList("Social Network", target.getAccount().getSocialNetwork()));
            tableFormatter.addRow(Arrays.asList("Social Username", target.getAccount().getSocialNetworkId()));
            tableFormatter.addRow(Arrays.asList("STOMP Session ID", target.getStompSessionId()));
            tableFormatter.addRow(Arrays.asList("Remote Address", target.getRemoteAddr()));
            tableFormatter.addRow(Arrays.asList("User Agent String", target.getUserAgent()));
        }

        tableFormatter.toTable(output, COLOR);

        return output;
    }
}
