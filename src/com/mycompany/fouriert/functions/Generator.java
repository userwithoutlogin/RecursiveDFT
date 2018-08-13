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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author andrey_pushkarniy
 */
public class Generator {
     
     private final RecursiveDiscreteTransform fourierTransform;
     LinkedList<Complex> spectrumSamples;
     LinkedList<Double> estimates;
     Stream generator ;
     Function[] functions ;
     

    public Generator(RecursiveDiscreteTransform fourierTransform, Function ...  functions) {
        this.fourierTransform = fourierTransform;
        this.functions = functions;
        spectrumSamples = new LinkedList();
        estimates = new LinkedList();
    }
    
    public void start(){
//         Stream <Double> stream = Stream.generate(()->{
//            return func.calc();
//         });
      Arrays.stream(functions).forEach(function->{
         Stream.generate(()->{
            return function.calc();
         }).limit(36).forEach(timeSample->{ 
             System.out.println(timeSample);
                spectrumSamples.add(fourierTransform.direct(timeSample));
                estimates.add(fourierTransform.calculatePhasorEstimateQality());
         });
      });
//          stream.limit(36).
//                  forEach(timeSample->{ 
//              spectrumSamples.add(fourierTransform.direct(timeSample));
//              System.out.println(timeSample+ "    "+ spectrumSamples.getLast());
//         });

         
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
