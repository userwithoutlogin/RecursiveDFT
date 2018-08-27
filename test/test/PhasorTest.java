/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

 
import com.mycompany.fouriert.utils.PhaseShiftsBetweenPhasors;
import com.mycompany.fouriert.utils.Complex;
import com.mycompany.fouriert.errorcorrection.TransientMonitor;
import com.mycompany.fouriert.ft.RecursivePhasor;
import com.mycompany.fouriert.functions.CosineFunction;
import com.mycompany.fouriert.functions.Function;
import com.mycompany.fouriert.functions.Generator;
import com.mycompany.fouriert.utils.AveragingAlgorithm;
import com.mycompany.fouriert.utils.ResamplingFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
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
         PATH_TO_FILE     - path to fille which contains  samples of real sine
      */
      final int    WINDOW_WIDTH = 24;      
      final double NOMINAL_FREQUENCY = 50.0;   
      Path PATH_TO_FILE = Paths.get("./realsine.txt").toAbsolutePath().normalize();
      
 
       
     @Test
     public void findingRealSignalFaultSample(){
         /**
          * monitor1(..2,..3) - it detects, when sine begins breaking
          * phasor1(..2,..3)  - phasor performing discrete Fourier transform(DFT) with recursive update of estimation
          */
         TransientMonitor monitor1 = new TransientMonitor(WINDOW_WIDTH);
         TransientMonitor monitor2 = new TransientMonitor(WINDOW_WIDTH);
         TransientMonitor monitor3 = new TransientMonitor(WINDOW_WIDTH);

         RecursivePhasor phasor1 = new RecursivePhasor(WINDOW_WIDTH,monitor1);
         RecursivePhasor phasor2 = new RecursivePhasor(WINDOW_WIDTH,monitor2);
         RecursivePhasor phasor3 = new RecursivePhasor(WINDOW_WIDTH,monitor3);

         try {
            analyzeFileData( PATH_TO_FILE,1, phasor1);
            analyzeFileData( PATH_TO_FILE,2, phasor2);
            analyzeFileData( PATH_TO_FILE,3, phasor3);
         } catch (IOException ex) {
             Logger.getLogger(PhasorTest.class.getName()).log(Level.SEVERE, null, ex);
         }
          
         assertTrue("fault sample of the first   sine  has number 81" , phasor1.getN()  == 81);
         assertTrue("fault sample of the second  sine  has number 80" , phasor2.getN()  == 80);
         assertTrue("fault sample of the third   sine  has number 80" , phasor3.getN()  == 80);
              
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
//          double amplitude = 100.0;
//          double phase1 = Math.PI/3;
//          double phase2 = Math.PI/6;
//          int limitPointNumbers = 10000;
//          
//          Function cosine1 = new CosineFunction(amplitude, phase1, WINDOW_WIDTH, NOMINAL_FREQUENCY);
//          Function cosine2 = new CosineFunction(amplitude, phase2, WINDOW_WIDTH, NOMINAL_FREQUENCY);
//         
////          RecursivePhasor fourierTransform1 =  new RecursivePhasor(WINDOW_WIDTH);
////          RecursivePhasor fourierTransform2 =  new RecursivePhasor(WINDOW_WIDTH);
//          
//          Generator generator1 = new Generator(fourierTransform1, frequencyDeviation,cosine1 );
//          Generator generator2 = new Generator(fourierTransform2, frequencyDeviation,cosine2 );
//          
//          generator1.start(limitPointNumbers);
//          generator2.start(limitPointNumbers);
//
//          return Arrays.asList(generator1,generator2);
          return null;
     }
     public void analyzeFileData( Path pathToFile,int functionNumber,RecursivePhasor phasor) throws IOException{
          /**
           * Snippet chooses value belongs desirable function (function with number functionNumber), 
           * then it estimates value and if fault is detected stream is stopped.
           */  
          Files.lines(pathToFile, StandardCharsets.UTF_8)
                  .map(line->{
                      String[] values = line.split(",");
                      return new Double( values[functionNumber+1] );
                    })
                  .map(timeSample ->{ phasor.accept( timeSample);
                                      return phasor.isFault(); }
                   )
                  .anyMatch(fault->fault);
     }
 

    
}
