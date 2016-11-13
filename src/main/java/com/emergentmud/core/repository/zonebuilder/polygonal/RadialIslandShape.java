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
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Random;

@Component
public class RadialIslandShape implements IslandShape {
    private final int bumps;
    private final double startAngle;
    private final double dipAngle;
    private final double dipWidth;

    @Inject
    public RadialIslandShape(Random random) {
        bumps = random.nextInt(5) + 1;
        startAngle = random.nextDouble() * 2 * Math.PI;
        dipAngle = random.nextDouble() * 2 * Math.PI;
        dipWidth = random.nextDouble() * .5 + .2;
    }

    @Override
    public boolean isWater(Rectangle bounds, Point p) {
        p = new Point(2 * (p.x / bounds.width - 0.5), 2 * (p.y / bounds.height - 0.5));

        double angle = Math.atan2(p.y, p.x);
        double length = 0.5 * (Math.max(Math.abs(p.x), Math.abs(p.y)) + p.length());

        double r1 = 0.5 + 0.40 * Math.sin(startAngle + bumps * angle + Math.cos((bumps + 3) * angle));
        double r2 = 0.7 - 0.20 * Math.sin(startAngle + bumps * angle - Math.sin((bumps + 2) * angle));
        if (Math.abs(angle - dipAngle) < dipWidth
                || Math.abs(angle - dipAngle + 2 * Math.PI) < dipWidth
                || Math.abs(angle - dipAngle - 2 * Math.PI) < dipWidth) {
            r1 = r2 = 0.2;
        }
        double ISLAND_FACTOR = 1.07;
        return !(length < r1 || (length > r1 * ISLAND_FACTOR && length < r2));

        //return false;

        /*if (noise == null) {
         noise = new Perlin2d(.125, 8, MyRandom.seed).createArray(257, 257);
         }
         int x = (int) ((p.x + 1) * 128);
         int y = (int) ((p.y + 1) * 128);
         return noise[x][y] < .3 + .3 * p.l2();*/

        /*boolean eye1 = new Point(p.x - 0.2, p.y / 2 + 0.2).length() < 0.05;
         boolean eye2 = new Point(p.x + 0.2, p.y / 2 + 0.2).length() < 0.05;
         boolean body = p.length() < 0.8 - 0.18 * Math.sin(5 * Math.atan2(p.y, p.x));
         return !(body && !eye1 && !eye2);*/
    }

    int getBumps() {
        return bumps;
    }

    double getStartAngle() {
        return startAngle;
    }

    double getDipAngle() {
        return dipAngle;
    }

    double getDipWidth() {
        return dipWidth;
    }
}
