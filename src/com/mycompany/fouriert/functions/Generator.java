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
     List<Complex> spectrumSamples;
      List<Double> errorEstimates;
      List<Double> x;
     Function[] functions ;
     double df;
   
 
     

    public Generator(RecursiveDiscreteTransform fourierTransform,double df,Function ...  functions) {
        this.fourierTransform = fourierTransform;
        this.functions = functions;
        spectrumSamples = new ArrayList();
        errorEstimates = new ArrayList();
        this.df = df;
         
    }
    
    public void start( int limitPointNubers ){
 
         Arrays.stream(functions).forEach(function->{
         Stream.generate(()->{
              
//            double df = !frequencyDeviations.isEmpty() ? frequencyDeviations.get(ThreadLocalRandom.current().nextInt(0, frequencyDeviations.size() )):0.0; 
            return function.calc( df);
         }).limit(limitPointNubers).forEach(timeSample->{ 
             spectrumSamples.add(fourierTransform.direct(timeSample));
             //System.out.println(timeSample);
             if(fourierTransform.getMonitor()!=null)
                 errorEstimates.add(fourierTransform.calculatePhasorEstimateQality()); 
             //fourierTransform.phasorEstimateOffNominalF(df, fNom);
         });
      });
 
         
    }

    public List<Complex> getSpectrumSamples() {
        return spectrumSamples;
    }

    public List<Double> getErrorEstimates() {
        return errorEstimates;
    }

    public Function[] getFunctions() {
        return functions;
    }
    
    
}
