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
import signal.CosineFunction;

/**
 *
 * @author andrey_pushkarniy
 */
public class ResamplingFilterTest {
    /* 
     * WINDOW_WIDTH     - размер окна фазора 
     * NOMINAL_FREQUECY - номинальная частота
    */
    final int    WINDOW_WIDTH = 24;      
    final double NOMINAL_FREQUECY = 50.0;  
    public ResamplingFilterTest() {
    }
    
      
     @Test 
     public void phaseShiftOnOffNominalFrequency(){
         /*
            deviationFromPhaseShifts - deviation of values of phase shift  from 30 degrees  between 2 function
            frequencyDeviation       - frequency deviation off nominal  frequency
         */
          double frequencyDeviation = 1.8;
          double precision = 1e-13;   
          double amplitude1  = 100*Math.sqrt(2);
          double phase1  = Math.PI/3.0;
          double amplitude2  = 100*Math.sqrt(2);
          double phase2  = Math.PI/6.0;
          
          
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
         
          CosineFunction cosine1   = new CosineFunction(amplitude1 ,phase1 ,WINDOW_WIDTH,NOMINAL_FREQUECY);
          CosineFunction cosine2   = new CosineFunction(amplitude2 ,phase2 ,WINDOW_WIDTH,NOMINAL_FREQUECY);
          
          List<Double> samples1    = generateSamples(cosine1 , 48,frequencyDeviation);
          List<Double> recSamples1    = recalculateSamples(resamplingFilter1, samples1);
     
          List<Double> samples2    = generateSamples(cosine2 , 48,frequencyDeviation);
          List<Double> recSamples2    = recalculateSamples(resamplingFilter2, samples2);
       
          List<Complex> phasors1   = getPhasors(recursiveDFT1 ,recSamples1 );
          List<Complex> phasors2   = getPhasors(recursiveDFT2 ,recSamples2 );
           
          List<Double> phaseShifts = PhaseShiftsBetweenPhasors.calc(phasors1 ,phasors2);  
          List<Double> deviationPhaseShift = phaseShifts.stream()
                  .map(shift->Math.abs(shift-30.0))
                  .collect(Collectors.toList());
          assertTrue("phase shift between two signals must be deviate from 30 degree not greater than 0.08 degree"
                  + " on off-nominal frequency 51.8Hz",
                  deviationPhaseShift.stream().allMatch(shift->shift<0.08)
          );
     }
    
      public List<Complex> getPhasors(Function<Double,Complex> recursiveDFT,List<Double> samples){
         return samples.stream()
                 .map(recursiveDFT)
                 .filter(phasor->phasor!=null)
                 .collect(Collectors.toList());
     }
      public List<Double> generateSamples(CosineFunction  cosine,int pointsCount,double df){
         List<Double> list= new ArrayList();
         IntStream.range(0, pointsCount).forEach(i->{
             list.add(cosine.calc(df));
         });
         return list;
     }
      public List<Double> recalculateSamples(Function<Double,Double> resamplingFilter,List<Double> originalSamples){
          return originalSamples.stream()
                  .map(resamplingFilter)
                  .collect(Collectors.toList());
      }
}
