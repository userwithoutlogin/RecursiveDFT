/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.testsignal;

import com.mycompany.fouriert.errorcorrection.TransientMonitor;
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
public class PhasorTest {
      /* 
         WINDOW_WIDTH     - размер окна фазора 
         NOMINAL_FREQUECY - номинальная частота
      */
      final int    WINDOW_WIDTH = 24;      
      final double NOMINAL_FREQUECY = 60.0;   
      
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
      
//     @Test
//     public void findErrorsInPhasorRepresentation (){        
//        /*
//          precision          - погрешность, до которой оценку фазора можно считать равной 0
//          frequencyDeviation - отклонение частоты от номинального значения
//          amplitude          - амплитуда   тестируемого сигнала
//          phase              - фазовый сдвиг  тестируемого сигнала
//          fourierTransform   - фазер с рекурсивным обновлением оценки, расширяющий дискретное преобразование Фурье(ДПФ)  
//          cosine             - функция, задающая  тестовый сигнал
//          generator          - генерирует отсчеты  исследуемого сигнала и передает их фазеру для формирования спектра сигнала
//          spectrumSamples    - спектральные отсчеты, получаемые после  ДПФ над значениями  тестового сигнала
//          monitor            - вычисляет величину ошибки оценки фазора
//          limitPointNumbers  - количество точек, подсчитываемое генератором  
//          phasorErrors       - список значений, показывабщих на сколько ошибся фазор при выполнении оценки
//          countErrors        - число оценок фазора, в которых он допустил ошибку 
//        */
//        double precision = 1e-10;
//        double frequencyDeviation = 0.0;
//        double amplitude1 = 100.0;
//        double phase1 = Math.PI/4;
//        double amplitude2 = 50.0;
//        double phase2 = Math.PI/8;
//        int limitPointNubers = 36;
//        List<Double> phasorErrors  = new ArrayList();
//         double[]  sinArray  = new double[WINDOW_WIDTH];
//        double[]  cosArray  = new double[WINDOW_WIDTH];
//        
//        for(int i=0;i<cosArray.length;i++){
//            cosArray[i] =  Math.cos( i  * 2.0 * Math.PI / cosArray.length );  
//            sinArray[i] =  Math.sin( i  * 2.0 * Math.PI / cosArray.length );  
//        }
//        
//        
//        TransientMonitor monitor = new TransientMonitor( cosArray,sinArray);
//        
//        RecursiveDFT  recursiveDFT =  new RecursiveDFT( cosArray,sinArray);
//   
//        CosineFunction cosine1 = new CosineFunction(amplitude1,phase1  ,WINDOW_WIDTH ,NOMINAL_FREQUECY);        
//        CosineFunction cosine2 = new CosineFunction(amplitude2,phase2  ,WINDOW_WIDTH ,NOMINAL_FREQUECY); 
//        cosine2.setX(36.0);
//        Generator generator = new Generator(recursiveDFT,frequencyDeviation, cosine1,cosine2 ); 
//        generator.setMonitor(monitor);
//        generator.start(limitPointNubers);    
//         
//        phasorErrors = generator.getErrorEstimates();
//        int countErrors = phasorErrors.stream().filter(error-> error>precision).collect(Collectors.toList()).size();
//        
//          
//         assertEquals(7, countErrors);
//     }
     
//       @Ignore(value = "true")
//     @Test
//     public void phaseShiftBetweenSignalsOnNominalFrequency(){
//        /* 
//           precision - two phase  are considered as equals, if their difference not greater than precision 
//           phasesCosine1(phasesCosine2)          - значения фазового сдвига функции cosine1(cosine2)
//           spectrumSamples1(spectrumSamples2)    - spectrum samples obtained from DFT over values of test signal
//           generators                            - list containing two generators for generate test signal samples 
//                                                   and send their to phasor for signal spectrum forming
//        */
//          double precision = 1e-13;   
//          double frequencyDeviation = 0.0;
//          
//          List<Generator> generators = getGenerators(frequencyDeviation);
//          
//          List<Complex> spectrumSamples1  = generators.get(0).getSpectrumSamples() ;
//          List<Complex> spectrumSamples2  = generators.get(1).getSpectrumSamples();  
//          
//          //it chooses entries of list where phasor estimate exists and calculate phase shift between signals
//          List<Double> phaseShifts = PhaseShiftsBetweenPhasors.calc(
//                  spectrumSamples1.subList(WINDOW_WIDTH, spectrumSamples1.size()),
//                  spectrumSamples2.subList(WINDOW_WIDTH, spectrumSamples2.size())
//          );       
//          
//          assertTrue("phase shift between two signals must be constant and equals 30 degree on nominal frequency 50Hz", 
//                  isPhaseShiftConstant(phaseShifts,30.0,precision)
//          );
//     }
//    @Ignore(value = "true")
//     @Test
//     public void phaseShiftBetweenSignalsOnOffNominalFrequency(){
//          /*
//            deviationFromPhaseShifts              - deviation of values of phase shift  from 30 degrees  between 2 functions 
//            frequencyDeviation                    - frequency deviation off nominal  frequency
//            spectrumSamples1(spectrumSamples2)    - spectrum samples obtained from DFT over values of test signal
//            generators                            - list containing two generators for generate test signal samples 
//                                                    and send their to phasor for signal spectrum forming
//         */
//          double frequencyDeviation = 1.8;
//          
//          List<Generator> generators = getGenerators(frequencyDeviation);
//          
//          List<Complex> spectrumSamples1  = generators.get(0).getSpectrumSamples() ;
//          List<Complex> spectrumSamples2  = generators.get(1).getSpectrumSamples();  
//          
//          //it chooses entries of list where phasor estimate exists for calculate phase shift 
//          List<Double> phaseShifts = PhaseShiftsBetweenPhasors.calc(
//                  spectrumSamples1.subList(WINDOW_WIDTH, spectrumSamples1.size()),
//                  spectrumSamples2.subList(WINDOW_WIDTH, spectrumSamples2.size())
//          );
//          
//          List<Double> deviationFromPhaseShifts = phaseShifts.
//                                                  stream().
//                                                  map(phase->Math.abs(phase-30.0)).
//                                                  collect(Collectors.toList());           
//
//          assertTrue("phase shift between two signals must be deviate from 30 degree not greater than 1.05 degree"
//                  + " on off-nominal frequency 53.6Hz", 
//                  deviationFromPhaseShifts.stream().allMatch(deviation->   deviation < 1.05 )
//          );
//     }
     
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
       
     
//     public List<Generator> getGenerators(double frequencyDeviation){
//        
//         /*
//          precision                             - two phase  are considered as equals, if their difference 
//                                                  not greater than precision 
//          frequencyDeviation                    - frequency deviation off nominal  frequency
//          amplitude1(amplitude2)                - amplitude of tested signal
//          phase1(phase2)                        - phase shift of tested signal
//          fourierTransform1(fourierTransform2)  - phasor performing discrete Fourier transform(DFT) with 
//                                                  recursive update of estimation 
//          cosine1(cosine2)                      - representation of test signal
//          generator1(generator2)                - generates test signal samples and send it to phasor for 
//                                                  signal spectrum forming
//          spectrumSamples1(spectrumSamples2)    - spectrum samples obtained from DFT over values of test signal
//          limitPointNumbers                     - quantity of test signal samples        
//          
//       */          
//          double amplitude = 100.0;
//          double phase1 = Math.PI/3;
//          double phase2 = Math.PI/6;
//          int limitPointNumbers = 48;
//          
//          Function cosine1 = new CosineFunction(amplitude, phase1, WINDOW_WIDTH, NOMINAL_FREQUECY);
//          Function cosine2 = new CosineFunction(amplitude, phase2, WINDOW_WIDTH, NOMINAL_FREQUECY);
//         
//          RecursiveDiscreteTransform fourierTransform1 =  new RecursiveDiscreteTransform(WINDOW_WIDTH);
//          RecursiveDiscreteTransform fourierTransform2 =  new RecursiveDiscreteTransform(WINDOW_WIDTH);
//          
//          Generator generator1 = new Generator(fourierTransform1, frequencyDeviation,cosine1 );
//          Generator generator2 = new Generator(fourierTransform2, frequencyDeviation,cosine2 );
//          
//          generator1.start(limitPointNumbers);
//          generator2.start(limitPointNumbers);
//
//          return Arrays.asList(generator1,generator2);
//     }

}
