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
import com.emergentmud.core.model.Room;
import com.emergentmud.core.model.Zone;
import com.emergentmud.core.repository.BiomeRepository;
import com.emergentmud.core.repository.RoomRepository;
import com.emergentmud.core.repository.ZoneRepository;
import com.hoten.delaunay.geom.Rectangle;
import com.hoten.delaunay.voronoi.Center;
import com.hoten.delaunay.voronoi.Corner;
import com.hoten.delaunay.voronoi.Edge;
import com.hoten.delaunay.voronoi.nodename.as3delaunay.Voronoi;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PolygonalZoneBuilderTest {
    @Spy
    private Random random;

    @Mock
    private LloydsRelaxation lloydsRelaxation;

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private BiomeRepository biomeRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BiomeSelector biomeSelector;

    @Mock
    private ImageBuilder imageBuilder;

    @Mock
    private VoronoiGraphBuilder voronoiGraphBuilder;

    @Mock
    private ElevationBuilder elevationBuilder;

    @Mock
    private MoistureBuilder moistureBuilder;

    @Mock
    private Zone zone;

    @Mock
    private BufferedImage map;

    @Captor
    private ArgumentCaptor<List<Room>> roomListCaptor;

    private int worldSites = 3000;
    private int worldExtent = 200;
    private int worldLloyds = 1;

    private List<Biome> biomes = new ArrayList<>();
    private int[] pixels = new int[worldExtent * worldExtent];

    private PolygonalZoneBuilder polygonalZoneBuilder;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        for (int i = 0; i < 3; i++) {
            biomes.add(new Biome("Biome " + i, i));
        }

        when(lloydsRelaxation.relaxPoints(any(Voronoi.class))).thenCallRealMethod();
        when(zoneRepository.save(any(Zone.class))).thenReturn(zone);
        when(roomRepository.save(anyListOf(Room.class))).thenAnswer(new Answer<List<Room>>() {
            @Override
            public List<Room> answer(InvocationOnMock invocation) throws Throwable {
                //noinspection unchecked
                List<Room> roomList = invocation.getArgumentAt(0, List.class);

                roomList.forEach(r -> r.setId(UUID.randomUUID().toString()));

                return roomList;
            }
        });
        when(biomeRepository.findAll()).thenReturn(biomes);
        when(biomeRepository.findByName(eq("Ocean"))).thenReturn(biomes.get(0));
        when(imageBuilder.build(any(), eq(random), any(), any(), any())).thenReturn(map);
        when(map.getHeight()).thenReturn(worldExtent);
        when(map.getWidth()).thenReturn(worldExtent);
        when(map.getRGB(eq(0), eq(0), eq(worldExtent), eq(worldExtent), eq(pixels), eq(0), eq(worldExtent))).thenAnswer(new Answer<int[]>() {
            @Override
            public int[] answer(InvocationOnMock invocation) throws Throwable {
                return pixels;
            }
        });

        polygonalZoneBuilder = new PolygonalZoneBuilder(
                random,
                worldSites,
                worldExtent,
                worldLloyds,
                lloydsRelaxation,
                zoneRepository,
                biomeRepository,
                roomRepository,
                biomeSelector,
                imageBuilder,
                voronoiGraphBuilder,
                elevationBuilder,
                moistureBuilder
        );
    }

    @Test
    public void testBuild() throws Exception {
        Zone zone = polygonalZoneBuilder.build(0L, 0L, 0L);

        verify(voronoiGraphBuilder).buildGraph(any(Voronoi.class), anyListOf(Edge.class), anyListOf(Center.class), anyListOf(Corner.class));
        verify(voronoiGraphBuilder).improveCorners(anyListOf(Edge.class), anyListOf(Corner.class));
        verify(elevationBuilder).assignCornerElevations(any(Rectangle.class), anyListOf(Corner.class));
        verify(elevationBuilder).assignOceanCoastAndLand(anyListOf(Center.class), anyListOf(Corner.class));
        verify(elevationBuilder).redistributeElevations(anyListOf(Corner.class));
        verify(elevationBuilder).assignPolygonElevations(anyListOf(Center.class));
        verify(elevationBuilder).calculateDownslopes(anyListOf(Corner.class));
        verify(moistureBuilder).createRivers(any(Rectangle.class), anyListOf(Corner.class));
        verify(moistureBuilder).assignCornerMoisture(anyListOf(Corner.class));
        verify(moistureBuilder).redistributeMoisture(anyListOf(Corner.class));
        verify(moistureBuilder).assignPolygonMoisture(anyListOf(Center.class));
        verify(biomeSelector).assignBiomes(anyListOf(Center.class));

        verify(lloydsRelaxation, times(worldLloyds)).relaxPoints(any(Voronoi.class));

        verify(roomRepository, times(worldExtent)).save(roomListCaptor.capture());

        List<List<Room>> savedRooms = roomListCaptor.getAllValues();

        savedRooms.forEach(roomList -> {
            assertTrue(roomList.size() == worldExtent);

            roomList.forEach(r -> {
                assertNotNull(r.getId());
                assertNotNull(r.getX());
                assertNotNull(r.getY());
                assertEquals(0L, (long)r.getZ());
                assertEquals(zone, r.getZone());
                assertNotNull(r.getBiome());
            });
        });

        assertNotNull(zone);
    }
}
