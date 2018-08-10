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
public class CosineFunction implements Function {
        private final Double a;
        
        private final Double fi;
        private Double x = 0.0;
        private Double delta;
     

    public CosineFunction(double amplitude, double phase,double delta,double x0) {
        this.a = amplitude;
       this.delta = delta;
        this.fi = phase;
        x = x0;
    }   
    
    public CosineFunction(double amplitude, double phase,double f,double delta,double x0) {
        this.a = amplitude;
       this.delta = delta;
        this.fi = phase;
        x = x0;
    }   
    
    
        @Override
    public Double calc( ) {        
//        double cos  =  a * Math.cos(x*2.0*Math.PI/24 + fi);    
        double cos  =  a * Math.cos(x*2.0*Math.PI/24 + fi);    
        x+= delta;
        return cos;
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
 
}
