/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.functions;

import com.mycompany.fouriert.utils.Complex;
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
        private Double delta = 1.0;
        private Double freqNom = 50.0;
        private Integer windowWidth;
         
    public CosineFunction(double amplitude, double phase,int windowWidth,double freqNom) {
        this.a = amplitude;
      this.delta = delta;
        this.fi = phase;
         this.freqNom = freqNom;
        this.windowWidth =  windowWidth;
    }   
    
      
    
    
        @Override
    public Double calc(  double df) {    
        double cos  =  a * Math.cos(x*2.0*Math.PI*((df+freqNom)/freqNom)/windowWidth + fi)  ;    
        x+= delta;
        return cos;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public void setDelta(Double delta) {
        this.delta = delta;
    }

    public void setFreqNom(Double freqNom) {
        this.freqNom = freqNom;
    }
    
    
}
