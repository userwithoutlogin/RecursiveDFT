/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.mycompany.fouriert.complex.Complex;
import com.mycompany.fouriert.errorcorrection.TransientMonitor;
import com.mycompany.fouriert.ft.RecursiveDiscreteTransform;
import com.mycompany.fouriert.functions.CosineFunction;
import com.mycompany.fouriert.functions.Function;
import com.mycompany.fouriert.functions.Generator;
import java.util.ArrayList;
import java.util.List;
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
public class OffNominalFrequencyTest {
      List<Complex> samples; 
      double precision = Math.pow(10, -3);
      int windowWidth = 10;
      
    public OffNominalFrequencyTest() {
        double fNom = 50.0;
        double amplitude = 100.0;
        double phase = Math.PI/4;
        double delta = 1.0;
        double sartPoint = 0.0;
        double frequencyDeviation = 0.00;
        samples = new ArrayList();
       
        RecursiveDiscreteTransform fourierTransform =  new RecursiveDiscreteTransform(windowWidth, fNom);
        Function cosine = new CosineFunction(amplitude,phase  ,    fNom,  delta  ,sartPoint,windowWidth );
        Generator generator = new Generator(fourierTransform,frequencyDeviation,cosine ); 
        generator.start( );    
        samples = generator.getSpectrumSamples();
         
        
    }
     
     @Test
     public void phaseTest(){
         assertTrue("complex number with the phase pi/4 exists", isPhaseConstant(Math.PI/4));         
     }
     @Test
     public void amplitudeTest(){
         assertTrue("complex number with the amplitude 100 exists", isAmplitudeConstant(100/Math.sqrt(2)));
     }
     public boolean isPhaseConstant(double value){
         return samples.subList(windowWidth, samples.size()).stream()
                    .map(sample->sample.arg())
                    .allMatch(arg->compareFPNumbers(arg,value) );
     }
     public boolean isAmplitudeConstant(double value){
         
         return samples.subList(windowWidth, samples.size()).stream()
                    .map(sample->sample.amplitude())
                    .allMatch(arg->compareFPNumbers(arg,value) );
     }
     public boolean compareFPNumbers(double n1,double n2){
         double t   = Math.abs(n1-n2);
                 
        return Math.abs(n1-n2)<precision;
     }
}
