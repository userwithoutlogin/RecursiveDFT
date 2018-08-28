/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

 
import com.mycompany.fouriert.utils.PhaseShiftsBetweenPhasors;
import com.mycompany.fouriert.utils.Complex;
import com.mycompany.fouriert.errorcorrection.TransientMonitor;
import com.mycompany.fouriert.phasor.RecursivePhasor;
 
 
 
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
      public void analyzeFileData( Path pathToFile,int functionNumber,RecursivePhasor phasor) throws IOException{
          /**
           * Snippet chooses value belongs desirable function(signal) (function with number functionNumber), 
           * then it estimates value and if fault is detected stream is stopped.
           */  
          Files.lines(pathToFile, StandardCharsets.UTF_8)
                  .map(line->{
                     return new Double( line.split(",")[functionNumber+1] );
                   })
                  .map(timeSample ->{ phasor.accept( timeSample);
                                      return phasor.isFault(); 
                   })
                  .anyMatch(fault->fault);
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
     
     
    
}
