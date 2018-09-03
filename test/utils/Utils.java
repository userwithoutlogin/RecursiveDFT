/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

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
import java.util.stream.IntStream;
import test.realsignal.PhasorTest;

/**
 *
 * @author root
 */
public class Utils {
    /**
     * PATH_TO_FILE - path to file where locates real signal samples 
     */
    public static final String PATH_TO_FILE = "test/resources/realsine.txt";
    
     public static List<Complex> getPhasors(List<Double> samples,Function<Double,Complex> recursivePhasor){
          List<Complex> phasors = null;
         
         /**
          * It loads first 24 samples, and applying phasor to them
          */
         phasors = samples.stream()
                    .map(recursivePhasor)
                 .collect(Collectors.toList());

         return phasors;
      }
     public static List<Double>  getSamplesFromFile(Path path,int signalIndex,int limit){
         List<Double> samples = null;
          
          /**
           * It loads  first 24 samples, and applying phasor to them
           */ 
          Path pathToFile = Paths.get("./test/resources/realsine.txt").toAbsolutePath().normalize();
          
          System.out.println(pathToFile);
          try {
              samples =  Files.lines(path , StandardCharsets.UTF_8)
                      .map(line->{
                          return new Double( line.split(",")[1+signalIndex] );
                      })
                      .limit(limit)
                      .collect(Collectors.toList());
                      
          } catch (IOException ex) {
              Logger.getLogger(PhasorTest.class.getName()).log(Level.SEVERE, null, ex);
          }
          return samples;
     }
     public static List<Double>  resample(List<Double> samples,Function<Double,Double> resamplingFilter){
         return samples.stream()
                 .map(resamplingFilter)
                 .collect(Collectors.toList());
     }
     public static boolean compareFPNumbers(double n1,double n2,double precision){
      // function of comparison of two floating point numbers       
       return Math.abs(n1-n2)<precision;
     }
     public static boolean isPhaseShiftInRange(double from, double to, List<Double> phaseShifts) {
        return phaseShifts.stream()
                .allMatch(shift->{
                    return shift >= 110 && shift <=129;
                });
    }
     public static List<Double> generateSamples(CosineFunction  cosine,int pointsCount,double df){
         List<Double> list= new ArrayList();
         IntStream.range(0, pointsCount).forEach(i->{
             list.add(cosine.calc(df));
         });
         return list;
     }
    
 
}
