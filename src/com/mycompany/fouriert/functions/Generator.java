/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.functions;

import com.mycompany.fouriert.complex.Complex;
import com.mycompany.fouriert.ft.FourierTransform;
import com.mycompany.fouriert.ft.RecursiveDiscreteTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author andrey_pushkarniy
 */
public class Generator {
     
     private final RecursiveDiscreteTransform fourierTransform;
     LinkedList<Complex> spectrumSamples;
     LinkedList<Double> estimates;
     
     Function[] functions ;
     
     private List<Double> frequencyDeviations  ; 
      
   
    public void initFreqDeviations(double start,double end,double delta){
           for(double d = start;d<=end;d+=delta)
               frequencyDeviations.add(d);
            
    }
     

    public Generator(RecursiveDiscreteTransform fourierTransform, Function ...  functions) {
        this.fourierTransform = fourierTransform;
        this.functions = functions;
        spectrumSamples = new LinkedList();
        estimates = new LinkedList();
       frequencyDeviations = new ArrayList(); 
    }
    
    public void start(){
 
         Arrays.stream(functions).forEach(function->{
         Stream.generate(()->{
              
            double df = !frequencyDeviations.isEmpty() ? frequencyDeviations.get(ThreadLocalRandom.current().nextInt(0, frequencyDeviations.size() )):0.0; 
            return function.calc( df);
         }).limit(36).forEach(timeSample->{ 
             spectrumSamples.add(fourierTransform.direct(timeSample));
             estimates.add(fourierTransform.calculatePhasorEstimateQality());
         });
      });
 
         
    }

    public List<Complex> getSpectrumSamples() {
        return spectrumSamples;
    }

    public LinkedList<Double> getEstimates() {
        return estimates;
    }

    public Function[] getFunctions() {
        return functions;
    }
    
    
}
