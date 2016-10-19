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

package com.emergentmud.core.repository;

import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.Zone;
import com.hoten.delaunay.geom.Point;
import com.hoten.delaunay.voronoi.nodename.as3delaunay.Voronoi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class PolygonalZoneBuilder implements ZoneBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(PolygonalZoneBuilder.class);
    private static final Random RANDOM = new Random();
    private static final int EXTENT = 128;

    private ZoneRepository zoneRepository;
    private RoomRepository roomRepository;

    @Inject
    public PolygonalZoneBuilder(ZoneRepository zoneRepository, RoomRepository roomRepository) {
        this.zoneRepository = zoneRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public Zone build(Long x, Long y, Long z) {
        Zone zone = new Zone();
        Zone savedZone;

        zone.setColor(new int[] {RANDOM.nextInt(256), RANDOM.nextInt(256), RANDOM.nextInt(256)});
        savedZone = zoneRepository.save(zone);

        List<Point> points = generatePoints((int)Math.pow(2, 8));
        List<Room> rooms = new ArrayList<>();
        Voronoi voronoi = new Voronoi((ArrayList<Point>)points, null);

        voronoi = relaxPoints(voronoi);

        voronoi.siteCoords().forEach(p -> {
            Room room = new Room();

            room.setX((long)(EXTENT * p.x) - (EXTENT / 2));
            room.setY((long)(EXTENT * p.y) - (EXTENT / 2));
            room.setZ(z);
            room.setZone(savedZone);

//            LOGGER.info("Generated room at ({}, {})", room.x, room.y);

            rooms.add(room);
        });

        Room room = new Room();

        room.setX(x);
        room.setY(y);
        room.setZ(z);
        room.setZone(savedZone);

        rooms.add(room);

        roomRepository.save(rooms);

        return zone;
    }

    private List<Point> generatePoints(int count) {
        List<Point> points = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            points.add(new Point(RANDOM.nextDouble(), RANDOM.nextDouble()));
        }

        return points;
    }

    /**
     * Lloyd's Relaxation. The random number generator tends to make clumps of points
     * and this will smooth them out so they're more evenly distributed.
     *
     * https://en.wikipedia.org/wiki/Lloyd%27s_algorithm
     */
    private Voronoi relaxPoints(Voronoi voronoi) {
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

        return new Voronoi((ArrayList<Point>)points, null, voronoi.get_plotBounds());
    }
}
