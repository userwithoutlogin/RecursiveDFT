/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

 
import com.mycompany.fouriert.utils.PhaseShiftsBetweenPhasors;
import com.mycompany.fouriert.utils.Complex;
import com.mycompany.fouriert.errorcorrection.TransientMonitor;
import com.mycompany.fouriert.ft.RecursiveDiscreteTransform;
import com.mycompany.fouriert.functions.CosineFunction;
import com.mycompany.fouriert.functions.Function;
import com.mycompany.fouriert.functions.Generator;
import com.mycompany.fouriert.utils.AveragingAlgorithm;
import com.mycompany.fouriert.utils.ResamplingFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class PhasorTest {
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
        
        RecursiveDiscreteTransform fourierTransform =  new RecursiveDiscreteTransform(WINDOW_WIDTH);
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
     @Ignore(value = "true")
     @Test
     public void findErrorsInPhasorRepresentation (){        
        /*
          precision          - погрешность, до которой оценку фазора можно считать равной 0
          frequencyDeviation - отклонение частоты от номинального значения
          amplitude          - амплитуда   тестируемого сигнала
          phase              - фазовый сдвиг  тестируемого сигнала
          fourierTransform   - фазер с рекурсивным обновлением оценки, расширяющий дискретное преобразование Фурье(ДПФ)  
          cosine             - функция, задающая  тестовый сигнал
          generator          - генерирует отсчеты  исследуемого сигнала и передает их фазеру для формирования спектра сигнала
          spectrumSamples    - спектральные отсчеты, получаемые после  ДПФ над значениями  тестового сигнала
          monitor            - вычисляет величину ошибки оценки фазора
          limitPointNumbers  - количество точек, подсчитываемое генератором  
          phasorErrors       - список значений, показывабщих на сколько ошибся фазор при выполнении оценки
          countErrors        - число оценок фазора, в которых он допустил ошибку 
        */
        double precision = 1e-10;
        double frequencyDeviation = 1.0;
        double amplitude = 100.0;
        double phase = Math.PI/4;
        int limitPointNubers = 30;
        List<Double> phasorErrors  = new ArrayList();
        
        TransientMonitor monitor = new TransientMonitor(WINDOW_WIDTH);
       
        RecursiveDiscreteTransform fourierTransform =  new RecursiveDiscreteTransform(WINDOW_WIDTH);
        fourierTransform.setMonitor(monitor);
        Function cosine1 = new CosineFunction(amplitude,phase  ,WINDOW_WIDTH ,NOMINAL_FREQUECY);        
        Generator generator = new Generator(fourierTransform,frequencyDeviation, cosine1 ); 
        generator.start(limitPointNubers);    
         
        phasorErrors = generator.getErrorEstimates();
        int countErrors = phasorErrors.stream().filter(error-> error>precision).collect(Collectors.toList()).size();
        
         /*
            Так как сигнал cosine1 имеет частоту, отличную от номинальной, 
            все 7 [limitPointNubers - (WINDOW_WIDTH -1) = 30-(24-1) = 6] оценок фазора будут ошибочны
        */ 
         assertEquals(7, countErrors);
     }
     @Ignore(value = "true")
     @Test
     public void phaseShiftBetweenSignalsOnNominalFrequency(){
        /* 
           precision - погрешность, до которой  2  фазы могут считаться равными
        */
          double precision = 1e-13;   
          double frequencyDeviation = 0.0;
          
          List<Generator> generators = phaseShiftsBetweenSignals(frequencyDeviation);
          
          List<Complex> spectrumSamples1  = generators.get(0).getSpectrumSamples() ;
          List<Complex> spectrumSamples2  = generators.get(1).getSpectrumSamples();  
          
          //Для подсчета фазового сдвига выбираем записи где оценка фазора существует
          List<Double> phaseShifts = PhaseShiftsBetweenPhasors.calc(
                  spectrumSamples1.subList(WINDOW_WIDTH, spectrumSamples1.size()),
                  spectrumSamples2.subList(WINDOW_WIDTH, spectrumSamples2.size())
          );       
          
          assertTrue("phase shift between two signals must be constant and equals 30 degree on nominal frequency 50Hz", 
                  isPhaseShiftConstant(phaseShifts,30.0,precision)
          );
     }
    @Ignore(value = "true")
     @Test
     public void phaseShiftBetweenSignalsOnOffNominalFrequency(){
          /*
            deviationFromPhaseShifts - отклонение значений фазового сдвига мажду двумя функциями от 30 градусов
            frequencyDeviation       - отклонение частоты от номинального значения
         */
          double frequencyDeviation = 1.8;
          
          List<Generator> generators = phaseShiftsBetweenSignals(frequencyDeviation);
          
          List<Complex> spectrumSamples1  = generators.get(0).getSpectrumSamples() ;
          List<Complex> spectrumSamples2  = generators.get(1).getSpectrumSamples();  
          
          //Для подсчета фазового сдвига выбираем записи где оценка фазора существует
          List<Double> phaseShifts = PhaseShiftsBetweenPhasors.calc(
                  spectrumSamples1.subList(WINDOW_WIDTH, spectrumSamples1.size()),
                  spectrumSamples2.subList(WINDOW_WIDTH, spectrumSamples2.size())
          );
          
          List<Double> deviationFromPhaseShifts = phaseShifts.
                                                  stream().
                                                  map(phase->Math.abs(phase-30.0)).
                                                  collect(Collectors.toList());           
//          phaseShifts.forEach(phase->{
//                System.out.println(phase);
//            });
          assertTrue("phase shift between two signals must be deviate from 30 degree not greater than 1.05 degree"
                  + " on off-nominal frequency 53.6Hz", 
                  deviationFromPhaseShifts.stream().allMatch(deviation->   deviation < 1.05 )
          );
     }
//     @Ignore(value = "true")
     @Test
     public void phaseShiftBetweenSignalsOnOffNominalFrequencyWithThreePointAveraging(){
          /*
            deviationFromPhaseShifts - отклонение значений фазового сдвига мажду двумя функциями от 30 градусов
            frequencyDeviation       - отклонение частоты от номинального значения
            averagedPhaseShifts      - отклонение значений фазового сдвига мажду двумя функциями от 30 градусов
                                       усредненные по алгоритму "three point averages" 
         */
          double frequencyDeviation = 1.8;
          List<Generator> generators = phaseShiftsBetweenSignals(frequencyDeviation);
          
          List<Complex> spectrumSamples1  = generators.get(0).getSpectrumSamples() ;
          List<Complex> spectrumSamples2  = generators.get(1).getSpectrumSamples();  
          
          //Для подсчета фазового сдвига выбираем записи где ошенка фазора существует
          List<Double> phaseShifts = PhaseShiftsBetweenPhasors.calc(
                  spectrumSamples1.subList(WINDOW_WIDTH, spectrumSamples1.size()),
                  spectrumSamples2.subList(WINDOW_WIDTH, spectrumSamples2.size())
          );
                      
          List<Double> averagedDeviationPhaseShifts = AveragingAlgorithm.threePoint(phaseShifts)
                                                .stream()
                                                .map(shifted->Math.abs(shifted-30.0))
                                                .collect(Collectors.toList());
          List<Double> aver = AveragingAlgorithm.threePoint(phaseShifts);
            aver.forEach(phase->{
                System.out.println(phase);
            });
          assertTrue("phase shift between two signals must be deviate from 30 degree not greater than 0.94 degree"
                  + " on off-nominal frequency 53.6Hz", 
                  averagedDeviationPhaseShifts.stream().allMatch(deviation->   deviation < 0.94 )
          );
     }
     @Ignore(value = "true")
     @Test 
     public void phaseShiftBetweenSignalsOnOffNominalFrequencyWithResamplingFilter(){
         /*
            deviationFromPhaseShifts - отклонение значений фазового сдвига мажду двумя функциями от 30 градусов
            frequencyDeviation       - отклонение частоты от номинального значения
         */
          double frequencyDeviation = 1.8;
          double actualFrequency = NOMINAL_FREQUECY+frequencyDeviation;
          RecursiveDiscreteTransform transform1 = new RecursiveDiscreteTransform(WINDOW_WIDTH);
          RecursiveDiscreteTransform transform2 = new RecursiveDiscreteTransform(WINDOW_WIDTH);
          
          List<Generator> generators = phaseShiftsBetweenSignals(frequencyDeviation);
          
          List<Double> cosine1 = generators.get(0).getTimeSamples();
          List<Double> cosine2 = generators.get(1).getTimeSamples();
          
          List<Complex> cosine1Spectrum = new ArrayList();
          List<Complex> cosine2Spectrum = new ArrayList();
          List<Double> phaseShiftBetween = new ArrayList();
          
          List<Double> cosine1Resampled = ResamplingFilter.resample(cosine1,
                                                                    WINDOW_WIDTH,
                                                                    NOMINAL_FREQUECY,
                                                                    actualFrequency);
          List<Double> cosine2Resampled = ResamplingFilter.resample(cosine2,
                                                                    WINDOW_WIDTH,
                                                                    NOMINAL_FREQUECY,
                                                                    actualFrequency);
          
          for(int i=0;i<cosine1Resampled.size();i++){
              cosine1Spectrum.add(transform1.direct(cosine1Resampled.get(i)));
              cosine2Spectrum.add(transform2.direct(cosine2Resampled.get(i)));
          }
          
//          List<Double> phase1 = cosine1Spectrum.stream()
//                                    .map(spectrumSample->Math.toDegrees(spectrumSample.arg()))
//                                    .collect(Collectors.toList());
//          List<Double> phase2 = cosine2Spectrum.stream()
//                                    .map(spectrumSample->Math.toDegrees(spectrumSample.arg()))
//                                    .collect(Collectors.toList());
           List<Double> phaseShifts = PhaseShiftsBetweenPhasors.calc(
                  cosine1Spectrum.subList(WINDOW_WIDTH, cosine1Spectrum.size()),
                  cosine2Spectrum.subList(WINDOW_WIDTH, cosine2Spectrum.size())
          );
          List<Double> deviationPhaseShift = phaseShifts.stream()
                                             .map(shift->shift-30.0)
                                              .collect(Collectors.toList());
//          phaseShifts.forEach(shift->{
//              System.out.println(shift);
//          });
          assertTrue("phase shift between two signals must be deviate from 30 degree not greater than 0.94 degree"
                  + " on off-nominal frequency 53.6Hz",
                  deviationPhaseShift.stream().allMatch(shift->shift<0.1)
          );
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
      
     public List<Generator> phaseShiftsBetweenSignals(double frequencyDeviation){
        
         /*
          precision                             - погрешность, до которой 2 фазы могут считаться равными
          frequencyDeviation                    - отклонение частоты от номинального значения
          amplitude1(amplitude2)                - амплитуда первого(второго) тестируемого сигнала
          phase1(phase2)                        - фазовый сдвиг первого(второго) тестируемого сигнала
          fourierTransform1(fourierTransform2)  - фазер с рекурсивным обновлением оценки, расширяющий дискретное преобразование Фурье(ДПФ) для первого(второго) сигнала
          cosine1(cosine2)                      - функция, задающая первый(второй) тестовый сигнал
          generator1(generator2)                - генерирует отсчеты первого(второго) исследуемого сигнала и передает их фазору для формирования спектра сигнала
          spectrumSamples1(spectrumSamples2)    - спектральные отсчеты, получаемые после  ДПФ над значениями первого(второго) тестового сигнала
          limitPointNumbers                     - количество точек, подсчитываемое генератором        
          phasesCosine1(phasesCosine1)          - значения фазы первого(второго) сигнала, полученные от фазора 
          phasesCosine1(phasesCosine2)          - значения фазового сдвига функции cosine1(cosine2)
          phaseShifts                           - значения фазового сдвига между функциями cosine1(cosine2)
       */          
          double amplitude = 100.0;
          double phase1 = Math.PI/3;
          double phase2 = Math.PI/6;
          int limitPointNumbers = 1000;
          
          Function cosine1 = new CosineFunction(amplitude, phase1, WINDOW_WIDTH, NOMINAL_FREQUECY);
          Function cosine2 = new CosineFunction(amplitude, phase2, WINDOW_WIDTH, NOMINAL_FREQUECY);
         
          RecursiveDiscreteTransform fourierTransform1 =  new RecursiveDiscreteTransform(WINDOW_WIDTH);
          RecursiveDiscreteTransform fourierTransform2 =  new RecursiveDiscreteTransform(WINDOW_WIDTH);
          
          Generator generator1 = new Generator(fourierTransform1, frequencyDeviation,cosine1 );
          Generator generator2 = new Generator(fourierTransform2, frequencyDeviation,cosine2 );
          
          generator1.start(limitPointNumbers);
          generator2.start(limitPointNumbers);

          return Arrays.asList(generator1,generator2);
     }

     

    
}
