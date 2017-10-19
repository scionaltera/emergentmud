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

public class Fbm2D implements Noise2D {
    public static final double DEFAULT_LACUNARITY = 2.0;
    public static final double DEFAULT_GAIN = 0.5;
    private Noise2D source;

    private int octaves = 1;
    private double lacunarity = DEFAULT_LACUNARITY;
    private double gain = DEFAULT_GAIN;
    private double frequency = 1; // base freq
    private double[] freq;
    private double[] ampl;
    @SuppressWarnings("unused")
    private double maxAmpl;

    public Fbm2D() {
    }

    public Fbm2D(Noise2D source) {
        setSource(source);
    }

    public double getLacunarity() {
        return lacunarity;
    }

    public void setLacunarity(double lacunarity) {
        this.lacunarity = lacunarity;
        init();
    }

    public double getGain() {
        return gain;
    }

    public void setGain(double gain) {
        this.gain = gain;
        init();
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
        init();
    }

    public int getOctaves() {
        return octaves;
    }

    public void setOctaves(int octaves) {
        this.octaves = octaves;
        init();
    }

    private void init() {
        freq = new double[octaves];
        ampl = new double[octaves];
        freq[0] = frequency;
        ampl[0] = gain;
        maxAmpl = gain;
        if (octaves > 1) {
            for (int i = 1; i < octaves; ++i) {
                freq[i] = freq[i - 1] * lacunarity;
                ampl[i] = ampl[i - 1] * gain;
                maxAmpl += ampl[i];
            }
        }
    }

    public double get(double x, double y) {
        double total = 0;
        for (int i = 0; i < octaves; ++i) {
            total += source.get(x * freq[i], y * freq[i]) * ampl[i];
        }
        return total;
    }

    public Noise2D getSource() {
        return source;
    }

    public void setSource(Noise2D source) {
        this.source = source;
    }

}