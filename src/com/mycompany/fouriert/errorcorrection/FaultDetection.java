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
public class FaultDetection implements Function<Double,Boolean>{
    private RecursivePhasor   phasor;
    private Function<TransientMonitorSource, Double> monitor;
    private final TransientMonitorSource monitorSourse = new TransientMonitorSource();
    private double  allowableDeviationPercent = 115;
    private Double  maximumAmplitude;
    
    /**
     * @param  timeSample - time sample of analyzed signal
     * @return fault      - it is set to true , if error of estimation exceeds allowable limitation 
     */
    @Override
    public Boolean apply(Double timeSample) {
       
        Complex spectrumSample = phasor.apply(timeSample);
        monitorSourse.setSpectrumSample(spectrumSample);
        monitorSourse.setTimeSample(timeSample);
        int n = phasor.getN() ;
        int windowWidth =phasor.getWindowWidth(); 
        Boolean fault = false;
       
        /**
         * If the buffer has just been filled , snippet calculates error of estimation for all time samples,
         * located in the buffer.
         */
        if (n == windowWidth) {
             updateMaxAmplitude(spectrumSample.getAmplitude() * Math.sqrt(2.0));
             List<Double> buffer = phasor.getBuffer();             
             for (int i = 0; i < buffer.size(); i++) {
                 monitorSourse.setTimeSample(buffer.get(i));
                 
                 double error = monitor.apply(monitorSourse);
                 fault = isEstimateFault(error);
                 if (fault) 
                     break;                 
             }            
        }
        /**
         * When window has started to move, snippet calculates error of estimation for only last time sample,
         * located in the buffer. 
         */
        else if(n > windowWidth){
             updateMaxAmplitude(spectrumSample.getAmplitude() * Math.sqrt(2.0));
             monitorSourse.setTimeSample(timeSample);
             double error = monitor.apply(monitorSourse);
             fault = isEstimateFault(error);
             
        } 
              
        return fault;
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
        double percent = maximumAmplitude != null ? (amplitude / maximumAmplitude) * 100 : 0.0;
         
        if (maximumAmplitude == null || (percent < allowableDeviationPercent && amplitude > maximumAmplitude)) {
            maximumAmplitude = amplitude;
        }
    }
    
    
    
    
    /**
     * @return the phasor
     */
    public RecursivePhasor getPhasor() { //Dependecy injection
        return phasor;
    }

    /**
     * @param phasor the phasor to set
     */
    public void setPhasor(RecursivePhasor phasor) {
        this.phasor = phasor;
    }

    /**
     * @return the monitor
     */
    public Function<TransientMonitorSource, Double> getMonitor() {
        return monitor;
    }

    /**
     * @param monitor the monitor to set
     */
    public void setMonitor(Function<TransientMonitorSource, Double> monitor) {
        this.monitor = monitor;
    }

    
    
    
}
