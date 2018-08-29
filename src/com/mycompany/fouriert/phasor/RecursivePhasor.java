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
     * n                - number of current sample
     * buffer           - phasor's window
     * windowWidth      - phasor's window width
     * phasor           - accumulated phasor's estimation
     * normingConstant  - normalizing multiplier of DFT
     * has been detected 
     */
 
     public  int n;
     public  int k;
     private LinkedList<Double> buffer;
     private Integer windowWidth; 
     private Complex phasor = new Complex(0.0,0.0);
     private double  normingConstant ;
     private double [] cosArray;
     private double [] sinArray;

    public LinkedList<Double> getBuffer() {
        return buffer;
    }
      
    public RecursivePhasor(double[] cosArray,double[] sinArray ) {                  
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
           Complex newSpectrumSample = getExp().multiply(newSample-oldSample).multiply(normingConstant); 
           phasor =  phasor.add(newSpectrumSample) ;
    }
    
    /**
     * @return - Fourier's complex multiplyer 
     */
    private Complex getExp( ){
//        return Complex.initByEuler(1,-k*2.0*Math.PI/windowWidth );
        return new Complex(cosArray[k],-sinArray[k]);
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
    public void updatePhasorEstimate(double newSample) {  
          if(windowWidth > buffer.size())
               accumulateFirstPhasor(newSample);
          else{              
               double deletedSample = shiftWindow(newSample);                 
               updatePhasor(newSample, deletedSample);  
          }             
          updateN();
            n++;
    }   
     
    public Complex getPhasor() {
        return phasor;
    }
    public int  getN() {
        return n;
    }

    @Override
    public Complex apply(Double timeSample) {
        updatePhasorEstimate(timeSample);
        return windowWidth > buffer.size() ? null : phasor;

    }

    public Integer getWindowWidth() {
        return windowWidth;
    }
    private void updateN(){
        k++;
        if(k == cosArray.length )
            k=0;
    }
    
}
