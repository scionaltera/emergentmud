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

package com.emergentmud.core.command;

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseCommand implements Command {
    private List<Subcommand> subCommands = new ArrayList<>();
    private List<Parameter> parameters = new ArrayList<>();
    private String description = "No description.";

    @Override
    public abstract GameOutput execute(GameOutput output, Entity entity, String command, String[] tokens, String raw);

    @Override
    public GameOutput usage(GameOutput output, String command) {
        if (!subCommands.isEmpty()) {
            output.append("[white]Description[dwhite]: [white]" + description);
            output.append(String.format("[white]Usage[dwhite]: [white]%s [dwhite]&lt;[white]sub-command[dwhite]&gt;", command.toUpperCase()));
            output.append("[white]Sub-commands[dwhite]:");

            subCommands.forEach(sc -> {
                StringBuilder buf = new StringBuilder("[white]");

                buf.append(sc.name);
                buf.append(" ");

                sc.parameters.forEach(p -> {
                    if (p.isRequired()) {
                        buf.append("[dwhite]&lt;");
                    } else {
                        buf.append("[dwhite][");
                    }

                    buf.append("[white]");
                    buf.append(p.getName());

                    if (p.isRequired()) {
                        buf.append("[dwhite]&gt; ");
                    } else {
                        buf.append("[dwhite]] ");
                    }
                });

                if (sc.parameters.isEmpty()) {
                    buf.append(" ");
                }

                buf.append("[dwhite]- [white]");
                buf.append(sc.description);

                output.append(buf.toString());
            });
        } else if (!parameters.isEmpty()) {
            StringBuilder buf = new StringBuilder();

            output.append("[white]Description[dwhite]: [white]" + description);

            buf.append("[white]Usage[dwhite]: [white]");
            buf.append(command.toUpperCase());
            buf.append(" ");

            parameters.forEach(p -> {
                if (p.isRequired()) {
                    buf.append("[dwhite]&lt;");
                } else {
                    buf.append("[dwhite][");
                }

                buf.append("[white]");
                buf.append(p.getName());

                if (p.isRequired()) {
                    buf.append("[dwhite]&gt; ");
                } else {
                    buf.append("[dwhite]] ");
                }
            });

            output.append(buf.toString().trim());
        } else {
            output.append("[white]Description[dwhite]: [white]" + description);
            output.append("[white]Usage[dwhite]: [white]" + command.toUpperCase());
        }

        return output;
    }

    public String getDescription() {
        return description;
    }

    protected void addParameter(String parameter, boolean isRequired) {
        parameters.add(new Parameter(parameter, isRequired));
    }

    protected void addSubcommand(String command, String description, Parameter... parameters) {
        subCommands.add(new Subcommand(command, description, Arrays.asList(parameters)));
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    private static class Subcommand {
        private String name;
        private String description;
        private List<Parameter> parameters = new ArrayList<>();

        Subcommand(String name, String description, List<Parameter> parameters) {
            this.name = name;
            this.description = description;
            this.parameters = parameters;
        }
    }
}
