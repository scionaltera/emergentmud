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

import com.emergentmud.core.model.EmoteMetadata;
import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import com.emergentmud.core.repository.EmoteMetadataRepository;
import com.emergentmud.core.util.InputUtil;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class EmoteEditCommand extends BaseCommand {
    static final Sort SORT = new Sort("priority", "name");

    private EmoteMetadataRepository emoteMetadataRepository;
    private InputUtil inputUtil;

    @Inject
    public EmoteEditCommand(EmoteMetadataRepository emoteMetadataRepository, InputUtil inputUtil) {
        this.emoteMetadataRepository = emoteMetadataRepository;
        this.inputUtil = inputUtil;

        setDescription("Edit and prioritize emotes.");
        addSubcommand("list", "List all emotes.");
        addSubcommand("show", "Show details of an emote.",
                new Parameter("emote name", true));
        addSubcommand("add", "Add a new emote.",
                new Parameter("emote name", true));
        addSubcommand("set", "Set a field on an emote.",
                new Parameter("emote name", true),
                new Parameter("number", true),
                new Parameter("message", true));
        addSubcommand("priority", "Set priority for an emote.",
                new Parameter("emote name", true),
                new Parameter("priority", true));
        addSubcommand("delete", "Delete an emote.",
                new Parameter("emote name", true));
    }

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String command, String[] tokens, String raw) {
        if (tokens.length > 0) {
            if ("list".equals(tokens[0])) {
                output.append("[yellow]Priority\tName");
                output.append("[yellow]--------------");

                emoteMetadataRepository.findAll(SORT)
                        .forEach(emote -> output.append(String.format("[yellow]%d\t%s",
                                emote.getPriority(),
                                emote.getName())));
            } else if ("show".equals(tokens[0])) {
                if (tokens.length != 2) {
                    usage(output, command);

                    return output;
                }

                EmoteMetadata metadata = emoteMetadataRepository.findByName(tokens[1]);

                if (metadata == null) {
                    output.append("[yellow]No such emote found.");

                    return output;
                }

                output.append(String.format("[yellow](%d) %s", metadata.getPriority(), metadata.getName()));
                output.append(String.format("[yellow]1[dyellow]) [yellow]To Self (no target): %s", metadata.getToSelfUntargeted() == null ? "[Empty]" : metadata.getToSelfUntargeted()));
                output.append(String.format("[yellow]2[dyellow]) [yellow]To Room (no target): %s", metadata.getToRoomUntargeted() == null ? "[Empty]" : metadata.getToRoomUntargeted()));
                output.append(String.format("[yellow]3[dyellow]) [yellow]To Self (targeted): %s", metadata.getToSelfWithTarget() == null ? "[Empty]" : metadata.getToSelfWithTarget()));
                output.append(String.format("[yellow]4[dyellow]) [yellow]To Target: %s", metadata.getToTarget() == null ? "[Empty]" : metadata.getToTarget()));
                output.append(String.format("[yellow]5[dyellow]) [yellow]To Room (targeted): %s", metadata.getToRoomWithTarget() == null ? "[Empty]" : metadata.getToRoomWithTarget()));
                output.append(String.format("[yellow]6[dyellow]) [yellow]To Self (self targeted): %s", metadata.getToSelfAsTarget() == null ? "[Empty]" : metadata.getToSelfAsTarget()));
                output.append(String.format("[yellow]7[dyellow]) [yellow]To Room (self targeted): %s", metadata.getToRoomTargetingSelf() == null ? "[Empty]" : metadata.getToRoomTargetingSelf()));
            } else if ("add".equals(tokens[0])) {
                if (tokens.length != 2) {
                    usage(output, command);

                    return output;
                }

                EmoteMetadata metadata = new EmoteMetadata();

                metadata.setName(tokens[1]);
                metadata.setPriority(100);

                emoteMetadataRepository.save(metadata);

                output.append("[yellow]Added new emote.");
            } else if ("set".equals(tokens[0])) {
                if (tokens.length < 4) {
                    usage(output, command);

                    return output;
                }

                EmoteMetadata metadata = emoteMetadataRepository.findByName(tokens[1]);

                if (metadata == null) {
                    output.append("[yellow]No such emote found.");

                    return output;
                }

                Integer fieldNumber;

                try {
                    fieldNumber = Integer.parseInt(tokens[2]);
                } catch (NumberFormatException e) {
                    usage(output, command);

                    return output;
                }

                String message = inputUtil.chopWords(raw, 3);

                switch (fieldNumber) {
                    case 1: metadata.setToSelfUntargeted(message); break;
                    case 2: metadata.setToRoomUntargeted(message); break;
                    case 3: metadata.setToSelfWithTarget(message); break;
                    case 4: metadata.setToTarget(message); break;
                    case 5: metadata.setToRoomWithTarget(message); break;
                    case 6: metadata.setToSelfAsTarget(message); break;
                    case 7: metadata.setToRoomTargetingSelf(message); break;
                    default:
                        output.append("[yellow]There is no message with that number.");

                        return output;
                }

                emoteMetadataRepository.save(metadata);

                output.append("[yellow]Updated emote.");
            } else if ("priority".equals(tokens[0])) {
                if (tokens.length != 3) {
                    usage(output, command);

                    return output;
                }

                EmoteMetadata metadata = emoteMetadataRepository.findByName(tokens[1]);

                try {
                    metadata.setPriority(Integer.valueOf(tokens[2]));
                } catch (NumberFormatException ex) {
                    output.append("[yellow]Priority must be an integer.");

                    return output;
                }

                emoteMetadataRepository.save(metadata);

                output.append("[yellow]Updated priority.");
            } else if ("delete".equals(tokens[0])) {
                if (tokens.length != 2) {
                    usage(output, command);

                    return output;
                }

                EmoteMetadata metadata = emoteMetadataRepository.findByName(tokens[1]);

                if (metadata == null) {
                    output.append("[yellow]No such emote found.");

                    return output;
                }

                emoteMetadataRepository.delete(metadata);

                output.append("[yellow]Deleted emote.");
            }

            return output;
        }

        usage(output, command);

        return output;
    }
}
