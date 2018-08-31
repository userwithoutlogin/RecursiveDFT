/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author andrey_pushkarniy
 */
public class PhaseShiftsBetweenPhasors {

    public static List<Double> calc(List<Complex> spectrumSamples1, List<Complex> spectrumSamples2) {
         List<Double> phaseShifts = new ArrayList();
          List<Double> phasesCosine1 = spectrumSamples1 
                  .stream()
                  .map(sample->sample.getArg() )
                  .collect(Collectors.toList());
          List<Double> phasesCosine2 = spectrumSamples2 
                  .stream()
                  .map(sample->sample.getArg() )
                  .collect(Collectors.toList());
           
          for(int i=0;i<phasesCosine1.size();i++)
              phaseShifts.add(Math.toDegrees(phasesCosine1.get(i)-phasesCosine2.get(i)) );
          return phaseShifts;
    }
    
}
