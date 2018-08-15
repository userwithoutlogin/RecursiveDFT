/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.functions;

import com.mycompany.fouriert.complex.Complex;

/**
 *
 * @author root
 */
public class NewClass {
    public static void main(String[] args) {
        int windowWidth = 24;
        double fNom=60.0;
        for(double df = -5.0;df<=5;df+=0.5){
        double sinQ = Math.sin((2.0*Math.PI/windowWidth)*windowWidth*((2.0*fNom+df)/(2.0*fNom)))/
                (windowWidth*Math.sin((2.0*Math.PI/windowWidth)*((2.0*fNom+df)/(2.0*fNom))));
        Complex p = Complex.initByEuler(1, -(2.0*Math.PI/windowWidth)*((2.0*fNom+df)/(fNom))*((windowWidth-1.0)/2.0))
                .multiply(sinQ); 
            System.out.println(p.amplitude()+"   "+(p.arg()*180/Math.PI)+"  "+p);
        }
//        
//        double sinQ = Math.sin((2.0*Math.PI/windowWidth)*windowWidth*((2.0*fNom+df)/(2.0*fNom)))/
//                (windowWidth*Math.sin((2.0*Math.PI/windowWidth)*((2.0*fNom+df)/(2.0*fNom))));
//        Complex q = Complex.initByEuler(1, -(2.0*Math.PI/windowWidth)*((2.0*fNom+df)/(fNom))*((windowWidth-1)/2))
//                .multiply(sinQ);
    }
}
