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

package com.emergentmud.core.repository.loader;

import com.emergentmud.core.model.Pronoun;
import com.emergentmud.core.repository.PronounRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Component
public class PronounLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(PronounLoader.class);

    private PronounRepository pronounRepository;

    @Inject
    public PronounLoader(PronounRepository pronounRepository) {
        this.pronounRepository = pronounRepository;
    }

    @PostConstruct
    public void onConstruct() {
        if (pronounRepository.count() == 0) {
            LOGGER.warn("No pronouns found! Loading default pronouns...");

            List<Pronoun> pronouns = new ArrayList<>();

            pronouns.add(new Pronoun("male", "he", "him", "his", "his", "himself"));
            pronouns.add(new Pronoun("female", "she", "her", "her", "hers", "herself"));
            pronouns.add(new Pronoun("neutral", "they", "them", "their", "theirs", "themself"));

            pronounRepository.save(pronouns);
        }
    }
}
