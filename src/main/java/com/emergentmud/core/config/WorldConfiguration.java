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

package com.emergentmud.core.config;

import com.emergentmud.core.repository.BiomeRepository;
import com.emergentmud.core.repository.zonebuilder.polygonal.BiomeSelector;
import com.emergentmud.core.repository.zonebuilder.polygonal.ElevationBuilder;
import com.emergentmud.core.repository.zonebuilder.polygonal.ImageBuilder;
import com.emergentmud.core.repository.zonebuilder.polygonal.IslandShape;
import com.emergentmud.core.repository.zonebuilder.polygonal.LloydsRelaxation;
import com.emergentmud.core.repository.zonebuilder.polygonal.MoistureBuilder;
import com.emergentmud.core.repository.zonebuilder.polygonal.PolygonalZoneBuilder;
import com.emergentmud.core.repository.RoomRepository;
import com.emergentmud.core.repository.ZoneBuilder;
import com.emergentmud.core.repository.ZoneRepository;
import com.emergentmud.core.repository.zonebuilder.polygonal.RadialIslandShape;
import com.emergentmud.core.repository.zonebuilder.polygonal.VoronoiGraphBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.Random;

@Configuration
public class WorldConfiguration {
    public static final int SEED = 92948; // TODO inject me from configuration

    @Inject
    private LloydsRelaxation lloydsRelaxation;

    @Inject
    private ZoneRepository zoneRepository;

    @Inject
    private BiomeRepository biomeRepository;

    @Inject
    private RoomRepository roomRepository;

    @Inject
    private BiomeSelector biomeSelector;

    @Inject
    private ImageBuilder imageBuilder;

    @Inject
    private VoronoiGraphBuilder voronoiGraphBuilder;

    @Inject
    private ElevationBuilder elevationBuilder;

    @Inject
    private MoistureBuilder moistureBuilder;

    @Bean(name = "worldRandom")
    public Random random() {
        Random random = new Random();

        random.setSeed(SEED);

        return random;
    }

    @Bean
    public IslandShape islandShape() {
        return new RadialIslandShape(random());
    }

    @Bean
    public ZoneBuilder zoneBuilder() {
        return new PolygonalZoneBuilder(
                random(),
                lloydsRelaxation,
                zoneRepository,
                biomeRepository,
                roomRepository,
                biomeSelector,
                imageBuilder,
                voronoiGraphBuilder,
                elevationBuilder,
                moistureBuilder);
    }
}
