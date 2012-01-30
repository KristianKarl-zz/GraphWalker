package org.graphwalker.example;

import org.graphwalker.core.annotations.After;
import org.graphwalker.core.annotations.Before;

public class ShouldNotFindThisClass {

    @Before
    public void setup() {
        System.out.println("Call the Before method");
    }

    @After
    public void after() {
        System.out.println("Call the After method");
    }
}
