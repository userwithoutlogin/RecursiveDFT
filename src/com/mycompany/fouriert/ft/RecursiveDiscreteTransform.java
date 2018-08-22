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
 *
 * @author root
 */
public class RecursiveDiscreteTransform implements FourierTransform{
     TransientMonitor monitor ; 
     SampleGenerator sampleGenerator =new SampleGenerator();
     private LinkedList<Double> buffer;
     private int n = 0;
     private Integer windowWidth; 
     Complex spectSample = new Complex(0.0,0.0);
     double normingConstant ;
     
     
    public RecursiveDiscreteTransform(Integer width ) {                  
        this.windowWidth = width;
        
        buffer = new LinkedList(); 
        normingConstant = Math.sqrt(2)/windowWidth;
    }
         
 
    public void updatePhasorEstimate(double newTimeSample) {  
          if(windowWidth > buffer.size()){
              accumulateFirstSpectrumSample(newTimeSample);
          }
          else{
               double deletedSample = shiftWindow(newTimeSample);                 
               updateSpectrumSample(newTimeSample, deletedSample);                
          }
          n++; 
    }
    
    public double shiftWindow(double newTimeSample){
       double removedTimeSample = buffer.remove(0);
       buffer.add(newTimeSample);
       return removedTimeSample;          
    }  
    public void updateSpectrumSample(double timeSampleNew,double timeSampleOld){
           Complex newSpectrumSample = getExp().multiply( timeSampleNew-timeSampleOld).multiply(normingConstant); 
           spectSample =  spectSample.add(newSpectrumSample) ;
    }
    public Complex getExp( ){
        return Complex.initByEuler(1,-n*2.0*Math.PI/windowWidth );
    }
    public void accumulateFirstSpectrumSample(double newTimeSample){
           buffer.add(newTimeSample);
//     forms the first spectrum sample while buffer fills
             spectSample = spectSample.add(getExp().multiply(newTimeSample).multiply(normingConstant));              
    }
     
    public Complex getSample(){
          return windowWidth == buffer.size() ? spectSample : new Complex(0.0,0.0);        
    }
 
    @Override
    public Complex direct(Double timeSample) {
        Complex spectrumSample = new Complex(0.0,0.0);
        updatePhasorEstimate(timeSample);
        return spectrumSample.add( getSample());  
    }
   
    
    public double  calculatePhasorEstimateQality() throws UnsupportedOperationException{
      if(monitor!=null)  
          return buffer.size() == windowWidth ? monitor.calculatePhasorEstimateQality(spectSample, n,buffer ) : 0.0;
      else throw new UnsupportedOperationException();
    }  
    
    public void setMonitor(TransientMonitor monitor) {
        this.monitor = monitor;
        
    }

    public TransientMonitor getMonitor() {
        return monitor;
    }
    
}
