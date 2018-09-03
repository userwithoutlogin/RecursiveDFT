package test.realsignal;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.mycompany.fouriert.phasor.RecursiveDFT;
import com.mycompany.fouriert.utils.Complex;
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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import test.realsignal.PhasorTest;
import utils.Utils;

/**
 *
 * @author root
 */
public class PhaseShiftBetweenSignalsTest {
    final String PATH_TO_FILE = "./realsine.txt"; 
    final int    WINDOW_WIDTH = 24;
    public PhaseShiftBetweenSignalsTest() {
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
          
          List<Double> samples1 = Utils.getSamplesFromFile(pathToFile, 1, WINDOW_WIDTH);
          List<Double> samples2 = Utils.getSamplesFromFile(pathToFile, 2, WINDOW_WIDTH);
          
          List<Complex> phasors1 =Utils.getPhasors(samples1, recursivePhasor1).stream()
                                     .filter(phasor->phasor!=null)
                                     .collect(Collectors.toList());
          
          List<Complex> phasors2 = Utils.getPhasors(samples2, recursivePhasor2).stream()
                                     .filter(phasor->phasor!=null)
                                     .collect(Collectors.toList());
          
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
                  Utils.isPhaseShiftInRange(110.0,129.0,phaseShifts)
          );
     }
     
       
}
