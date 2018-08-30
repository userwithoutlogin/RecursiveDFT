/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.functions;

import com.mycompany.fouriert.errorcorrection.TransientMonitor;
import com.mycompany.fouriert.errorcorrection.TransientMonitorSource;
import com.mycompany.fouriert.phasor.RecursiveDFT;
import com.mycompany.fouriert.utils.Complex;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author andrey_pushkarniy
 */
public class Generator {
     private   TransientMonitor monitor;
     private final RecursiveDFT recursiveDFT;
     List<Complex> spectrumSamples;
      List<Double> errorEstimates;
      List<Double> timeSamples;
      List<Double> x;
     Function[] functions ;
     double df;
   
 
     

    public Generator(RecursiveDFT  fourierTransform,double df,Function ...  functions) {
        this.recursiveDFT = fourierTransform;
        this.functions = functions;
        spectrumSamples = new ArrayList();
        errorEstimates = new ArrayList();
        timeSamples = new ArrayList();
        this.df = df;
         
    }
    
    public void start( int limitPointNubers ){
 
         Arrays.stream(functions).forEach(function->{
         Stream.generate(()->{
              
//            double df = !frequencyDeviations.isEmpty() ? frequencyDeviations.get(ThreadLocalRandom.current().nextInt(0, frequencyDeviations.size() )):0.0; 
            return function.calc( df);
         }).limit(limitPointNubers)
           .forEach(sample->{ 
              Complex phasor =  recursiveDFT.apply(sample);
             
              
             if(monitor!=null&&phasor!=null){
             spectrumSamples.add(phasor);
                 TransientMonitorSource source = new TransientMonitorSource();
                 source.setPhasor(phasor);
                 source.setSample(sample);
                 double error = monitor.apply(source);
             System.out.println((sample+"").replace(".", ","));
             
              timeSamples.add(sample);
                 errorEstimates.add(error); 
             }
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

    public List<Double> getTimeSamples() {
        return timeSamples;
    }

    public TransientMonitor getMonitor() {
        return monitor;
    }

    public void setMonitor(TransientMonitor monitor) {
        this.monitor = monitor;
    }
    
    
}