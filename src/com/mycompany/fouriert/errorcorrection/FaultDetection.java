/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.errorcorrection;

import com.mycompany.fouriert.phasor.RecursiveDFT;
import com.mycompany.fouriert.utils.Complex;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author andrey_pushkarniy
 */
public class FaultDetection implements Function<Double, Boolean> {

    private Function<Double, Complex> recursiveDFT;
    private Function<TransientMonitorSource, Double> monitor;
    private final TransientMonitorSource monitorSourse = new TransientMonitorSource();
    private double allowableDeviationPercent = 115;


    /**
     * @param  sample  - sample of analyzed signal
     * @return fault   - it is set to true , if error of estimation exceeds allowable limitation 
     */
    @Override
    public Boolean apply(Double sample) {
        Complex phasor = recursiveDFT.apply(sample);
        monitorSourse.setPhasor(phasor);
        monitorSourse.setSample(sample);
        monitorSourse.setSample(sample);
        Double error = monitor.apply(monitorSourse);
        if (error != null) {
             return isEstimateFault(error, phasor.getAmplitude() * Math.sqrt(2.0));
        }
        return null;
    }

     /**
     * Function compares error value with actual amplitude value, if error greater than  actualAmplitude by a percentage, 
     * which greater than allowableDeviationPercent, current   estimation of phasor is considered as faulted.
     * @param  error         - error , being difference between sample and recalculated sample 
     * @return faultDetected - variable points out that estimate of phasor is fault
     */
    private boolean isEstimateFault(double error, double actualAmplitude) {
        double percent = (error / actualAmplitude) * 100;
        return percent > allowableDeviationPercent;
    }

    public Function<Double, Complex> getRecursiveDFT() {
        return recursiveDFT;
    }

    public void setRecursiveDFT(Function<Double, Complex> recursiveDFT) {
        this.recursiveDFT = recursiveDFT;
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
