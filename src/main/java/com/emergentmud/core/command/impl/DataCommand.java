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

package com.emergentmud.core.command.impl;

import com.emergentmud.core.command.BaseCommand;
import com.emergentmud.core.command.TableFormatter;
import com.emergentmud.core.model.Account;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EntityRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Component
public class DataCommand extends BaseCommand {
    private EntityRepository entityRepository;

    @Inject
    public DataCommand(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;

        setDescription("Show the contents of database entries.");
        addSubcommand("entity", "Show all Entity objects that are associated with an Account.");
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String command, String[] tokens, String raw) {
        if (tokens.length != 1) {
            usage(output, command);

            return output;
        } else if ("entity".equals(tokens[0])) {
            List<Entity> entities = entityRepository.findByAccountIsNotNull();
            TableFormatter tableFormatter = new TableFormatter(
                    "Player Characters in Database",
                    Arrays.asList("Name", "Social Network", "Social ID", "Created", "Last Login"),
                    "Entity",
                    "Entities"
            );

            entities.sort(Comparator.comparing(Entity::getName));
            entities.forEach(e -> {
                Account account = e.getAccount();

                tableFormatter.addRow(Arrays.asList(
                        e.getName(),
                        account.getSocialNetwork(),
                        account.getSocialNetworkId(),
                        new Date(e.getCreationDate()).toString(),
                        e.getLastLoginDate() == null ? "Never" : new Date(e.getLastLoginDate()).toString()
                ));
            });

            tableFormatter.toTable(output, "yellow");
        } else {
            usage(output, command);
        }

        return output;
    }
}
