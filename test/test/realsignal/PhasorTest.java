/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.realsignal;

 
import test.*;
import com.mycompany.fouriert.utils.Complex;
 
import com.mycompany.fouriert.phasor.RecursiveDFT;
import com.mycompany.fouriert.utils.PhaseShiftsBetweenPhasors;
 
 
 
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
import signal.CosineFunction;
/**
 *
 * @author andrey_pushkarniy
 */
public class PhasorTest {
      /* 
         WINDOW_WIDTH     - phasor`s window size 
       */
      final int    WINDOW_WIDTH = 24;      
      final String PATH_TO_FILE = "./realsine.txt"; 
     
      @Ignore(value = "true")
      @Test
      public  void phasorObtaining(){
          double[]  sinArray  = new double[WINDOW_WIDTH];
          double[]  cosArray  = new double[WINDOW_WIDTH];
        
          for(int i=0;i<cosArray.length;i++){
            cosArray[i] =  Math.cos( i  * 2.0 * Math.PI / cosArray.length );  
            sinArray[i] =  Math.sin( i  * 2.0 * Math.PI / cosArray.length );  
          }
         
         
          Function<Double,Complex> recursivePhasor = new RecursiveDFT(cosArray,sinArray );
          Path pathToFile = Paths.get(PATH_TO_FILE).toAbsolutePath().normalize();
          
          
          List<Complex> phasors = generatePhasors(pathToFile,1,recursivePhasor,WINDOW_WIDTH);
          
          assertTrue("The first phasor, obtained from  recursivePhasor,when buffer has just been filled,"
                  + " must be equals 3272.18 -j 1630.63 ", 
                  phasors.get(0).equals(new Complex(3272.17832498552,-1630.6305607908655)));           
     }
      
     @Test
     public void phaseShiftBetweenSignals(){
        /* 
           precision - two phase  are considered as equals, if their difference not greater than precision 
        */
          double precision = 1e-13;   
          double frequencyDeviation = 0.0;
          double amplitude1 = 100;
          double amplitude2 = 100;
          double phase1 = Math.PI/3;
          double phase2 = Math.PI/6;
          
          double[]  sinArray  = new double[WINDOW_WIDTH];
          double[]  cosArray  = new double[WINDOW_WIDTH];
        
          for (int i = 0; i < cosArray.length; i++) {
             cosArray[i] = Math.cos(i * 2.0 * Math.PI / cosArray.length);
             sinArray[i] = Math.sin(i * 2.0 * Math.PI / cosArray.length);
          }
          
          Function<Double,Complex> recursivePhasor1 = new RecursiveDFT(cosArray,sinArray );
          Function<Double,Complex> recursivePhasor2 = new RecursiveDFT(cosArray,sinArray );
          Path pathToFile = Paths.get(PATH_TO_FILE).toAbsolutePath().normalize();
          
          List<Complex> phasors1 = generatePhasors(pathToFile,1,recursivePhasor1,48);
          List<Complex> phasors2 = generatePhasors(pathToFile,2,recursivePhasor2,48);
          
          List<Double> arg1 = phasors1.stream()
                              .map(phasor->Math.toDegrees(phasor.getArg()))
                              .collect(Collectors.toList());
          List<Double> arg2 = phasors2.stream()
                              .map(phasor->Math.toDegrees(phasor.getArg()))
                              .collect(Collectors.toList());
          
          //it chooses entries of list where phasor estimate exists for calculate phase shift 
          List<Double> phaseShifts = PhaseShiftsBetweenPhasors.calc(phasors1 ,phasors2);       
          
          assertTrue("phase shift between two signals locaes in range"
                  + " from 110 degree to 129 degree", 
                  isPhaseShiftInRange(110.0,129.0,phaseShifts)
          );
     }
     
     public List<Complex> generatePhasors(Path path,int signalIndex,Function<Double,Complex> recursivePhasor,int limit){
          List<Complex> samples = new ArrayList();
          
          /**
           * It loads  first 24 samples, and applying phasor to them
           */
          try {
              samples =  Files.lines(path , StandardCharsets.UTF_8)
                      .map(line->{
                          return new Double( line.split(",")[1+signalIndex] );
                      })
                      .map(recursivePhasor)
                      .limit(limit)
                      .filter(phasor -> phasor!=null)
                      .collect(Collectors.toList());
                      
          } catch (IOException ex) {
              Logger.getLogger(PhasorTest.class.getName()).log(Level.SEVERE, null, ex);
          }
          return samples;
      }
     private boolean isPhaseShiftInRange(double from, double to, List<Double> phaseShifts) {
        return phaseShifts.stream()
                .allMatch(shift->{
                    return shift >= 110 && shift <=129;
                });
    }
}
