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
import utils.CosineFunction;
import utils.Utils;
/**
 *
 * @author andrey_pushkarniy
 */
public class PhasorTest {
      /* 
         WINDOW_WIDTH     - phasor`s window size 
       */
      final int    WINDOW_WIDTH = 24;      
       
      @Ignore(value = "true")
      @Test
      public  void phasorObtaining(){
         /**
          * recursiveDFT             - it performa discrete Fourier transform(DFT) with recursive update of estimation
          * cosArray(sinArray)       - sines(cosines) values which are calculated for 24 points of window in advance. 
          * Because sine(cosine) function is periodic.
          * resamplingFilter1(..2)   - performs recalculate time samples, changing distanse between them.
          * samples                  - samples of real signal
          * phasors                  - phasors of two signals
         */
          double[]  sinArray  = new double[WINDOW_WIDTH];
          double[]  cosArray  = new double[WINDOW_WIDTH];
        
          for(int i=0;i<cosArray.length;i++){
            cosArray[i] =  Math.cos( i  * 2.0 * Math.PI / cosArray.length );  
            sinArray[i] =  Math.sin( i  * 2.0 * Math.PI / cosArray.length );  
          }
         
         
          Function<Double,Complex> recursiveDFT = new RecursiveDFT(cosArray,sinArray );
          Path pathToFile = Paths.get(Utils.PATH_TO_FILE).toAbsolutePath().normalize();
          
          List<Double> samples = Utils.getSamplesFromFile(pathToFile, 1, WINDOW_WIDTH);
          List<Complex> phasors = Utils.getPhasors(samples, recursiveDFT);
          
          assertTrue("The first phasor, obtained from  recursivePhasor,when buffer has just been filled,"
                  + " must be equals 3272.18 -j 1630.63 ", 
                  phasors.get(0).equals(new Complex(3272.17832498552,-1630.6305607908655)));           
     }
      
     
    
}
