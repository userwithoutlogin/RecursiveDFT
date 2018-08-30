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
public class RecursiveDFT implements Function<Double,Complex>{
    /**
     * arrayIndex       - index in the cosArray (sinArray)
     * buffer           - phasor's window
     * windowWidth      - phasor's window width
     * phasor           - accumulated phasor's estimation
     * normingConstant  - normalizing multiplier of DFT
     * has been detected 
     * cosArray(sinArray) - sines(cosines) values which are calculated for 24 points in advance. 
     * Because sine(cosine) function is periodic.
     */
 
     private  int arrayIndex;      
     private LinkedList<Double> buffer;
     private int windowWidth; 
     private Complex phasor = new Complex(0.0,0.0);
     private double  normingConstant ;
     private double [] cosArray;
     private double [] sinArray;

    public LinkedList<Double> getBuffer() {
        return buffer;
    }
      
    public RecursiveDFT(double[] cosArray,double[] sinArray ) {                  
        this.windowWidth = cosArray.length;
        buffer = new LinkedList(); 
        normingConstant = Math.sqrt(2)/windowWidth;
        this.cosArray = cosArray;
        this.sinArray = sinArray;
        
    }
        
    
    // window is shifted by adding new value and deleting old one
    private double shiftWindow(double newSample){
       double removedSample = buffer.remove(0);
       buffer.add(newSample);
       return removedSample;          
    }  
    
    /**
     * Function find DFT over difference between newSample and oldSample
     * and adds it to phasor 
     * @param  oldSample - first sample of buffer before shift
     */
    private void updatePhasor(double newSample,double oldSample){
           Complex newPhasor = getExp().multiply(newSample-oldSample).multiply(normingConstant); 
           phasor =  phasor.add(newPhasor) ;
    }
    
    /**
     * @return - Fourier's complex multiplyer 
     */
    private Complex getExp( ){
         return new Complex(cosArray[arrayIndex],-sinArray[arrayIndex]);
    }
    
    private void accumulateFirstPhasor(double newSample){
           buffer.add(newSample);
//     forms the first phasor estimate while buffer fills
             phasor = phasor.add(getExp().multiply(newSample).multiply(normingConstant));     
     }
 
     /**
     * Function accumulate the first phasor estimate while buffer fills 
     * and then, with every window shift, a difference between a new coming sample 
     * and deleted sample is transformed by DFT and  added to phasor. 
     */    
    public void performDirectTransform(double newSample) {  
          if(windowWidth > buffer.size())
               accumulateFirstPhasor(newSample);
          else{              
               double deletedSample = shiftWindow(newSample);                 
               updatePhasor(newSample, deletedSample);  
          }             
          updateN();
      }   
     
    public Complex getPhasor() {
        return phasor;
    }
    

    @Override
    public Complex apply(Double timeSample) {
        performDirectTransform(timeSample);
        return windowWidth > buffer.size() ? null : phasor;

    }

    
    private void updateN(){
        arrayIndex++;
        if(arrayIndex == cosArray.length )
            arrayIndex=0;
    }
    
}
