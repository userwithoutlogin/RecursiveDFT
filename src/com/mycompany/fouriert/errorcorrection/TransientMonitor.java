/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.errorcorrection;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author andrey_pushkarniy
 */
public class TransientMonitor {
    private double[][] tn ;
    private double[][] test = new double[24][24];
    private int windowWidth;
    private int k;
    
    public TransientMonitor(int windowWidth) {
        this.windowWidth = windowWidth;
        tn = new double[windowWidth][windowWidth]; 
        initMatrix();
    }
    
    private void initMatrix(){
        
        for(int i=0;i<windowWidth;i++){
             k =i;
            for(int j=0 ;j<windowWidth;j++,k++){
             updateIndex();
            
             tn[i][j] = i ==j ?1.0-(2.0/windowWidth):
                    -(2.0/windowWidth)*Math.cos(k*2.0*Math.PI/windowWidth);
//        
//             tn[i][j] = i ==j ? (2.0/windowWidth):(2.0/windowWidth)*Math.cos(Math.toRadians(k*2.0*Math.PI));
 
                  
              }
           
         }
    }
    public double calculatePhasorEstimateQality(List samples){
         
        double [] monitorVector = getMonitorVector(samples);
        
//        return Arrays.stream(monitorVector)
//                .reduce(0.0,(v1,v2)->Math.abs(v1 + v2));    
        double est =0.0;
        for(int i = 0;i<samples.size();i++){
            est+=  Math.abs( monitorVector[i]);
        }
        return est;
    }
    private double[] getMonitorVector(List timeSamples){
       double[]  errorVector = new double[windowWidth];
       for(int i=0;i<windowWidth;i++){
           double value=0.0;
           for(int t=0;t<windowWidth;t++)
              value+=tn[i][t] * (double)timeSamples.get(t);
           errorVector[i] = value;
        }
       return errorVector;
    }  
    
   
    private int updateIndex( ){
         k =windowWidth > k  ? k++: 0;
         return k;
    }    
    public static void main(String[] args) {
//        TransientMonitor monitor = new TransientMonitor(24);
//       monitor.getMonitorVector(Arrays.asList(1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,
//               1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0));
    }
}
