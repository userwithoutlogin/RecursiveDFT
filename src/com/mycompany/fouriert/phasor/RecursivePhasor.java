/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.phasor;

import com.mycompany.fouriert.errorcorrection.DataForMonitor;
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
 * transformed 	by the DFT, to accumulated spectrum sample, and deleting old time sample from window.
 * @author root
 */
public class RecursivePhasor implements Phasor{
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
     public int n ;
     private LinkedList<Double> buffer;
     private Integer windowWidth; 
     private Complex spectrumSample = new Complex(0.0,0.0);
     private double  normingConstant ;
      
      
    public RecursivePhasor(Integer width ,TransientMonitor monitor) {                  
        this.windowWidth = width;
        buffer = new LinkedList(); 
        normingConstant = Math.sqrt(2)/windowWidth;
        this.monitor = monitor;
    }
        
    
    // window is shifted by adding new value and deleting old one
    private double shiftWindow(double newTimeSample){
       double removedTimeSample = buffer.remove(0);
       buffer.add(newTimeSample);
       return removedTimeSample;          
    }  
    
    /**
     * Function find DFT over difference between newTimeSample and oldTimeSample
     * and adds it to spectrumSample 
     * @param  oldTimeSample - first time sample of buffer before shift
     */
    private void updateSpectrumSample(double newTimeSample,double oldTimeSample){
           Complex newSpectrumSample = getExp().multiply(newTimeSample-oldTimeSample).multiply(normingConstant); 
           spectrumSample =  spectrumSample.add(newSpectrumSample) ;
    }
    
    /**
     * @return - Fourier's complex multiplyer 
     */
    private Complex getExp( ){
        return Complex.initByEuler(1,-n*2.0*Math.PI/windowWidth );
    }
    
    private void accumulateFirstSpectrumSample(double newTimeSample){
           buffer.add(newTimeSample);
//     forms the first spectrum sample while buffer fills
             spectrumSample = spectrumSample.add(getExp().multiply(newTimeSample).multiply(normingConstant));     
     }
    
    
    
    /**
     * Function performs estimation of time sample 
     * and validates spectrum sample, if buffer has already been filled
     */
    @Override
    public void accept(Object timeSample) {
        if(timeSample instanceof Double){
           updatePhasorEstimate((Double)timeSample);
           validateSample();  
        }
    }
    
     /**
     * Function accumulate the first spectrum sample while buffer fills 
     * and then, with every window shift, a difference between a new coming time sample 
     * and deleted sample is transformed by DFT and  added to spectrumSample. 
     */
    @Override
    public void updatePhasorEstimate(double newTimeSample) {  
          if(windowWidth > buffer.size())
               accumulateFirstSpectrumSample(newTimeSample);
          else{              
               double deletedSample = shiftWindow(newTimeSample);                 
               updateSpectrumSample(newTimeSample, deletedSample);  
                
          }             
          n++;
            
    }   
    
    /**
     * Function delegates verification to transient monitor.
     * After window initializing, validation is performed with using the whole buffer,
     * when window has started to move, validation is performed with using only last sample in window.
     */
    public void validateSample() throws UnsupportedOperationException{
           if(n == 24) 
                IntStream.range(0, buffer.size())
                         .mapToObj(i->{
                           DataForMonitor data =  new DataForMonitor(buffer.get(i), i, spectrumSample);
                           return data;
                       }).forEach(monitor);
           else if(n > 24) 
               monitor.validateSample( spectrumSample , n , buffer.getLast() );
           
    } 
   
    
    public boolean isFault() {
         return monitor.isFaultDetected();
    }
    public Complex getSpectrumSample() {
        return spectrumSample;
    }
    public int     getN() {
        return n;
    }
    
    
}
