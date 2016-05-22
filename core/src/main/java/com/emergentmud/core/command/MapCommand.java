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

package com.emergentmud.core.command;

import com.emergentmud.core.model.Entity;
import com.emergentmud.core.model.stomp.GameOutput;
import opensimplex.OpenSimplexNoise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MapCommand implements Command {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapCommand.class);
    private static final int MAP_EXTENT = 20;
    private static final OpenSimplexNoise BIG_SIMPLEX_NOISE = new OpenSimplexNoise(2309480L);
    private static final OpenSimplexNoise DETAIL_SIMPLEX_NOISE = new OpenSimplexNoise(9879238L);

    @Override
    public GameOutput execute(GameOutput output, Entity entity, String[] tokens, String raw) {
        for (long y = entity.getY() + MAP_EXTENT, i = 0; y >= entity.getY() - MAP_EXTENT; y--, i++) {
            StringBuilder line = new StringBuilder();

            for (long x = entity.getX() - MAP_EXTENT; x <= entity.getX() + MAP_EXTENT; x++) {
                if (x == entity.getX() && y == entity.getY()) {
                    line.append("[cyan][]</span>");
                } else {
                    byte noise = noise(x, y);

                    if (noise < 0) {
                        line.append(String.format("<span style='color: #%02x%02x%02x'>[]</span>", 0, 0, noise + 256));
                    } else {
                        line.append(String.format("<span style='color: #%02x%02x%02x'>[]</span>", 0, noise + 128, 0));
                    }
                }
            }

            if (i % 10 == 0) {
                line.append(String.format("  [yellow]%d", y));
            }

            output.append(line.toString());
        }

        StringBuilder line = new StringBuilder("[yellow]");
        int offset = 0;

        for (long x = entity.getX() - MAP_EXTENT, i = 0; x <= entity.getX() + MAP_EXTENT; x++, i++) {
            if (i % 10 == 0) {
                line.append(x + offset);

                int length = Long.toString(x).length();

                if (length == 1) {
                    line.append("&nbsp;");
                } else {
                    offset += Math.max(0, length - 2);
                }
            } else {
                if (offset >= 2) {
                    offset -= 2;
                } else if (offset == 1) {
                    offset--;
                    line.append("&nbsp;");
                } else {
                    line.append("&nbsp;&nbsp;");
                }
            }
        }

        output.append(line.toString());

        return output;
    }

    private byte noise(long x, long y) {
        return (byte)((bigNoise(x, y) + detailNoise(x, y)) / 2.0);
    }

    private byte detailNoise(long x, long y) {
        final int octaves = 8;
        final double gain = 0.45;
        final double lacunarity = 2.5;
        double total = 0.0;
        double frequency = 1.0 / MAP_EXTENT;
        double amplitude = gain;

        for (int i = 0; i < octaves; ++i) {
            total += DETAIL_SIMPLEX_NOISE.eval((float)x * frequency, (float)y * frequency) * amplitude;
            frequency *= lacunarity;
            amplitude *= gain;
        }

        total = clamp(total);

        byte result = (byte)Math.round(Byte.MAX_VALUE * total);

        LOGGER.trace("Detail noise: ({}, {}) Total: {} -> {}", x, y, total, result);

        return result;
    }

    private byte bigNoise(long x, long y) {
        final int octaves = 8;
        final double gain = 0.95;
        final double lacunarity = 0.4;
        double total = 0.0;
        double frequency = 1.0 / MAP_EXTENT;
        double amplitude = gain;

        for (int i = 0; i < octaves; ++i) {
            total += BIG_SIMPLEX_NOISE.eval((float)x * frequency, (float)y * frequency) * amplitude;
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
