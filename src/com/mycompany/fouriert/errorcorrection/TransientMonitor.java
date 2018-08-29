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
 * Calculates absolute value of error between actual time sample and recalculated time sample.
 * @author andrey_pushkarniy
 */
public class TransientMonitor  implements Function<TransientMonitorSource,Double>{
    /**
     * n                         - number of current time sample
     */
   
    private int     n;
    private double[] cosArray ;
    private double[] sinArray ;
    
    public TransientMonitor( ) {
        initSinCosArray();
        n = cosArray.length-1;
    }
   
        
    /**
     * Function calculate error, being difference between timeSample and recalculated time sample, 
     * obtained from phasor representation.
     * @param phasor     - estimate of phasor
     * @param sample     - sample from buffer of phasor
     * @return error , being difference between sample and recalculated sample 
     */ 
    
    private Double calcuateError(Complex phasor,double sample){
        double error = Math.abs(
                sample - phasor.getAmplitude() * Math.sqrt(2.0) * (cosArray[n] * Math.cos(phasor.getArg()) - sinArray[n] * Math.sin(phasor.getArg()))
        );
        updateN();
        return error;
    }
    
    @Override
    public Double apply(TransientMonitorSource data) {
           return calcuateError(data.gePhasor(), data.getSample());
    }
     
    public void initSinCosArray(){
        sinArray  = new double[24];
        cosArray = new double[24];
        for(int i=0;i<cosArray.length;i++){
            cosArray[i] =  Math.cos( i  * 2.0 * Math.PI / cosArray.length );  
            sinArray[i] =  Math.sin( i  * 2.0 * Math.PI / cosArray.length );  
        }
        
    } 
    private void updateN(){
        ++n;
        if(n == cosArray.length - 1 )
            n=0;
    }
}
