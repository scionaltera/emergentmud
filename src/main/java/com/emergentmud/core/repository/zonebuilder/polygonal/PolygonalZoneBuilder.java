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
import com.hoten.delaunay.geom.Point;
import com.hoten.delaunay.geom.Rectangle;
import com.hoten.delaunay.voronoi.Center;
import com.hoten.delaunay.voronoi.Corner;
import com.hoten.delaunay.voronoi.Edge;
import com.hoten.delaunay.voronoi.nodename.as3delaunay.Voronoi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class PolygonalZoneBuilder implements ZoneBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(PolygonalZoneBuilder.class);

    private Random random;
    private ZoneRepository zoneRepository;
    private BiomeRepository biomeRepository;
    private RoomRepository roomRepository;
    private BiomeSelector biomeSelector;
    private ImageBuilder imageBuilder;
    private VoronoiGraphBuilder voronoiGraphBuilder;
    private ElevationBuilder elevationBuilder;

    @Inject
    public PolygonalZoneBuilder(Random random,
                                ZoneRepository zoneRepository,
                                BiomeRepository biomeRepository,
                                RoomRepository roomRepository,
                                BiomeSelector biomeSelector,
                                ImageBuilder imageBuilder,
                                VoronoiGraphBuilder voronoiGraphBuilder,
                                ElevationBuilder elevationBuilder) {
        this.random = random;
        this.zoneRepository = zoneRepository;
        this.biomeRepository = biomeRepository;
        this.roomRepository = roomRepository;
        this.biomeSelector = biomeSelector;
        this.imageBuilder = imageBuilder;
        this.voronoiGraphBuilder = voronoiGraphBuilder;
        this.elevationBuilder = elevationBuilder;
    }

    @Override
    public Zone build(Long x, Long y, Long z) {
        final int EXTENT = 2000;
        final int SITES = 30000;

        List<Edge> edges = new ArrayList<>();
        List<Center> centers = new ArrayList<>();
        List<Corner> corners = new ArrayList<>();

        Zone zone = new Zone();
        zone = zoneRepository.save(zone);

        LOGGER.info("Generating points...");
        Voronoi voronoi = new Voronoi(SITES, EXTENT, EXTENT, random, null);
        voronoi = relaxPoints(voronoi);

        Rectangle bounds = voronoi.get_plotBounds();

        voronoiGraphBuilder.buildGraph(voronoi, edges, centers, corners);
        voronoiGraphBuilder.improveCorners(edges, corners);

        elevationBuilder.assignCornerElevations(bounds, corners);
        elevationBuilder.assignOceanCoastAndLand(centers, corners);
        elevationBuilder.redistributeElevations(landCorners(corners), corners);
        elevationBuilder.assignPolygonElevations(centers);

        calculateDownslopes(corners);
        createRivers(bounds, corners);
        assignCornerMoisture(corners);
        redistributeMoisture(landCorners(corners));
        assignPolygonMoisture(centers);
        assignBiomes(centers);

        List<Biome> allBiomes = biomeRepository.findAll();
        Map<Integer, Biome> biomesByColor = new HashMap<>();
        Biome oceanBiome = biomeRepository.findByName("Ocean");

        allBiomes.forEach(biome -> biomesByColor.put(biome.getColor(), biome));

        BufferedImage map = imageBuilder.build(SITES, bounds, random, edges, centers, corners);
        int[] pixels = new int[map.getHeight() * map.getWidth()];
        pixels = map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());

        // create a new Room for each pixel
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
                    LOGGER.debug("Failed to set biome for room at ({}, {}, {}) using color: {}",
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

    /**
     * Lloyd's Relaxation. The random number generator tends to make clumps of points
     * and this will smooth them out so they're more evenly distributed.
     *
     * https://en.wikipedia.org/wiki/Lloyd%27s_algorithm
     */
    private Voronoi relaxPoints(Voronoi voronoi) {
        LOGGER.info("Relaxing points...");
        List<Point> points = voronoi.siteCoords();

        points.forEach(p -> {
            List<Point> region = voronoi.region(p);
            double x = 0;
            double y = 0;

            for (Point c : region) {
                x += c.x;
                y += c.y;
            }

            x /= region.size();
            y /= region.size();
            p.x = x;
            p.y = y;
        });

        return new Voronoi(points, null, voronoi.get_plotBounds());
    }

    private List<Corner> landCorners(List<Corner> corners) {
        return corners.stream().filter(c -> !c.ocean && !c.coast).collect(Collectors.toList());
    }

    private void calculateDownslopes(List<Corner> corners) {
        LOGGER.info("Calculating slopes...");
        for (Corner c : corners) {
            Corner down = c;
            //System.out.println("ME: " + c.elevation);
            for (Corner a : c.adjacent) {
                //System.out.println(a.elevation);
                if (a.elevation <= down.elevation) {
                    down = a;
                }
            }
            c.downslope = down;
        }
    }

    private void createRivers(Rectangle bounds, List<Corner> corners) {
        LOGGER.info("Creating rivers...");
        for (int i = 0; i < bounds.width / 2; i++) {
            Corner c = corners.get(random.nextInt(corners.size()));
            if (c.ocean || c.elevation < 0.3 || c.elevation > 0.9) {
                continue;
            }
            // Bias rivers to go west: if (q.downslope.x > q.x) continue;
            while (!c.coast) {
                if (c == c.downslope) {
                    break;
                }
                Edge edge = lookupEdgeFromCorner(c, c.downslope);
                if (edge != null && (!edge.v0.water || !edge.v1.water)) {
                    edge.river++;
                    c.river++;
                    c.downslope.river++;  // TODO: fix double count
                }
                c = c.downslope;
            }
        }
    }

    private Edge lookupEdgeFromCorner(Corner c, Corner downslope) {
        for (Edge e : c.protrudes) {
            if (e.v0 == downslope || e.v1 == downslope) {
                return e;
            }
        }
        return null;
    }

    private void assignCornerMoisture(List<Corner> corners) {
        LOGGER.info("Assigning corner moisture...");
        Deque<Corner> queue = new ArrayDeque<>();
        for (Corner c : corners) {
            if ((c.water || c.river > 0) && !c.ocean) {
                c.moisture = c.river > 0 ? Math.min(3.0, (0.2 * c.river)) : 1.0;
                queue.push(c);
            } else {
                c.moisture = 0.0;
            }
        }

        while (!queue.isEmpty()) {
            Corner c = queue.pop();
            for (Corner a : c.adjacent) {
                double newM = .9 * c.moisture;
                if (newM > a.moisture) {
                    a.moisture = newM;
                    queue.add(a);
                }
            }
        }

        // Salt water
        corners.stream().filter(c -> c.ocean || c.coast).forEach(c -> c.moisture = 1.0);
    }

    private void redistributeMoisture(List<Corner> landCorners) {
        LOGGER.info("Redistributing moisture...");
        Collections.sort(landCorners, (o1, o2) -> {
            if (o1.moisture > o2.moisture) {
                return 1;
            } else if (o1.moisture < o2.moisture) {
                return -1;
            }
            return 0;
        });
        for (int i = 0; i < landCorners.size(); i++) {
            landCorners.get(i).moisture = (double) i / landCorners.size();
        }
    }

    private void assignPolygonMoisture(List<Center> centers) {
        LOGGER.info("Assigning moisture...");
        for (Center center : centers) {
            double total = 0;
            for (Corner c : center.corners) {
                total += c.moisture;
            }
            center.moisture = total / center.corners.size();
        }
    }


    private void assignBiomes(List<Center> centers) {
        LOGGER.info("Assigning biomes...");
        for (Center center : centers) {
            center.biome = biomeSelector.getBiome(center);
        }
    }
}
