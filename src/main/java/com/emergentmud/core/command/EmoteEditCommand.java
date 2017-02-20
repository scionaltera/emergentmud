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
public class EmoteEditCommand implements Command {
    static final Sort SORT = new Sort("priority", "name");

    private EmoteMetadataRepository emoteMetadataRepository;
    private InputUtil inputUtil;

    @Inject
    public EmoteEditCommand(EmoteMetadataRepository emoteMetadataRepository, InputUtil inputUtil) {
        this.emoteMetadataRepository = emoteMetadataRepository;
        this.inputUtil = inputUtil;
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
                    usage(output);

                    return output;
                }

                EmoteMetadata metadata = emoteMetadataRepository.findByName(tokens[1]);

                if (metadata == null) {
                    output.append("[yellow]No such emote found.");

                    return output;
                }

                output.append(String.format("[yellow](%d) %s", metadata.getPriority(), metadata.getName()));
                output.append(String.format("[yellow]To Self (no target): %s", metadata.getToSelfUntargeted() == null ? "[Empty]" : metadata.getToSelfUntargeted()));
                output.append(String.format("[yellow]To Room (no target): %s", metadata.getToRoomUntargeted() == null ? "[Empty]" : metadata.getToRoomUntargeted()));
                output.append(String.format("[yellow]To Self (targeted): %s", metadata.getToSelfWithTarget() == null ? "[Empty]" : metadata.getToSelfWithTarget()));
                output.append(String.format("[yellow]To Target: %s", metadata.getToTarget() == null ? "[Empty]" : metadata.getToTarget()));
                output.append(String.format("[yellow]To Room (targeted): %s", metadata.getToRoomWithTarget() == null ? "[Empty]" : metadata.getToRoomWithTarget()));
                output.append(String.format("[yellow]To Self (self targeted): %s", metadata.getToSelfAsTarget() == null ? "[Empty]" : metadata.getToSelfAsTarget()));
                output.append(String.format("[yellow]To Room (self targeted): %s", metadata.getToRoomTargetingSelf() == null ? "[Empty]" : metadata.getToRoomTargetingSelf()));
            } else if ("add".equals(tokens[0])) {
                if (tokens.length != 2) {
                    usage(output);

                    return output;
                }

                EmoteMetadata metadata = new EmoteMetadata();

                metadata.setName(tokens[1]);
                metadata.setPriority(100);

                emoteMetadataRepository.save(metadata);

                output.append("[yellow]Added new emote.");
            } else if ("set".equals(tokens[0])) {
                if (tokens.length < 4) {
                    usage(output);

                    return output;
                }

                EmoteMetadata metadata = emoteMetadataRepository.findByName(tokens[1]);

                if (metadata == null) {
                    output.append("[yellow]No such emote found.");

                    return output;
                }

                if (!"self".equals(tokens[2])
                        && !"room".equals(tokens[2])
                        && !"targetself".equals(tokens[2])
                        && !"target".equals(tokens[2])
                        && !"targetroom".equals(tokens[2])
                        && !"selftarget".equals(tokens[2])
                        && !"selftargetroom".equals(tokens[2])) {
                    usage(output);

                    return output;
                }

                String message = inputUtil.chopWords(raw, 3);

                if ("self".equals(tokens[2])) {
                    metadata.setToSelfUntargeted(message);
                } else if ("room".equals(tokens[2])) {
                    metadata.setToRoomUntargeted(message);
                } else if ("targetself".equals(tokens[2])) {
                    metadata.setToSelfWithTarget(message);
                } else if ("target".equals(tokens[2])) {
                    metadata.setToTarget(message);
                } else if ("targetroom".equals(tokens[2])) {
                    metadata.setToRoomWithTarget(message);
                } else if ("selftarget".equals(tokens[2])) {
                    metadata.setToSelfAsTarget(message);
                } else if ("selftargetroom".equals(tokens[2])) {
                    metadata.setToRoomTargetingSelf(message);
                }

                emoteMetadataRepository.save(metadata);

                output.append("[yellow]Updated emote.");
            } else if ("priority".equals(tokens[0])) {
                if (tokens.length != 3) {
                    usage(output);

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
                    usage(output);

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

        usage(output);

        return output;
    }

    private void usage(GameOutput output) {
        output.append("[yellow]Usage:");
        output.append("[yellow]list - List all emotes.");
        output.append("[yellow]show &lt;emote name&gt; - Show details of an emote.");
        output.append("[yellow]add &lt;emote name&gt; - Add a new emote.");
        output.append("[yellow]set &lt;emote name&gt; &lt;self|room|targetself|target|targetroom|selftarget|selftargetroom&gt; &lt;message&gt; - Set a field on an emote.");
        output.append("[yellow]priority &lt;emote name&gt; &lt;priority&gt; - Set priority for an emote.");
        output.append("[yellow]delete &lt;emote name&gt; - Delete an emote.");
    }
}
