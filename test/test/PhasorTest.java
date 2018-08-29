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
       */
      final int    WINDOW_WIDTH = 24;      
      final String PATH_TO_FILE = "./realsine.txt"; 
      
     @Test
     public  void applyTest(){
         
          Function<Double,Complex> recursivePhasor = new RecursivePhasor(WINDOW_WIDTH );
          Path pathToFile = Paths.get(PATH_TO_FILE).toAbsolutePath().normalize();
          
          
          List<Complex> phasors = launchPhasor(pathToFile,1,recursivePhasor);
          
          
          assertTrue("The first phasor, obtained from  recursivePhasor,when buffer has just been filled,"
                  + " must be equals 3272.18 -j 1630.63 ", 
                  phasors.get(0).equals(new Complex(3272.17832498552,-1630.6305607908655)));           
     }
     
     public List<Complex> launchPhasor(Path path,int signalIndex,Function<Double,Complex> recursivePhasor){
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
                      .limit(WINDOW_WIDTH)
                      .filter(phasor -> phasor!=null)
                      .collect(Collectors.toList());
                      
          } catch (IOException ex) {
              Logger.getLogger(PhasorTest.class.getName()).log(Level.SEVERE, null, ex);
          }
          return samples;
      }

 
     
     
    
}
