/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.mycompany.fouriert.complex.Complex;
import com.mycompany.fouriert.errorcorrection.TransientMonitor;
import com.mycompany.fouriert.ft.RecursiveDiscreteTransform;
import com.mycompany.fouriert.functions.CosineFunction;
import com.mycompany.fouriert.functions.Function;
import com.mycompany.fouriert.functions.Generator;
import java.util.ArrayList;
import java.util.List;
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
public class PhasorTest {
      /* 
         WINDOW_WIDTH     - размер окна фазора 
         NOMINAL_FREQUECY - номинальная частота
      */
      final int    WINDOW_WIDTH = 24;      
      final double NOMINAL_FREQUECY = 50.0;   
      
      
     @Test
     public void  phasorRepresentationMustBeConstantOnNominalFrequency(){
        /*
          precision          - точность, в пределах которой 2 амплитуды(фазы) могут считаться равными
          frequencyDeviation - отклонение частоты от номинального значения
          amplitude          - амплитуда тестируемого сигнала
          phase              - фазовый сдвиг тестируемого сигнала
          fourierTransform   - фазер с рекурсивным обновлением оценки, расширяющий дискретное преобразование Фурье(ДПФ)
          cosine             - функция, задающая тестовый сигнал
          generator          - генерирует отсчеты исследуемого сигнала и передает их фазеру для формирования спектра сигнала
          spectrumSamples    - спектральные отсчеты, получаемые после  ДПФ над значениями тестового сигнала
          limitPointNubers   - количество точекб подсчитываемое генератором
        */
                 
        double precision = 1e-13;
        double frequencyDeviation = 1.0;
        double amplitude = 100.0;
        double phase = Math.PI/4;
        int limitPointNubers = 36;
        List<Complex> spectrumSamples  = new ArrayList();
       
        RecursiveDiscreteTransform fourierTransform =  new RecursiveDiscreteTransform(WINDOW_WIDTH);
        Function cosine = new CosineFunction(amplitude,phase  ,WINDOW_WIDTH ,NOMINAL_FREQUECY);        
        Generator generator = new Generator(fourierTransform,frequencyDeviation,NOMINAL_FREQUECY,cosine ); 
        generator.start(limitPointNubers);    
        spectrumSamples = generator.getSpectrumSamples();
        
        assertTrue("Phase is constant and equals to "+phase+" for all samples",         isPhaseConstant(phase  ,  spectrumSamples,   precision));  
        assertTrue("Amplitude is constant and equals to "+amplitude+" for all samples", isAmplitudeConstant(amplitude/Math.sqrt(2),  spectrumSamples,  precision));
        
     }
     
     @Test
     public void phaseShiftBetweenTwoSignalsMustBeConstantOnNominalFrequency(){
         
     }
     @Test
     public void corruptedSinusoidLeadsToPhasorEstimateError(){
        double precision = 1e-13;
        double frequencyDeviation = 0.0;
        double amplitude1 = 100.0;
        double amplitude2 = 50.0;
        double phase1 = Math.PI/4;
        double phase2 = Math.PI/8;
        int limitPointNubers = 36;
        List<Complex> spectrumSamples  = new ArrayList();
        List<Double> phasorErrors  = new ArrayList();
        
        TransientMonitor monitor = new TransientMonitor(WINDOW_WIDTH);
        RecursiveDiscreteTransform fourierTransform =  new RecursiveDiscreteTransform(WINDOW_WIDTH);
        fourierTransform.setMonitor(monitor);
        Function cosine1 = new CosineFunction(amplitude1,phase1  ,WINDOW_WIDTH ,NOMINAL_FREQUECY);        
        Function cosine2 = new CosineFunction(amplitude2,phase2  ,WINDOW_WIDTH ,NOMINAL_FREQUECY);        
        Generator generator = new Generator(fourierTransform,frequencyDeviation,NOMINAL_FREQUECY,cosine1,cosine2 ); 
        generator.start(limitPointNubers);    
        spectrumSamples = generator.getSpectrumSamples();
        phasorErrors = generator.getErrorEstimates();
        int n = countOfInconstantSamples( spectrumSamples.subList(WINDOW_WIDTH, spectrumSamples.size()));
        
         assertEquals(n, phasorErrors.size());
     }
     
     
     public boolean isPhaseConstant(double phase,List<Complex> spectrumSamples,double precision){
         /* 
           Фазы всех спектральных отсчетов (spectrumSamples) должны быть постоянными и
           совпадать со значением (phase) с заданной точностью  (precision) 
         */  
         return spectrumSamples.subList(WINDOW_WIDTH, spectrumSamples.size()).stream()
                    .map(sample->sample.arg())
                    .allMatch(arg->compareFPNumbers(arg,phase,precision) );
     }
     public boolean isAmplitudeConstant(double amplitude,List<Complex> spectrumSamples,double precision){
       /* 
         Амплитуды всех спектральных отсчетов (spectrumSamples) должны быть постоянными и
         совпадать со значением (amplitude) с заданной точностью  (precision) 
       */  
         return spectrumSamples.subList(WINDOW_WIDTH, spectrumSamples.size()).stream()
                    .map(sample->sample.amplitude())
                    .allMatch(arg->compareFPNumbers(arg,amplitude,precision) );
     }
     public boolean compareFPNumbers(double n1,double n2,double precision){
       return Math.abs(n1-n2)<precision;
     }
     public int countOfInconstantSamples(List<Complex> spectrumSamples){
         int count=0;
         for(int i=1;i<spectrumSamples.size();i++){
             Complex tmp = spectrumSamples.get(i-1);
             if(!tmp.isEqual(spectrumSamples.get(i)))count++;
         }
         return count;        
     }
}
