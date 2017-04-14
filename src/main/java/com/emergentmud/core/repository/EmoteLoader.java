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

package com.emergentmud.core.repository;

import com.emergentmud.core.model.EmoteMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Component
public class EmoteLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmoteLoader.class);

    private EmoteMetadataRepository emoteMetadataRepository;

    @Inject
    public EmoteLoader(EmoteMetadataRepository emoteMetadataRepository) {
        this.emoteMetadataRepository = emoteMetadataRepository;
    }

    @PostConstruct
    public void loadEmotes() {
        if (emoteMetadataRepository.count() == 0) {
            LOGGER.warn("No emotes found! Loading default emotes...");

            List<EmoteMetadata> emotes = new ArrayList<>();

            emotes.add(setEmoteFields(new EmoteMetadata("nod", 100), new String[] {
                    "You nod.",
                    "%self% nods.",
                    "You nod to %target%.",
                    "%self% nods to you.",
                    "%self% nods to %target%."
            }));
            emotes.add(setEmoteFields(new EmoteMetadata("shake", 100), new String[] {
                    "You shake your head.",
                    "%self% shakes %his% head.",
                    "You shake your head at %target%.",
                    "%self% shakes %his% head at you.",
                    "%self% shakes %his% head at %target%.",
                    "You shake your arms and legs a little to loosen up.",
                    "%self% shakes %his% arms and legs a little to loosen up."
            }));
            emotes.add(setEmoteFields(new EmoteMetadata("smile", 100), new String[] {
                    "You smile.",
                    "%self% smiles.",
                    "You smile at %target%.",
                    "%self% smiles at you.",
                    "%self% smiles at %target%."
            }));
            emotes.add(setEmoteFields(new EmoteMetadata("frown", 100), new String[] {
                    "You frown.",
                    "%self% frowns.",
                    "You frown at %target%.",
                    "%self% frowns at you.",
                    "%self% frowns at %target%."
            }));
            emotes.add(setEmoteFields(new EmoteMetadata("laugh", 100), new String[] {
                    "You laugh.",
                    "%self% laughs.",
                    "You point and laugh at %target%.",
                    "%self% points and laughs at you.",
                    "%self% points and laughs at %target%.",
                    "You laugh at your own ridiculousness.",
                    "%self% laughs at %his% own ridiculousness."
            }));
            emotes.add(setEmoteFields(new EmoteMetadata("cry", 100), new String[] {
                    "You cry.",
                    "%self% cries.",
                    "You look at %target% and begin to cry.",
                    "%self% looks at you and begins to cry.",
                    "%self% looks at %target% and begins to cry.",
                    "You cry, lost in your own misfortune.",
                    "%self% cries, lost in %his% own misfortune."
            }));
            emotes.add(setEmoteFields(new EmoteMetadata("wink", 100), new String[] {
                    "You wink.",
                    "%self% winks.",
                    "You wink at %target%.",
                    "%self% winks at you.",
                    "%self% winks at %target%."
            }));
            emotes.add(setEmoteFields(new EmoteMetadata("blink", 100), new String[] {
                    "You blink.",
                    "%self% blinks.",
                    "You look at %target%, blinking in disbelief.",
                    "%self% looks at you, blinking in disbelief.",
                    "%self% looks at %target%, blinking in disbelief."
            }));
            emotes.add(setEmoteFields(new EmoteMetadata("burp", 100), new String[] {
                    "You burp.",
                    "%self% burps.",
                    "You move in close to %target% and burp in %his% face.",
                    "%self% moves in close to you and burps in your face.",
                    "%self% moves in close to %target% and burps in %his% face."
            }));
            emotes.add(setEmoteFields(new EmoteMetadata("fart", 100), new String[] {
                    "You fart.",
                    "%self% farts.",
                    "You move in close to %target% and let out a fart.",
                    "%self% moves in close to you and lets out a fart.",
                    "%self% moves in close to %target% and lets out a fart."
            }));
            emotes.add(setEmoteFields(new EmoteMetadata("yawn", 100), new String[] {
                    "You yawn.",
                    "%self% yawns.",
                    "You look at %target% and let out a big yawn.",
                    "%self% looks at you and lets out a big yawn.",
                    "%self% looks at %target% and lets out a big yawn."
            }));
            emotes.add(setEmoteFields(new EmoteMetadata("roll", 100), new String[] {
                    "You roll your eyes.",
                    "%self% rolls %his% eyes.",
                    "You roll your eyes at %target%.",
                    "%self% rolls %his% eyes at you.",
                    "%self% rolls %his% eyes at %target%."
            }));
            emotes.add(setEmoteFields(new EmoteMetadata("brow", 100), new String[] {
                    "You raise an eyebrow.",
                    "%self% raises an eyebrow.",
                    "You look at %target%, raising one eyebrow.",
                    "%self% looks at you, raising one eyebrow.",
                    "%self% looks at %target%, raising one eyebrow."
            }));
            emotes.add(setEmoteFields(new EmoteMetadata("wave", 100), new String[] {
                    "You wave.",
                    "%self% waves.",
                    "You wave to %target%.",
                    "%self% waves to you.",
                    "%self% waves to %target%.",
                    "You move your body from side to side, channeling the ocean waves.",
                    "%self% moves %his% body from side to side, channeling the ocean waves."
            }));

            emoteMetadataRepository.save(emotes);
        }
    }

    private EmoteMetadata setEmoteFields(EmoteMetadata emote, String[] messages) {
        emote.setToSelfUntargeted(messages[0]);
        emote.setToRoomUntargeted(messages[1]);
        emote.setToSelfWithTarget(messages[2]);
        emote.setToTarget(messages[3]);
        emote.setToRoomWithTarget(messages[4]);

        if (messages.length == 7) {
            emote.setToSelfAsTarget(messages[5]);
            emote.setToRoomTargetingSelf(messages[6]);
        }

        return emote;
    }
}
