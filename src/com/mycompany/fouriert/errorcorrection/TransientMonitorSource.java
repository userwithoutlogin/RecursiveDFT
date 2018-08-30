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
    
    private double sample;
    private Complex phasor;
    
    public double getSample() {
        return sample;
    }

    public void setSample(double sample) {
        this.sample = sample;
    }

    public Complex getPhasor() {
        return phasor;
    }

    public void setPhasor(Complex phasor) {
        this.phasor = phasor;
    }

    
    
    
}
