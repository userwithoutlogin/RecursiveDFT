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
      List<Double> estimates;
     Function[] functions ;
     double df;
     double fNom;
 
     

    public Generator(RecursiveDiscreteTransform fourierTransform,double df,double fNom,Function ...  functions) {
        this.fourierTransform = fourierTransform;
        this.functions = functions;
        spectrumSamples = new ArrayList();
        estimates = new ArrayList();
        this.df = df;
        this.fNom = fNom;
    }
    
    public void start(  ){
 
         Arrays.stream(functions).forEach(function->{
         Stream.generate(()->{
              
//            double df = !frequencyDeviations.isEmpty() ? frequencyDeviations.get(ThreadLocalRandom.current().nextInt(0, frequencyDeviations.size() )):0.0; 
            return function.calc( df);
         }).limit(36).forEach(timeSample->{ 
             spectrumSamples.add(fourierTransform.direct(timeSample));
             if(fourierTransform.getMonitor()!=null)
                 estimates.add(fourierTransform.calculatePhasorEstimateQality()); 
             fourierTransform.phasorEstimateOffNominalF(df, fNom);
         });
      });
 
         
    }

    public List<Complex> getSpectrumSamples() {
        return spectrumSamples;
    }

    public List<Double> getEstimates() {
        return estimates;
    }

    public Function[] getFunctions() {
        return functions;
    }
    
    
}
