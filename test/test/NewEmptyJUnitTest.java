/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.mycompany.fouriert.complex.Complex;
import com.mycompany.fouriert.ft.FourierTransform;
import com.mycompany.fouriert.ft.RecoursiveDiscreteTransform;
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
     Function sine ;
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
        //sine = new SineFunction( 2.0,60.0,Math.PI);
        spectrumSamples = new ArrayList();
//        generator = new Generator(fourierTransform,sine);
//        generator.start();
        
    }
    
    @After
    public void tearDown() {
        
    }
     
     @Test
     public void hello() {
           List<Double> d = getf();
          d.addAll(getf1());
          for(int i=0;i<72;i++){
              Complex c = fourierTransform.direct(d.get(i));
            System.out.println(  i+":  "+d.get(i) +"   "+c+ "      amp= "+c.amplitude());
          }
     
         
        //generator.start();
     }
     
     public List<Double> getf(){
         List<Double> l = new ArrayList();
         for(int i=0;i<36;i++)
             l.add(f(i));
         return l;
     }
     public List<Double> getf1(){
         List<Double> l = new ArrayList();
         for(int i=36;i<72;i++)
             l.add(f1(i));
         return l;
     }
     
     public double f(double x){
         return 100*Math.cos((Math.PI/12)*x+Math.PI/4);
     }
     public double f1(double x){
         return 50*Math.cos(x*(Math.PI/12) + Math.PI/8);
     }
}
