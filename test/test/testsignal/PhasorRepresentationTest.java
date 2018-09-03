/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.testsignal;

import utils.CosineFunction;
 
 
import com.mycompany.fouriert.phasor.RecursiveDFT;
import com.mycompany.fouriert.utils.Complex;
import java.util.List;
 
import java.util.stream.Collectors;
import org.junit.Test;
import static org.junit.Assert.*;
import utils.Utils;

/**
 *
 * @author andrey_pushkarniy
 */
public class PhasorRepresentationTest {
      /* 
         WINDOW_WIDTH     - размер окна фазора 
         NOMINAL_FREQUECY - номинальная частота
      */
      final int    WINDOW_WIDTH = 24;      
      final double NOMINAL_FREQUECY = 50.0;   
      
     @Test
     public void phasorsOnNominalFrequency(){
        /**
          precision          - if difference between two double values less than precision , this numbers are equals
          frequencyDeviation - frequency deviation from nominal frequency
          amplitude          - amplitude of tested signal
          phase              - phase shift of tested signal
          recursiveDFT       - it performs discrete Fourier transform(DFT) with recursive update of estimation
          cosine             - creates samples of tested signal 
          limitPointNumbers  - count points of sine
        */                 
        double precision          = 1e-13;
        double frequencyDeviation = 0.0;
        double amplitude          = 100.0;
        double phase              = Math.PI/4;
        int limitPointNumbers     = 36;
        
        double[]  sinArray  = new double[WINDOW_WIDTH];
        double[]  cosArray  = new double[WINDOW_WIDTH];
        
        for(int i=0;i<cosArray.length;i++){
            cosArray[i] =  Math.cos( i  * 2.0 * Math.PI / cosArray.length );  
            sinArray[i] =  Math.sin( i  * 2.0 * Math.PI / cosArray.length );  
        }
        
        
        RecursiveDFT recursiveDFT =  new RecursiveDFT(cosArray,sinArray);
        CosineFunction cosine = new CosineFunction(amplitude,phase  ,WINDOW_WIDTH ,NOMINAL_FREQUECY);        
        List<Double> samples = Utils.generateSamples(cosine,limitPointNumbers,frequencyDeviation);
        
        List<Complex> phasors = Utils.getPhasors(samples,recursiveDFT).stream()
                                    .filter(phasor->phasor!=null)
                                    .collect(Collectors.toList());
              
        
        // Amplitude and phase are phaser representation 
        assertTrue("Phase must be constant and equals to pi/4 for all samples on nominal frequency 50Hz",
                isPhaseConstant(phase  ,  phasors,   precision)
        );  
        assertTrue("Amplitude must be constant and equals to 100/sqrt(2) for all samples on nominal frequency 50Hz",
                isAmplitudeConstant(100/Math.sqrt(2),  phasors,  precision)
        );        
     }
    
     
     
     public boolean isPhaseConstant(double phase,List<Complex> spectrumSamples,double precision){
         /* 
           Фазы всех спектральных отсчетов (spectrumSamples) должны быть постоянными и
           совпадать со значением (phase) с заданной точностью  (precision) при номинальной частоте сигнала
         */  
         return spectrumSamples.stream()
                    .map(sample->sample.getArg())
                    .allMatch(arg->Utils.compareFPNumbers(arg,phase,precision) );
     }
     public boolean isAmplitudeConstant(double amplitude,List<Complex> spectrumSamples,double precision){
       /* 
         Амплитуды всех спектральных отсчетов (spectrumSamples) должны быть постоянными и
         совпадать со значением (amplitude) с заданной точностью  (precision) при номинальной частоте сигнала
       */  
         return spectrumSamples.stream()
                    .map(sample->sample.getAmplitude())
                    .allMatch(arg->Utils.compareFPNumbers(arg,amplitude,precision) );
     }
     
}
