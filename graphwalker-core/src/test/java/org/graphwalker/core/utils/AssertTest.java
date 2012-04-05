package org.graphwalker.core.utils;

import org.junit.Test;

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
        } catch (AssertionError e) {
            if (!e.getMessage().endsWith("Test assertNotNull with message")) {
                throw new AssertionError("Bad message");
            }
        }
    }


    /*
    @Test
    public void assertEquals() {

    }
    */
    /*
    @Test public void assertEquals(Object actual, Object expected) {
    @Test public void assertEquals(String actual, String expected, String message) {
    @Test public void assertEquals(String actual, String expected) {
    @Test public void assertEquals(double actual, double expected, double delta, String message) {
    @Test public void assertEquals(double actual, double expected, double delta) {
    @Test public void assertEquals(float actual, float expected, float delta, String message) {
    @Test public void assertEquals(float actual, float expected, float delta) {
    @Test public void assertEquals(long actual, long expected, String message) {
    @Test public void assertEquals(long actual, long expected) {
    @Test public void assertEquals(boolean actual, boolean expected, String message) {
    @Test public void assertEquals(boolean actual, boolean expected) {
    @Test public void assertEquals(byte actual, byte expected, String message) {
    @Test public void assertEquals(byte actual, byte expected) {
    @Test public void assertEquals(char actual, char expected, String message) {
    @Test public void assertEquals(char actual, char expected) {
    @Test public void assertEquals(short actual, short expected, String message) {
    @Test public void assertEquals(short actual, short expected) {
    @Test public void assertEquals(int actual, int expected, String message) {
    @Test public void assertEquals(int actual, int expected) {
    @Test public void assertEquals(final byte[] actual, final byte[] expected) {
    @Test public void assertEquals(final byte[] actual, final byte[] expected, final String message) {
    @Test public void assertEquals(Set actual, Set expected) {
    @Test public void assertEquals(Map actual, Map expected) {
    @Test public void assertEquals(Collection actual, Collection expected) {
    @Test public void assertEquals(Collection actual, Collection expected, String message) {
    @Test public void assertEquals(Object[] actual, Object[] expected, String message) {
    @Test public void assertEquals(Object[] actual, Object[] expected) {
    */
    /*

    @Test
    public void assertSame() {

    }

    @Test
    public void assertNotSame() {

    }

    @Test
    public void assertEqualsNoOrder() {

    }
    */
}
