/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.mycompany.fouriert.errorcorrection.TransientMonitor;
import com.mycompany.fouriert.errorcorrection.TransientMonitorSource;
import com.mycompany.fouriert.phasor.RecursivePhasor;
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
public class TransientMonitorTest {
     final String PATH_TO_FILE = "./realsine.txt"; 
     final int    WINDOW_WIDTH = 24; 
    
    public TransientMonitorTest() {
        
    }
    
     
      @Test
     public void applyTest(){
          
         
          double precision = 1e-10;
          Function<Double,Complex> recursivePhasor = new RecursivePhasor(WINDOW_WIDTH );
          Function<TransientMonitorSource,Double> monitor = new TransientMonitor( );
          Path pathToFile = Paths.get(PATH_TO_FILE).toAbsolutePath().normalize();
          TransientMonitorSource source = new TransientMonitorSource();
          
          
          List<Complex> samples = launchPhasor(pathToFile,1,recursivePhasor );
          
          source.setSample(5273.0);
          source.setPhasor(samples.get(0));
          double error = monitor.apply(source);
           
          assertTrue("The  error of phasor estimation, "
                  + "between the last sample in the window  and recalculated sample,"
                  + "when buffer has just been filled,"
                  + "must be equals 1399.97",
                  compareFPNumbers(error, 1399.9734917936921,precision));
     }
     
     public List<Complex> launchPhasor(Path path,int signalIndex,Function<Double,Complex> recursivePhasor ){
          List<Complex> samples = new ArrayList();
          
          /**
           * It loads  first 24 samples, and applying phasor to them
           */
          try {
              samples =  Files.lines(path , StandardCharsets.UTF_8)
                      .map(line->{
                          return new Double( line.split(",")[1+signalIndex] );
                      })
                      .map(recursivePhasor )
                      .limit(WINDOW_WIDTH)
                      .filter(phasor->phasor!=null)
                      .collect(Collectors.toList());
                      
          } catch (IOException ex) {
              Logger.getLogger(PhasorTest.class.getName()).log(Level.SEVERE, null, ex);
          }
          return samples;
      }
      public boolean compareFPNumbers(double n1,double n2,double precision){
      // function of comparison of two floating point numbers       
       return Math.abs(n1-n2)<precision;
     }
}
