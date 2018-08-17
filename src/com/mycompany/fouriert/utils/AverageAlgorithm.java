/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.fouriert.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author andrey_pushkarniy
 */
public class AverageAlgorithm {
    public static List<Double> threePoint(List<Double> points){
        List<Double> averagedPoints = new ArrayList();
        IntStream.range(0, points.size()).forEach(i->{
            if(i<points.size()-2)
                averagedPoints.add(points.subList(i, i+3)
                    .stream()
                    .mapToDouble(point->point).average()
                    .getAsDouble());
        });
        return averagedPoints;                    
    }
    
}
