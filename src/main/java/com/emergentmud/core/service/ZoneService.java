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

import com.emergentmud.core.model.WhittakerGridLocation;
import com.emergentmud.core.model.Zone;
import com.emergentmud.core.repository.WhittakerGridLocationRepository;
import com.emergentmud.core.repository.ZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
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

    public Zone fetchZone(Long x, Long y) {
        return zoneRepository.findZoneByBottomLeftXLessThanEqualAndTopRightXGreaterThanEqualAndBottomLeftYLessThanEqualAndTopRightYGreaterThanEqual(x, x, y, y);
    }

    public Zone createZone(Long x, Long y) {
        Zone zone = fetchZone(x, y);

        if (zone != null) {
            LOGGER.debug("Request to create zone that already exists: ({}, {})", x, y);
            return zone;
        }

        zone = new Zone();

        zone.setTopRightX(x);
        zone.setTopRightY(y);
        zone.setBottomLeftX(x);
        zone.setBottomLeftY(y);

        expandZoneBorders(zone);
        selectZoneBiome(zone);

        zone = zoneRepository.save(zone);

        LOGGER.info("Saving new zone: {} ({}, {}) ({}, {})",
                zone.getBiome().getName(),
                zone.getBottomLeftX(),
                zone.getBottomLeftY(),
                zone.getTopRightX(),
                zone.getTopRightY());

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
                    zone.setTopRightX(zone.getTopRightX() + value);
                    LOGGER.debug("Expanding right edge to {}", zone.getTopRightX());
                    break;
                case 1:
                    zone.setTopRightY(zone.getTopRightY() + value);
                    LOGGER.debug("Expanding top edge to {}", zone.getTopRightY());
                    break;
                case 2:
                    zone.setBottomLeftX(zone.getBottomLeftX() - value);
                    LOGGER.debug("Expanding left edge to {}", zone.getBottomLeftX());
                    break;
                case 3:
                    zone.setBottomLeftY(zone.getBottomLeftY() - value);
                    LOGGER.debug("Expanding bottom edge to {}", zone.getBottomLeftY());
                    break;
            }

            collisions = zoneRepository.findZonesByBottomLeftXLessThanEqualAndTopRightXGreaterThanEqualAndBottomLeftYLessThanEqualAndTopRightYGreaterThanEqual(
                    zone.getTopRightX(),
                    zone.getBottomLeftX(),
                    zone.getTopRightY(),
                    zone.getBottomLeftY()
            );

            if (!collisions.isEmpty()) {
                LOGGER.debug("Collision detected");

                switch (direction) {
                    case 0:
                        zone.setTopRightX(zone.getTopRightX() - value);
                        break;
                    case 1:
                        zone.setTopRightY(zone.getTopRightY() - value);
                        break;
                    case 2:
                        zone.setBottomLeftX(zone.getBottomLeftX() + value);
                        break;
                    case 3:
                        zone.setBottomLeftY(zone.getBottomLeftY() + value);
                        break;
                }
            }

            tries++;
        }
        while ((zone.getTopRightX() - zone.getBottomLeftX() < 10 || zone.getTopRightY() - zone.getBottomLeftY() < 10) && tries < 25);
    }

    private void selectZoneBiome(Zone zone) {
        List<Zone> neighbors = zoneRepository.findZonesByBottomLeftXLessThanEqualAndTopRightXGreaterThanEqualAndBottomLeftYLessThanEqualAndTopRightYGreaterThanEqual(
                zone.getTopRightX() + 1,
                zone.getBottomLeftX() - 1,
                zone.getTopRightY() + 1,
                zone.getBottomLeftY() - 1
        );

        List<WhittakerGridLocation> allWhittakerGridLocations = whittakerGridLocationRepository.findAll();
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
