/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.phasor;

 
import com.mycompany.fouriert.utils.Complex;
 
 
import java.util.LinkedList;
 
import java.util.function.Function;

/**
 * Phasor with recursive update estimation, extending discrete Fourier transform(DFT).
 * @author root
 */
public class RecursivePhasor implements Function<Double,Complex>{
    /**
     * n                - number of current time sample
     * buffer           - phasor's window
     * windowWidth      - phasor's window width
     * spectrumSample   - accumulated phasor's estimation
     * normingConstant  - normalizing multiplier of DFT
     * has been detected 
     */
 
     public  int n;
     private LinkedList<Double> buffer;
     private Integer windowWidth; 
     private Complex spectrumSample = new Complex(0.0,0.0);
     private double  normingConstant ;
 

    public LinkedList<Double> getBuffer() {
        return buffer;
    }
      
    public RecursivePhasor(int width ) {                  
        this.windowWidth = width;
        buffer = new LinkedList(); 
        normingConstant = Math.sqrt(2)/windowWidth;
 
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
     
    public Complex getSpectrumSample() {
        return spectrumSample;
    }
    public int  getN() {
        return n;
    }

    @Override
    public Complex apply(Double timeSample) {
        updatePhasorEstimate(timeSample);
        return windowWidth > buffer.size() ? new Complex(0.0, 0.0) : spectrumSample;

    }

    public Integer getWindowWidth() {
        return windowWidth;
    }
    
    
}
