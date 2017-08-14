package com.cloud.common.utils;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by voron on 21.04.2017.
 */
public class RandomizerTest {

    @Test
    public void randomString() throws Exception {
        Set<String> generatedStrings = new HashSet<>();

        for (int i=0; i< 1000000; i++){
            String generated = Randomizer.randomString(10);
            if (generatedStrings.contains(generated)){
                throw new IllegalArgumentException("duplicated value: "+generated+" on iteration: "+i);
            } else {
                generatedStrings.add(generated);
            }

            if (i%100000 == 0) {
                System.out.println("Made: "+i);
            }
        }
    }

}