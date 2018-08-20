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
 * 
 */
public class ResamplingFilter {

    /**
     * Function which performs recalculate time samples, changing distanse between them, 
     * thanks to it all of this samples will be place into a  period of off-nominal frequency
     * @param timeSamples - list with values of tested signal
     * @param windowWidth - phasors window width
     * @param f           - off-nominal frequency
     * @param f0          - nominal frequency
     * @return            - list containing resampled time samples
     */
    public static List<Double> resample(List<Double> timeSamples, int windowWidth, double f, double f0) {
        /**
         * theta      - this is the angle between two neighbour time samples  
         * gamma      - this is the angle between the current time sample and recalculated time sample
         * deltaGamma - value on which gamma  angle changes when  jump to a new time sample
         * xres       - resampled time sample
         */
        double theta = 2.0 * Math.PI / (double)windowWidth;
        double gamma = theta;
        double deltaGamma = Math.abs(2.0 * Math.PI * f / ((double)windowWidth * f0) - gamma);
        List<Double> resampled = new ArrayList(timeSamples.size());
       
        //adds first time sample without changes because gamma angle equals to theta angle
        resampled.add(timeSamples.get(0));
        gamma -= deltaGamma;

        for (int i = 0; i < timeSamples.size() - 1; i++) {
            double xres = timeSamples.get(i) * (Math.sin(theta - gamma) / Math.sin(theta))
                    + timeSamples.get(i + 1) * (Math.sin(gamma) / Math.sin(theta));
            resampled.add(xres);
            gamma -= deltaGamma;
        }

        return resampled;
    }

}
