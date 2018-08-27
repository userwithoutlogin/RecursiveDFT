/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.ft;

import com.mycompany.fouriert.utils.Complex;
import java.util.List;
import java.util.function.Consumer;

/**
 *
 * @author root
 */
public interface FourierTransform extends Consumer{
// public  Double reverse( Complex  spectrumSample);
    public  Complex direct(Double timeSamples);
}
