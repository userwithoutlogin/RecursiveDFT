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
    private SampleGenerator generator = new SampleGenerator();
    private double nominalFrequency; 
    private double actualFrequency; 
    private int windowWidth;

    public TransientMonitor(double nominalFrequency, double actualFrequency, int windowWidth) {
        this.nominalFrequency = nominalFrequency;
        this.actualFrequency = actualFrequency;
        this.windowWidth = windowWidth;
    }
 
    
     
 
    public double calculatePhasorEstimateQality( Complex  sample,int n,List<Double> timeSamples){
         List<Double> monitorVector = new ArrayList();
         int delta =  n - windowWidth;
         n = delta;
         for(int k=0;k<timeSamples.size();k++,n++){
            double recalculatedSample = sample.amplitude()*Math.sqrt(2.0) * Math.cos((n*2.0*Math.PI*(actualFrequency)/(windowWidth*nominalFrequency)) + sample.arg()); 
//            double recalculatedSample = generator.timeSample(windowWidth,n, sample);
            monitorVector.add( timeSamples.get(k) - recalculatedSample);            
             
        }
        
              
        return monitorVector.stream().reduce(0.0, (smpl1,smpl2)->Math.abs(smpl1)+Math.abs(smpl2));
    }
  
}
