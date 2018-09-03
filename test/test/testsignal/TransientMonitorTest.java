/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.testsignal;

import com.mycompany.fouriert.errorcorrection.FaultDetection;
import com.mycompany.fouriert.errorcorrection.TransientMonitor;
import com.mycompany.fouriert.errorcorrection.TransientMonitorSource;
import com.mycompany.fouriert.phasor.RecursiveDFT;
import com.mycompany.fouriert.utils.Complex;
import java.nio.file.Path;
import java.nio.file.Paths;
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
public class TransientMonitorTest {
      /* 
         WINDOW_WIDTH     - window width of phasor 
         NOMINAL_FREQUECY - nominal frequency of signal
      */
      final int    WINDOW_WIDTH = 24;      
      final double NOMINAL_FREQUECY = 50.0;  
    
    
    public TransientMonitorTest() {
    }
     @Test
      public void findNumberOfErroneousSample(){        
        /**
          * precision          - two double values considers equals, if their difference less than precision 
          * frequencyDeviation - frequency deviation from nominal frequency
          * amplitude          - amplitude of tested signal
          * phase              - phase shift of tested signal
          * cosine             - creates samples of tsted signal 
          * limitPointNumbers  - count points of sine  
          * monitor            - it detects, when sine begins breaking
          * recursiveDFT    - it performa discrete Fourier transform(DFT) with recursive update of estimation
          * cosArray(sinArray) - sines(cosines) values which are calculated for 24 points in advance. 
          * Because sine(cosine) function is periodic.
        */
        double precision = 1e-13;     
        double amplitude = 100.0;
        double phase = Math.PI/4;
        double incorrectSample  = 215.0 ;
        double expectedSample   = 70.71067811865477 ;
        
         int limitPointNubers = 24;
         double frequencyDeviation = 0.0;
         double[] sinArray = new double[WINDOW_WIDTH];
         double[] cosArray = new double[WINDOW_WIDTH];

         for (int i = 0; i < cosArray.length; i++) {
             cosArray[i] = Math.cos(i * 2.0 * Math.PI / cosArray.length);
             sinArray[i] = Math.sin(i * 2.0 * Math.PI / cosArray.length);
         }

         CosineFunction cosine = new CosineFunction(amplitude, phase, WINDOW_WIDTH, NOMINAL_FREQUECY);

         TransientMonitor monitor = new TransientMonitor(cosArray, sinArray);
         RecursiveDFT recursiveDFT = new RecursiveDFT(cosArray, sinArray);

         
         List<Double> samples = Utils.generateSamples(cosine, limitPointNubers, frequencyDeviation);
         List<Complex> phasors = samples.stream()
                 .map(recursiveDFT)
                 .collect(Collectors.toList());

         List<Double> errors = new ArrayList();

         for (int i = 0; i < phasors.size(); i++) {
             TransientMonitorSource source = new TransientMonitorSource();
             source.setPhasor(phasors.get(i));
             source.setSample(samples.get(i));
             errors.add(monitor.apply(source));
         }
         
         //Finds error for incorrect sample
         TransientMonitorSource source = new TransientMonitorSource();
         source.setPhasor(phasors.get(phasors.size() - 1));
         source.setSample(215.0);
         errors.add(monitor.apply(source));
         assertTrue("Error between incorrectSample and expectedSample equals to 144.3" ,
                 Utils.compareFPNumbers(errors.get(errors.size() - 1), incorrectSample - expectedSample, precision));
    
     }
 
}
