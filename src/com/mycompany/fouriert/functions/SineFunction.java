/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.functions;

import com.mycompany.fouriert.complex.Complex;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author andrey_pushkarniy
 */
public class SineFunction implements Function {
        private final Double amplitude;
        private final Double frequency;
        private final Double phase;
        private Double timeArg = 0.0;
        private Integer N = 256;

    public SineFunction(Double amplitude, Double frequency, Double phase) {
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.phase = phase;
    }   
    
    
        @Override
    public Double calc( ) {
        
        double sin  = 3.0 * Math.sin(timeArg +Math.PI)/*+2*Math.sin(10*Math.PI*timeArg)*/;
        timeArg +=Math.PI/6;  
        return sin;
    }
    
    public double getTimeArg(){
          return  timeArg;
    }
    
//    @Override
//    public Iterator getIterator() {
//        return new FunctionIterator();
//    }
      
    
//    private class FunctionIterator implements Iterator{
//         
//        private Double t = 0.0;
//        @Override
//        public boolean hasNext() {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public Object next() {
//            t += 1/frequency;
//            return calcPoint(t);
//        }
//        
//    }

    public Double getFrequency() {
        return frequency;
    }
     public double f(int x){
         return 100*Math.cos(x*Math.PI/12 + Math.PI/4);
     }
     public double f1(int x){
         return 50*Math.cos(x*Math.PI/12 + Math.PI/8);
     }
     public List<Double> getPoints( ){
         List<Double> a = new ArrayList();
         for(int i= 0;i<36;i++)
             a.add(f(i));
         return a;
     }
     public List<Double> getPoints1( ){
         List<Double> a = new ArrayList();
         for(int i= 36;i<72;i++)
             a.add(f1(i));
         return a;
     } 
}
