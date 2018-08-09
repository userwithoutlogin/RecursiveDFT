/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.ft;

import com.mycompany.fouriert.complex.Complex;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author root
 */
public class SampleGenerator {
    private List<Complex> buffer;
    
    public double timeSample(int samplesCount,int n,List<Complex> samples){
         double timeSample = 0.0;
         
        for(int k=0;k<samplesCount;k++){
                Complex temp = Complex.initByEuler(1,2*Math.PI*k*n/samplesCount);
                timeSample += (samples.get(k).multiply(temp)).getRe();
            }
        return timeSample;
    }
    public Complex spectrumSample(int samplesCount,int n,List<Double> samples){
        Complex spectrumSample = new Complex(0.0,0.0);
        buffer = new ArrayList();
        for(int k=0;k<samplesCount;k++){
//                Complex temp = Complex.initByEuler(1,-2*Math.PI*k*n/samplesCount);
                Complex temp = Complex.initByEuler(1,-2*Math.PI*k/samplesCount);
                spectrumSample = spectrumSample.add( temp.multiply(samples.get(k) ));
            }
        return spectrumSample;
    }
}
