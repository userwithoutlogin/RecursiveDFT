/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.errorcorrection;

 
import com.mycompany.fouriert.utils.Complex;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 *
 * @author andrey_pushkarniy
 */
public class TransientMonitor implements Consumer{
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
    
    
    
     /**
     * Function checks, that phasor's estimate is valid , comparing error with maximumAmplitude, 
     * if the fault has not been detected yet. 
     * At the first time a whole buffer is analyzed
     * @param sample          - current phasor's estimate 
     * @param n               - number of current time sample
     * @param timeSamples     - time samples from phasor window
     */
    public void validateSample(Complex sample, int n, List<Double> timeSamples) {
        if (!faultDetected ) {
//          number of the first sample in the window
             int k = n - windowWidth;
             updateMaxAmplitude(sample.amplitude()*Math.sqrt(2.0));
            for (int i = 0; i < timeSamples.size(); i++, k++) 
                   analyzeTimeSample(sample, k, timeSamples.get(i));
        }
        
   }
    /**
     * Overloaded version of validateSample function, it is used when window has started to move. 
     * In this case it is necessary  to analyze only last time sample.
     * @param sample          - current phasor's estimate 
     * @param n               - number of current time sample
     * @param timeSample      - last time sample from phasor window
     */
    public void validateSample(Complex sample, int n, double timeSample) {
        if (!faultDetected ) {
//          number of the first sample in the window
             int k = n - windowWidth;
             updateMaxAmplitude(sample.amplitude()*Math.sqrt(2.0));
            analyzeTimeSample(sample, n, timeSample);
        }
        
   }
 
      /**
     * Function sets value for faultDetected. 
     * @param sample          - current phasor's estimate 
     * @param n               - number of current time sample
     * @param timeSample      - time sample from buffer     
     */
    private void    analyzeTimeSample(Complex sample, int n, double timeSample ){
         /**
         * error     - fromdeviation recalculated time sample, 
         * obtained with help  phasor's estimate, off obtained time sample
         * percent   - how much error value exceeds the maximum amplitude
         */ 
         double t= sample.amplitude() * Math.sqrt(2.0) * Math.cos((n  * 2.0 * Math.PI / windowWidth) + sample.arg());
         double error = Math.abs(
                timeSample - sample.amplitude() * Math.sqrt(2.0) * Math.cos((n  * 2.0 * Math.PI / windowWidth) + sample.arg())
         );
        
/**
 *      If error greater than  maximumAmplitude by a percentage, which greater than allowableDeviationPercent,
 *      current phasor estimate is considered as faulted.
 */
        if (maximumAmplitude < error) {
            double percent = (error / maximumAmplitude - 1) * 100;
            faultDetected = percent > allowableDeviationPercent;
            numberOfFaultSample = faultDetected ? n : null;
        }
  
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
    public void setAllowableDeviationPercent(double allowableDeviationPercent) {
        this.allowableDeviationPercent = allowableDeviationPercent;
    }
    public boolean isFaultDetected() {
        return faultDetected;
    }
    public Integer getNumberOfFaultSample() {
        return numberOfFaultSample;
    }

    @Override
    public void accept(Object t) {
        if(t instanceof DataForMonitor){
            DataForMonitor dataForMonitor = (DataForMonitor)t;
            validateSample(dataForMonitor.getSpectrumSample(), dataForMonitor.getN(), dataForMonitor.getTimeSample());
        }
        else
            throw new UnsupportedOperationException("Object passed to accept must be instance of DataForMonitor"); //To change body of generated methods, choose Tools | Templates.
    }
     
}
