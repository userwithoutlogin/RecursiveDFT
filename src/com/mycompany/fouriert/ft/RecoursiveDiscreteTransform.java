/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.ft;

import com.mycompany.fouriert.complex.Complex;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
 
import java.util.LinkedList;

/**
 *
 * @author root
 */
public class RecoursiveDiscreteTransform implements FourierTransform{
      
     SampleGenerator sampleGenerator =new SampleGenerator();
     private LinkedList<Double> buffer;
     private  Complex previousSpectrumSample  ; 
     private Complex spectrumSampleParts   ;
     int n = 0;
     private Integer windowWidth; 
      Complex spectSample = new Complex(0.0,0.0);
      double normingConstant ;
     
    public RecoursiveDiscreteTransform(Integer width) {                  
        this.windowWidth = width;
        spectrumSampleParts = new Complex(0.0,0.0);
        buffer = new LinkedList(); 
        normingConstant = Math.sqrt(2)/windowWidth;
    }
         
      public void updatePhasorEstimate(double newTimeSample) {  
          if(windowWidth > buffer.size()){
               buffer.add(newTimeSample);
             spectrumSampleParts = spectrumSampleParts.add(getExp().multiply(newTimeSample).multiply(normingConstant)); 
          }
          else{
                double deletedSample = shiftWindow(newTimeSample);
                if(previousSpectrumSample == null)
                    spectSample =  spectSample.add(spectrumSampleParts) ;  
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
        previousSpectrumSample = getExp().multiply( timeSampleNew-timeSampleOld).multiply(normingConstant); 
              spectSample =  spectSample.add(previousSpectrumSample) ;
    }
    public Complex getExp( ){
        return Complex.initByEuler(1,- n*2*Math.PI/windowWidth );
    }
    
      
    public Complex getSample(){
          return spectSample;        
    }
    @Override
    public Complex direct(Double timeSample) {
        Complex spectrumSample = new Complex(0.0,0.0);
        updatePhasorEstimate(timeSample);
        return spectrumSample.add( getSample());  
    }
    
}
