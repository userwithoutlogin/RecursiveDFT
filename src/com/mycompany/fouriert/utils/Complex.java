/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.utils;

import com.mycompany.fouriert.utils.*;

/**
 *
 * @author root
 */
public final class Complex  {
    private final double re;
    private final double im;
    private   Double arg;
    private   Double amplitude;
    private Double precision = 1e-13;
   
    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public synchronized Double getArg(){
        return arg == null ? calculateArgOnQuarter() : arg;
     }
    public synchronized  Double getAmplitude(){
      return amplitude == null ? Math.sqrt(Math.pow(re,2)+Math.pow(im,2)) : amplitude;
    }
    public Complex multiply(double constant){
       return new Complex(re*constant,im*constant);
    }
    public Complex multiply(Complex multiplier){
        return new Complex(re*multiplier.re - im*multiplier.im, re*multiplier.im + im*multiplier.re);
    }
    public Complex add(Complex added){
        return new Complex(re+added.re,im+added.im);
    }
    public Complex sub(Complex deleted){
        return new Complex(re-deleted.re,im-deleted.im);
    }
     
    public Complex conjugate(){
        return new Complex(re,-im);
    }
    
    public Double getRe() {
        return re;
    }
    public Double getIm() {
        return im;
    }
    //подсчет аргумента компексного числа в зависимости от того,
    //в какой четверти координатной плоскости находится комплексное число
    private Double calculateArgOnQuarter(){
        if (re > 0 && im == 0) 
            return 0.0;
        else if (re > 0 && im > 0) 
            return Math.atan(im / re);
        else if (re == 0 && im > 0) 
            return Math.PI / 2.0;
        else if (re < 0 && im > 0) 
            return Math.atan(im / re) + Math.PI;
        else if (re < 0 && im == 0) 
            return Math.PI;
        else if (re < 0 && im < 0) 
            return Math.atan(im / re) - Math.PI;
        else if (re == 0 && im < 0) 
            return Math.PI + Math.PI / 2;
        else 
            return Math.atan(im / re);
        
    }
    
    @Override
    public String toString() {
//        String sign = im<0? " - ":" + ";
//        return     String.format("%."+precision+"f",re) + 
//                sign +"j"+  String.format("%."+precision+"f",Math.abs(im))  ;
        String sign = im<0? " - ":" + ";
        return     re + 
                sign +"j"+   Math.abs(im)  ;
    } 

 
    @Override
    public boolean equals(Object obj) {
       Complex complex = (Complex)obj;
       return (Math.abs(re-complex.re)<precision)&&(Math.abs(im-complex.im)<precision);
    }
    
    
}
