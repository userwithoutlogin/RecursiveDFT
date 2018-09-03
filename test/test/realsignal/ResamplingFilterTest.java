/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.realsignal;

import test.testsignal.*;
import com.mycompany.fouriert.phasor.RecursiveDFT;
import com.mycompany.fouriert.utils.Complex;
import com.mycompany.fouriert.utils.PhaseShiftsBetweenPhasors;
import com.mycompany.fouriert.utils.ResamplingFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import utils.CosineFunction;
import utils.Utils;

/**
 *
 * @author andrey_pushkarniy
 */
public class ResamplingFilterTest {
    /* 
     * WINDOW_WIDTH     - window width of phasor 
    */
    final int    WINDOW_WIDTH = 24;      
    final double NOMINAL_FREQUECY = 50.0;  
    Path PATH_TO_FILE = Paths.get("./realsine.txt").toAbsolutePath().normalize();
    
    public ResamplingFilterTest() {
    }
    
      
     @Test 
     public void phaseShiftOnOffNominalFrequency(){
         /*
            deviationFromPhaseShifts - deviation of values of phase shift  from 30 degrees  between 2 function
            frequencyDeviation       - frequency deviation off nominal  frequency
         */
          double frequencyDeviation = 1.8;
           
          double[]  sinArray  = new double[WINDOW_WIDTH];
          double[]  cosArray  = new double[WINDOW_WIDTH];
        
          for (int i = 0; i < cosArray.length; i++) {
             cosArray[i] = Math.cos(i * 2.0 * Math.PI / cosArray.length);
             sinArray[i] = Math.sin(i * 2.0 * Math.PI / cosArray.length);
          }
          
          Function<Double,Complex> recursiveDFT1 = new RecursiveDFT(cosArray, sinArray);
          Function<Double,Double>  resamplingFilter1 = new ResamplingFilter(WINDOW_WIDTH,NOMINAL_FREQUECY+frequencyDeviation,NOMINAL_FREQUECY);
          
          Function<Double,Complex> recursiveDFT2 = new RecursiveDFT(cosArray, sinArray);
          Function<Double,Double>  resamplingFilter2 = new ResamplingFilter(WINDOW_WIDTH,NOMINAL_FREQUECY+frequencyDeviation,NOMINAL_FREQUECY);
          
          List<Double> samples1 = Utils.getSamplesFromFile(PATH_TO_FILE, 1, WINDOW_WIDTH*2);
          List<Double> samples2 = Utils.getSamplesFromFile(PATH_TO_FILE, 2, WINDOW_WIDTH*2);
          
            samples1 = Utils.resample(samples1,resamplingFilter1);
            samples2 = Utils.resample(samples2,resamplingFilter2);
          
          
          List<Complex> phasors1   = Utils.getPhasors(samples1,recursiveDFT1).stream()
                                        .filter(phasor->phasor!=null)
                                        .collect(Collectors.toList());
          List<Complex> phasors2   = Utils.getPhasors(samples2,recursiveDFT2).stream()
                                        .filter(phasor->phasor!=null)
                                        .collect(Collectors.toList());
          
          List<Double> phaseShifts = PhaseShiftsBetweenPhasors.calc(phasors1 ,phasors2);  
          
          assertTrue("phase shift between two signals must be locate in range"
                  + " from 112 till 126  on off-nominal frequency 51.8Hz",
                  phaseShifts.stream().allMatch(shift->shift>=112&&shift<=127)
          );
     }
    
        
}
