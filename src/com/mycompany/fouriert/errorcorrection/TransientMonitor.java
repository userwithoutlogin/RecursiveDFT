/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.errorcorrection;

 
import com.mycompany.fouriert.phasor.RecursiveDFT;
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
    
    private int arrayIndex;
    private double[] cosArray;
    private double[] sinArray;

    public TransientMonitor(double[] cosArray, double[] sinArray) {
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
    private Double calcuateError(Complex phasor, double sample) {
        if (phasor != null) {
//            System.out.println((sample+"").replace(".", ","));
            double r =  Math.sqrt(2.0) * (cosArray[arrayIndex] * phasor.getRe() - sinArray[arrayIndex] * phasor.getIm());
            System.out.println((r+"").replace(".", ","));
            return Math.abs(sample - Math.sqrt(2.0) * (cosArray[arrayIndex] * phasor.getRe() - sinArray[arrayIndex] * phasor.getIm()));
        }
        return null;
    }

    @Override
    public Double apply(TransientMonitorSource data) {
        Double error = calcuateError(data.getPhasor(), data.getSample());
        ++arrayIndex;
        if (arrayIndex >= cosArray.length) {
            arrayIndex = 0;
        }
        return error;
    }
}
