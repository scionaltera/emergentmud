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
import com.hoten.delaunay.voronoi.Edge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Random;

@Component
public class MoistureBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(MoistureBuilder.class);

    private Random random;
    private LandCornerFilter landCornerFilter;

    @Inject
    public MoistureBuilder(Random random, LandCornerFilter landCornerFilter) {
        this.random = random;
        this.landCornerFilter = landCornerFilter;
    }

    public void createRivers(Rectangle bounds, List<Corner> corners) {
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

    public void assignCornerMoisture(List<Corner> corners) {
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

    public void redistributeMoisture(List<Corner> corners) {
        LOGGER.info("Redistributing moisture...");
        List<Corner> landCorners = landCornerFilter.landCorners(corners);
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

    public void assignPolygonMoisture(List<Center> centers) {
        LOGGER.info("Assigning moisture...");
        for (Center center : centers) {
            double total = 0;
            for (Corner c : center.corners) {
                total += c.moisture;
            }
            center.moisture = total / center.corners.size();
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
}
