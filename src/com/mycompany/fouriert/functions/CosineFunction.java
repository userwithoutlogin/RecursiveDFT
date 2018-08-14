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
        private Double freqNom;
        

    public CosineFunction(double amplitude, double phase,double delta, double x0) {
        this.a = amplitude;
      this.delta = delta;
        this.fi = phase;
        x = x0;
    }   
    
    public CosineFunction(double amplitude, double phase,double freqNom,double delta, double x0) {
        this.a = amplitude;
       this.delta = delta;
        this.fi = phase;
        this.freqNom = freqNom;
        x = x0;
    }   
    
    
        @Override
    public Double calc(  double df) {    
        double cos  =  a * Math.cos(x*2.0*Math.PI*((df+freqNom)/freqNom)/24 + fi);    
        x+= delta;
        return cos;
    }
    
 
}
