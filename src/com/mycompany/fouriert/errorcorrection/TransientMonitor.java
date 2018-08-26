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
    /**
     * windowWidth               - phasor`s window size 
     * allowableDeviationPercent - percent till which sample does not erroneous
     * maximumAmplitude          - maximum amplitude of signal
     * faultDetected             - value is set to true, when error of phasor representation 
     * has  exceeded maximum amplitude by allowable percentage 
     */
    private int     windowWidth;
    private double  allowableDeviationPercent = 15;
    private Double  maximumAmplitude;
    private boolean faultDetected = false;
    private Integer numberOfFaultSample = null;

    public TransientMonitor(int windowWidth) {
        this.windowWidth = windowWidth;
    }
    
    
    public void analizeHarmonic(Complex sample, int n, double timeSample ){
         
         
              double  error = Math.abs(
                        timeSample  - sample.amplitude() * Math.sqrt(2.0) * Math.cos((n * 2.0 * Math.PI / windowWidth) + sample.arg())
                );
/**
 *      If error greater than maximum amplitude by a percentage, which greater than desirable,
 *      current phasor estimate is considered as faulted.
 */
                if (maximumAmplitude < error) {
                    double percent = (error / maximumAmplitude - 1) * 100;
                    faultDetected = percent > allowableDeviationPercent;
                    numberOfFaultSample = faultDetected?n+1:null;
                }
            
    }
    
    /**
     * Function checks, that phasor's estimate is valid , comparing error with maximum amplitude. 
     * @param sample          - current phasor's estimate 
     * @param n               - number of current time sample
     * @param timeSamples     - time sample which has been deleted from buffer
     * @return faultDetected  - value is set to true, when error of phasor representation 
     */
    public boolean isEstimateErroneous(Complex sample, int n, List<Double> timeSamples) {
        /**
         * error     - deviation recalculated time sample from phasor's estimate, 
         * from obtained time sample
         * percent   - how much error value exceeds the maximum amplitude
         */        
        
        if (!faultDetected ) {
//          Converts to number of time sample which has been deleted from buffer.
             int k = n - windowWidth;
              
            updateMaxAmplitude(sample.amplitude()*Math.sqrt(2.0));
            
            if(k==0)
            for (int i = 0; i < timeSamples.size(); i++, k++) 
//                error = Math.abs(
//                        timeSample.get(i) - sample.amplitude() * Math.sqrt(2.0) * Math.cos((k * 2.0 * Math.PI / windowWidth) + sample.arg())
//                );
//                if (maximumAmplitude < error) {
//                    double percent = (error / maximumAmplitude - 1) * 100;
//                    faultDetected = percent > allowableDeviationPercent;
//                }
                analizeHarmonic(sample, k, timeSamples.get(i));
            else{
                 
                analizeHarmonic(sample, n-1, timeSamples.get(timeSamples.size()-1));
            }
        }
        return faultDetected;
    }
    /**
     * Obtains a current phasor's estimate and returns one, if 
     * error has not been detected, otherwise returns zero complex number.
     * @param  sample          - current phasor's estimate 
     * @param  n               - umber of current time sample
     * @param  timeSamples      - time sample which has been deleted from buffer
     * @return tempSample      - equals to current phasor's estimate ,
     * if error has not been detected, else equals to zero complex number
     */
    public Complex validateSample(Complex sample, int n, List<Double> timeSamples) {
        Complex tempSample = sample; 
        System.out.println("--------------------------- n= "+n);
        if (isEstimateErroneous(tempSample, n, timeSamples)) {
            tempSample = new Complex(0.0, 0.0);
        }
         
        return tempSample;
        
   }
    
    /**
     *  Function updates maximumAmplitude, if  'amplitude' is greater than maximumAmplitude, 
     *  but less or equals than maximumAmplitude with accounting 'allowableDeviationPercent'.
     *  @param amplitude - amplitude is obtained from current estimate of phasor
     */
    public void updateMaxAmplitude(double amplitude) {
        double allowableDeviation = 10;
     // percent - percentage by which 'amplitude' is greater than maximumAmplitude
        double percent = maximumAmplitude != null ? (amplitude / maximumAmplitude - 1) * 100 : 0.0;
         
        if (maximumAmplitude == null || (percent < allowableDeviation && amplitude > maximumAmplitude)) {
            maximumAmplitude = amplitude;
        }
    }
    public void setAllowableDeviationPercent(double allowableDeviationPercent) {
        this.allowableDeviationPercent = allowableDeviationPercent;
    }
    public boolean isFaultDetected() {
        return faultDetected;
    }

    public Integer getNumberOfFaultSample() {
        return numberOfFaultSample;
    }
     
}
