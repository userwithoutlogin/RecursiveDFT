/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.errorcorrection;

import com.mycompany.fouriert.ft.SampleGenerator;
import com.mycompany.fouriert.utils.Complex;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author andrey_pushkarniy
 */
public class TransientMonitor {
    private int     windowWidth;
    private double  allowableDeviationPercent = 15;
    private Double  maximumAmplitude;
    private boolean faultDetected = false;

    public TransientMonitor(int windowWidth) {
        this.windowWidth = windowWidth;
    }
    public boolean calculatePhasorEstimateQality(Complex sample, int n, double timeSample) {
        if (faultDetected == false) {
            n -= windowWidth;
            double amplitude = sample.amplitude();
            updateMaxAmplitude(amplitude);
            double error = Math.abs(timeSample - amplitude * Math.sqrt(2.0) * Math.cos((n * 2.0 * Math.PI / windowWidth) + sample.arg()));
            double percent = (error / maximumAmplitude - 1) * 100;
            if (percent > allowableDeviationPercent) {
                faultDetected = true;
            }
        }
        return faultDetected;
    }
    public void updateMaxAmplitude(double amplitude) {
        double percent = maximumAmplitude != null ? (amplitude / maximumAmplitude - 1) * 100 : 0.0;
        if (maximumAmplitude == null || (percent < allowableDeviationPercent && amplitude > maximumAmplitude)) {
            maximumAmplitude = amplitude;
        }
    }

    public Complex getSample(Complex sample, int n, double timeSample) {
        Complex tempSample = sample;
        calculatePhasorEstimateQality(tempSample, n, timeSample);
        if (faultDetected) {
            tempSample = new Complex(0.0, 0.0);
        }
        return tempSample;
    }

    public void setAllowableDeviationPercent(double allowableDeviationPercent) {
        this.allowableDeviationPercent = allowableDeviationPercent;
    }
  
}
