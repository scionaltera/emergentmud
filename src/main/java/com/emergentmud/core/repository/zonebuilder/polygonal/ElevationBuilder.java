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

package com.emergentmud.core.repository.zonebuilder.polygonal;

import com.hoten.delaunay.geom.Rectangle;
import com.hoten.delaunay.voronoi.Center;
import com.hoten.delaunay.voronoi.Corner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

@Component
public class ElevationBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElevationBuilder.class);

    private IslandShape islandShape;

    @Inject
    public ElevationBuilder(IslandShape islandShape) {
        this.islandShape = islandShape;
    }

    public void assignCornerElevations(Rectangle bounds, List<Corner> corners) {
        LOGGER.info("Assigning corner elevations...");
        Deque<Corner> queue = new ArrayDeque<>();
        for (Corner c : corners) {
            c.water = islandShape.isWater(bounds, c.loc);
            if (c.border) {
                c.elevation = 0;
                queue.add(c);
            } else {
                c.elevation = Double.MAX_VALUE;
            }
        }

        while (!queue.isEmpty()) {
            Corner c = queue.pop();
            for (Corner a : c.adjacent) {
                double newElevation = 0.01 + c.elevation;
                if (!c.water && !a.water) {
                    newElevation += 1;
                }
                if (newElevation < a.elevation) {
                    a.elevation = newElevation;
                    queue.add(a);
                }
            }
        }
    }

    public void assignOceanCoastAndLand(List<Center> centers, List<Corner> corners) {
        LOGGER.info("Assigning ocean and land...");
        Deque<Center> queue = new ArrayDeque<>();
        final double waterThreshold = .3;
        for (final Center center : centers) {
            int numWater = 0;
            for (final Corner c : center.corners) {
                if (c.border) {
                    center.border = center.water = center.ocean = true;
                    queue.add(center);
                }
                if (c.water) {
                    numWater++;
                }
            }
            center.water = center.ocean || ((double) numWater / center.corners.size() >= waterThreshold);
        }
        while (!queue.isEmpty()) {
            final Center center = queue.pop();
            center.neighbors.stream().filter(n -> n.water && !n.ocean).forEach(n -> {
                n.ocean = true;
                queue.add(n);
            });
        }
        for (Center center : centers) {
            boolean oceanNeighbor = false;
            boolean landNeighbor = false;
            for (Center n : center.neighbors) {
                oceanNeighbor |= n.ocean;
                landNeighbor |= !n.water;
            }
            center.coast = oceanNeighbor && landNeighbor;
        }

        for (Corner c : corners) {
            int numOcean = 0;
            int numLand = 0;
            for (Center center : c.touches) {
                numOcean += center.ocean ? 1 : 0;
                numLand += !center.water ? 1 : 0;
            }
            c.ocean = numOcean == c.touches.size();
            c.coast = numOcean > 0 && numLand > 0;
            c.water = c.border || ((numLand != c.touches.size()) && !c.coast);
        }
    }

    public void redistributeElevations(List<Corner> landCorners, List<Corner> corners) {
        LOGGER.info("Redistributing elevations...");
        Collections.sort(landCorners, (o1, o2) -> {
            if (o1.elevation > o2.elevation) {
                return 1;
            } else if (o1.elevation < o2.elevation) {
                return -1;
            }
            return 0;
        });

        final double SCALE_FACTOR = 1.1;
        for (int i = 0; i < landCorners.size(); i++) {
            double y = (double) i / landCorners.size();
            double x = Math.sqrt(SCALE_FACTOR) - Math.sqrt(SCALE_FACTOR * (1 - y));
            x = Math.min(x, 1);
            landCorners.get(i).elevation = x;
        }

        corners.stream().filter(c -> c.ocean || c.coast).forEach(c -> c.elevation = 0.0);
    }

    public void assignPolygonElevations(List<Center> centers) {
        LOGGER.info("Assigning elevations to polygons...");
        for (Center center : centers) {
            double total = 0;
            for (Corner c : center.corners) {
                total += c.elevation;
            }
            center.elevation = total / center.corners.size();
        }
    }
}
