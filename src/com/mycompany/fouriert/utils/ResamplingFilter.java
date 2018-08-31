/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.utils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 *
 * @author andrey_pushkarniy
 */
public class ResamplingFilter implements Function<Double,Double>{
    private double f;
    private double f0;
    private int windowWidth;
    private double theta  ;
    private static double theta1  ;
    private double gamma;
    private double deltaGamma ;
    private Double prevSample;
    public ResamplingFilter(int windowWidth,double f,double f0) {
        this.windowWidth = windowWidth;
        this.f = f;
        this.f0 = f0;
        theta = 2*Math.PI/(double)windowWidth;
        gamma = theta;
        deltaGamma = Math.abs(2*Math.PI*f/((double)windowWidth*f0) - gamma);
    }
 

    @Override
    public Double apply(Double sample) {
     double resampled = 0.0;
     if(prevSample != null){
         gamma-=deltaGamma;
         resampled =  prevSample*(Math.sin(theta-gamma)/Math.sin(theta))+
                            sample*(Math.sin(gamma)/Math.sin(theta));
         prevSample = sample;
         return resampled;
         
     }
      prevSample = sample;
      return   sample*(Math.sin(gamma)/Math.sin(theta));         
    }
    
}