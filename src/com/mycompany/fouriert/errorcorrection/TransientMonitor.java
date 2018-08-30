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
     * arrayIndex         - index in the cosArray (sinArray)
     * cosArray(sinArray) - sines(cosines) values which are calculated for 24 points in advance. 
     * Because sine(cosine) function is periodic.
     */
    
    private int      arrayIndex;
    private double[] cosArray ;
    private double[] sinArray ;
    
    public TransientMonitor(double[] cosArray,double[] sinArray ) {
        //initSinCosArrays();
        /**
         * In the first time, calculation of error occurs  
         * using last sample in the first window(n=23 starting from 0) and the first obtained phasor
         */
        arrayIndex = cosArray.length-1;
        this.cosArray = cosArray;
        this.sinArray = sinArray;
    }
   
        
    /**
     * Function calculate error, being difference between sample and recalculated sample, 
     * obtained from phasor representation.
     * @param phasor     - estimation of phasor
     * @param sample     - sample from buffer of phasor
     * @return error , being difference between sample and recalculated sample 
     */ 
    
    private Double calcuateError(Complex phasor,double sample){
        double error = Math.abs(sample - Math.sqrt(2.0)* ( cosArray[arrayIndex] * phasor.getRe() - sinArray[arrayIndex] * phasor.getIm())
        );
        updateN();
        return error;
    }
    
    @Override
    public Double apply(TransientMonitorSource data) {
           return calcuateError(data.gePhasor(), data.getSample());
    }
     
    
    private void updateN(){
        ++arrayIndex;
        if(arrayIndex == cosArray.length )
            arrayIndex=0;
    }
}
