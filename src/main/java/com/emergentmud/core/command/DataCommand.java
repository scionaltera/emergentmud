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

import com.emergentmud.core.model.Account;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.Essence;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.AccountRepository;
import com.emergentmud.core.repository.EssenceRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@Component
public class DataCommand implements Command {
    private EssenceRepository essenceRepository;
    private AccountRepository accountRepository;

    @Inject
    public DataCommand(EssenceRepository essenceRepository, AccountRepository accountRepository) {
        this.essenceRepository = essenceRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String command, String[] tokens, String raw) {
        if (tokens.length != 1) {
            usage(output);

            return output;
        } else if ("essence".equals(tokens[0])) {
            List<Essence> essences = essenceRepository.findAll();

            output.append("[dyellow][ [yellow]Essences in Database [dyellow]]");

            StringBuilder buf = new StringBuilder();

            buf.append("<table class=\"table\">");
            buf.append("<tr><th>[dyellow]Name</th><th>[dyellow]Social Network</th><th>[dyellow]Social ID</th>" +
                    "<th>[dyellow]Created</th><th>[dyellow]Last Login</th></tr>");

            essences.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
            essences.forEach(e -> {
                Account account = accountRepository.findOne(e.getAccountId());

                buf.append(String.format("<tr><td>[yellow]%s</td><td>[yellow]%s</td><td>[yellow]%s</td>" +
                                "<td>[yellow]%s</td><td>[yellow]%s</td></tr>",
                    e.getName(),
                    account.getSocialNetwork(),
                    account.getSocialNetworkId(),
                    new Date(e.getCreationDate()),
                    e.getLastLoginDate() == null ? "Never" : new Date(e.getLastLoginDate())));
            });

            buf.append("</table>");
            output.append(buf.toString());
            output.append(String.format("[dyellow]%d Essence%s listed.",
                    essences.size(),
                    essences.size() == 1 ? "" : "s"));
        } else {
            usage(output);
        }

        return output;
    }

    private void usage(GameOutput output) {
        output.append("[yellow]Usage: DATA &lt;essence&gt;");
    }
}
