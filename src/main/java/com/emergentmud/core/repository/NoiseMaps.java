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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.olamedia.noise.Fbm2D;
import ru.olamedia.noise.Noise2D;
import ru.olamedia.noise.SimplexNoise;

@Component
public class NoiseMaps {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoiseMaps.class);
    private static final double SCALE = 0.007;
    private static final int OCTAVES = 8;
    private static final long SEED_ELEVATION = 167L;
    private static final long SEED_MOISTURE = 98L;
    private static final int WORLD_SIZE = 2048;
    private static final double HYPOTENUSE = Math.sqrt(Math.pow(WORLD_SIZE / 2, 2) + Math.pow(WORLD_SIZE / 2, 2));

    private Fbm2D elevation;
    private Fbm2D moisture;

    // TODO properly document all the magic numbers in here

    public NoiseMaps() {
        Noise2D noiseElevation = new SimplexNoise();
        Noise2D noiseMoisture = new SimplexNoise();

        ((SimplexNoise)noiseElevation).setSeed(SEED_ELEVATION);
        ((SimplexNoise)noiseMoisture).setSeed(SEED_MOISTURE);

        elevation = new Fbm2D(noiseElevation);
        elevation.setFrequency(SCALE);
        elevation.setOctaves(OCTAVES);

        moisture = new Fbm2D(noiseMoisture);
        moisture.setFrequency(SCALE);
        moisture.setOctaves(OCTAVES);
    }

    public int getElevation(long x, long y) {
        int value = (int)(8 * ((elevation.get(x, y) - distanceModifier(distance(x, y)) + 1) / 2.0)) - 2;

        if (value < 0) {
            value = 0;
        }

        if (value > 4) {
            value = 4;
        }

        return value;
    }

    public int getMoisture(long x, long y) {
        int value =  1 + (int)(6 * ((moisture.get(x, y) + 1) / 2.0));

        if (value < 1) {
            value = 1;
        }

        if (value > 6) {
            value = 6;
        }

        return value;
    }

    private double distance(long x, long y) {
        return Math.sqrt(Math.pow(x - (WORLD_SIZE / 2), 2) + Math.pow(y - (WORLD_SIZE / 2), 2));
    }

    private double distanceModifier(double distance) {
        return Math.pow(1.5 * (distance / HYPOTENUSE), 7);
    }
}
