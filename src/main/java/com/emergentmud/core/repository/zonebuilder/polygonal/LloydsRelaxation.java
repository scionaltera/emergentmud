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

import com.hoten.delaunay.geom.Point;
import com.hoten.delaunay.voronoi.nodename.as3delaunay.Voronoi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LloydsRelaxation {
    private static final Logger LOGGER = LoggerFactory.getLogger(LloydsRelaxation.class);

    /**
     * Lloyd's Relaxation. The random number generator tends to make clumps of points
     * and this will smooth them out so they're more evenly distributed.
     *
     * https://en.wikipedia.org/wiki/Lloyd%27s_algorithm
     */
    public Voronoi relaxPoints(Voronoi voronoi) {
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
}
