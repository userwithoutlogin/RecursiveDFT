/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.mycompany.fouriert.errorcorrection.FaultDetection;
import com.mycompany.fouriert.errorcorrection.TransientMonitor;
import com.mycompany.fouriert.phasor.RecursivePhasor;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class RealSignalTest {
     final int    WINDOW_WIDTH = 24;      
     final String PATH_TO_FILE = "./realsine.txt"; 
      
    public RealSignalTest() {
    }
    
   @Test
   public void findingFaultSampleInRealSignal(){
         /**
          * monitor1(..2,..3)         - it detects, when sine begins breaking
          * recursivePhasor1(..2,..3) - it performa discrete Fourier transform(DFT) with recursive update of estimation
          * faultDetection1(..2,..3)  - it evaluates if the error exceeds allowable limitation
          */
         Path pathToFile = Paths.get(PATH_TO_FILE).toAbsolutePath().normalize();
         TransientMonitor monitor1 = new TransientMonitor( );
         TransientMonitor monitor2 = new TransientMonitor( );
         TransientMonitor monitor3 = new TransientMonitor( );

         RecursivePhasor recursivePhasor1 = new RecursivePhasor(WINDOW_WIDTH );
         RecursivePhasor recursivePhasor2 = new RecursivePhasor(WINDOW_WIDTH );
         RecursivePhasor recursivePhasor3 = new RecursivePhasor(WINDOW_WIDTH );
         
         FaultDetection faultDetection1 = new FaultDetection();
         faultDetection1.setMonitor(monitor1);
         faultDetection1.setRecursivePhasor(recursivePhasor1);
         
         FaultDetection faultDetection2 = new FaultDetection();
         faultDetection2.setMonitor(monitor2);
         faultDetection2.setRecursivePhasor(recursivePhasor2);
         
         FaultDetection faultDetection3 = new FaultDetection();
         faultDetection3.setMonitor(monitor3);
         faultDetection3.setRecursivePhasor(recursivePhasor3);
         
         try {
            findErroneousSampe(pathToFile,1, faultDetection1);
            findErroneousSampe(pathToFile,2, faultDetection2);
            findErroneousSampe(pathToFile,3, faultDetection3);
         } catch (IOException ex) {
             Logger.getLogger(PhasorTest.class.getName()).log(Level.SEVERE, null, ex);
         }
          
         assertTrue("fault sample of the first   sine  has number 81" , recursivePhasor1.getN()  == 81);
         assertTrue("fault sample of the second  sine  has number 80" , recursivePhasor2.getN()  == 80);
         assertTrue("fault sample of the third   sine  has number 80" , recursivePhasor3.getN()  == 80);
              
     } 
   public void findErroneousSampe( Path pathToFile,int functionNumber,Function<Double,Boolean> faultDetection) throws IOException{
          /**
           * Snippet chooses value belongs desirable  signal  (signal with number functionNumber), 
           * then  estimates of value, filters null values  and if fault is detected, stops the stream.
           */  

           Files.lines(pathToFile, StandardCharsets.UTF_8) 
                  .map(line->{
                     return new Double( line.split(",")[functionNumber+1] );
                   })
                  .map(faultDetection)
                  .filter(fault->fault!=null)
                  .anyMatch(fault->fault);
        
     }
}
