/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.errorcorrection;

import com.mycompany.fouriert.utils.Complex;

/**
 *
 * @author andrey_pushkarniy
 */
public class DataForMonitor {
    private double timeSample;
    private int n;
    private Complex spectrumSample;

    public DataForMonitor(double timeSample, int n, Complex spectrumSample) {
        this.timeSample = timeSample;
        this.n = n;
        this.spectrumSample = spectrumSample;
    }

    public double getTimeSample() {
        return timeSample;
    }

    public int getN() {
        return n;
    }

    public Complex getSpectrumSample() {
        return spectrumSample;
    }
    
    
}
