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

import com.emergentmud.core.model.Biome;
import com.emergentmud.core.repository.BiomeRepository;
import com.hoten.delaunay.geom.Rectangle;
import com.hoten.delaunay.voronoi.Center;
import com.hoten.delaunay.voronoi.Corner;
import com.hoten.delaunay.voronoi.Edge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import static com.emergentmud.core.config.WorldConfiguration.SEED;

@Component
public class ImageBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageBuilder.class);

    private BiomeRepository biomeRepository;

    @Inject
    public ImageBuilder(BiomeRepository biomeRepository) {
        this.biomeRepository = biomeRepository;
    }

    public BufferedImage build(int sites, Rectangle bounds, Random random, List<Edge> edges, List<Center> centers, List<Corner> corners) {
        final BufferedImage map = new BufferedImage((int)bounds.width, (int)bounds.height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = map.createGraphics();

        paint(graphics, random, bounds, edges, centers, corners, true, true, false, false, false);

        LOGGER.info("Created bitmap: width = {}, height = {}", map.getWidth(), map.getHeight());

        try {
            File file = new File(String.format("maps/seed-%d-sites-%d-lloyds-%d.png", SEED, sites, 1));

            if (file.mkdirs()) {
                ImageIO.write(map, "PNG", file);
            } else {
                LOGGER.error("Unable to make directory for map export!");
            }
        } catch (IOException ioe) {
            LOGGER.error("Unable to export map as PNG image: ", ioe);
        }

        return map;
    }

    //also records the area of each voronoi cell
    private void paint(Graphics2D g, Random random, Rectangle bounds, List<Edge> edges, List<Center> centers, List<Corner> corners,
                       boolean drawBiomes, boolean drawRivers, boolean drawSites, boolean drawCorners, boolean drawDelaunay) {

        Biome riverBiome = biomeRepository.findByName("River");

        //draw via triangles
        for (Center c : centers) {
            drawPolygon(g, bounds, c, drawBiomes ? getColor(c.biome) : new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
        }

        for (Edge e : edges) {
            if (drawDelaunay) {
                g.setStroke(new BasicStroke(1));
                g.setColor(Color.YELLOW);
                g.drawLine((int) e.d0.loc.x, (int) e.d0.loc.y, (int) e.d1.loc.x, (int) e.d1.loc.y);
            }
            if (drawRivers && e.river > 0) {
                g.setStroke(new BasicStroke(1 + (int) Math.sqrt(e.river * 2)));
                g.setColor(new Color(riverBiome.getColor()));
                g.drawLine((int) e.v0.loc.x, (int) e.v0.loc.y, (int) e.v1.loc.x, (int) e.v1.loc.y);
            }
        }

        if (drawSites) {
            g.setColor(Color.RED);
            centers.forEach((s) -> g.fillOval((int) (s.loc.x - 2), (int) (s.loc.y - 2), 4, 4));
        }

        if (drawCorners) {
            g.setColor(Color.WHITE);
            corners.forEach((c) -> g.fillOval((int) (c.loc.x - 2), (int) (c.loc.y - 2), 4, 4));
        }
    }

    private void drawPolygon(Graphics2D g, Rectangle bounds, Center c, Color color) {
        g.setColor(color);

        //only used if Center c is on the edge of the graph. allows for completely filling in the outer polygons
        Corner edgeCorner1 = null;
        Corner edgeCorner2 = null;
        c.area = 0;
        for (Center n : c.neighbors) {
            Edge e = edgeWithCenters(c, n);

            if (e == null || e.v0 == null) {
                //outermost voronoi edges aren't stored in the graph
                continue;
            }

            //find a corner on the exterior of the graph
            //if this Edge e has one, then it must have two,
            //finding these two corners will give us the missing
            //triangle to render. this special triangle is handled
            //outside this for loop
            Corner cornerWithOneAdjacent = e.v0.border ? e.v0 : e.v1;
            if (cornerWithOneAdjacent.border) {
                if (edgeCorner1 == null) {
                    edgeCorner1 = cornerWithOneAdjacent;
                } else {
                    edgeCorner2 = cornerWithOneAdjacent;
                }
            }

            drawTriangle(g, e.v0, e.v1, c);
            c.area += Math.abs(c.loc.x * (e.v0.loc.y - e.v1.loc.y)
                    + e.v0.loc.x * (e.v1.loc.y - c.loc.y)
                    + e.v1.loc.x * (c.loc.y - e.v0.loc.y)) / 2;
        }

        //handle the missing triangle
        if (edgeCorner2 != null) {
            //if these two outer corners are NOT on the same exterior edge of the graph,
            //then we actually must render a polygon (w/ 4 points) and take into consideration
            //one of the four corners (either 0,0 or 0,height or width,0 or width,height)
            //note: the 'missing polygon' may have more than just 4 points. this
            //is common when the number of sites are quite low (less than 5), but not a problem
            //with a more useful number of sites.
            //TODO: find a way to fix this

            if (closeEnough(edgeCorner1.loc.x, edgeCorner2.loc.x, 1)) {
                drawTriangle(g, edgeCorner1, edgeCorner2, c);
            } else {
                int[] x = new int[4];
                int[] y = new int[4];
                x[0] = (int) c.loc.x;
                y[0] = (int) c.loc.y;
                x[1] = (int) edgeCorner1.loc.x;
                y[1] = (int) edgeCorner1.loc.y;

                //determine which corner this is
                x[2] = (int) ((closeEnough(edgeCorner1.loc.x, bounds.x, 1) || closeEnough(edgeCorner2.loc.x, bounds.x, .5)) ? bounds.x : bounds.right);
                y[2] = (int) ((closeEnough(edgeCorner1.loc.y, bounds.y, 1) || closeEnough(edgeCorner2.loc.y, bounds.y, .5)) ? bounds.y : bounds.bottom);

                x[3] = (int) edgeCorner2.loc.x;
                y[3] = (int) edgeCorner2.loc.y;

                g.fillPolygon(x, y, 4);
                c.area += 0; //TODO: area of polygon given vertices
            }
        }
    }

    private Edge edgeWithCenters(Center c1, Center c2) {
        for (Edge e : c1.borders) {
            if (e.d0 == c2 || e.d1 == c2) {
                return e;
            }
        }
        return null;
    }

    private void drawTriangle(Graphics2D g, Corner c1, Corner c2, Center center) {
        int[] x = new int[3];
        int[] y = new int[3];
        x[0] = (int) center.loc.x;
        y[0] = (int) center.loc.y;
        x[1] = (int) c1.loc.x;
        y[1] = (int) c1.loc.y;
        x[2] = (int) c2.loc.x;
        y[2] = (int) c2.loc.y;
        g.fillPolygon(x, y, 3);
    }

    private boolean closeEnough(double d1, double d2, double diff) {
        return Math.abs(d1 - d2) <= diff;
    }

    private Color getColor(Biome biome) {
        if (biome == null) {
            return Color.MAGENTA;
        }

        return new Color(biome.getColor());
    }
}
