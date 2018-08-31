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
import signal.CosineFunction;

/**
 *
 * @author andrey_pushkarniy
 */
public class TransientMonitorTest {
      /* 
         WINDOW_WIDTH     - размер окна фазора 
         NOMINAL_FREQUECY - номинальная частота
      */
      final int    WINDOW_WIDTH = 24;      
      final double NOMINAL_FREQUECY = 50.0;  
    
    
    public TransientMonitorTest() {
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
        double precision = 1e-13;     
        double amplitude = 100.0;
        double phase = Math.PI/4;
        double incorrectSample = 215.0 ;
        double expectedSample   = 70.71067811865477 ;
        
        int limitPointNubers = 24;
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
        
        // FaultDetection faultDetection = new FaultDetection();
        // faultDetection.setMonitor(monitor);
        // faultDetection.setRecursiveDFT(recursiveDFT);
         /**
          * Generates correct samples
          */
         List<Double> samples =  generateSamples(cosine, limitPointNubers,frequencyDeviation);
         List<Complex> phasors = samples.stream()
                 .map(recursiveDFT)
                 .collect(Collectors.toList());
         /**
          * Adds incorrect 49th sample 
          */
         //86.60254037844383
         //samples.add(incorrectSample);
         List<Double> errors = new ArrayList();
        // Integer number = findNumberOFError(faultDetection, samples)+1;
         for(int i=0;i<phasors.size();i++){
            // Complex phasor = recursiveDFT.apply(samples.get(i));
             TransientMonitorSource source = new TransientMonitorSource();
             source.setPhasor(phasors.get(i));
             source.setSample(samples.get(i));
             errors.add(monitor.apply(source));
         }
         TransientMonitorSource source = new TransientMonitorSource();
             source.setPhasor(phasors.get(phasors.size()-1));
             source.setSample(215.0);
        errors.add( monitor.apply(source));
         assertTrue("48th sample is erroneous" ,
                 compareFPNumbers(errors.get(errors.size() - 1), incorrectSample - expectedSample, precision));
    
     }
      public List<Double> generateSamples(CosineFunction  cosine,int pointsCount,double df){
         List<Double> list= new ArrayList();
         IntStream.range(0, pointsCount).forEach(i->{
             list.add(cosine.calc(df));
         });
         return list;
     }
     public boolean compareFPNumbers(double n1,double n2,double precision){
      //Функция сравнения чисел с плавающей точкой   
       return Math.abs(n1-n2)<precision;
     }   
}
