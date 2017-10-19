package ru.olamedia.noise;

// The MIT License
//
// Copyright (c) 2013 olamedia
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
/**
 * @author olamedia
 *
 */
public class Value {
    public static final double abs(final double value) {
        return value < 0 ? -value : value;
    }

    public static final double add(final double value1, final double value2) {
        return value1 + value2;
    }

    public static final double blend(double value1, double value2, double control) {
        return lerp(value1, value2, (control + 1) / 2.0);
    }

    public static final double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public static final double constant(final double value) {
        return value;
    }

    public static final double exponent(double value, double exponent) {
        return (Math.pow(abs(value + 1.0) * 0.5, exponent) * 2.0 - 1.0);
    }

    public static final double max(double value1, double value2) {
        return Math.max(value1, value2);
    }

    public static final double min(final double value1, final double value2) {
        return Math.min(value1, value2);
    }

    public static final double multiply(final double value1, final double value2) {
        return value1 * value2;
    }

    public static final double scale(final double value, final double scale) {
        return value * scale;
    }

    public static final double bias(final double value, final double bias) {
        return value + bias;
    }

    public static final double scaleBias(final double value, final double scale, final double bias) {
        return value * scale + bias;
    }

    public static final double select(double value1, double value2, double control, double low, double high,
                                      double edgeFalloff) {
        if (edgeFalloff == 0.0) {
            if (control > low && control < high) {
                return value2;
            }
            return value1;
        }
        // completely out
        if (control < low - edgeFalloff) {
            return value1;
        }
        if (control > high + edgeFalloff) {
            return value1;
        }
        //
        if (control < low + edgeFalloff) {
            // low edge
            return lerp(value1, value2, scurve(0.5 + 0.5 * (control - low) / edgeFalloff));
        }
        if (control < high - edgeFalloff) {
            // completely inside
            return value2;
        }
        // high edge
        return lerp(value2, value1, scurve(0.5 + 0.5 * (control - high) / edgeFalloff));
    }

    /**
     * Fractional Brownian Motion 2D
     * frequency = 1 / hgrid
     *
     * @param octaves
     * @return
     */
    public static final double fbm(final Noise2D noise, final double x, final double y, final int octaves,
                                   final double H, double frequency, final double lacunarity, final double gain) {
        double total = 0;
        double amplitude = gain;
        // , frequency *= lacunarity
        for (int i = 0; i < octaves; ++i, frequency *= lacunarity, amplitude *= gain) {
            total += noise.get(x * frequency, y * frequency) * amplitude;
        }
        return clamp(total, -1, 1);
    }

    public static final double turbulence(double value) {
        return abs(value);
    }

    public static final double ridged(double value) {
        return ridge(value, 1.0);
    }

    public static final double turbulence(Noise2D noise, double x, double y, int octaves) {
        float total = 0.0f;
        double fscale = 1.0;
        for (int i = 0; i <= octaves; i++, fscale *= 2) {
            total += Math.abs(noise.get(fscale * x, fscale * y)) / fscale;
        }
        return clamp(total, -1.0, 1);
    }

    public static final double ridgedMulti(Noise2D noise, double x, double y, int octaves, double frequency,
                                           double lacunarity, double gain) {
        double total = 0;
        gain = 2.0;
        double prev = 1.0;
        double offset = 1.0;
        double baseWeight = -1.0;
        for (int octave = 0; octave < octaves; ++octave) {
            double weight = Math.pow(frequency, baseWeight);
            double signal = ridge(noise.get(x, y), offset);
            total += signal * prev * weight;
            prev = clamp(signal * gain, 0, 1);
            // next octave
            frequency *= lacunarity;
            x *= lacunarity;
            y *= lacunarity;
        }

        return clamp(total * 1.25 - 1, -1, 1);
    }

    /**
     * Linear interpolation
     *
     * @param a
     * @param b
     * @param alpha
     * @return
     */
    public static final double lerp(final double a, final double b, final double alpha) {
        return a + alpha * (b - a);
    }

    public static final double scurve(final double t) {
        return (t * t * (3.0 - 2.0 * t));
    }

    /**
     * Used by Perlin noise
     *
     * @param t
     * @return
     */
    public static final double fade(final double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    public static final double cosineInterpolate(double a, double b, double alpha) {
        return a + (1 - Math.cos(alpha * PI)) * 0.5 * (b - a);
    }

    /**
     * Cubic interpolation
     * a0 a1 ... x ... b1 b0
     *
     * @param a0
     * @param a1
     * @param b1
     * @param b0
     * @param x
     * @return
     */
    public static final double cubicInterpolate(double a0, double a1, double b1, double b0, double x) {
        return x * (x * ((x - 1) * (b0 - b1) + (2 - x) * (a0 - a1)) + b1 - a0) + a1;
    }

    public static final int fastFloor(final double t) {
        return t > 0 ? (int) t : (int) t - 1;
    }

    public static final double square(final double value) {
        return value * value;
    }

    public static float noise(int x, int y) {
        // simple noise
        int n = x + y * 57;
        n = (n << 13) ^ n;
        return (1.0f - ((n * (n * n * 15731 + 789221) + 1376312589) & 0x7fffffff) / 1073741824f);
    }

    public static final double ridge(final double h, final double offset) {
        return square(offset - abs(h));
    }

    public static final double PI = Math.PI;
}