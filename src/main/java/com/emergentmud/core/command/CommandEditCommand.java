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

package com.emergentmud.core.command;

import com.emergentmud.core.model.CommandMetadata;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.CommandMetadataRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class CommandEditCommand implements Command {
    static final Sort SORT = new Sort("priority", "name");

    private CommandMetadataRepository commandMetadataRepository;

    @Inject
    public CommandEditCommand(CommandMetadataRepository commandMetadataRepository) {
        this.commandMetadataRepository = commandMetadataRepository;
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String[] tokens, String raw) {
        if (tokens.length > 0) {
            if ("list".equals(tokens[0])) {
                output.append("[yellow]Priority\tName\tAdmin");
                output.append("[yellow]---------------------");

                commandMetadataRepository.findAll(SORT)
                        .stream()
                        .forEach(cm -> output.append(String.format("[yellow]%d\t%s\t%s",
                                cm.getPriority(),
                                cm.getName(),
                                cm.isAdmin())));
            } else if ("add".equals(tokens[0])) {
                if (tokens.length != 4) {
                    usage(output);

                    return output;
                }

                CommandMetadata metadata = new CommandMetadata();

                metadata.setName(tokens[1]);
                metadata.setBeanName(tokens[2]);
                metadata.setAdmin(true); // all new commands default to admin-only as a safety measure

                try {
                    metadata.setPriority(Integer.valueOf(tokens[3]));
                } catch (NumberFormatException ex) {
                    output.append("[yellow]Priority must be an integer.");

                    return output;
                }

                commandMetadataRepository.save(metadata);

                output.append("[yellow]Added new command.");
            } else if ("priority".equals(tokens[0])) {
                if (tokens.length != 3) {
                    usage(output);

                    return output;
                }

                CommandMetadata metadata = commandMetadataRepository.findByName(tokens[1]);

                try {
                    metadata.setPriority(Integer.valueOf(tokens[2]));
                } catch (NumberFormatException ex) {
                    output.append("[yellow]Priority must be an integer.");

                    return output;
                }

                commandMetadataRepository.save(metadata);

                output.append("[yellow]Updated priority.");
            } else if ("admin".equals(tokens[0])) {
                if (tokens.length != 3) {
                    usage(output);

                    return output;
                }

                CommandMetadata metadata = commandMetadataRepository.findByName(tokens[1]);

                metadata.setAdmin(Boolean.valueOf(tokens[2]));

                commandMetadataRepository.save(metadata);

                output.append("[yellow]Updated admin flag.");
            } else {
                usage(output);
            }

            return output;
        }

        usage(output);

        return output;
    }

    private void usage(GameOutput output) {
        output.append("[yellow]Usage:");
        output.append("[yellow]list - List all commands.");
        output.append("[yellow]add &lt;command name&gt; &lt;bean name&gt; &lt;priority&gt; - Add a new command.");
        output.append("[yellow]priority &lt;command name&gt; &lt;priority&gt; - Set priority for a command.");
        output.append("[yellow]admin &lt;command name&gt; &lt;true|false&gt; - Restrict a command to administrators.");
    }
}
