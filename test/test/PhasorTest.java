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
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.scene.shape.Path;
 
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
         WINDOW_WIDTH     - phasor`s window size 
         NOMINAL_FREQUECY - nominal frequency
      */
      final int    WINDOW_WIDTH = 24;      
      final double NOMINAL_FREQUENCY = 50.0;   
      
     @Ignore(value = "true")
     @Test
     public void phasorRepresentationsOnNominalFrequency(){
        /*
          precision          - two phase (amplitudes) are considered as equals, if their difference not greater than precision  
          frequencyDeviation - frequency deviation off nominal  frequency
          amplitude          - amplitude of tested signal
          phase              - phase shift of tested signal
          fourierTransform   - phasor performing discrete Fourier transform(DFT) with recursive update of estimation
          cosine             - representation of test signal
          generator          - generates test signal samples and send it to phasor for signal spectrum forming
          spectrumSamples    - spectrum samples obtained from DFT over values of test signal
          limitPointNumbers  - quantity of test signal samples
        */                 
        double precision = 1e-13;
        double frequencyDeviation = 0.0;
        double amplitude = 100.0;
        double phase = Math.PI/4;
        int limitPointNumbers = 36;
        
        RecursiveDiscreteTransform fourierTransform =  new RecursiveDiscreteTransform(WINDOW_WIDTH);
        Function cosine = new CosineFunction(amplitude,phase  ,WINDOW_WIDTH ,NOMINAL_FREQUENCY);        
        Generator generator = new Generator(fourierTransform,frequencyDeviation, cosine ); 
        generator.start(limitPointNumbers);    
        List<Complex> spectrumSamples = generator.getSpectrumSamples();
        
        // amplitude and phase consists phasor`s representation
        assertTrue("Phase must be constant and equals to pi/4 for all samples on nominal frequency 50Hz",
                isPhaseConstant(phase  ,  spectrumSamples,   precision)
        );  
        assertTrue("Amplitude must be constant and equals to 100/sqrt(2) for all samples on nominal frequency 50Hz",
                isAmplitudeConstant(100/Math.sqrt(2),  spectrumSamples,  precision)
        );        
     }
        @Ignore("true")
     @Test
     public void findErrorsInPhasorRepresentation (){        
        /*
          precision          - phasor`s estimate is considered as zero if it`s less than precision
          frequencyDeviation - frequency deviation off nominal  frequency
          amplitude          - amplitude of tested signal
          phase              - phase shift of tested signal
          fourierTransform   - phasor performing discrete Fourier transform(DFT) with recursive update of estimation  
          cosine             - representation of test signal
          generator          - generates test signal samples and send it to phasor for signal spectrum forming
          spectrumSamples    - spectrum samples obtained from DFT over values of test signal
          monitor            - computes error`s value of phasor`s estimate
          limitPointNumbers  - quantity of test signal samples  
          phasorErrors       - list of errors of phasor`s estimates
          countErrors        - quantity of estimates in which phasor made error 
        */
        double precision = 1e-10;
        double frequencyDeviation = 1.0;
        double amplitude = 100.0;
        double phase = Math.PI/4;
        int limitPointNubers = 30;
        List<Double> phasorErrors  = new ArrayList();
        
        TransientMonitor monitor = new TransientMonitor(NOMINAL_FREQUENCY,NOMINAL_FREQUENCY +frequencyDeviation,WINDOW_WIDTH);
       
        RecursiveDiscreteTransform fourierTransform =  new RecursiveDiscreteTransform(WINDOW_WIDTH);
        fourierTransform.setMonitor(monitor);
        Function cosine1 = new CosineFunction(amplitude,phase  ,WINDOW_WIDTH ,NOMINAL_FREQUENCY);        
        Generator generator = new Generator(fourierTransform,frequencyDeviation, cosine1 ); 
        generator.start(limitPointNubers);    
         
        phasorErrors = generator.getErrorEstimates();
        int countErrors = phasorErrors.stream().filter(error-> error>precision).collect(Collectors.toList()).size();
        
         /*
            because  signal 'cosine1' have frequency not equal nominal value, 
            all 7 [limitPointNubers - (WINDOW_WIDTH -1) = 30-(24-1) = 6]  estimates will be eroneous            
        */ 
         assertEquals(7, countErrors);
     }
     @Ignore(value = "true")
     @Test
     public void phaseShiftBetweenSignalsOnNominalFrequency(){
        /* 
           precision - two phase  are considered as equals, if their difference not greater than precision 
           phasesCosine1(phasesCosine2)          - значения фазового сдвига функции cosine1(cosine2)
           spectrumSamples1(spectrumSamples2)    - spectrum samples obtained from DFT over values of test signal
           generators                            - list containing two generators for generate test signal samples 
                                                   and send their to phasor for signal spectrum forming
        */
          double precision = 1e-13;   
          double frequencyDeviation = 0.0;
          
          List<Generator> generators = getGenerators(frequencyDeviation);
          
          List<Complex> spectrumSamples1  = generators.get(0).getSpectrumSamples() ;
          List<Complex> spectrumSamples2  = generators.get(1).getSpectrumSamples();  
          
          //it chooses entries of list where phasor estimate exists and calculate phase shift between signals
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
            deviationFromPhaseShifts              - deviation of values of phase shift  from 30 degrees  between 2 functions 
            frequencyDeviation                    - frequency deviation off nominal  frequency
            spectrumSamples1(spectrumSamples2)    - spectrum samples obtained from DFT over values of test signal
            generators                            - list containing two generators for generate test signal samples 
                                                    and send their to phasor for signal spectrum forming
         */
          double frequencyDeviation = 1.8;
          
          List<Generator> generators = getGenerators(frequencyDeviation);
          
          List<Complex> spectrumSamples1  = generators.get(0).getSpectrumSamples() ;
          List<Complex> spectrumSamples2  = generators.get(1).getSpectrumSamples();  
          
          //it chooses entries of list where phasor estimate exists for calculate phase shift 
          List<Double> phaseShifts = PhaseShiftsBetweenPhasors.calc(
                  spectrumSamples1.subList(WINDOW_WIDTH, spectrumSamples1.size()),
                  spectrumSamples2.subList(WINDOW_WIDTH, spectrumSamples2.size())
          );
          
          List<Double> deviationFromPhaseShifts = phaseShifts.
                                                  stream().
                                                  map(phase->Math.abs(phase-30.0)).
                                                  collect(Collectors.toList());           

          assertTrue("phase shift between two signals must be deviate from 30 degree not greater than 1.05 degree"
                  + " on off-nominal frequency 53.6Hz", 
                  deviationFromPhaseShifts.stream().allMatch(deviation->   deviation < 1.05 )
          );
     }
     @Ignore(value = "true")
     @Test
     public void phaseShiftBetweenSignalsOnOffNominalFrequencyWithThreePointAveraging(){
          /*
            frequencyDeviation                    - frequency deviation off nominal  frequency
            averagedPhaseShifts                   - deviations of values of phase shift from 30 degrees between 2 function, 
                                                    where their values averaged on three-point algorithm
            spectrumSamples1(spectrumSamples2)    - spectrum samples obtained from DFT over values of test signal
            generators                            - list containing two generators for generate test signal samples 
                                                    and send their to phasor for signal spectrum forming
            averagedDeviationPhaseShifts          - averaged deviation of phase shift from 30 degrees between two signals 
         */
          double frequencyDeviation = 1.8;
          List<Generator> generators = getGenerators(frequencyDeviation);
          
          List<Complex> spectrumSamples1  = generators.get(0).getSpectrumSamples() ;
          List<Complex> spectrumSamples2  = generators.get(1).getSpectrumSamples();  
          
          //it chooses entries of list where phasor estimate exists for calculate phase shift 
          List<Double> phaseShifts = PhaseShiftsBetweenPhasors.calc(
                  spectrumSamples1.subList(WINDOW_WIDTH, spectrumSamples1.size()),
                  spectrumSamples2.subList(WINDOW_WIDTH, spectrumSamples2.size())
          );
                      
          List<Double> averagedDeviationPhaseShifts = AveragingAlgorithm.threePoint(phaseShifts)
                                                .stream()
                                                .map(shifted->Math.abs(shifted-30.0))
                                                .collect(Collectors.toList());
          
          assertTrue("phase shift between two signals must be deviate from 30 degree not greater than 0.94 degree"
                  + " on off-nominal frequency 53.6Hz", 
                  averagedDeviationPhaseShifts.stream().allMatch(deviation->   deviation < 0.94 )
          );
     }
      @Ignore("true")
     @Test 
     public void phaseShiftBetweenSignalsOnOffNominalFrequencyWithResamplingFilter(){
         /*
            deviationFromPhaseShifts - deviation of values of phase shift  from 30 degrees  between 2 function
            actualFrequency          - nominal frequency plus frequency deviation
            frequencyDeviation       - frequency deviation off nominal  frequency
            transform1(transform2)   - phasor performing discrete Fourier transform(DFT) with recursive update of estimation  
         */
          double frequencyDeviation = 1.8;
          double actualFrequency = NOMINAL_FREQUENCY+frequencyDeviation;
          RecursiveDiscreteTransform transform1 = new RecursiveDiscreteTransform(WINDOW_WIDTH);
          RecursiveDiscreteTransform transform2 = new RecursiveDiscreteTransform(WINDOW_WIDTH);
          
          List<Generator> generators = getGenerators(frequencyDeviation);
          
          List<Double> cosine1 = generators.get(0).getTimeSamples();
          List<Double> cosine2 = generators.get(1).getTimeSamples();
          
          List<Complex> cosine1Spectrum = new ArrayList();
          List<Complex> cosine2Spectrum = new ArrayList();
          List<Double> phaseShiftBetween = new ArrayList();
          
          List<Double> cosine1Resampled = ResamplingFilter.resample(cosine1,
                                                                    WINDOW_WIDTH,
                                                                    NOMINAL_FREQUENCY,
                                                                    actualFrequency);
          List<Double> cosine2Resampled = ResamplingFilter.resample(cosine2,
                                                                    WINDOW_WIDTH,
                                                                    NOMINAL_FREQUENCY,
                                                                    actualFrequency);
          
          for(int i=0;i<cosine1Resampled.size();i++){
              cosine1Spectrum.add(transform1.direct(cosine1Resampled.get(i)));
              cosine2Spectrum.add(transform2.direct(cosine2Resampled.get(i)));
          }
                   
           List<Double> phaseShifts = PhaseShiftsBetweenPhasors.calc(
                  cosine1Spectrum.subList(WINDOW_WIDTH, cosine1Spectrum.size()),
                  cosine2Spectrum.subList(WINDOW_WIDTH, cosine2Spectrum.size())
          );
          List<Double> deviationPhaseShift = phaseShifts.stream()
                                             .map(shift->shift-30.0)
                                              .collect(Collectors.toList());
//         
          assertTrue("phase shift between two signals must be deviate from 30 degree not greater than 0.06 degree"
                  + " on off-nominal frequency 53.6Hz",
                  deviationPhaseShift.stream().allMatch(shift->shift<0.08)
          );
     }
     
      
     @Test
     public void testRealSignals(){
         List<Double> shift12 = new ArrayList();
         List<Double> shift23 = new ArrayList();
         List<Double> error1 = new ArrayList();
         List<Double> error2 = new ArrayList();
         List<Double> error3 = new ArrayList();
         List<Double> timeSample1 = new ArrayList();
         List<Double> timeSample2 = new ArrayList();
         List<Double> timeSample3 = new ArrayList();
         List<Double> timeSampleRes1 = new ArrayList();
         List<Double> timeSampleRes2 = new ArrayList();
         List<Double> timeSampleRes3 = new ArrayList();
         double fDeviation = 1.8;
        
         
         TransientMonitor monitor1 = new TransientMonitor(NOMINAL_FREQUENCY+ fDeviation,NOMINAL_FREQUENCY  ,WINDOW_WIDTH);
         TransientMonitor monitor2 = new TransientMonitor(NOMINAL_FREQUENCY+ fDeviation,NOMINAL_FREQUENCY  ,WINDOW_WIDTH);
         TransientMonitor monitor3 = new TransientMonitor(NOMINAL_FREQUENCY+ fDeviation,NOMINAL_FREQUENCY  ,WINDOW_WIDTH);
         
         RecursiveDiscreteTransform  transform1 = new RecursiveDiscreteTransform(WINDOW_WIDTH);
         RecursiveDiscreteTransform  transform2 = new RecursiveDiscreteTransform(WINDOW_WIDTH);
         RecursiveDiscreteTransform  transform3 = new RecursiveDiscreteTransform(WINDOW_WIDTH);
      
         transform1.setMonitor(monitor1);
         transform2.setMonitor(monitor2);
         transform3.setMonitor(monitor3);
         
         List<Complex> spectrum1 = new ArrayList();
         List<Complex> spectrum2 = new ArrayList();
         List<Complex> spectrum3 = new ArrayList();
          try {
              Files.lines(Paths.get("C:/realsine.txt"), StandardCharsets.UTF_8).forEach(str->{
                        String[] values = str.split(",");
                         timeSample1.add((new Double(values[2]) ));
                         timeSample2.add((new Double(values[3]) ));
                         timeSample3.add((new Double(values[4]) ));
              });
              timeSampleRes1 = ResamplingFilter.resample(timeSample1, WINDOW_WIDTH, NOMINAL_FREQUENCY+fDeviation , NOMINAL_FREQUENCY);
              timeSampleRes2 = ResamplingFilter.resample(timeSample2, WINDOW_WIDTH, NOMINAL_FREQUENCY , NOMINAL_FREQUENCY);
              timeSampleRes3 = ResamplingFilter.resample(timeSample3, WINDOW_WIDTH, NOMINAL_FREQUENCY , NOMINAL_FREQUENCY);
              
              for(int i=0;i<timeSample1.size();i++){
                  spectrum1.add(transform1.direct(timeSample1.get(i)));
                  spectrum2.add(transform2.direct(timeSample2.get(i)));
                  spectrum3.add(transform3.direct(timeSample3.get(i)));
                  error1.add(transform1.calculatePhasorEstimateQality( ));
                  error2.add(transform2.calculatePhasorEstimateQality( ));
                  error3.add(transform3.calculatePhasorEstimateQality( ));
                 // shift12.add(Math.toDegrees(spectrum1.get(i).arg()-spectrum2.get(i).arg()));
                 String str = new String(""+Math.toDegrees(spectrum3.get(i).arg()));
                 String str1 = new String(""+(spectrum1.get(i).amplitude()*Math.sqrt(2)));
//                  System.out.println(/*(spectrum1.get(i).amplitude()*Math.sqrt(2))+"  "+*/ str.replace(".", ","));
                    double phase2 = Math.toDegrees(spectrum2.get(i).arg());
                    double phase1 = Math.toDegrees(spectrum1.get(i).arg());
                    double shift = phase1>phase2?phase1-phase2:phase2-phase1;
                  System.out.println(/*(spectrum1.get(i).amplitude()*Math.sqrt(2))+"  "+*/ (new String(""+ shift)).replace(".", ","));
              }
              int y=0;
              
          } catch (IOException ex) {
              Logger.getLogger(PhasorTest.class.getName()).log(Level.SEVERE, null, ex);
          }
     } 
     
     
     public boolean isPhaseConstant(double phase,List<Complex> spectrumSamples,double precision){
         /* 
           phases of all spectrum samples(spectrumSamples) must be constant and equal 
           to value (phase) with setted precision on nominal frequency of signal
         */  
         return spectrumSamples.subList(WINDOW_WIDTH, spectrumSamples.size()).stream()
                    .map(sample->sample.arg())
                    .allMatch(arg->compareFPNumbers(arg,phase,precision) );
     }
     public boolean isAmplitudeConstant(double amplitude,List<Complex> spectrumSamples,double precision){
       /* 
         amplitudes of all spectrum samples(spectrumSamples) must be constant and equal 
         to value (amplitude) with setted precision on nominal frequency of signal
       */  
         return spectrumSamples.subList(WINDOW_WIDTH, spectrumSamples.size()).stream()
                    .map(sample->sample.amplitude())
                    .allMatch(arg->compareFPNumbers(arg,amplitude,precision) );
     }
     public boolean isPhaseShiftConstant(List<Double> phaseShifts,double shift,double precision){
        /* 
         phase shifts between two signals must be constant and equal 
         to value (shift) with setted precision on nominal frequency of signal
       */  
         return phaseShifts.stream().allMatch(arg->compareFPNumbers(arg, shift, precision));
     }
     public boolean compareFPNumbers(double n1,double n2,double precision){
      // function of comparison of two floating point numbers   
       return Math.abs(n1-n2)<precision;
     }
     public List<Generator> getGenerators(double frequencyDeviation){
        
         /*
          precision                             - two phase  are considered as equals, if their difference 
                                                  not greater than precision 
          frequencyDeviation                    - frequency deviation off nominal  frequency
          amplitude1(amplitude2)                - amplitude of tested signal
          phase1(phase2)                        - phase shift of tested signal
          fourierTransform1(fourierTransform2)  - phasor performing discrete Fourier transform(DFT) with 
                                                  recursive update of estimation 
          cosine1(cosine2)                      - representation of test signal
          generator1(generator2)                - generates test signal samples and send it to phasor for 
                                                  signal spectrum forming
          spectrumSamples1(spectrumSamples2)    - spectrum samples obtained from DFT over values of test signal
          limitPointNumbers                     - quantity of test signal samples        
          
       */          
          double amplitude = 100.0;
          double phase1 = Math.PI/3;
          double phase2 = Math.PI/6;
          int limitPointNumbers = 48;
          
          Function cosine1 = new CosineFunction(amplitude, phase1, WINDOW_WIDTH, NOMINAL_FREQUENCY);
          Function cosine2 = new CosineFunction(amplitude, phase2, WINDOW_WIDTH, NOMINAL_FREQUENCY);
         
          RecursiveDiscreteTransform fourierTransform1 =  new RecursiveDiscreteTransform(WINDOW_WIDTH);
          RecursiveDiscreteTransform fourierTransform2 =  new RecursiveDiscreteTransform(WINDOW_WIDTH);
          
          Generator generator1 = new Generator(fourierTransform1, frequencyDeviation,cosine1 );
          Generator generator2 = new Generator(fourierTransform2, frequencyDeviation,cosine2 );
          
          generator1.start(limitPointNumbers);
          generator2.start(limitPointNumbers);

          return Arrays.asList(generator1,generator2);
     }

     

    
}
