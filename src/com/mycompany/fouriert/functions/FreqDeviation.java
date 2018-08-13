/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.functions;

import com.mycompany.fouriert.complex.Complex;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author andrey_pushkarniy
 */
public class FreqDeviation {
    
    public static void main(String[] args) {
        List<Complex> p = new ArrayList();
        double dt = 2*Math.PI/24;
        Complex  c =null;
        for(double df = -5.0;df<=5;df+=0.5){
            double sin = Math.sin(24.0*2.0*Math.PI*(df-60)*(1/df)/2.0)  /  (24.0*Math.sin(24.0*2*Math.PI*(df-60)*(1/df)/2.0));
            Complex y= Complex.initByEuler(1, 23*2*Math.PI*(df-60)*dt/2.0);
            p.add(y);
            System.out.println(y.arg());
        }
    }
}
