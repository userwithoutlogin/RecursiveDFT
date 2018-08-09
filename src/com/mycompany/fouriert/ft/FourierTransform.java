/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.ft;

import com.mycompany.fouriert.complex.Complex;
import java.util.List;

/**
 *
 * @author root
 */
public interface FourierTransform {
// public  Double reverse( Complex  spectrumSample);
    public  Complex direct(Double timeSamples);
}
