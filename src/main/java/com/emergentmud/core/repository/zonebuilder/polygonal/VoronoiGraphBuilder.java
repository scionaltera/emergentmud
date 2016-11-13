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

import com.hoten.delaunay.geom.Point;
import com.hoten.delaunay.geom.Rectangle;
import com.hoten.delaunay.voronoi.Center;
import com.hoten.delaunay.voronoi.Corner;
import com.hoten.delaunay.voronoi.Edge;
import com.hoten.delaunay.voronoi.nodename.as3delaunay.LineSegment;
import com.hoten.delaunay.voronoi.nodename.as3delaunay.Voronoi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class VoronoiGraphBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(VoronoiGraphBuilder.class);

    public void buildGraph(Voronoi voronoi, List<Edge> edges, List<Center> centers, List<Corner> corners) {
        LOGGER.info("Building graph...");
        final Rectangle bounds = voronoi.get_plotBounds();
        final HashMap<Point, Center> pointCenterMap = new HashMap<>();
        final List<Point> points = voronoi.siteCoords();

        points.forEach((p) -> {
            Center c = new Center();
            c.loc = p;
            c.index = centers.size();
            centers.add(c);
            pointCenterMap.put(p, c);
        });

        //bug fix
        centers.forEach(c -> voronoi.region(c.loc));

        final List<com.hoten.delaunay.voronoi.nodename.as3delaunay.Edge> libedges = voronoi.edges();
        final HashMap<Integer, Corner> pointCornerMap = new HashMap<>();

        for (com.hoten.delaunay.voronoi.nodename.as3delaunay.Edge libedge : libedges) {
            final LineSegment vEdge = libedge.voronoiEdge();
            final LineSegment dEdge = libedge.delaunayLine();

            final Edge edge = new Edge();
            edge.index = edges.size();
            edges.add(edge);

            edge.v0 = makeCorner(bounds, corners, pointCornerMap, vEdge.p0);
            edge.v1 = makeCorner(bounds, corners, pointCornerMap, vEdge.p1);
            edge.d0 = pointCenterMap.get(dEdge.p0);
            edge.d1 = pointCenterMap.get(dEdge.p1);

            // Centers point to edges. Corners point to edges.
            if (edge.d0 != null) {
                edge.d0.borders.add(edge);
            }
            if (edge.d1 != null) {
                edge.d1.borders.add(edge);
            }
            if (edge.v0 != null) {
                edge.v0.protrudes.add(edge);
            }
            if (edge.v1 != null) {
                edge.v1.protrudes.add(edge);
            }

            // Centers point to centers.
            if (edge.d0 != null && edge.d1 != null) {
                addToCenterList(edge.d0.neighbors, edge.d1);
                addToCenterList(edge.d1.neighbors, edge.d0);
            }

            // Corners point to corners
            if (edge.v0 != null && edge.v1 != null) {
                addToCornerList(edge.v0.adjacent, edge.v1);
                addToCornerList(edge.v1.adjacent, edge.v0);
            }

            // Centers point to corners
            if (edge.d0 != null) {
                addToCornerList(edge.d0.corners, edge.v0);
                addToCornerList(edge.d0.corners, edge.v1);
            }
            if (edge.d1 != null) {
                addToCornerList(edge.d1.corners, edge.v0);
                addToCornerList(edge.d1.corners, edge.v1);
            }

            // Corners point to centers
            if (edge.v0 != null) {
                addToCenterList(edge.v0.touches, edge.d0);
                addToCenterList(edge.v0.touches, edge.d1);
            }
            if (edge.v1 != null) {
                addToCenterList(edge.v1.touches, edge.d0);
                addToCenterList(edge.v1.touches, edge.d1);
            }
        }
    }

    // This looks suspiciously like a Lloyd's Relaxation. -- PHK
    public void improveCorners(List<Corner> corners) {
        LOGGER.info("Improving graph corners...");
        Point[] newP = new Point[corners.size()];
        for (Corner c : corners) {
            if (c.border) {
                newP[c.index] = c.loc;
            } else {
                double x = 0;
                double y = 0;
                for (Center center : c.touches) {
                    x += center.loc.x;
                    y += center.loc.y;
                }
                newP[c.index] = new Point(x / c.touches.size(), y / c.touches.size());
            }
        }
        corners.forEach(c -> c.loc = newP[c.index]);
    }

    public void computeEdgeMidpoints(List<Edge> edges) {
        LOGGER.info("Computing edge midpoints...");
        edges.stream()
                .filter(e -> e.v0 != null && e.v1 != null)
                .forEach(e -> e.setVornoi(e.v0, e.v1));
    }

    // Helper functions for the following for loop; ideally these would be inlined
    private void addToCornerList(List<Corner> list, Corner c) {
        if (c != null && !list.contains(c)) {
            list.add(c);
        }
    }

    private void addToCenterList(List<Center> list, Center c) {
        if (c != null && !list.contains(c)) {
            list.add(c);
        }
    }

    //ensures that each corner is represented by only one corner object
    private Corner makeCorner(Rectangle bounds, List<Corner> corners, HashMap<Integer, Corner> pointCornerMap, Point p) {
        if (p == null) {
            return null;
        }
        int index = (int) ((int) p.x + (int) (p.y) * bounds.width * 2);
        Corner c = pointCornerMap.get(index);
        if (c == null) {
            c = new Corner();
            c.loc = p;
            c.border = bounds.liesOnAxes(p);
            c.index = corners.size();
            corners.add(c);
            pointCornerMap.put(index, c);
        }
        return c;
    }
}
