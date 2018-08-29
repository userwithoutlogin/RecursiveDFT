/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

 
import com.mycompany.fouriert.errorcorrection.FaultDetection;
import com.mycompany.fouriert.utils.PhaseShiftsBetweenPhasors;
import com.mycompany.fouriert.utils.Complex;
import com.mycompany.fouriert.errorcorrection.TransientMonitor;
import com.mycompany.fouriert.errorcorrection.TransientMonitorSource;
 
import com.mycompany.fouriert.phasor.RecursivePhasor;
 
 
 
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
 
import java.util.List;
import java.util.function.Function;
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
         
      */
      final int    WINDOW_WIDTH = 24;      
      final double NOMINAL_FREQUENCY = 50.0;   
      final String PATH_TO_FILE = "./realsine.txt"; 
      
     @Test
     public  void phasorTest(){
         
          Function<Double,Complex> phasor = new RecursivePhasor(WINDOW_WIDTH );
          Path pathToFile = Paths.get(PATH_TO_FILE).toAbsolutePath().normalize();
          
          
          List<Complex> samples = launchPhasor(pathToFile,1,phasor);
          
          
          assertTrue("The first spectrum sample, obtained from phasor,when buffer has just been filled,"
                  + " must be equals 3272.18 -j 1630.63 ", 
                  samples.get(0).equals(new Complex(3272.17832498552,-1630.6305607908655)));           
     }
      
     @Test
     public void monitorTest(){
          
         
          double precision = 1e-10;
          Function<Double,Complex> phasor = new RecursivePhasor(WINDOW_WIDTH );
          Function<TransientMonitorSource,Double> monitor = new TransientMonitor(WINDOW_WIDTH);
          Path pathToFile = Paths.get(PATH_TO_FILE).toAbsolutePath().normalize();
          TransientMonitorSource source = new TransientMonitorSource();
          
          
          List<Complex> samples = launchPhasor(pathToFile,1,phasor);
          
          source.setTimeSample(2188.0);
          source.setSpectrumSample(samples.get(0));
          double error = monitor.apply(source);
           
          assertTrue("The first errro of phasor estimation, "
                  + "when buffer has just been filled, must be equals 2439.56",
                  compareFPNumbers(error, 2439.558965697799,precision));
     }
      
     @Test
     public void faultDetectionTest(){
          
         RecursivePhasor phasor = new RecursivePhasor(WINDOW_WIDTH);
         Function<TransientMonitorSource, Double> monitor = new TransientMonitor(WINDOW_WIDTH);
         Path pathToFile = Paths.get(PATH_TO_FILE).toAbsolutePath().normalize();
          

         double correctTimeSample   = 2118.0;
         double incorrectTimeSample = 21118.0;
      
        launchPhasor(pathToFile, 1, phasor);

         FaultDetection faultDetection = new FaultDetection();
         faultDetection.setMonitor(monitor);
         faultDetection.setPhasor(phasor);

         boolean noError = faultDetection.apply(correctTimeSample);
         boolean hasError = faultDetection.apply(incorrectTimeSample);

         assertFalse("fault has not been detected ", noError);
         assertTrue("fault has  been detected    ",  hasError);
                   
                  
     }
      
     @Test
     public void findingFaultSampleInRealSignal(){
         /**
          * monitor1(..2,..3)        - it detects, when sine begins breaking
          * phasor1(..2,..3)         - phasor performing discrete Fourier transform(DFT) with recursive update of estimation
          * faultDetection1(..2,..3) - it evaluates if the error exceeds allowable limitation
          */
         Path pathToFile = Paths.get(PATH_TO_FILE).toAbsolutePath().normalize();
         TransientMonitor monitor1 = new TransientMonitor(WINDOW_WIDTH);
         TransientMonitor monitor2 = new TransientMonitor(WINDOW_WIDTH);
         TransientMonitor monitor3 = new TransientMonitor(WINDOW_WIDTH);

         RecursivePhasor phasor1 = new RecursivePhasor(WINDOW_WIDTH );
         RecursivePhasor phasor2 = new RecursivePhasor(WINDOW_WIDTH );
         RecursivePhasor phasor3 = new RecursivePhasor(WINDOW_WIDTH );
         
         FaultDetection faultDetection1 = new FaultDetection();
         faultDetection1.setMonitor(monitor1);
         faultDetection1.setPhasor(phasor1);
         
         FaultDetection faultDetection2 = new FaultDetection();
         faultDetection2.setMonitor(monitor2);
         faultDetection2.setPhasor(phasor2);
         
         FaultDetection faultDetection3 = new FaultDetection();
         faultDetection3.setMonitor(monitor3);
         faultDetection3.setPhasor(phasor3);
         
         try {
            findErroneousSampe(pathToFile,1, faultDetection1);
            findErroneousSampe(pathToFile,2, faultDetection2);
            findErroneousSampe(pathToFile,3, faultDetection3);
         } catch (IOException ex) {
             Logger.getLogger(PhasorTest.class.getName()).log(Level.SEVERE, null, ex);
         }
          
         assertTrue("fault sample of the first   sine  has number 81" , phasor1.getN()  == 81);
         assertTrue("fault sample of the second  sine  has number 80" , phasor2.getN()  == 80);
         assertTrue("fault sample of the third   sine  has number 80" , phasor3.getN()  == 80);
              
     } 
      
      
      
      public void findErroneousSampe( Path pathToFile,int functionNumber,Function<Double,Boolean> faultDetection) throws IOException{
          /**
           * Snippet chooses value belongs desirable function(signal) (function with number functionNumber), 
           * then it estimates value and if fault is detected stream is stopped.
           */  

           Files.lines(pathToFile, StandardCharsets.UTF_8) 
                  .map(line->{
                     return new Double( line.split(",")[functionNumber+1] );
                   })
                  .map(faultDetection)
                  .anyMatch(fault->fault);
//          
          
//          
     }
      public List<Complex> launchPhasor(Path path,int signalIndex,Function<Double,Complex> phasor){
          List<Complex> samples = new ArrayList();
          
          /**
           * It loads  first 24 samples, and applying phasor to them
           */
          try {
              samples =  Files.lines(path , StandardCharsets.UTF_8)
                      .map(line->{
                          return new Double( line.split(",")[1+signalIndex] );
                      })
                      .map(phasor)
                      .limit(WINDOW_WIDTH)
                      .filter(spectrumSample->!spectrumSample.equals(new Complex(0.0,0.0)))
                      .collect(Collectors.toList());
                      
          } catch (IOException ex) {
              Logger.getLogger(PhasorTest.class.getName()).log(Level.SEVERE, null, ex);
          }
          return samples;
      }

     
     public boolean isPhaseConstant(double phase,List<Complex> spectrumSamples,double precision){
         /* 
           phases of all spectrum samples(spectrumSamples) must be constant and equal 
           to value (phase) with setted precision on nominal frequency of signal
         */  
         return spectrumSamples.subList(WINDOW_WIDTH, spectrumSamples.size()).stream()
                    .map(sample->sample.getArg())
                    .allMatch(arg->compareFPNumbers(arg,phase,precision) );
     }
     public boolean isAmplitudeConstant(double amplitude,List<Complex> spectrumSamples,double precision){
       /* 
         amplitudes of all spectrum samples(spectrumSamples) must be constant and equal 
         to value (amplitude) with setted precision on nominal frequency of signal
       */  
         return spectrumSamples.subList(WINDOW_WIDTH, spectrumSamples.size()).stream()
                    .map(sample->sample.getAmplitude())
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
