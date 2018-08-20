/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.utils;

/**
 *
 * @author andrey_pushkarniy
 */
public class FPCompensation {
     double error;
    
    public double sum(double a,double b, boolean isNull){
        double x = a+b;
        double b_virt = x-a;
        double a_virt = x - b_virt;
        double b_roundoff = b-b_virt;
        double a_roundoff = a-a_virt;
        double y = a_roundoff-b_roundoff;
        if(isNull)
            error = y;
        else 
            error +=y;
        return x;
    }
    
    public static void main(String[] args) {
        FPCompensation fp = new FPCompensation();
//        double result = 0.0;
//        double val = 2.7892;
//        val = val/10000000000.0;
//        double result1 = 0.0;
//        double val1 = 2.7892;
//        val1 = val1/10000000000.0;
//        
//        for(long  i = 0;i<10000000000L;i++){
//            result = fp.sum(result,val,false);
//            result1 +=val1; 
//        }
//        
//        System.out.println(result);
//        System.out.println(fp.error);
//        System.out.println("----------------");
//        System.out.println(result1);
        double y = 0.1+0.3+0.07 ;
       
        double y1 = 0.0;
         y1 = fp.sum(y1, 0.1, false);
        y1 = fp.sum(y1, 0.3, false);
        y1 = fp.sum(y1, 0.07, false);
        
        System.out.println(y);
        System.out.println("----------------------");
        System.out.println(y1-fp.error);
        System.out.println(fp.error);
        
    }
}
