/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.ft;

import com.mycompany.fouriert.utils.Complex;
import com.mycompany.fouriert.errorcorrection.TransientMonitor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
 
import java.util.LinkedList;

/**
 * Phasor with recursive update estimation, extending discrete Fourier transform(DFT).
 * Phasor's estimate is obtained by motioning window with adding new time sample ,
 * transformed 	by the DFT, to accumulated spectrum sample, and deleting old time sample from window
 * @author root
 */
public class RecursiveDiscreteTransform implements FourierTransform{
    /**
     * monitor          - finds erroneous phasor's estimations
     * n                - number of current time sample
     * buffer           - phasor's window
     * windowWidth      - phasor's window width
     * spectrumSample   - accumulated phasor's estimation
     * normingConstant  - normalizing multiplier of DFT
     * fault            - it's set to true if fault in phasor's estimate 
     * has been detected 
     */
     TransientMonitor monitor ;
     private int n ;
     private LinkedList<Double> buffer;
     private Integer windowWidth; 
     private Complex spectrumSample = new Complex(0.0,0.0);
     private double  normingConstant ;
     private boolean fault = false;
      
    public RecursiveDiscreteTransform(Integer width ) {                  
        this.windowWidth = width;
        buffer = new LinkedList(); 
        normingConstant = Math.sqrt(2)/windowWidth;
        monitor = new TransientMonitor(windowWidth);
    }
        
    /**
     * Function accumulate the first spectrum sample while buffer fills 
     * and then, with every window shift, a difference between a new coming time sample 
     * and deleted sample is transformed by DFT and  added to spectrumSample. 
     */
    public void updatePhasorEstimate(double newTimeSample) {  
          if(windowWidth > buffer.size())
               accumulateFirstSpectrumSample(newTimeSample);
          else{              
               double deletedSample = shiftWindow(newTimeSample);                 
               updateSpectrumSample(newTimeSample, deletedSample);                
          }
          n++; 
    }
    // window is shifted by adding new value and deleting old one
    public double shiftWindow(double newTimeSample){
       double removedTimeSample = buffer.remove(0);
       buffer.add(newTimeSample);
       return removedTimeSample;          
    }  
    /**
     * Function find DFT over difference between newTimeSample and oldTimeSample
     * and adds it to spectrumSample 
     * @param  oldTimeSample - first time sample of buffer before shift
     */
    public void updateSpectrumSample(double newTimeSample,double oldTimeSample){
           Complex newSpectrumSample = getExp().multiply(newTimeSample-oldTimeSample).multiply(normingConstant); 
           spectrumSample =  spectrumSample.add(newSpectrumSample) ;
    }
    /**
     * @return - Fourier's complex multiplyer 
     */
    public Complex getExp( ){
        return Complex.initByEuler(1,-n*2.0*Math.PI/windowWidth );
    }
    public void accumulateFirstSpectrumSample(double newTimeSample){
           buffer.add(newTimeSample);
//     forms the first spectrum sample while buffer fills
             spectrumSample = spectrumSample.add(getExp().multiply(newTimeSample).multiply(normingConstant));     
     }
    
    /**
     * While buffer is not filled returns zero complex number.
     * Otherwise performs a spectrum sample check  with a monitor.
     */
    private Complex getSample(){
        /*
         * fault - obtain from monitor.It is set to true, if fault is detected.
        */     
         if(windowWidth != buffer.size() )
             return new Complex(0.0,0.0);
         else {
             fault = monitor.isFaultDetected();
             return monitor.validateSample(spectrumSample,n,buffer);
         }
    }
    /**
      Function update phasor's estimate and returns it 
    */
    @Override
    public Complex direct(Double timeSample) {
       updatePhasorEstimate(timeSample);
       return  getSample();  
    }
    
    public void setMonitor(TransientMonitor monitor) {
        this.monitor = monitor;
        
    }
    public TransientMonitor getMonitor() {
        return monitor;
    }
      
    public boolean isFault() {
        return fault;
    }
    public Integer getNumberOfFaultSample(){
        return monitor.getNumberOfFaultSample();
    }
    
    
}
