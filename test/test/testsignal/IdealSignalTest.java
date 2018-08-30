/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.testsignal;

import com.mycompany.fouriert.errorcorrection.FaultDetection;
import com.mycompany.fouriert.errorcorrection.TransientMonitor;
import com.mycompany.fouriert.errorcorrection.TransientMonitorSource;
import com.mycompany.fouriert.functions.CosineFunction;
 
import com.mycompany.fouriert.functions.Generator;
import com.mycompany.fouriert.phasor.RecursiveDFT;
import com.mycompany.fouriert.utils.Complex;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.junit.Ignore;

/**
 *
 * @author andrey_pushkarniy
 */
public class IdealSignalTest {
      /* 
         WINDOW_WIDTH     - размер окна фазора 
         NOMINAL_FREQUECY - номинальная частота
      */
      final int    WINDOW_WIDTH = 24;      
      final double NOMINAL_FREQUECY = 50.0;   
      
     @Ignore(value = "true")
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
         List<Double> samples =  generateSamples(cosine, limitPointNubers);
         /**
          * Adds incorrect 49th sample 
          */
         samples.add(incorrectSample);
         Integer number = findNumberOFError(faultDetection, samples)+1;
         
         assertTrue("48th sample is erroneous",number == 49  );
    
     }
     
     @Test
     public void phasorsOnNominalFrequency(){
        /**
          precision          - if difference between two double values less than precision , this numbers are equals
          frequencyDeviation - frequency deviation from nominal frequency
          amplitude          - amplitude of tested signal
          phase              - phase shift of tested signal
          recursiveDFT       - it performa discrete Fourier transform(DFT) with recursive update of estimation
          cosine             - creates samples of tsted signal 
          limitPointNumbers  - count points of sine
        */                 
        double precision          = 1e-13;
        double frequencyDeviation = 0.0;
        double amplitude          = 100.0;
        double phase              = Math.PI/4;
        int limitPointNumbers     = 36;
        
        double[]  sinArray  = new double[WINDOW_WIDTH];
        double[]  cosArray  = new double[WINDOW_WIDTH];
        
        for(int i=0;i<cosArray.length;i++){
            cosArray[i] =  Math.cos( i  * 2.0 * Math.PI / cosArray.length );  
            sinArray[i] =  Math.sin( i  * 2.0 * Math.PI / cosArray.length );  
        }
        
        
        RecursiveDFT recursiveDFT =  new RecursiveDFT(cosArray,sinArray);
        CosineFunction cosine = new CosineFunction(amplitude,phase  ,WINDOW_WIDTH ,NOMINAL_FREQUECY);        
        List<Double> samples = generateSamples(cosine,limitPointNumbers);
        List<Complex> phasors = getPhasors(recursiveDFT, samples);
          
     
        
        // Amplitude and phase are phaser representation 
        assertTrue("Phase must be constant and equals to pi/4 for all samples on nominal frequency 50Hz",
                isPhaseConstant(phase  ,  phasors,   precision)
        );  
        assertTrue("Amplitude must be constant and equals to 100/sqrt(2) for all samples on nominal frequency 50Hz",
                isAmplitudeConstant(100/Math.sqrt(2),  phasors,  precision)
        );        
     }
      
    
      
     public boolean areAllEntriesEqualsToZero(List<Double> list){
         return list.stream().allMatch(entry->entry<1e-10);
     }
     public boolean isPhaseConstant(double phase,List<Complex> spectrumSamples,double precision){
         /* 
           Фазы всех спектральных отсчетов (spectrumSamples) должны быть постоянными и
           совпадать со значением (phase) с заданной точностью  (precision) при номинальной частоте сигнала
         */  
         return spectrumSamples.stream()
                    .map(sample->sample.getArg())
                    .allMatch(arg->compareFPNumbers(arg,phase,precision) );
     }
     public boolean isAmplitudeConstant(double amplitude,List<Complex> spectrumSamples,double precision){
       /* 
         Амплитуды всех спектральных отсчетов (spectrumSamples) должны быть постоянными и
         совпадать со значением (amplitude) с заданной точностью  (precision) при номинальной частоте сигнала
       */  
         return spectrumSamples.stream()
                    .map(sample->sample.getAmplitude())
                    .allMatch(arg->compareFPNumbers(arg,amplitude,precision) );
     }
     public boolean isPhaseShiftConstant(List<Double> phaseShifts,double shift,double precision){
        /* 
         Фазовые сдвиги между двумя сигналами,найденные из оценок фазоров, должны быть постоянными и
         совпадать со значением (shift) с заданной точностью  (precision) при номинальной частоте сигнала 
       */  
         return phaseShifts.stream().allMatch(arg->compareFPNumbers(arg, shift, precision));
     }
     public boolean compareFPNumbers(double n1,double n2,double precision){
      //Функция сравнения чисел с плавающей точкой   
       return Math.abs(n1-n2)<precision;
     }
       
     public List<Double> generateSamples(CosineFunction  cosine,int pointsCount){
         List<Double> list= new ArrayList();
         IntStream.range(0, pointsCount).forEach(i->{
             list.add(cosine.calc(0.0));
         });
         return list;
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
     public List<Complex> getPhasors(Function<Double,Complex> recursiveDFT,List<Double> samples){
         return samples.stream()
                 .map(recursiveDFT)
                 .filter(phasor->phasor!=null)
                 .collect(Collectors.toList());
     }
}
