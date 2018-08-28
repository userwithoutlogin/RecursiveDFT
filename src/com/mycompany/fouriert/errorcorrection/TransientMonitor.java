/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.errorcorrection;

 
import com.mycompany.fouriert.phasor.RecursivePhasor;
import com.mycompany.fouriert.utils.Complex;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 *
 * @author andrey_pushkarniy
 */
public class TransientMonitor  implements Function<RecursivePhasor,Boolean>{
    /**
     * windowWidth               - phasor`s window size 
     * allowableDeviationPercent - percent till which sample does not erroneous
     * maximumAmplitude          - maximum amplitude of signal
     */
    private int     windowWidth;
    private double  allowableDeviationPercent = 115;
    private Double  maximumAmplitude;
 

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
                timeSample - sample.getAmplitude() * Math.sqrt(2.0) * Math.cos((n  * 2.0 * Math.PI / windowWidth) + sample.getArg())
               );
    }
    
    /**
     * Function compares error value with maximum amplitude value, if error greater than  maximumAmplitude by a percentage, 
     * which greater than allowableDeviationPercent, current   estimate of phasor is considered as faulted.
     * @param  error         - error , being difference between timeSample and recalculated time sample 
     * @return faultDetected - variable points out that estimate of phasor is fault
     */
    private boolean isEstimateFault(double error){
        boolean faultDetected = false;
        if (maximumAmplitude < error) {
            double percent = (error / maximumAmplitude ) * 100;
            faultDetected = percent > allowableDeviationPercent;
        }
        return faultDetected;
    }
    
    /**
     *  Function updates maximumAmplitude, if  'amplitude' is greater than maximumAmplitude, 
     *  but less or equals than maximumAmplitude with accounting 'allowableDeviationPercent'.
     *  @param amplitude - amplitude is obtained from current estimate of phasor
     */
    private void updateMaxAmplitude(double amplitude) {
      // percent - percentage by which 'amplitude' is greater than maximumAmplitude
        double percent = maximumAmplitude != null ? (amplitude / maximumAmplitude - 1) * 100 : 0.0;
         
        if (maximumAmplitude == null || (percent < allowableDeviationPercent && amplitude > maximumAmplitude)) {
            maximumAmplitude = amplitude;
        }
    }
    
    
    
    @Override
    public Boolean apply(RecursivePhasor phasor) {
         boolean faultDetected = false;
         int n = phasor.getN();
         Complex spectrumSample = phasor.getSpectrumSample();
         
         /**
          * After buffer initialization ,snippet  calculates error for each time sample, located in a buffer.
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
             double error = calcuateError(spectrumSample, n, phasor.getBuffer().getLast());
             faultDetected = isEstimateFault(error);
         }
         return faultDetected;
    }
    
 
    
    public void setAllowableDeviationPercent(double allowableDeviationPercent) {
        this.allowableDeviationPercent = allowableDeviationPercent;
    }
     
    
}
