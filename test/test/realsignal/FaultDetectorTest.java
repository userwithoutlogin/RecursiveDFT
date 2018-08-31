/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.realsignal;

import test.*;
import com.mycompany.fouriert.errorcorrection.FaultDetection;
import com.mycompany.fouriert.errorcorrection.TransientMonitor;
import com.mycompany.fouriert.errorcorrection.TransientMonitorSource;
import com.mycompany.fouriert.phasor.RecursiveDFT;
import com.mycompany.fouriert.utils.Complex;
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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author andrey_pushkarniy
 */
public class FaultDetectorTest {
    final String PATH_TO_FILE = "./realsine.txt"; 
    final int    WINDOW_WIDTH = 24;
    
    public FaultDetectorTest() {
    }
    
     @Test
     public void errorDetection(){
         /**
          * cosArray(sinArray) - sines(cosines) values which are calculated for 24 points in advance. 
          * Because sine(cosine) function is periodic.
          */
        double[]  sinArray  = new double[WINDOW_WIDTH];
        double[]  cosArray  = new double[WINDOW_WIDTH];
        
        for(int i=0;i<cosArray.length;i++){
            cosArray[i] =  Math.cos( i  * 2.0 * Math.PI / cosArray.length );  
            sinArray[i] =  Math.sin( i  * 2.0 * Math.PI / cosArray.length );  
        }
           
         Function<Double, Complex> recursivePhasor = new RecursiveDFT(cosArray,sinArray);
         Function<TransientMonitorSource, Double> monitor = new TransientMonitor(cosArray,sinArray );
         Path pathToFile = Paths.get(PATH_TO_FILE).toAbsolutePath().normalize();
          
         //correct last sample of the first window
         double correctSample   = 5273.0;
         double incorrectSample = 21118.0;
        /**
         * Calculate phasor for 24th sample
         */
        launchPhasor(pathToFile, 1, recursivePhasor);

         FaultDetection faultDetection = new FaultDetection();
         faultDetection.setMonitor(monitor);
         faultDetection.setRecursiveDFT(recursivePhasor);

         boolean noError  = faultDetection.apply(correctSample);
         boolean hasError = faultDetection.apply(incorrectSample);

         assertFalse("fault has not been detected ", noError);
         assertTrue("fault has  been detected    ",  hasError);
                   
                  
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
                      .limit(24)
                      .filter(phasor->phasor!=null)
                      .collect(Collectors.toList());
                      
          } catch (IOException ex) {
              Logger.getLogger(PhasorTest.class.getName()).log(Level.SEVERE, null, ex);
          }
          return samples;
      }
}
