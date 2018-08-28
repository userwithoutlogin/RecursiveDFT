/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.phasor;

import java.util.function.Consumer;

/**
 *
 * @author andrey_pushkarniy
 */
public interface Phasor extends Consumer {
    void updatePhasorEstimate(double newTimeSample);
    
}
