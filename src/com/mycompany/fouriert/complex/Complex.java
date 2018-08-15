/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.complex;

/**
 *
 * @author root
 */
public class Complex  {
    private final Double re;
    private final Double im;
   private Double precision = 1e-10;
   
    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    
    
    public Double arg(){
        return calculateArgOnQuarter();
     }
    public Double amplitude(){
        return Math.sqrt(Math.pow(re,2)+Math.pow(im,2));
    }
    public Complex multiply(double constant){
       return new Complex(re*constant,im*constant);
    }
    public Complex multiply(Complex multiplier){
        
        return new Complex(amplitude()*multiplier.amplitude()*Math.cos(Math.toRadians(arg()+multiplier.arg())),
                            amplitude()*multiplier.amplitude()*Math.sin(Math.toRadians(arg()+multiplier.arg()))
        );
    }
    public Complex add(Complex added){
        return new Complex(re+added.getRe(),im+added.getIm());
    }
    public Complex sub(Complex deleted){
        return new Complex(re-deleted.getRe(),im-deleted.getIm());
    }
    //инициализция и возврат комлексного числа по формуле Эйлера
    public static Complex initByEuler(double module,double arg){
        return new Complex(module*Math.cos(arg),module*Math.sin(arg));
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
//    в какой четверти координатной плоскости находится комплексное число
    private Double calculateArgOnQuarter(){
        if(re>0 && im==0)
            return 0.0; 
        else if(re>0&&im>0)
            return   Math.atan(im/re); 
        else if(re==0&&im>0)
            return Math.PI/2.0; 
        else if(re<0&&im>0)
            return Math.atan(im/re)+Math.PI; 
        else if(re<0&&im==0)
            return Math.PI; 
        else if(re<0&&im<0)
            return Math.atan(im/re)-Math.PI; 
        else if(re==0&&im<0)
            return Math.PI+Math.PI/2; 
        else if(re>0&&im<0)
            return Math.atan(im/re); 
         return 0.0;
    }
    
    @Override
    public String toString() {
        String sign = im<0? " - ":" + ";
        return     String.format("%."+precision+"f",re) + 
                sign +"j"+  String.format("%."+precision+"f",Math.abs(im))  ;
    } 

    public boolean isEqual(Complex c){
      boolean reEq = Math.abs(re-c.re)<precision;
      boolean imEq = Math.abs(im-c.im)<precision;
      return reEq&&imEq;
    }   
    
    
}
