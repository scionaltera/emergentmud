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
 *
 * Parts of this file are adapted from Connor Clark's map generation
 * implementation available here: https://github.com/Hoten/Java-Delaunay
 */

package com.emergentmud.core.repository.zonebuilder.polygonal;

import com.emergentmud.core.model.Biome;
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.Zone;
import com.emergentmud.core.repository.BiomeRepository;
import com.emergentmud.core.repository.RoomRepository;
import com.emergentmud.core.repository.ZoneBuilder;
import com.emergentmud.core.repository.ZoneRepository;
import com.hoten.delaunay.geom.Rectangle;
import com.hoten.delaunay.voronoi.Center;
import com.hoten.delaunay.voronoi.Corner;
import com.hoten.delaunay.voronoi.Edge;
import com.hoten.delaunay.voronoi.nodename.as3delaunay.Voronoi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class PolygonalZoneBuilder implements ZoneBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(PolygonalZoneBuilder.class);

    private Random random;
    private int worldSites;
    private long worldExtent;
    private int worldLloyds;
    private LloydsRelaxation lloydsRelaxation;
    private ZoneRepository zoneRepository;
    private BiomeRepository biomeRepository;
    private RoomRepository roomRepository;
    private BiomeSelector biomeSelector;
    private ImageBuilder imageBuilder;
    private VoronoiGraphBuilder voronoiGraphBuilder;
    private ElevationBuilder elevationBuilder;
    private MoistureBuilder moistureBuilder;

    @Inject
    public PolygonalZoneBuilder(Random random,
                                @Qualifier(value = "worldSites") int worldSites,
                                @Qualifier(value = "worldExtent") long worldExtent,
                                @Qualifier(value = "worldLloyds") int worldLloyds,
                                LloydsRelaxation lloydsRelaxation,
                                ZoneRepository zoneRepository,
                                BiomeRepository biomeRepository,
                                RoomRepository roomRepository,
                                BiomeSelector biomeSelector,
                                ImageBuilder imageBuilder,
                                VoronoiGraphBuilder voronoiGraphBuilder,
                                ElevationBuilder elevationBuilder,
                                MoistureBuilder moistureBuilder) {
        this.random = random;
        this.worldSites = worldSites;
        this.worldExtent = worldExtent;
        this.worldLloyds = worldLloyds;
        this.lloydsRelaxation = lloydsRelaxation;
        this.zoneRepository = zoneRepository;
        this.biomeRepository = biomeRepository;
        this.roomRepository = roomRepository;
        this.biomeSelector = biomeSelector;
        this.imageBuilder = imageBuilder;
        this.voronoiGraphBuilder = voronoiGraphBuilder;
        this.elevationBuilder = elevationBuilder;
        this.moistureBuilder = moistureBuilder;
    }

    @Override
    public Zone build(Long x, Long y, Long z) {
        Voronoi voronoi = generatePoints();
        Rectangle bounds = voronoi.get_plotBounds();

        List<Edge> edges = new ArrayList<>();
        List<Center> centers = new ArrayList<>();
        List<Corner> corners = new ArrayList<>();

        voronoiGraphBuilder.buildGraph(voronoi, edges, centers, corners);
        voronoiGraphBuilder.improveCorners(corners);
        voronoiGraphBuilder.computeEdgeMidpoints(edges);

        elevationBuilder.assignCornerElevations(bounds, corners);
        elevationBuilder.assignOceanCoastAndLand(centers, corners);
        elevationBuilder.redistributeElevations(corners);
        elevationBuilder.assignPolygonElevations(centers);
        elevationBuilder.calculateDownslopes(corners);

        moistureBuilder.createRivers(bounds, corners);
        moistureBuilder.assignCornerMoisture(corners);
        moistureBuilder.redistributeMoisture(corners);
        moistureBuilder.assignPolygonMoisture(centers);

        biomeSelector.assignBiomes(centers);

        return buildZone(bounds, edges, centers, corners);
    }

    private Voronoi generatePoints() {
        LOGGER.info("Generating points...");
        Voronoi voronoi = new Voronoi(worldSites, worldExtent, worldExtent, random, null);

        for (int i = 0; i < worldLloyds; i++) {
            voronoi = lloydsRelaxation.relaxPoints(voronoi);
        }

        return voronoi;
    }

    private Zone buildZone(Rectangle bounds, List<Edge> edges, List<Center> centers, List<Corner> corners) {
        Zone zone = new Zone();
        zone = zoneRepository.save(zone);

        List<Biome> allBiomes = biomeRepository.findAll();
        Map<Integer, Biome> biomesByColor = new HashMap<>();
        Biome oceanBiome = biomeRepository.findByName("Ocean");

        allBiomes.forEach(biome -> biomesByColor.put(biome.getColor(), biome));

        BufferedImage map = imageBuilder.build(bounds, random, edges, centers, corners);
        int[] pixels = new int[map.getHeight() * map.getWidth()];
        pixels = map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());

        // create a new Room for each pixel
        LOGGER.info("Generating rooms from image...");
        for (long scanY = 0; scanY < map.getHeight(); scanY++) {
            List<Room> rooms = new ArrayList<>();

            for (long scanX = 0; scanX < map.getWidth(); scanX++) {
                Room room = new Room();

                room.setX(scanX);
                room.setY((map.getHeight() - 1) - scanY);
                room.setZ(0L);
                room.setZone(zone);

                int color = pixels[(int)(scanY * map.getWidth() + scanX)];
                color &= 0xFFFFFF;

                room.setBiome(biomesByColor.get(color));

                if (room.getBiome() == null) {
                    LOGGER.debug("Failed to set biome for room at ({}, {}, {}) with color: {}",
                            room.getX(), room.getY(), room.getZ(), Integer.toHexString(color));

                    room.setBiome(oceanBiome); // hide glitches around the edge of the map
                }

                rooms.add(room);
            }

            roomRepository.save(rooms);

            LOGGER.info("Saved {} rooms, row {} of {}.", rooms.size(), scanY + 1, map.getHeight());
        }

        return zone;
    }
}
