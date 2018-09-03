/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.testsignal;

import com.mycompany.fouriert.phasor.RecursiveDFT;
import com.mycompany.fouriert.utils.Complex;
import com.mycompany.fouriert.utils.PhaseShiftsBetweenPhasors;
import com.mycompany.fouriert.utils.ResamplingFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
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
     * NOMINAL_FREQUECY - nominal frequency of signal
    */
    final int    WINDOW_WIDTH = 24;      
    final double NOMINAL_FREQUECY = 50.0;  
    public ResamplingFilterTest() {
    }
    
      
     @Test 
     public void resamplingTest(){
           /**
          * precision          - two double values considers equals, if their difference less than precision 
          * frequencyDeviation - frequency deviation from nominal frequency
          * amplitude          - amplitude of tested signal
          * phase              - phase shift of tested signal
          * cosine             - creates samples of tsted signal 
          * monitor            - it detects, when sine begins breaking
          * recursiveDFT       - it performa discrete Fourier transform(DFT) with recursive update of estimation
          * cosArray(sinArray) - sines(cosines) values which are calculated for 24 points of window in advance. 
          * Because sine(cosine) function is periodic.
          * resamplingFilter   - performs recalculate time samples, changing distanse between them.
          * recSamples         - samples after resampling filter
        */
         
          double frequencyDeviation = 0.5;
          double precision = 1e-10;   
          double amplitude = 100*Math.sqrt(2);
          double phase  = Math.PI/4.0;
           
          
          
          double[]  sinArray  = new double[WINDOW_WIDTH];
          double[]  cosArray  = new double[WINDOW_WIDTH];
        
          for (int i = 0; i < cosArray.length; i++) {
             cosArray[i] = Math.cos(i * 2.0 * Math.PI / cosArray.length);
             sinArray[i] = Math.sin(i * 2.0 * Math.PI / cosArray.length);
          }
          
          Function<Double,Complex> recursiveDFT = new RecursiveDFT(cosArray, sinArray);
          Function<Double,Double>  resamplingFilter = new ResamplingFilter(WINDOW_WIDTH,NOMINAL_FREQUECY+frequencyDeviation,NOMINAL_FREQUECY);
          
          CosineFunction cosine   = new CosineFunction(amplitude ,phase ,WINDOW_WIDTH,NOMINAL_FREQUECY);
          
          List<Double> samples    = Utils.generateSamples(cosine , 25,frequencyDeviation);
          List<Double> recSamples    = Utils.resample( samples,resamplingFilter);
     
          
          assertTrue("The first recalculated sample must be equals to this first original sample", 
                  Utils.compareFPNumbers(samples.get(0), recSamples.get(0), precision)
          );
     }
    
}
