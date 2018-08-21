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
public class RoundingErrorCompensator {
    /**
     * Rounding error
     */
      double error;

    /**
     * Function produces summation and collect rounding error.
     * Rounding error greater than zero when the result of summation is less than expected
     * and vice versa, hence  in order to obtain expected result it nesessary to add this error 
     * to result of summation.
     * @param  a - first additive component
     * @param  b - second additive component
     * @return x - result of addition with rounding error
     */
    public double sum(double a, double b) {
        /**
         * b_virtual  - term 'b' plus error which it brings 
         * a_virtual  - term 'a' plus error which it brings 
         * b_roundoff - subtrahend between original term(b) and term(b_virtual) 
         * with error 
         * a_roundoff - subtrahend between original term(a) and term(a_virtual) 
         * with error 
         * y          - is the result of summation of rounding errors 
         * which are brought terms 'a' and 'b'
         */
        double x = a + b;
        double b_virtual  =  x - a;
        double a_virtual  =  x - b_virtual;
        double b_roundoff =  b - b_virtual;
        double a_roundoff =  a - a_virtual;
        double y = a_roundoff + b_roundoff;
        error += y;
        return x;
    }

    public double getError() {
        return error;
    }
       
}
