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

import com.emergentmud.core.model.Biome;
import com.emergentmud.core.model.WhittakerGridLocation;
import com.emergentmud.core.model.Zone;
import com.emergentmud.core.repository.WhittakerGridLocationRepository;
import com.emergentmud.core.repository.ZoneRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ZoneServiceTest {
    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private WhittakerGridLocationRepository whittakerGridLocationRepository;

    @Mock
    private Random random;

    @Mock
    private Zone zone;

    List<WhittakerGridLocation> allWhittakerGridLocations = new ArrayList<>();

    private ZoneService zoneService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(zoneRepository.save(any(Zone.class))).thenAnswer(i -> {
            Zone zone = i.getArgumentAt(0, Zone.class);

            zone.setId(UUID.randomUUID().toString());

            return zone;
        });

        for (int i = 0; i < 5; i++) {
            WhittakerGridLocation grid = mock(WhittakerGridLocation.class);
            Biome biome = mock(Biome.class);

            when(biome.getName()).thenReturn("Biome " + i);
            when(grid.getBiome()).thenReturn(biome);

            allWhittakerGridLocations.add(grid);
        }

        when(whittakerGridLocationRepository.findAll()).thenReturn(allWhittakerGridLocations);

        zoneService = new ZoneService(
                zoneRepository,
                whittakerGridLocationRepository,
                random
        );
    }

    @Test
    public void testFetchZone() throws Exception {
        zoneService.fetchZone(0L, 1L);

        verify(zoneRepository).findZoneByBottomLeftXLessThanEqualAndTopRightXGreaterThanEqualAndBottomLeftYLessThanEqualAndTopRightYGreaterThanEqual(eq(0L), eq(0L), eq(1L), eq(1L));
    }

    @Test
    public void testCreateZoneAlreadyExists() throws Exception {
        when(zoneRepository.findZoneByBottomLeftXLessThanEqualAndTopRightXGreaterThanEqualAndBottomLeftYLessThanEqualAndTopRightYGreaterThanEqual(eq(0L), eq(0L), eq(0L), eq(0L))).thenReturn(zone);

        Zone zoneResult = zoneService.createZone(0L, 0L);

        assertEquals(zone, zoneResult);
    }

    @Test
    public void testCreateZone() throws Exception {
        when(random.nextInt(eq(3))).thenReturn(0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2);
        when(random.nextInt(eq(4))).thenReturn(0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3);

        Zone zoneResult = zoneService.createZone(0L, 0L);

        assertNotNull(zoneResult);
    }
}
