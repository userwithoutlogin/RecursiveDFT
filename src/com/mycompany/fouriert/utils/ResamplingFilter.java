/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author root
 */
public class ResamplingFilter {

    public static List<Double> resample(List<Double> timeSamples,int windowWidth,double f,double f0) {
        double theta = 2*Math.PI/windowWidth;
        double gamma = theta;
        double deltaGamma =Math.abs(2*Math.PI*f/(windowWidth*f0) - gamma);
        List<Double> resampled = new ArrayList();
        
        for(int i=0;i<timeSamples.size()-1;i++){
            if(i==0){
                resampled.add(timeSamples.get(i));
                gamma-=deltaGamma;
            }
                 double xres = timeSamples.get(i)*(Math.sin(theta-gamma)/Math.sin(theta))+
                            timeSamples.get(i+1)*(Math.sin(gamma)/Math.sin(theta));
                 resampled.add(xres);
                 gamma -=deltaGamma;
            
            
        }
            
       return resampled;
    }
    
}
