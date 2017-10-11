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
import com.emergentmud.core.model.Account;
import com.emergentmud.core.model.Capability;
import com.emergentmud.core.model.CommandRole;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.AccountRepository;
import com.emergentmud.core.repository.CapabilityRepository;
import com.emergentmud.core.repository.WorldManager;
import com.emergentmud.core.service.EntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Optional;

@Component
public class ExileCommand extends BaseCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExileCommand.class);

    private EntityService entityService;
    private AccountRepository accountRepository;
    private CapabilityRepository capabilityRepository;
    private WorldManager worldManager;

    @Inject
    public ExileCommand(EntityService entityService,
                        AccountRepository accountRepository,
                        CapabilityRepository capabilityRepository,
                        WorldManager worldManager) {

        this.entityService = entityService;
        this.accountRepository = accountRepository;
        this.capabilityRepository = capabilityRepository;
        this.worldManager = worldManager;

        setDescription("Kick somebody out of the game.");
        addParameter("add|remove", true);
        addParameter("person", true);
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String command, String[] tokens, String raw) {
        if (tokens.length != 2) {
            usage(output, command);
            return output;
        }

        Capability playCapability = capabilityRepository.findByName(CommandRole.CHAR_PLAY.name());
        Capability createCapability = capabilityRepository.findByName(CommandRole.CHAR_NEW.name());

        Optional<Entity> targetOptional = entityService.entitySearchGlobal(entity, tokens[1]);

        if (!targetOptional.isPresent()) {
            output.append("[yellow]There is nobody by that name.");
            return output;
        }

        Entity target = targetOptional.get();
        Account account = target.getAccount();

        if (entity.equals(target)) {
            output.append("[yellow]You really don't want to do that.");
            return output;
        }

        if ("add".equalsIgnoreCase(tokens[0])) {
            if (!account.isCapable(playCapability) && !account.isCapable(createCapability)) {
                output.append("[yellow]They are already exiled.");
                return output;
            }

            account.removeCapabilities(playCapability, createCapability);

            accountRepository.save(account);

            LOGGER.info("{} has EXILED {}!", entity.getName(), target.getName());

            GameOutput targetOutput = new GameOutput()
                    .append(String.format("[yellow]%s EXILES you!", entity.getName()))
                    .append("<script type=\"text/javascript\">setTimeout(function(){ window.location=\"/\"; }, 1000);</script>");

            entityService.sendMessageToEntity(target, targetOutput);

            GameOutput roomOutput = new GameOutput()
                    .append(String.format("[yellow]%s EXILES %s!", entity.getName(), target.getName()))
                    .append(String.format("%s disappears in a puff of smoke.", target.getName()));

            entityService.sendMessageToRoom(target.getX(), target.getY(), target.getZ(), Arrays.asList(entity, target), roomOutput);

            worldManager.remove(target);

            output
                    .append(String.format("[yellow]You exile %s.", target))
                    .append(String.format("%s disappears in a puff of smoke.", target.getName()));
        } else if ("remove".equalsIgnoreCase(tokens[0])) {
            if (account.isCapable(playCapability) && account.isCapable(createCapability)) {
                output.append("[yellow]They are not exiled.");
                return output;
            }

            account.addCapabilities(playCapability, createCapability);

            accountRepository.save(account);

            LOGGER.info("{} has removed EXILE status for {}.", entity.getName(), target.getName());

            output.append(String.format("[yellow]You remove exile for %s.", target.getName()));
        } else {
            output.append("[yellow]You can either 'add' or 'remove' an exile.");
        }

        return output;
    }
}
