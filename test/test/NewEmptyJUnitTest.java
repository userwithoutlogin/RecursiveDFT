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
public class NewEmptyJUnitTest {
     RecursiveDiscreteTransform fourierTransform;
     List<Complex> spectrumSamples;
     Function cosine ;
     Function cosine1 ;
      Generator generator;
      double precision = Math.pow(10, -4);
      TransientMonitor monitor ;
    public NewEmptyJUnitTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        //                                             windowWidth , f nominal        
        fourierTransform =  new RecursiveDiscreteTransform(24,       60.0);
        monitor = new TransientMonitor(24);
        fourierTransform.setMonitor(monitor);
        //                            A     fi          fnom  deta   x0
//        cosine = new CosineFunction(100.0,Math.PI/4  ,   60.0, 1.0  ,0.0 );
//        cosine1 = new CosineFunction(50.0,Math.PI/8  ,  60.0, 1.0  ,36.0);
        spectrumSamples = new ArrayList();
//        generator = new Generator(fourierTransform,cosine,cosine1); 
//                                    start end   delta       
//        generator.initFreqDeviations(-0.5, 0.5, 0.05 );
//        generator.start();
         
    }
    
    @After
    public void tearDown() {
        
    }
     
//     @Test
//     public void output() {
//         List<Complex> specSamples = generator.getSpectrumSamples();
//         List<Double> est = generator.getEstimates();
//            for(int i=0;i<specSamples.size();i++){
//                System.out.println((i+1)+": "+specSamples.get(i)+"   ampl: "+specSamples.get(i).amplitude()+"   arg: "+specSamples.get(i).arg()+" Tn: "+est.get(i));
//            }            
//     }
     
     
     @Test
     public void phaseTest(){
         assertTrue("complex number with the phase pi/4 exists", phaseExists(Math.PI/4));
         assertTrue("complex number with the phase pi/4 exists", phaseExists(Math.PI/8));
     }
     @Test
     public void amplitudeTest(){
         assertTrue("complex number with the amplitude 50 exists", amplitudeExists(50/Math.sqrt(2)));
         assertTrue("complex number with the amplitude 100 exists", amplitudeExists(100/Math.sqrt(2)));
     }
     public boolean phaseExists(double value){
         List<Complex> samples =  generator.getSpectrumSamples();
         return samples.stream()
                    .map(sample->sample.arg())
                    .anyMatch(arg->compareFPNumbers(arg,value) );
     }
     public boolean amplitudeExists(double value){
         List<Complex> samples =  generator.getSpectrumSamples();
         return samples.stream()
                    .map(sample->sample.amplitude())
                    .anyMatch(arg->compareFPNumbers(arg,value) );
     }
     public boolean compareFPNumbers(double n1,double n2){
        return Math.abs(n1-n2)<precision;
     }
     
      
}
