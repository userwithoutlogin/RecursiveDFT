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
public class PhasorTest {
      /* 
         WINDOW_WIDTH     - размер окна фазора 
         NOMINAL_FREQUECY - номинальная частота
      */
      final int    WINDOW_WIDTH = 24;      
      final double NOMINAL_FREQUECY = 50.0;   
      
      
     @Test
     public void  phasorRepresentationsOnNominalFrequency(){
        /*
          precision          - точность, в пределах которой 2 амплитуды(фазы) могут считаться равными
          frequencyDeviation - отклонение частоты от номинального значения
          amplitude          - амплитуда тестируемого сигнала
          phase              - фазовый сдвиг тестируемого сигнала
          fourierTransform   - фазер с рекурсивным обновлением оценки, расширяющий дискретное преобразование Фурье(ДПФ)
          cosine             - функция, задающая тестовый сигнал
          generator          - генерирует отсчеты исследуемого сигнала и передает их фазеру для формирования спектра сигнала
          spectrumSamples    - спектральные отсчеты, получаемые после  ДПФ над значениями тестового сигнала
          limitPointNumbers   - количество точек, подсчитываемое генератором
        */
                 
        double precision = 1e-13;
        double frequencyDeviation = 0.0;
        double amplitude = 100.0;
        double phase = Math.PI/4;
        int limitPointNumbers = 36;
        
        RecursiveDiscreteTransform fourierTransform =  new RecursiveDiscreteTransform(WINDOW_WIDTH);
        Function cosine = new CosineFunction(amplitude,phase  ,WINDOW_WIDTH ,NOMINAL_FREQUECY);        
        Generator generator = new Generator(fourierTransform,frequencyDeviation, cosine ); 
        generator.start(limitPointNumbers);    
        List<Complex> spectrumSamples = generator.getSpectrumSamples();
        
        /* Амплитуда и фаза составляют представление фазора */
        assertTrue("Phase must be constant and equals to pi/4 for all samples on nominal frequency",
                isPhaseConstant(phase  ,  spectrumSamples,   precision)
        );  
        assertTrue("Amplitude must be constant and equals to 100/sqrt(2) for all samples on nominal frequency",
                isAmplitudeConstant(100/Math.sqrt(2),  spectrumSamples,  precision)
        );
        
     }
     
     @Test
     public void calculatePhaseShiftBetweenSignals(){
            /*
          precision                             - точность, в пределах которой 2 фазы могут считаться равными
          frequencyDeviation                    - отклонение частоты от номинального значения
          amplitude1(amplitude2)                - амплитуда первого(второго) тестируемого сигнала
          phase1(phase2)                        - фазовый сдвиг первого(второго) тестируемого сигнала
          fourierTransform1(fourierTransform2)  - фазер с рекурсивным обновлением оценки, расширяющий дискретное преобразование Фурье(ДПФ) для первого(второго) сигнала
          cosine1(cosine2)                      - функция, задающая первый(второй) тестовый сигнал
          generator1(generator2)                - генерирует отсчеты первого(второго) исследуемого сигнала и передает их фазеру для формирования спектра сигнала
          spectrumSamples1(spectrumSamples2)    - спектральные отсчеты, получаемые после  ДПФ над значениями первого(второго) тестового сигнала
          limitPointNumbers                     - количество точек, подсчитываемое генератором
          arg1(arg2)                            - значения фазы первого(второго) сигнала, полученные от фазора 
          phaseShifts                           - значения фазового сдвига между cosine1 и cosine2
        */
          
          double precision = 1e-13;
          double frequencyDeviation = 0.0;
          double amplitude = 100.0;
          double phase1 = Math.PI/3;
          double phase2 = Math.PI/6;
          int limitPointNumbers = 36;
          
          Function cosine1 = new CosineFunction(amplitude, phase1, WINDOW_WIDTH, NOMINAL_FREQUECY);
          Function cosine2 = new CosineFunction(amplitude, phase2, WINDOW_WIDTH, NOMINAL_FREQUECY);
         
          RecursiveDiscreteTransform fourierTransform1 =  new RecursiveDiscreteTransform(WINDOW_WIDTH);
          RecursiveDiscreteTransform fourierTransform2 =  new RecursiveDiscreteTransform(WINDOW_WIDTH);
          
          Generator generator1 = new Generator(fourierTransform1, frequencyDeviation,cosine1 );
          Generator generator2 = new Generator(fourierTransform2, frequencyDeviation,cosine2 );
          
          generator1.start(limitPointNumbers);
          generator2.start(limitPointNumbers);
          
          List<Complex> spectrumSamples1  = generator1.getSpectrumSamples() ;
          List<Complex> spectrumSamples2  = generator2.getSpectrumSamples();         
          
          List<Double> arg1 = spectrumSamples1.subList(WINDOW_WIDTH, spectrumSamples1.size())
                  .stream()
                  .map(sample->sample.arg() )
                  .collect(Collectors.toList());
          List<Double> arg2 = spectrumSamples2.subList(WINDOW_WIDTH, spectrumSamples2.size())
                  .stream()
                  .map(sample->sample.arg() )
                  .collect(Collectors.toList());
          
          List<Double> phaseShifts = new ArrayList();
          for(int i=0;i<arg1.size();i++)
              phaseShifts.add(arg1.get(i)-arg2.get(i));
              
          assertTrue("phase shift between cosine1 and cosine2 must be constant and equals pi/6 on nominal frequency", isPhaseShiftConstant(phaseShifts,Math.PI/6,precision));
     }
     
     public boolean isPhaseConstant(double phase,List<Complex> spectrumSamples,double precision){
         /* 
           Фазы всех спектральных отсчетов (spectrumSamples) должны быть постоянными и
           совпадать со значением (phase) с заданной точностью  (precision) при номинальной частоте сигнала
         */  
         return spectrumSamples.subList(WINDOW_WIDTH, spectrumSamples.size()).stream()
                    .map(sample->sample.arg())
                    .allMatch(arg->compareFPNumbers(arg,phase,precision) );
     }
     public boolean isAmplitudeConstant(double amplitude,List<Complex> spectrumSamples,double precision){
       /* 
         Амплитуды всех спектральных отсчетов (spectrumSamples) должны быть постоянными и
         совпадать со значением (amplitude) с заданной точностью  (precision) при номинальной частоте сигнала
       */  
         return spectrumSamples.subList(WINDOW_WIDTH, spectrumSamples.size()).stream()
                    .map(sample->sample.amplitude())
                    .allMatch(arg->compareFPNumbers(arg,amplitude,precision) );
     }
     public boolean compareFPNumbers(double n1,double n2,double precision){
      //Функция сравнения чисел с плавающей точкой   
       return Math.abs(n1-n2)<precision;
     }
     public boolean isPhaseShiftConstant(List<Double> phaseShifts,double shift,double precision){
        /* 
         Фазовые сдвиги между двумя сигналами,найденные из оценок фазоров, должны быть постоянными и
         совпадать со значением (shift) с заданной точностью  (precision) при номинальной частоте сигнала 
       */  
         return phaseShifts.stream().allMatch(arg->compareFPNumbers(arg, shift, precision));
     }
}
