/*
 * EmergentMUD - A modern MUD with a procedurally generated world.
 * Copyright (C) 2016 BoneVM, LLC
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

import com.emergentmud.core.command.MapCommand;
import opensimplex.OpenSimplexNoise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class NoiseUtility {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoiseUtility.class);

    private OpenSimplexNoise elevationBigSimplexNoise;
    private OpenSimplexNoise elevationDetailSimplexNoise;
    private OpenSimplexNoise waterTableBigSimplexNoise;
    private OpenSimplexNoise waterTableDetailSimplexNoise;

    @Inject
    public NoiseUtility(OpenSimplexNoise elevationBigSimplexNoise,
                        OpenSimplexNoise elevationDetailSimplexNoise,
                        OpenSimplexNoise waterTableBigSimplexNoise,
                        OpenSimplexNoise waterTableDetailSimplexNoise) {

        this.elevationBigSimplexNoise = elevationBigSimplexNoise;
        this.elevationDetailSimplexNoise = elevationDetailSimplexNoise;
        this.waterTableBigSimplexNoise = waterTableBigSimplexNoise;
        this.waterTableDetailSimplexNoise = waterTableDetailSimplexNoise;
    }

    public byte elevationNoise(long x, long y) {
        double result = ((bigNoise(x, y, elevationDetailSimplexNoise, 8, 0.45, 2.5)
                + detailNoise(x, y, elevationBigSimplexNoise, 8, 0.95, 0.4)) / 2.0);

        if (result > Byte.MAX_VALUE || result < Byte.MIN_VALUE) {
            throw new IllegalStateException("Noise result is out of range: " + result);
        }

        return (byte)result;
    }

    public byte waterTableNoise(long x, long y) {
        double result = ((bigNoise(x, y, waterTableDetailSimplexNoise, 8, 0.45, 2.5)
                + detailNoise(x, y, waterTableBigSimplexNoise, 8, 0.95, 0.4)) / 2.0);

        if (result > Byte.MAX_VALUE || result < Byte.MIN_VALUE) {
            throw new IllegalStateException("Noise result is out of range: " + result);
        }

        return (byte)result;
    }

    private byte detailNoise(long x, long y, final OpenSimplexNoise openSimplex, final int octaves, final double gain, final double lacunarity) {
        double total = 0.0;
        double frequency = 1.0 / MapCommand.MAP_EXTENT_X;
        double amplitude = gain;

        for (int i = 0; i < octaves; ++i) {
            total += openSimplex.eval((float)x * frequency, (float)y * frequency) * amplitude;
            frequency *= lacunarity;
            amplitude *= gain;
        }

        total = clamp(total);

        byte result = (byte)Math.round(Byte.MAX_VALUE * total);

        LOGGER.trace("Detail noise: ({}, {}) Total: {} -> {}", x, y, total, result);

        return result;
    }

    private byte bigNoise(long x, long y, final OpenSimplexNoise openSimplex, final int octaves, final double gain, final double lacunarity) {
        double total = 0.0;
        double frequency = 1.0 / MapCommand.MAP_EXTENT_X;
        double amplitude = gain;

        for (int i = 0; i < octaves; ++i) {
            total += openSimplex.eval((float)x * frequency, (float)y * frequency) * amplitude;
            frequency *= lacunarity;
            amplitude *= gain;
        }

        total = clamp(total);

        byte result = (byte)Math.round(Byte.MAX_VALUE * total);

        LOGGER.trace("Big noise: ({}, {}) Total: {} -> {}", x, y, total, result);

        return result;
    }

    private double clamp(double in) {
        if (in > 1.0) {
            in = 1.0;
        } else if (in < -1.0) {
            in = -1.0;
        }

        return in;
    }
}
