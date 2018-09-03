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
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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
import utils.Utils;
/**
 *
 * @author andrey_pushkarniy
 */
public class FaultDetectorTest {
    
    final int    WINDOW_WIDTH = 24;
    
    public FaultDetectorTest() {
    }
    
      
     
     @Test
   public void findingFaultSampleInRealSignal(){
         /**
          * monitor1(..2,..3)         - it detects, when sine begins breaking
          * recursivePhasor1(..2,..3) - it performa discrete Fourier transform(DFT) with recursive update of estimation
          * faultDetection1(..2,..3)  - it evaluates if the error exceeds allowable limitation
          * cosArray(sinArray) - sines(cosines) values which are calculated for 24 points in advance. 
          * Because sine(cosine) function is periodic.
          */
        double[]  sinArray  = new double[WINDOW_WIDTH];
        double[]  cosArray  = new double[WINDOW_WIDTH];
        
        for(int i=0;i<cosArray.length;i++){
            cosArray[i] =  Math.cos( i  * 2.0 * Math.PI / cosArray.length );  
            sinArray[i] =  Math.sin( i  * 2.0 * Math.PI / cosArray.length );  
        }
         
         Path pathToFile = Paths.get(Utils.PATH_TO_FILE).toAbsolutePath().normalize();
         TransientMonitor monitor1 = new TransientMonitor(cosArray,sinArray);
         TransientMonitor monitor2 = new TransientMonitor(cosArray,sinArray);
         TransientMonitor monitor3 = new TransientMonitor(cosArray,sinArray);

         RecursiveDFT recursivePhasor1 = new RecursiveDFT(cosArray,sinArray);
         RecursiveDFT recursivePhasor2 = new RecursiveDFT(cosArray,sinArray );
         RecursiveDFT recursivePhasor3 = new RecursiveDFT(cosArray,sinArray );
         
         FaultDetection faultDetection1 = new FaultDetection();
         faultDetection1.setMonitor(monitor1);
         faultDetection1.setRecursiveDFT(recursivePhasor1);
         
         FaultDetection faultDetection2 = new FaultDetection();
         faultDetection2.setMonitor(monitor2);
         faultDetection2.setRecursiveDFT(recursivePhasor2);
         
         FaultDetection faultDetection3 = new FaultDetection();
         faultDetection3.setMonitor(monitor3);
         faultDetection3.setRecursiveDFT(recursivePhasor3);
         
         Integer faultSampleNumber1 = null;
         Integer faultSampleNumber2 = null;
         Integer faultSampleNumber3 = null;
         
         try {
           faultSampleNumber1 = findErroneousSampe(pathToFile,1, faultDetection1);
            faultSampleNumber2 = findErroneousSampe(pathToFile,2, faultDetection2);
            faultSampleNumber3 = findErroneousSampe(pathToFile,3, faultDetection3);
         } catch (IOException ex) {
             Logger.getLogger(PhasorTest.class.getName()).log(Level.SEVERE, null, ex);
         }
          
         assertTrue("fault sample of the first   sine  has number 81" , faultSampleNumber1  == 81);
         assertTrue("fault sample of the second  sine  has number 80" , faultSampleNumber2  == 80);
         assertTrue("fault sample of the third   sine  has number 80" , faultSampleNumber3  == 80);
              
     } 
   
   public int findErroneousSampe( Path pathToFile,int functionNumber,Function<Double,Boolean> faultDetection) throws IOException{
          /**
           * Snippet chooses value belongs desirable  signal  (signal with number functionNumber), 
           * then  estimates of value, filters null values  and if fault is detected, stops the stream.
           */  
  

        BufferedReader reader = null;
        Integer count = 0;
        try {
            reader = new BufferedReader(
                        new InputStreamReader(
                            new FileInputStream(pathToFile.toFile()), Charset.forName("UTF-8")));
            String line;
            while ((line = reader.readLine()) != null) {
                 count++;
                 double sample =  new Double( line.split(",")[functionNumber+1] );
                 Boolean fault = faultDetection.apply(sample);
                 if( fault!=null &&fault)
                     break;
                     
            }
        } catch (IOException e) {
            count = null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // log warning
                }
            }
        }
         return count;  
     }
     
    }
