/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.mycompany.fouriert.utils.RoundingErrorCompensator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author andrey_pushkarniy
 */
public class RoundingErrorTest {
    
    public RoundingErrorTest() {
    }
    
     @Test
     public void testOfTwoValuesSummation(){
          /**
           * withErrorCollect    - result of summation with rounding error compensating
           * withoutErrorCollect - result of summation without rounding error compensating
           * addition            - addition for withErrorCollect and withoutErrorCollect
           * compensator         - perform summaion and collects runding error 
           */
          double withErrorCollect    = 0.0;
          double withoutErrorCollect = 0.0;
          double addition = 0.1;
          RoundingErrorCompensator compensator = new RoundingErrorCompensator();
          
          
         for (int i = 0; i < 140000000; i++) {
             withErrorCollect     = compensator.sum(withErrorCollect, addition);
             withoutErrorCollect +=  addition;
         }
         withErrorCollect+=compensator.getError();
        /**
         * Is not right to compare double values using '==' operator ,
         * but in this test '==' uses in order to show that value with compensated error
         * precisely equals to expected value and value without compensated error
         * not equals to expected value 
         */
         assertTrue("Considering collected error we obtain the value, "
                 + "which precisely equals to expected value",withErrorCollect == 14000000);
         
         assertFalse("Not considering collected error we obtain the value, "
                 + "which not equals to expected value",withoutErrorCollect == 14000000);
     }
     
}
