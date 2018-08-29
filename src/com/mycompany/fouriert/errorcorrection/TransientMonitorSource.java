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
public class TransientMonitorSource {
    
    private double timeSample;
    private Complex spectrumSample;
    

   
    public double getTimeSample() {
        return timeSample;
    }

    public void setTimeSample(double timeSample) {
        this.timeSample = timeSample;
    }

    public Complex getSpectrumSample() {
        return spectrumSample;
    }

    public void setSpectrumSample(Complex spectrumSample) {
        this.spectrumSample = spectrumSample;
    }

    
    
    
}
