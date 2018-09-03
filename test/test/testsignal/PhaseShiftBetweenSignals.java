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
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import utils.CosineFunction;
import utils.Utils;

/**
 *
 * @author andrey_pushkarniy
 */
public class PhaseShiftBetweenSignals {
      /* 
         WINDOW_WIDTH     - размер окна фазора 
         NOMINAL_FREQUECY - номинальная частота
      */
      final int    WINDOW_WIDTH = 24;      
      final double NOMINAL_FREQUECY = 50.0; 
      
      
    public PhaseShiftBetweenSignals() {
    }
    
     
     @Test
     public void phaseShiftBetweenSignalsOnNominalFrequency(){
          /**
          * precision          - two double values considers equals, if their difference less than precision 
          * frequencyDeviation - frequency deviation from nominal frequency
          * amplitude1(..2)    - amplitude of tested signal
          * phase1(..2)        - phase shift of tested signal
          * cosine1(..2)       - creates samples of tsted signal 
          * recursiveDFT1(..2) - it performa discrete Fourier transform(DFT) with recursive update of estimation
          * cosArray(sinArray) - sines(cosines) values which are calculated for 24 points in advance. 
          * Because sine(cosine) function is periodic.
          * samples1(..2)            - samples of real signal
          * phasors1(..2)            - phasors of two signals
          * phaseShifts              - values of phase shift between two signals
        */
          double precision = 1e-13;   
          double frequencyDeviation = 0.0;
          double amplitude1 = 100;
          double amplitude2 = 100;
          double phase1 = Math.PI/3;
          double phase2 = Math.PI/6;
          
          double[]  sinArray  = new double[WINDOW_WIDTH];
          double[]  cosArray  = new double[WINDOW_WIDTH];
        
          for (int i = 0; i < cosArray.length; i++) {
             cosArray[i] = Math.cos(i * 2.0 * Math.PI / cosArray.length);
             sinArray[i] = Math.sin(i * 2.0 * Math.PI / cosArray.length);
          }
          
          Function<Double,Complex> recursiveDFT1 = new RecursiveDFT(cosArray, sinArray);
          Function<Double,Complex> recursiveDFT2 = new RecursiveDFT(cosArray, sinArray);
          
          CosineFunction cosine1 = new CosineFunction(amplitude1,phase1,WINDOW_WIDTH,NOMINAL_FREQUECY);
          CosineFunction cosine2 = new CosineFunction(amplitude2,phase2,WINDOW_WIDTH,NOMINAL_FREQUECY);
          
          List<Double> samples1 = Utils.generateSamples(cosine1, 48,frequencyDeviation);
          List<Double> samples2 = Utils.generateSamples(cosine2, 48,frequencyDeviation);
          
          List<Complex> phasors1  = Utils.getPhasors(samples1,recursiveDFT1).stream()
                                        .filter(phasor->phasor!=null)
                                        .collect(Collectors.toList());
          List<Complex> phasors2  = Utils.getPhasors(samples2,recursiveDFT2).stream()
                                        .filter(phasor->phasor!=null)
                                        .collect(Collectors.toList());
          
          
          //it chooses entries of list where phasor estimate exists for calculate phase shift 
          List<Double> phaseShifts = PhaseShiftsBetweenPhasors.calc(phasors1 ,phasors2);       
          
          assertTrue("phase shift between two signals must be constant "
                  + "and equals 30 degree on nominal frequency 50Hz", 
                  isPhaseShiftConstant(phaseShifts,30.0,precision)
          );
     }
     @Test
     public void phaseShiftBetweenSignalsOnOffNominalFrequency(){
            /**
          * frequencyDeviation - frequency deviation from nominal frequency
          * amplitude1(..2)    - amplitude of tested signal
          * phase1(..2)        - phase shift of tested signal
          * cosine1(..2)       - creates samples of tsted signal 
          * recursiveDFT1(..2) - it performa discrete Fourier transform(DFT) with recursive update of estimation
          * cosArray(sinArray) - sines(cosines) values which are calculated for 24 points in advance. 
          * Because sine(cosine) function is periodic.
          * samples1(..2)            - samples of real signal
          * phasors1(..2)            - phasors of two signals
          * phaseShifts              - values of phase shift between two signals
          * deviationFromPhaseShifts - deviation of phase shift between two signals from real value
        */
          double frequencyDeviation = 1.8;
          double amplitude1 = 100;
          double amplitude2 = 100;
          double phase1 = Math.PI/3;
          double phase2 = Math.PI/6;
          
          double[]  sinArray  = new double[WINDOW_WIDTH];
          double[]  cosArray  = new double[WINDOW_WIDTH];
        
          for (int i = 0; i < cosArray.length; i++) {
             cosArray[i] = Math.cos(i * 2.0 * Math.PI / cosArray.length);
             sinArray[i] = Math.sin(i * 2.0 * Math.PI / cosArray.length);
          }
          
          Function<Double,Complex> recursiveDFT1 = new RecursiveDFT(cosArray, sinArray);
          Function<Double,Complex> recursiveDFT2 = new RecursiveDFT(cosArray, sinArray);
          
          CosineFunction cosine1 = new CosineFunction(amplitude1,phase1,WINDOW_WIDTH,NOMINAL_FREQUECY);
          CosineFunction cosine2 = new CosineFunction(amplitude2,phase2,WINDOW_WIDTH,NOMINAL_FREQUECY);
          
          List<Double> samples1 = Utils.generateSamples(cosine1, 48,frequencyDeviation);
          List<Double> samples2 = Utils.generateSamples(cosine2, 48,frequencyDeviation);
          
          List<Complex> phasors1  = Utils.getPhasors(samples1,recursiveDFT1).stream()
                                        .filter(phasor->phasor!=null)
                                        .collect(Collectors.toList());
          List<Complex> phasors2  = Utils.getPhasors(samples2,recursiveDFT2).stream()
                                        .filter(phasor->phasor!=null)
                                        .collect(Collectors.toList());
          
          
          
          //it chooses entries of list where phasor estimate exists for calculate phase shift 
          List<Double> phaseShifts = PhaseShiftsBetweenPhasors.calc(phasors1,phasors2);
          
          List<Double> deviationFromPhaseShifts = phaseShifts.
                                                  stream().
                                                  map(phase->Math.abs(phase-30.0)).
                                                  collect(Collectors.toList());           

          assertTrue("phase shift between two signals must  deviates from 30 degree not greater than 1.08 degree"
                  + " on off-nominal frequency 51.8Hz", 
                  deviationFromPhaseShifts.stream().allMatch(deviation->   deviation < 1.08 )
          );
     }
     @Test 
     public void phaseShiftOnOffNominalFrequencyUsingResamplingFilter(){
         /**
          * frequencyDeviation - frequency deviation from nominal frequency
          * amplitude1(..2)    - amplitude of tested signal
          * phase1(..2)        - phase shift of tested signal
          * cosine1(..2)       - creates samples of tsted signal 
          * recursiveDFT1(..2) - it performa discrete Fourier transform(DFT) with recursive update of estimation
          * cosArray(sinArray) - sines(cosines) values which are calculated for 24 points in advance. 
          * Because sine(cosine) function is periodic.
          * samples1(..2)            - samples of real signal
          * phasors1(..2)            - phasors of two signals
          * phaseShifts              - values of phase shift between two signals
          * deviationFromPhaseShifts - deviation of phase shift between two signals from real value
          * resamplingFilter1(..2)   - performs recalculate time samples, changing distanse between them.
          * recSamples1(..2)         - samples after resampling filter
        */
          double frequencyDeviation = 1.8;
       
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
          
          List<Double> samples1    = Utils.generateSamples(cosine1 , 48,frequencyDeviation);
          List<Double> recSamples1 = Utils.resample(samples1,resamplingFilter1);
     
          List<Double> samples2    = Utils.generateSamples(cosine2 , 48,frequencyDeviation);
          List<Double> recSamples2 = Utils.resample(samples2,resamplingFilter2);
       
          List<Complex> phasors1   = Utils.getPhasors(recSamples1,recursiveDFT1  ).stream()
                  .filter(phasor->phasor!=null)
                  .collect(Collectors.toList());
          List<Complex> phasors2   = Utils.getPhasors(recSamples2,recursiveDFT2 ).stream()
                  .filter(phasor->phasor!=null)
                  .collect(Collectors.toList());
           
          List<Double> phaseShifts = PhaseShiftsBetweenPhasors.calc(phasors1 ,phasors2);  
          List<Double> deviationPhaseShift = phaseShifts.stream()
                  .map(shift->Math.abs(shift-30.0))
                  .collect(Collectors.toList());
          assertTrue("phase shift between two signals must be deviate from 30 degree not greater than 0.08 degree"
                  + " on off-nominal frequency 51.8Hz",
                  deviationPhaseShift.stream().allMatch(shift->shift<0.08)
          );
     }
     
      
     
     public boolean isPhaseShiftConstant(List<Double> phaseShifts,double shift,double precision){
        /* 
         Фазовые сдвиги между двумя сигналами,найденные из оценок фазоров, должны быть постоянными и
         совпадать со значением (shift) с заданной точностью  (precision) при номинальной частоте сигнала 
       */  
         return phaseShifts.stream().allMatch(arg->Utils.compareFPNumbers(arg, shift, precision));
     }
 
}
