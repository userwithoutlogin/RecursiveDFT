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
import com.mycompany.fouriert.functions.Function;
import com.mycompany.fouriert.functions.Generator;
import com.mycompany.fouriert.phasor.RecursiveDFT;
import com.mycompany.fouriert.utils.Complex;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
 
import java.util.stream.Collectors;
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
     public void phasorRepresentationsOnNominalFrequency(){
        /*
          precision          - погрешность, до которой  2 амплитуды(фазы) могут считаться равными
          frequencyDeviation - отклонение частоты от номинального значения
          amplitude          - амплитуда тестируемого сигнала
          phase              - фазовый сдвиг тестируемого сигнала
          fourierTransform   - фазер с рекурсивным обновлением оценки, расширяющий дискретное преобразование Фурье(ДПФ)
          cosine             - функция, задающая тестовый сигнал
          generator          - генерирует отсчеты исследуемого сигнала и передает их фазору для формирования спектра сигнала
          spectrumSamples    - спектральные отсчеты, получаемые после  ДПФ над значениями тестового сигнала
          limitPointNumbers  - количество точек, подсчитываемое генератором
        */                 
        double precision = 1e-13;
        double frequencyDeviation = 0.0;
        double amplitude = 100.0;
        double phase = Math.PI/4;
        int limitPointNumbers = 36;
         double[]  sinArray  = new double[WINDOW_WIDTH];
        double[]  cosArray  = new double[WINDOW_WIDTH];
        
        for(int i=0;i<cosArray.length;i++){
            cosArray[i] =  Math.cos( i  * 2.0 * Math.PI / cosArray.length );  
            sinArray[i] =  Math.sin( i  * 2.0 * Math.PI / cosArray.length );  
        }
        
        
        RecursiveDFT fourierTransform =  new RecursiveDFT(cosArray,sinArray);
        Function cosine = new CosineFunction(amplitude,phase  ,WINDOW_WIDTH ,NOMINAL_FREQUECY);        
        Generator generator = new Generator(fourierTransform,frequencyDeviation, cosine );
 
        generator.start(limitPointNumbers);    
        List<Complex> spectrumSamples = generator.getSpectrumSamples();
        
        // Амплитуда и фаза составляют представление фазора 
        assertTrue("Phase must be constant and equals to pi/4 for all samples on nominal frequency 50Hz",
                isPhaseConstant(phase  ,  spectrumSamples,   precision)
        );  
        assertTrue("Amplitude must be constant and equals to 100/sqrt(2) for all samples on nominal frequency 50Hz",
                isAmplitudeConstant(100/Math.sqrt(2),  spectrumSamples,  precision)
        );        
     }
      
     @Test
     public void findErrorsInPhasorRepresentation (){        
        /**
          * precision          - if difference between two double values less than precision , this numbers are equals
          * frequencyDeviation - frequency deviation from nominal frequency
          * amplitude          - amplitude of tested signal
          * phase              - phase shift of tested signal
          * cosine             - creates samples of tsted signal 
          * generator          - generates samples , performs to them recursiveDTF and calculates error of estimation
          * limitPointNumbers  - count points of sine  
          * phasorsErrors      - list of errors being difference between actual  and recalculated samples
          * monitor            - it detects, when sine begins breaking
          * recursivePhasor    - it performa discrete Fourier transform(DFT) with recursive update of estimation
          * cosArray(sinArray) - sines(cosines) values which are calculated for 24 points in advance. 
          * Because sine(cosine) function is periodic.
        */
        double precision = 1e-10;
        double frequencyDeviation = 0.0;
        double amplitude = 100.0;
        double phase = Math.PI/4;
        double incorrectSample = 200.71067811865477;
        int limitPointNubers = 48;
        List<Double> phasorsErrors  = new ArrayList();
        double[]  sinArray  = new double[WINDOW_WIDTH];
        double[]  cosArray  = new double[WINDOW_WIDTH];
        
         for (int i = 0; i < cosArray.length; i++) {
             cosArray[i] = Math.cos(i * 2.0 * Math.PI / cosArray.length);
             sinArray[i] = Math.sin(i * 2.0 * Math.PI / cosArray.length);
         }
        TransientMonitor monitor = new TransientMonitor( cosArray,sinArray);
        RecursiveDFT  recursiveDFT =  new RecursiveDFT( cosArray,sinArray);
   
        CosineFunction cosine = new CosineFunction(amplitude,phase  ,WINDOW_WIDTH ,NOMINAL_FREQUECY);        
        Generator generator = new Generator(recursiveDFT,frequencyDeviation, cosine); 
        generator.setMonitor(monitor);
        
        generator.start(limitPointNubers);    
        phasorsErrors = generator.getErrorEstimates();
         
        /**
         * Finds phasor for erroneous sample and pass it through monitor 
         */
         TransientMonitorSource source = new TransientMonitorSource();
         Complex phasor = recursiveDFT.apply(incorrectSample);
         source.setPhasor(phasor);
         source.setSample(incorrectSample);
         double error = monitor.apply(source);
          
         assertTrue("Error for every sample sine on nominal frequency is near zero",
                 areAllEntriesEqualsToZero(phasorsErrors));
   
         assertTrue("If we find phasor using incorrect sample(80.71) "
                 + "instead of expected sample(70.71), "
                 + "we will get error near 9.2, not 10 because of errors recalculating",
                 compareFPNumbers(9.17,error,precision) );
     }
      
     public boolean areAllEntriesEqualsToZero(List<Double> list){
         return list.stream().allMatch(entry->entry<1e-10);
     }
     public boolean isPhaseConstant(double phase,List<Complex> spectrumSamples,double precision){
         /* 
           Фазы всех спектральных отсчетов (spectrumSamples) должны быть постоянными и
           совпадать со значением (phase) с заданной точностью  (precision) при номинальной частоте сигнала
         */  
         return spectrumSamples.subList(WINDOW_WIDTH, spectrumSamples.size()).stream()
                    .map(sample->sample.getArg())
                    .allMatch(arg->compareFPNumbers(arg,phase,precision) );
     }
     
     public boolean isAmplitudeConstant(double amplitude,List<Complex> spectrumSamples,double precision){
       /* 
         Амплитуды всех спектральных отсчетов (spectrumSamples) должны быть постоянными и
         совпадать со значением (amplitude) с заданной точностью  (precision) при номинальной частоте сигнала
       */  
         return spectrumSamples.subList(WINDOW_WIDTH, spectrumSamples.size()).stream()
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
       
      

}
