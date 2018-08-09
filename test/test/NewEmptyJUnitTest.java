/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.mycompany.fouriert.complex.Complex;
import com.mycompany.fouriert.ft.FourierTransform;
import com.mycompany.fouriert.ft.RecoursiveDiscreteTransform;
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
     FourierTransform fourierTransform;
     List<Complex> spectrumSamples;
     Function cosine ;
     Function cosine1 ;
      Generator generator;
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
        fourierTransform =  new RecoursiveDiscreteTransform(24);
        cosine = new CosineFunction(100.0,Math.PI/4 ,1.0 ,0.0);
        cosine1 = new CosineFunction(50.0,Math.PI/8 ,1.0 ,36.0);
        spectrumSamples = new ArrayList();
    }
    
    @After
    public void tearDown() {
        
    }
     
     @Test
     public void hello() {
           
        generator = new Generator(fourierTransform,cosine,cosine1);            
        generator.start();
        
        List<Complex> specSamples = generator.getSpectrumSamples();
            specSamples.forEach(sample->{
                System.out.println(sample+"   ampl: "+sample.amplitude()+"   arg: "+sample.arg());
            });
     }
     
     
}
