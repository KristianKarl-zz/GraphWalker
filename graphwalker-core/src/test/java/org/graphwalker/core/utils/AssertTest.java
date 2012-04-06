/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 - 2012 GraphWalker
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package org.graphwalker.core.utils;

import org.junit.Test;

import java.util.*;

public class AssertTest {

    @Test
    public void assertTrue() {
        Assert.assertTrue(true);
    }

    @Test(expected = AssertionError.class)
    public void assertTrueWithFalse() {
        Assert.assertTrue(false);
    }

    @Test
    public void assertTrueWithMessage() {
        try {
            Assert.assertTrue(false, "Test assertTrue with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test assertTrue with message")) {
                throw new AssertionError("Bad message");
            }
        }
    }

    @Test
    public void assertFalse() {
        Assert.assertFalse(false);
    }

    @Test(expected = AssertionError.class)
    public void assertFalseWithTrue() {
        Assert.assertFalse(true);
    }

    @Test
    public void assertFalseWithMessage() {
        try {
            Assert.assertTrue(false, "Test assertFalse with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test assertFalse with message")) {
                throw new AssertionError("Bad message");
            }
        }
    }

    @Test(expected = AssertionError.class)
    public void fail() {
        Assert.fail();
    }

    @Test
    public void failWithMessage() {
        try {
            Assert.fail("Test fail with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test fail with message")) {
                throw new AssertionError("Bad message");
            }
        }
    }

    @Test
    public void failWithCause() {
        try {
            Assert.fail("Test fail with message and cause", new RuntimeException());
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test fail with message and cause")) {
                throw new AssertionError("Bad message");
            }
            if (!e.getCause().getClass().equals(RuntimeException.class)) {
                throw new AssertionError("Bad cause");
            }
        }
    }

    @Test
    public void assertNull() {
        Assert.assertNull(null);
    }

    @Test(expected = AssertionError.class)
    public void assertNullWithObject() {
        Assert.assertNull(new Object());
    }

    @Test
    public void assertNullWithMessage() {
        try {
            Assert.assertNull(new Object(), "Test assertNull with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test assertNull with message")) {
                throw new AssertionError("Bad message");
            }
        }
    }

    @Test
    public void assertNotNull() {
        Assert.assertNotNull(new Object());
    }

    @Test(expected = AssertionError.class)
    public void assertNotNullWithNoObject() {
        Assert.assertNotNull(null);
    }

    @Test
    public void assertNotNullWithMessage() {
        try {
            Assert.assertNull(new Object(), "Test assertNotNull with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test assertNotNull with message")) {
                throw new AssertionError("Bad message");
            }
        }
    }

    @Test
    public void assertSame() {
        Object object = new Object();
        Assert.assertSame(object, object);
    }

    @Test(expected = AssertionError.class)
    public void assertSameWithNotSame() {
        Assert.assertSame(new Object(), new Object());
    }

    @Test
    public void assertSameWithMessage() {
        try {
            Assert.assertSame(new Object(), new Object(), "Test assertSame with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test assertSame with message")) {
                throw new AssertionError("Bad message");
            }
        }
    }

    @Test(expected = AssertionError.class)
    public void assertNotSame() {
        Object object = new Object();
        Assert.assertNotSame(object, object);
    }

    @Test
    public void assertNotSameWithNotSame() {
        Assert.assertNotSame(new Object(), new Object());
    }

    @Test
    public void assertNotSameWithMessage() {
        try {
            Object object = new Object();
            Assert.assertNotSame(object, object, "Test assertSame with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test assertSame with message")) {
                throw new AssertionError("Bad message");
            }
        }
    }

    @Test
    public void assertEquals() {
        Object object = new Object();
        Assert.assertEquals(object, object);
        Assert.assertEquals("assert Equals", "assert Equals");
        Assert.assertEquals(0.1d, 0.11d, 0.01d);
        Assert.assertEquals(0.1f, 0.11f, 0.01f);
        Assert.assertEquals(100L, 100L);
        Assert.assertEquals(true, true);
        Assert.assertEquals((byte) 8, (byte) 8);
        Assert.assertEquals('a', 'a');
        Assert.assertEquals((short) 1, (short) 1);
        Assert.assertEquals(1000, 1000);
        Assert.assertEquals(new byte[]{(byte) 1, (byte) 2, (byte) 3}, new byte[]{(byte) 1, (byte) 2, (byte) 3});
        Set<String> actualSet = new HashSet<String>();
        actualSet.add("value");
        Set<String> expectedSet = new HashSet<String>();
        expectedSet.add("value");
        Assert.assertEquals(actualSet, expectedSet);
        Map<String, String> actualMap = new HashMap<String, String>();
        actualMap.put("key", "value");
        Map<String, String> expectedMap = new HashMap<String, String>();
        expectedMap.put("key", "value");
        Assert.assertEquals(actualMap, expectedMap);
        Collection<String> actualCollection = new ArrayList<String>();
        actualCollection.add("value1");
        actualCollection.add("value2");
        Collection<String> expectedCollection = new ArrayList<String>();
        expectedCollection.add("value1");
        expectedCollection.add("value2");
        Assert.assertEquals(actualCollection, expectedCollection);
        Assert.assertEquals(new Object[]{object}, new Object[]{object});
    }

    @Test
    public void assertEqualsWithMessages() {
        try {
            Assert.assertEquals(new Object(), new Object(), "Test with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test with message")) {
                throw new AssertionError("Bad message");
            }
        }
        try {
            Assert.assertEquals("assert Equals", "assert not Equals", "Test with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test with message")) {
                throw new AssertionError("Bad message");
            }
        }
        try {
            Assert.assertEquals(0.1d, 0.11d, 0.001d, "Test with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test with message")) {
                throw new AssertionError("Bad message");
            }
        }
        try {
            Assert.assertEquals(0.1f, 0.11f, 0.001f, "Test with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test with message")) {
                throw new AssertionError("Bad message");
            }
        }
        try {
            Assert.assertEquals(100L, 1L, "Test with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test with message")) {
                throw new AssertionError("Bad message");
            }
        }
        try {
            Assert.assertEquals(true, false, "Test with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test with message")) {
                throw new AssertionError("Bad message");
            }
        }
        try {
            Assert.assertEquals((byte) 8, (byte) 16, "Test with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test with message")) {
                throw new AssertionError("Bad message");
            }
        }
        try {
            Assert.assertEquals('b', 'a', "Test with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test with message")) {
                throw new AssertionError("Bad message");
            }
        }
        try {
            Assert.assertEquals((short) 2, (short) 1, "Test with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test with message")) {
                throw new AssertionError("Bad message");
            }
        }
        try {
            Assert.assertEquals(1000, 1, "Test with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test with message")) {
                throw new AssertionError("Bad message");
            }
        }
        try {
            Assert.assertEquals(new byte[]{(byte) 1, (byte) 2, (byte) 3}, new byte[]{(byte) 3, (byte) 2, (byte) 1}, "Test with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test with message")) {
                throw new AssertionError("Bad message");
            }
        }
        try {
            Set<String> actualSet = new HashSet<String>();
            actualSet.add("value");
            Set<String> expectedSet = new HashSet<String>();
            expectedSet.add("another value");
            Assert.assertEquals(actualSet, expectedSet, "Test with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test with message")) {
                throw new AssertionError("Bad message");
            }
        }
        try {
            Map<String, String> actualMap = new HashMap<String, String>();
            actualMap.put("key", "value");
            Map<String, String> expectedMap = new HashMap<String, String>();
            expectedMap.put("differentKey", "value");
            Assert.assertEquals(actualMap, expectedMap, "Test with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test with message")) {
                throw new AssertionError("Bad message");
            }
        }
        try {
            Collection<String> actualCollection = new ArrayList<String>();
            actualCollection.add("value");
            actualCollection.add("anotherValue");
            Collection<String> expectedCollection = new ArrayList<String>();
            expectedCollection.add("value");
            expectedCollection.add("yetAnotherValue");
            Assert.assertEquals(actualCollection, expectedCollection, "Test with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test with message")) {
                throw new AssertionError("Bad message");
            }
        }
        try {
            Assert.assertEquals(new Object[]{new Object()}, new Object[]{new Object()}, "Test with message");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test with message")) {
                throw new AssertionError("Bad message");
            }
        }
    }

    @Test
    public void assertEqualsFailures() {
        try {
            Assert.assertEquals(new Object(), new Object());
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            // do nothing
        }
        try {
            Assert.assertEquals("assert Equals", "assert not Equals");
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            // do nothing
        }
        try {
            Assert.assertEquals(0.1d, 0.11d, 0.001d);
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            // do nothing
        }
        try {
            Assert.assertEquals(0.1f, 0.11f, 0.001f);
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            // do nothing
        }
        try {
            Assert.assertEquals(100L, 1L);
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            // do nothing
        }
        try {
            Assert.assertEquals(true, false);
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            // do nothing
        }
        try {
            Assert.assertEquals((byte) 8, (byte) 16);
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            // do nothing
        }
        try {
            Assert.assertEquals('b', 'a');
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            // do nothing
        }
        try {
            Assert.assertEquals((short) 2, (short) 1);
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            // do nothing
        }
        try {
            Assert.assertEquals(1000, 1);
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            // do nothing
        }
        try {
            Assert.assertEquals(new byte[]{(byte) 1, (byte) 2, (byte) 3}, new byte[]{(byte) 3, (byte) 2, (byte) 1});
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            // do nothing
        }
        try {
            Set<String> actualSet = new HashSet<String>();
            actualSet.add("value");
            Set<String> expectedSet = new HashSet<String>();
            expectedSet.add("another value");
            Assert.assertEquals(actualSet, expectedSet);
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            // do nothing
        }
        try {
            Map<String, String> actualMap = new HashMap<String, String>();
            actualMap.put("key", "value");
            Map<String, String> expectedMap = new HashMap<String, String>();
            expectedMap.put("differentKey", "value");
            Assert.assertEquals(actualMap, expectedMap);
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            // do nothing
        }
        try {
            Collection<String> actualCollection = new ArrayList<String>();
            actualCollection.add("value");
            actualCollection.add("anotherValue");
            Collection<String> expectedCollection = new ArrayList<String>();
            expectedCollection.add("value");
            expectedCollection.add("yetAnotherValue");
            Assert.assertEquals(actualCollection, expectedCollection);
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            // do nothing
        }
        try {
            Assert.assertEquals(new Object[]{new Object()}, new Object[]{new Object()});
            throw new VerifyError("Test failed");
        } catch (AssertionError e) {
            // do nothing
        }
    }

    @Test
    public void assertEqualsNoOrder() {
        Assert.assertEqualsNoOrder(new Integer[]{1, 2, 3}, new Integer[]{3, 2, 1});
    }

    @Test(expected = AssertionError.class)
    public void assertEqualsNoOrderFailure() {
        Assert.assertEqualsNoOrder(new Integer[]{1, 2, 3}, new Integer[]{2, 4, 1});
    }
}
