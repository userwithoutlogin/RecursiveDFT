/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.functions;

import com.mycompany.fouriert.complex.Complex;
import com.mycompany.fouriert.ft.FourierTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author andrey_pushkarniy
 */
public class Generator {
     
     private final FourierTransform fourierTransform;
     List<Complex> spectrumSamples;
     Stream generator ;
     Function sine ;

    public Generator(FourierTransform fourierTransform, Function sine) {
        this.fourierTransform = fourierTransform;
        this.sine = sine;
        spectrumSamples = new ArrayList();
    }
    
    public void start(){
         Stream.generate(()->{
            return sine.calc();
         }).limit(25).forEach(timeSample->{ 
             System.out.println("sine "+timeSample);
                spectrumSamples.add(fourierTransform.direct(timeSample));             
         });
    }

    public List<Complex> getSpectrumSamples() {
        return spectrumSamples;
    }
    
    
}
