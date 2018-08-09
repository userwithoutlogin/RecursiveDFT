/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.ft;

import com.mycompany.fouriert.complex.Complex;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
 
import java.util.LinkedList;

/**
 *
 * @author root
 */
public class RecoursiveDiscreteTransform implements FourierTransform{
      
     SampleGenerator generator =new SampleGenerator();
     private LinkedList<Double> buffer;
     boolean r = true;
      int n = 0;
     private Integer width; 
      Complex spectSam = new Complex(0.0,0.0);
      
     
    public RecoursiveDiscreteTransform(Integer width) {                  
        this.width = width;
       
        initBuffer() ;
       
        
    }
         
      public void shift(double timeSample) {  
            double normalize = Math.sqrt(2)/width;
             Complex e = Complex.initByEuler(1,- n*Math.PI/12 );
             
          if(width-1>buffer.size()){
             buffer.add(timeSample);
             n++;
          }
          else{
               Complex addition2 =new Complex(0.0,0.0);
                
              if(r){
                   buffer.add( timeSample); 
                  addition2 = generator.spectrumSample(buffer.size(), n, buffer).multiply(normalize); 
                   
              }
              else{
                   double deletedSample = buffer.remove(0);
                 buffer.add( timeSample); 
                  addition2 = e.multiply( timeSample-deletedSample).multiply(normalize); 
              }
              r=false;
           
         spectSam =  spectSam.add(addition2) ;
          n++;
       }
    }
 
    
    public Complex getSample(){
//        double normalize = Math.sqrt(2)/width;
////        Complex n_1 = generator.spectrumSample(buffer.size(), n, buffer);
//       // n_1 = n_1.multiply(normalize);
//        
//        Complex addition = Complex.initByEuler(1,-2*Math.PI).multiply(
//              buffer.getLast()-buffer.getFirst()
//        );
//        addition = addition.multiply(normalize);
        
//        Complex sample = new Complex(0.0,0.0);
//        
//        for (Complex b : buffer) 
//            sample = sample.add(b);
//        
//        if(prevSpecSample!= null)
//            sample.add(prevSpecSample);
//       prevSpecSample = sample;  
        return spectSam;        
    }
    
    
     @Override
    public Complex direct(Double timeSample) {
        Complex spectrumSample = new Complex(0.0,0.0);
        shift(timeSample);
        return spectrumSample.add( getSample());  
    }
    
    public void initBuffer(){
//        Complex spectrumSample = new Complex(0.0,0.0);
        buffer = new LinkedList();
//        for(int k=0;k<width;k++){
//           buffer.add(0.0);                
//        }
    }
}
