/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.errorcorrection;

 
import com.mycompany.fouriert.phasor.RecursivePhasor;
import com.mycompany.fouriert.utils.Complex;

import java.util.List;

import java.util.function.Function;


/**
 *
 * @author andrey_pushkarniy
 */
public class TransientMonitor  implements Function<TransientMonitorSource,Double>{
    /**
     * windowWidth               - phasor`s window size 
     * allowableDeviationPercent - percent till which sample does not erroneous
     * maximumAmplitude          - maximum amplitude of signal
     */
    private int     windowWidth;
    private int     n;
    private double allowableDeviationPercent;
    
    public TransientMonitor(int windowWidth) {
        this.windowWidth = windowWidth;
    }
   
        
    /**
     * Function calculate error, being difference between timeSample and recalculated time sample, 
     * obtained from phasor representation.
     * @param sample     - spectrum sample obtained from phasor
     * @param n          - time sample number
     * @param timeSample - time sample from buffer of phasor
     * @return error , being difference between timeSample and recalculated time sample 
     */ 
    private Double calcuateError(Complex sample, int n, double timeSample){
        return Math.abs(
                timeSample - sample.getAmplitude() * Math.sqrt(2.0) * Math.cos((n++  * 2.0 * Math.PI / windowWidth) + sample.getArg())
               );
    }
    
//    
    
    @Override
    public Double apply(TransientMonitorSource data) {
         
        
         Complex spectrumSample = phasor.getSpectrumSample();
         
         /**
          * After buffer initialization, snippet  calculates error for each time sample, located in a buffer.
          */
         if (n == 24) {
             List<Double> buffer = phasor.getBuffer();
             
             for (int i = 0; i < 24; i++) {
                 updateMaxAmplitude(spectrumSample.getAmplitude() * Math.sqrt(2.0));
                 double error = calcuateError(spectrumSample, i, buffer.get(i));
                 if (isEstimateFault(error)) {
                     faultDetected = true;
                     break;
                 }
             }
            
         } 
         /**
          * When window has started to move, snippet  calculates error for only last time sample in a buffer.
          */
         else if (n > 24) {
             updateMaxAmplitude(spectrumSample.getAmplitude() * Math.sqrt(2.0));
             double error = calcuateError(spectrumSample, n-1, phasor.getBuffer().getLast());
             faultDetected = isEstimateFault(error);
               
         }
         return faultDetected;
         
          
        
//        return calcuateError( data.getSpectrumSample(), n, data.getTimeSample());
    }
    
 
    
    public void setAllowableDeviationPercent(double allowableDeviationPercent) {
        this.allowableDeviationPercent = allowableDeviationPercent;
    }
     
    
}
