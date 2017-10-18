/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016-2017 Peter Keeler
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

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.olamedia.noise.Fbm2D;
import ru.olamedia.noise.Noise2D;
import ru.olamedia.noise.SimplexNoise;

import javax.inject.Inject;

@Component
public class NoiseMaps {
    public static final int MIN_ELEVATION = 0;
    public static final int MAX_ELEVATION = 4;
    public static final int MIN_MOISTURE = 1;
    public static final int MAX_MOISTURE = 6;

    private final int WORLD_EXTENT;
    private final double HYPOTENUSE;

    private Fbm2D elevation;
    private Fbm2D moisture;

    @Inject
    public NoiseMaps(@Qualifier("worldSeedElevation") long seedElevation,
                     @Qualifier("worldSeedMoisture") long seedMoisture,
                     @Qualifier("worldExtent") int worldExtent,
                     @Qualifier("worldScale") double worldScale,
                     @Qualifier("worldOctaves") int worldOctaves) {

        WORLD_EXTENT = worldExtent;
        HYPOTENUSE = Math.sqrt(Math.pow(WORLD_EXTENT / 2, 2) + Math.pow(WORLD_EXTENT / 2, 2));

        Noise2D noiseElevation = new SimplexNoise();
        Noise2D noiseMoisture = new SimplexNoise();

        ((SimplexNoise)noiseElevation).setSeed(seedElevation);
        ((SimplexNoise)noiseMoisture).setSeed(seedMoisture);

        elevation = new Fbm2D(noiseElevation);
        elevation.setFrequency(worldScale);
        elevation.setOctaves(worldOctaves);

        moisture = new Fbm2D(noiseMoisture);
        moisture.setFrequency(worldScale);
        moisture.setOctaves(worldOctaves);
    }

    public int getElevation(long x, long y) {
        /*
         * This is a magic formula that will ensure that elevation drops off down to ocean level before reaching
         * WORLD_EXTENT. If a player can move across ocean the game will let them go as far as they want, but all
         * they will find outside the boundaries is more ocean. This is what makes the land form in a circle with
         * more islands at the edges.
         *
         * It also magnifies the elevation noise which is just -1 to 1 to the right range, and clamps it within a
         * valid range.
         *
         * Tinkering with this expression will have substantial effects on your map.
         */
        int value = (int)(8 * ((elevation.get(x, y) - distanceModifier(distance(x, y)) + 1) / 2.0)) - 2;

        if (value < MIN_ELEVATION) {
            value = MIN_ELEVATION;
        }

        if (value > MAX_ELEVATION) {
            value = MAX_ELEVATION;
        }

        return value;
    }

    public int getMoisture(long x, long y) {
        /*
         * This is a magic formula that magnifies the moisture noise which is just -1 to 1 to a good distribution
         * within the valid range and clamps it to that range.
         *
         * Tinkering with this expression will have substantial effects on your map.
         */
        int value =  1 + (int)(6 * ((moisture.get(x, y) + 1) / 2.0));

        if (value < MIN_MOISTURE) {
            value = MIN_MOISTURE;
        }

        if (value > MAX_MOISTURE) {
            value = MAX_MOISTURE;
        }

        return value;
    }

    /*
     * 2D distance from (x, y) to the center of the map (half of WORLD_EXTENT along both axes).
     */
    private double distance(long x, long y) {
        return Math.sqrt(Math.pow(x - (WORLD_EXTENT / 2), 2) + Math.pow(y - (WORLD_EXTENT / 2), 2));
    }

    /*
     * Convert the linear distance to an exponentially growing value. I confess I just generated an image of the
     * world map a bunch of times and fiddled with this until it "felt" right. The basic idea is that the land
     * should drop in elevation slowly near the middle of the map and faster at the edges so that there is a lot
     * of playing space that isn't all tied up in islands, while keeping the map within the boundaries and away
     * from the edges to avoid weird looking islands with sharp vertical or horizontal edges.
     */
    private double distanceModifier(double distance) {
        return Math.pow(1.5 * (distance / HYPOTENUSE), 7);
    }
}
