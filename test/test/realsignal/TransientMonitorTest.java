/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.realsignal;

import test.*;
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
import java.util.stream.Stream;
import org.junit.Test;
import static org.junit.Assert.*;
import utils.Utils;

/**
 *
 * @author andrey_pushkarniy
 */
public class TransientMonitorTest {
     /**
      * WINDOW_WIDTH - window width of phasor
      */
     final int    WINDOW_WIDTH = 24; 
    
    public TransientMonitorTest() {
        
    }
    
     
     @Test
     public void estimationErrorObtaining(){
         
          /**
          * monitor            - it detects, when sine begins breaking
          * recursiveDFT       - it performa discrete Fourier transform(DFT) with recursive update of estimation
          * cosArray(sinArray) - sines(cosines) values which are calculated for 24 points in advance. 
          * Because sine(cosine) function is periodic.
          * error              - error of phasor estimation
        */
         
          double[]  sinArray  = new double[WINDOW_WIDTH];
          double[]  cosArray  = new double[WINDOW_WIDTH];
        
        for(int i=0;i<cosArray.length;i++){
            cosArray[i] =  Math.cos( i  * 2.0 * Math.PI / cosArray.length );  
            sinArray[i] =  Math.sin( i  * 2.0 * Math.PI / cosArray.length );  
        }
         
         
          Function<Double,Complex> recursiveDFT = new RecursiveDFT(cosArray,sinArray);
          Function<TransientMonitorSource,Double> monitor = new TransientMonitor(cosArray,sinArray );
          Path pathToFile = Paths.get(Utils.PATH_TO_FILE).toAbsolutePath().normalize();
          TransientMonitorSource source = new TransientMonitorSource();
          
          List<Double> samples  = Utils.getSamplesFromFile(pathToFile, 1, WINDOW_WIDTH);
          List<Complex> phasors = Utils.getPhasors(samples,  recursiveDFT);
          
          for(int i=0;i<22;i++){
              source.setPhasor(phasors.get(i));
              source.setSample(samples.get(i));
              monitor.apply(source);              
          }
          
          source.setSample(5273.0);
          source.setPhasor(phasors.get(phasors.size()-1));
          double error = monitor.apply(source);
           
          assertTrue("The  error of phasor estimation, "
                  + "between the last sample in the window  and recalculated sample,"
                  + "when buffer has just been filled,"
                  + "must be equals 2418.45",
                  Utils.compareFPNumbers(error, 2418.45,1e-2));
     }
     
     
}
