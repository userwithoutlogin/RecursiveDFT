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
 *
 * @author andrey_pushkarniy
 */
public class TransientMonitor  implements Function<TransientMonitorSource,Double>{
    /**
     * windowWidth               - phasor`s window size 
     * n                         - number of current time sample
     */
    private int     windowWidth;
    private int     n;
 
    
    public TransientMonitor(int windowWidth) {
        this.windowWidth = windowWidth;
    }
   
        
    /**
     * Function calculate error, being difference between timeSample and recalculated time sample, 
     * obtained from phasor representation.
     * @param sample     - spectrum sample obtained from phasor
     * @param timeSample - time sample from buffer of phasor
     * @return error , being difference between timeSample and recalculated time sample 
     */ 
    
    private Double calcuateError(Complex sample,double timeSample){
        return Math.abs(
                timeSample - sample.getAmplitude() * Math.sqrt(2.0) * Math.cos((n++  * 2.0 * Math.PI / windowWidth) + sample.getArg())
               );
    }
    
    @Override
    public Double apply(TransientMonitorSource data) {
          return calcuateError(data.getSpectrumSample(), data.getTimeSample());
    }
     
     
    
}
