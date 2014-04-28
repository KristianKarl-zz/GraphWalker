package org.graphwalker.core.common;

import org.junit.Test;

/**
 * @author Nils Olsson
 */
public class AssertTest {

    @Test
    public void assertions() {
        Assert.assertEquals(true, true);
        Assert.assertNotSame("1", "2");
        Assert.assertEquals(true, true, "message");
        Assert.assertNotEquals(1d, 2d, 0.5);
    }
}
