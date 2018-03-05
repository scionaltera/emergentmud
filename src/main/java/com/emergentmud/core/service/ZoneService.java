/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016-2018 Peter Keeler
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

package com.emergentmud.core.service;

import com.emergentmud.core.model.Coordinate;
import com.emergentmud.core.model.WhittakerGridLocation;
import com.emergentmud.core.model.Zone;
import com.emergentmud.core.repository.WhittakerGridLocationRepository;
import com.emergentmud.core.repository.ZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class ZoneService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ZoneService.class);

    private ZoneRepository zoneRepository;
    private Random random;

    private WhittakerGridLocationRepository whittakerGridLocationRepository;

    @Inject
    public ZoneService(ZoneRepository zoneRepository, WhittakerGridLocationRepository whittakerGridLocationRepository, Random random) {
        this.zoneRepository = zoneRepository;
        this.whittakerGridLocationRepository = whittakerGridLocationRepository;
        this.random = random;
    }

    public Zone fetchZone(Coordinate point) {
        return zoneRepository.findZoneAtPoint(point.getX(), point.getY());
    }

    public Zone createZone(Coordinate startLocation) {
        Zone zone = fetchZone(startLocation);

        if (zone != null) {
            LOGGER.debug("Request to create zone that already exists at: {}", startLocation);
            return zone;
        }

        zone = new Zone();

        zone.setTopRight(startLocation);
        zone.setBottomLeft(startLocation);

        expandZoneBorders(zone);
        selectZoneBiome(zone);

        zone = zoneRepository.save(zone);

        LOGGER.info("Saving new zone: {} {} {}",
                zone.getBiome().getName(),
                zone.getBottomLeft(),
                zone.getTopRight());

        return zone;
    }

    private void expandZoneBorders(Zone zone) {
        List<Zone> collisions;
        int tries = 0;

        do {
            int direction = random.nextInt(4);
            int value = random.nextInt(3) + 1;

            switch (direction) {
                case 0:
                    zone.setTopRight(new Coordinate(zone.getTopRight().getX() + value, zone.getTopRight().getY(), zone.getTopRight().getZ()));
                    LOGGER.debug("Expanding right edge to {}", zone.getTopRight().getX());
                    break;
                case 1:
                    zone.setTopRight(new Coordinate(zone.getTopRight().getX(), zone.getTopRight().getY() + value, zone.getTopRight().getZ()));
                    LOGGER.debug("Expanding top edge to {}", zone.getTopRight().getY());
                    break;
                case 2:
                    zone.setBottomLeft(new Coordinate(zone.getBottomLeft().getX() - value, zone.getBottomLeft().getY(), zone.getBottomLeft().getZ()));
                    LOGGER.debug("Expanding left edge to {}", zone.getBottomLeft().getX());
                    break;
                case 3:
                    zone.setBottomLeft(new Coordinate(zone.getBottomLeft().getX(), zone.getBottomLeft().getY() - value, zone.getBottomLeft().getZ()));
                    LOGGER.debug("Expanding bottom edge to {}", zone.getBottomLeft().getY());
                    break;
            }

            LOGGER.debug("Checking for collisions: {} {}", zone.getTopRight(), zone.getBottomLeft());

            collisions = zoneRepository.findZonesWithin(
                    zone.getTopRight().getX(),
                    zone.getTopRight().getY(),
                    zone.getBottomLeft().getX(),
                    zone.getBottomLeft().getY()
            );

            if (!collisions.isEmpty()) {
                LOGGER.debug("Collision detected");

                switch (direction) {
                    case 0:
                        zone.setTopRight(new Coordinate(zone.getTopRight().getX() - value, zone.getTopRight().getY(), zone.getTopRight().getZ()));
                        break;
                    case 1:
                        zone.setTopRight(new Coordinate(zone.getTopRight().getX(), zone.getTopRight().getY() - value, zone.getTopRight().getZ()));
                        break;
                    case 2:
                        zone.setBottomLeft(new Coordinate(zone.getBottomLeft().getX() + value, zone.getBottomLeft().getY(), zone.getBottomLeft().getZ()));
                        break;
                    case 3:
                        zone.setBottomLeft(new Coordinate(zone.getBottomLeft().getX(), zone.getBottomLeft().getY() + value, zone.getBottomLeft().getZ()));
                        break;
                }
            }

            tries++;
        }
        while ((zone.getTopRight().getX() - zone.getBottomLeft().getX() < 10 || zone.getTopRight().getY() - zone.getBottomLeft().getY() < 10) && tries < 25);
    }

    private void selectZoneBiome(Zone zone) {
        List<Zone> neighbors = zoneRepository.findZonesWithin(
                zone.getTopRight().getX() + 1,
                zone.getTopRight().getY() + 1,
                zone.getBottomLeft().getX() - 1,
                zone.getBottomLeft().getY() - 1
        );

        List<WhittakerGridLocation> allWhittakerGridLocations = new ArrayList<>();

        whittakerGridLocationRepository.findAll().forEach(allWhittakerGridLocations::add);

        List<WhittakerGridLocation> validWhittakerGridLocations = allWhittakerGridLocations.stream()
                .filter(w -> neighbors.stream().allMatch(n -> Math.abs(n.getElevation() - w.getElevation()) == 1 || Math.abs(n.getMoisture() - w.getMoisture()) == 1))
                .filter(w -> neighbors.stream().noneMatch(n -> Math.abs(n.getElevation() - w.getElevation()) > 1 || Math.abs(n.getMoisture() - w.getMoisture()) > 1))
                .filter(w -> neighbors.stream().noneMatch(n -> Math.abs(n.getElevation() - w.getElevation()) == 0 && Math.abs(n.getMoisture() - w.getMoisture()) == 0))
                .collect(Collectors.toList());

        if (validWhittakerGridLocations.isEmpty()) {
            LOGGER.warn("No matching grid locations!");
            LOGGER.warn(neighbors.stream()
                    .map(n -> String.format("Neighbor: %s e=%d m=%d", n.getBiome().getName(), n.getElevation(), n.getMoisture()))
                    .collect(Collectors.joining("\n")));

            validWhittakerGridLocations.addAll(allWhittakerGridLocations);
        }

        Collections.shuffle(validWhittakerGridLocations);

        zone.setBiome(validWhittakerGridLocations.get(0).getBiome());
        zone.setElevation(validWhittakerGridLocations.get(0).getElevation());
        zone.setMoisture(validWhittakerGridLocations.get(0).getMoisture());
    }
}
