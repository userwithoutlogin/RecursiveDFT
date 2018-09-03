/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.testsignal;

import com.mycompany.fouriert.errorcorrection.FaultDetection;
import com.mycompany.fouriert.errorcorrection.TransientMonitor;
import com.mycompany.fouriert.phasor.RecursiveDFT;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
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
public class FaultDetectionTest {
     /* 
         WINDOW_WIDTH     - размер окна фазора 
         NOMINAL_FREQUECY - номинальная частота
      */
      final int    WINDOW_WIDTH = 24;      
      final double NOMINAL_FREQUECY = 50.0;  
    
    
    public FaultDetectionTest() {
    }
    
     @Test
      public void findNumberOfErroneousSample(){        
        /**
          * frequencyDeviation - frequency deviation from nominal frequency
          * amplitude          - amplitude of tested signal
          * phase              - phase shift of tested signal
          * cosine             - creates samples of tsted signal 
          * limitPointNumbers  - count points of sine  
          * monitor            - it detects, when sine begins breaking
          * recursivePhasor    - it performa discrete Fourier transform(DFT) with recursive update of estimation
          * cosArray(sinArray) - sines(cosines) values which are calculated for 24 points in advance. 
          * Because sine(cosine) function is periodic.
        */
         
        double amplitude = 100.0;
        double phase = Math.PI/4;
        double incorrectSample = 215.0 ;
        int limitPointNubers = 48;
        double frequencyDeviation = 0.0;
        double[]  sinArray  = new double[WINDOW_WIDTH];
        double[]  cosArray  = new double[WINDOW_WIDTH];
        
         for (int i = 0; i < cosArray.length; i++) {
             cosArray[i] = Math.cos(i * 2.0 * Math.PI / cosArray.length);
             sinArray[i] = Math.sin(i * 2.0 * Math.PI / cosArray.length);
         }
        
         CosineFunction cosine = new CosineFunction(amplitude,phase  ,WINDOW_WIDTH ,NOMINAL_FREQUECY);        
 
         TransientMonitor monitor = new TransientMonitor( cosArray,sinArray);
         RecursiveDFT  recursiveDFT =  new RecursiveDFT( cosArray,sinArray);
        
         FaultDetection faultDetection = new FaultDetection();
         faultDetection.setMonitor(monitor);
         faultDetection.setRecursiveDFT(recursiveDFT);
         /**
          * Generates correct samples
          */
         List<Double> samples =  Utils.generateSamples(cosine,limitPointNubers, frequencyDeviation);
         /**
          * Adds incorrect 49th sample 
          */
         samples.add(incorrectSample);
         Integer number = findNumberOFError(faultDetection, samples)+1;
         
         assertTrue("49th sample is erroneous",number == 49  );
    
     }
      
      public Integer findNumberOFError(Function<Double,Boolean> faultDetector,List<Double> samples){
         Integer errorNumber = null;
         for(int i=0;i<samples.size();i++ ){  
             Boolean fault = faultDetector.apply(samples.get(i));
             if(fault!=null&&fault)
                 errorNumber = i;             
         }
         return errorNumber;
     } 
}
