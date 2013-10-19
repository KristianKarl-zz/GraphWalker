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
package org.graphwalker.core.common;

import org.graphwalker.core.Bundle;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @author Nils Olsson
 */
public final class Assert {

    private Assert() {
    }

    /**
     * <p>assertTrue.</p>
     *
     * @param condition a boolean.
     * @param message   a {@link java.lang.String} object.
     */
    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            fail(ResourceUtils.getText(Bundle.NAME, "assert.message", condition, true, message));
        }
    }

    /**
     * <p>assertTrue.</p>
     *
     * @param condition a boolean.
     */
    public static void assertTrue(boolean condition) {
        assertTrue(condition, "");
    }

    /**
     * <p>assertFalse.</p>
     *
     * @param condition a boolean.
     * @param message   a {@link java.lang.String} object.
     */
    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            fail(ResourceUtils.getText(Bundle.NAME, "assert.message", condition, false, message));
        }
    }

    /**
     * <p>assertFalse.</p>
     *
     * @param condition a boolean.
     */
    public static void assertFalse(boolean condition) {
        assertFalse(condition, "");
    }

    /**
     * <p>fail.</p>
     *
     * @param message   a {@link java.lang.String} object.
     * @param realCause a {@link java.lang.Throwable} object.
     */
    public static void fail(String message, Throwable realCause) {
        AssertionError assertionError = new AssertionError(message);
        assertionError.initCause(realCause);
        throw assertionError;
    }

    /**
     * <p>fail.</p>
     *
     * @param message a {@link java.lang.String} object.
     */
    public static void fail(String message) {
        throw new AssertionError(message);
    }

    /**
     * <p>fail.</p>
     */
    public static void fail() {
        fail("");
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a {@link java.lang.Object} object.
     * @param expected a {@link java.lang.Object} object.
     * @param message  a {@link java.lang.String} object.
     */
    public static void assertEquals(Object actual, Object expected, String message) {
        if (null != expected || null != actual) {
            if (null != expected) {
                if (expected.getClass().isArray()) {
                    assertArrayEquals(actual, expected, message);
                    return;
                } else if (expected.equals(actual)) {
                    return;
                }
            }
            fail(ResourceUtils.getText(Bundle.NAME, "assert.message", actual, expected, message));
        }
    }

    private static void assertArrayEquals(Object actual, Object expected, String message) {
        if (actual.getClass().isArray()) {
            int expectedLength = Array.getLength(expected);
            if (expectedLength == Array.getLength(actual)) {
                for (int i = 0; i < expectedLength; i++) {
                    Object _actual = Array.get(actual, i);
                    Object _expected = Array.get(expected, i);
                    try {
                        assertEquals(_actual, _expected);
                    } catch (AssertionError ae) {
                        fail(ResourceUtils.getText(Bundle.NAME, "assert.message", actual, expected, message));
                    }
                }
                return;
            }
        }
        fail(ResourceUtils.getText(Bundle.NAME, "assert.message", actual, expected, message));
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a {@link java.lang.Object} object.
     * @param expected a {@link java.lang.Object} object.
     */
    public static void assertEquals(Object actual, Object expected) {
        assertEquals(actual, expected, "");
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a {@link java.lang.String} object.
     * @param expected a {@link java.lang.String} object.
     * @param message  a {@link java.lang.String} object.
     */
    public static void assertEquals(String actual, String expected, String message) {
        assertEquals((Object) actual, (Object) expected, message);
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a {@link java.lang.String} object.
     * @param expected a {@link java.lang.String} object.
     */
    public static void assertEquals(String actual, String expected) {
        assertEquals(actual, expected, "");
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a double.
     * @param expected a double.
     * @param delta    a double.
     * @param message  a {@link java.lang.String} object.
     */
    public static void assertEquals(double actual, double expected, double delta, String message) {
        if (Double.isInfinite(expected)) {
            if (!(expected == actual)) {
                fail(ResourceUtils.getText(Bundle.NAME, "assert.message", actual, expected, message));
            }
        } else if (!(Math.abs(expected - actual) <= delta)) {
            fail(ResourceUtils.getText(Bundle.NAME, "assert.message", actual, expected, message));
        }
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a double.
     * @param expected a double.
     * @param delta    a double.
     */
    public static void assertEquals(double actual, double expected, double delta) {
        assertEquals(actual, expected, delta, "");
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a float.
     * @param expected a float.
     * @param delta    a float.
     * @param message  a {@link java.lang.String} object.
     */
    public static void assertEquals(float actual, float expected, float delta, String message) {
        if (Float.isInfinite(expected)) {
            if (!(expected == actual)) {
                fail(ResourceUtils.getText(Bundle.NAME, "assert.message", actual, expected, message));
            }
        } else if (!(Math.abs(expected - actual) <= delta)) {
            fail(ResourceUtils.getText(Bundle.NAME, "assert.message", actual, expected, message));
        }
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a float.
     * @param expected a float.
     * @param delta    a float.
     */
    public static void assertEquals(float actual, float expected, float delta) {
        assertEquals(actual, expected, delta, "");
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a long.
     * @param expected a long.
     * @param message  a {@link java.lang.String} object.
     */
    public static void assertEquals(long actual, long expected, String message) {
        assertEquals(Long.valueOf(actual), Long.valueOf(expected), message);
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a long.
     * @param expected a long.
     */
    public static void assertEquals(long actual, long expected) {
        assertEquals(actual, expected, "");
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a boolean.
     * @param expected a boolean.
     * @param message  a {@link java.lang.String} object.
     */
    public static void assertEquals(boolean actual, boolean expected, String message) {
        assertEquals(Boolean.valueOf(actual), Boolean.valueOf(expected), message);
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a boolean.
     * @param expected a boolean.
     */
    public static void assertEquals(boolean actual, boolean expected) {
        assertEquals(actual, expected, "");
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a byte.
     * @param expected a byte.
     * @param message  a {@link java.lang.String} object.
     */
    public static void assertEquals(byte actual, byte expected, String message) {
        assertEquals(Byte.valueOf(actual), Byte.valueOf(expected), message);
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a byte.
     * @param expected a byte.
     */
    public static void assertEquals(byte actual, byte expected) {
        assertEquals(actual, expected, "");
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a char.
     * @param expected a char.
     * @param message  a {@link java.lang.String} object.
     */
    public static void assertEquals(char actual, char expected, String message) {
        assertEquals(Character.valueOf(actual), Character.valueOf(expected), message);
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a char.
     * @param expected a char.
     */
    public static void assertEquals(char actual, char expected) {
        assertEquals(actual, expected, "");
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a short.
     * @param expected a short.
     * @param message  a {@link java.lang.String} object.
     */
    public static void assertEquals(short actual, short expected, String message) {
        assertEquals(Short.valueOf(actual), Short.valueOf(expected), message);
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a short.
     * @param expected a short.
     */
    public static void assertEquals(short actual, short expected) {
        assertEquals(actual, expected, "");
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a int.
     * @param expected a int.
     * @param message  a {@link java.lang.String} object.
     */
    public static void assertEquals(int actual, int expected, String message) {
        assertEquals(Integer.valueOf(actual), Integer.valueOf(expected), message);
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a int.
     * @param expected a int.
     */
    public static void assertEquals(int actual, int expected) {
        assertEquals(actual, expected, "");
    }

    /**
     * <p>assertNotNull.</p>
     *
     * @param object a {@link java.lang.Object} object.
     */
    public static void assertNotNull(Object object) {
        assertNotNull(object, "");
    }

    /**
     * <p>assertNotNull.</p>
     *
     * @param object  a {@link java.lang.Object} object.
     * @param message a {@link java.lang.String} object.
     */
    public static void assertNotNull(Object object, String message) {
        if (null == object) {
            fail(ResourceUtils.getText(Bundle.NAME, "assert.message", object, "not null", message));
        }
    }

    /**
     * <p>assertNull.</p>
     *
     * @param object a {@link java.lang.Object} object.
     */
    public static void assertNull(Object object) {
        assertNull(object, "");
    }

    /**
     * <p>assertNull.</p>
     *
     * @param object  a {@link java.lang.Object} object.
     * @param message a {@link java.lang.String} object.
     */
    public static void assertNull(Object object, String message) {
        if (null != object) {
            fail(ResourceUtils.getText(Bundle.NAME, "assert.message", object, null, message));
        }
    }

    /**
     * <p>assertSame.</p>
     *
     * @param actual   a {@link java.lang.Object} object.
     * @param expected a {@link java.lang.Object} object.
     * @param message  a {@link java.lang.String} object.
     */
    public static void assertSame(Object actual, Object expected, String message) {
        if (expected != actual) {
            failNotSame(actual, expected, message);
        }
    }

    /**
     * <p>assertSame.</p>
     *
     * @param actual   a {@link java.lang.Object} object.
     * @param expected a {@link java.lang.Object} object.
     */
    public static void assertSame(Object actual, Object expected) {
        assertSame(actual, expected, "");
    }

    /**
     * <p>assertNotSame.</p>
     *
     * @param actual   a {@link java.lang.Object} object.
     * @param expected a {@link java.lang.Object} object.
     * @param message  a {@link java.lang.String} object.
     */
    public static void assertNotSame(Object actual, Object expected, String message) {
        if (expected == actual) {
            failSame(actual, expected, message);
        }
    }

    /**
     * <p>assertNotSame.</p>
     *
     * @param actual   a {@link java.lang.Object} object.
     * @param expected a {@link java.lang.Object} object.
     */
    public static void assertNotSame(Object actual, Object expected) {
        assertNotSame(actual, expected, "");
    }

    private static void failSame(Object actual, Object expected, String message) {
        fail(ResourceUtils.getText(Bundle.NAME, "assert.same.message", actual, expected, message));
    }

    private static void failNotSame(Object actual, Object expected, String message) {
        fail(ResourceUtils.getText(Bundle.NAME, "assert.message", actual, expected, message));
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a {@link java.util.Collection} object.
     * @param expected a {@link java.util.Collection} object.
     */
    public static void assertEquals(Collection actual, Collection expected) {
        assertEquals(actual, expected, "");
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a {@link java.util.Collection} object.
     * @param expected a {@link java.util.Collection} object.
     * @param message  a {@link java.lang.String} object.
     */
    public static void assertEquals(Collection actual, Collection expected, String message) {
        if (actual != expected) {
            if (actual == null || expected == null) {
                fail(ResourceUtils.getText(Bundle.NAME, "assert.message", actual, expected, message));
            } else {
                Iterator actualIterator = actual.iterator();
                Iterator expectedIterator = expected.iterator();
                while (actualIterator.hasNext() && expectedIterator.hasNext()) {
                    Object e = expectedIterator.next();
                    Object a = actualIterator.next();
                    assertEquals(a, e, message);
                }
            }
        }
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   an array of {@link java.lang.Object} objects.
     * @param expected an array of {@link java.lang.Object} objects.
     * @param message  a {@link java.lang.String} object.
     */
    public static void assertEquals(Object[] actual, Object[] expected, String message) {
        if (actual != expected) {
            if (null == actual || null == expected) {
                fail(ResourceUtils.getText(Bundle.NAME, "assert.message", Arrays.toString(expected), Arrays.toString(actual), message));
            } else {
                assertEquals(Arrays.asList(actual), Arrays.asList(expected), message);
            }
        }
    }

    /**
     * <p>assertEqualsNoOrder.</p>
     *
     * @param actual   an array of {@link java.lang.Object} objects.
     * @param expected an array of {@link java.lang.Object} objects.
     * @param message  a {@link java.lang.String} object.
     */
    public static void assertEqualsNoOrder(Object[] actual, Object[] expected, String message) {
        if (actual != expected) {
            if (null == actual || null == expected) {
                fail(ResourceUtils.getText(Bundle.NAME, "assert.message", Arrays.toString(expected), Arrays.toString(actual), message));
            } else {
                List<Object> actualCollection = new ArrayList<Object>();
                java.util.Collections.addAll(actualCollection, actual);
                List<Object> expectedCollection = new ArrayList<Object>();
                java.util.Collections.addAll(expectedCollection, expected);
                actualCollection.removeAll(expectedCollection);
                if (actualCollection.size() != 0) {
                    fail(ResourceUtils.getText(Bundle.NAME, "assert.message", Arrays.toString(expected), Arrays.toString(actual), message));
                }
            }
        }
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   an array of {@link java.lang.Object} objects.
     * @param expected an array of {@link java.lang.Object} objects.
     */
    public static void assertEquals(Object[] actual, Object[] expected) {
        assertEquals(actual, expected, "");
    }

    /**
     * <p>assertEqualsNoOrder.</p>
     *
     * @param actual   an array of {@link java.lang.Object} objects.
     * @param expected an array of {@link java.lang.Object} objects.
     */
    public static void assertEqualsNoOrder(Object[] actual, Object[] expected) {
        assertEqualsNoOrder(actual, expected, "");
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   an array of byte.
     * @param expected an array of byte.
     */
    public static void assertEquals(byte[] actual, byte[] expected) {
        assertEquals(actual, expected, "");
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   an array of byte.
     * @param expected an array of byte.
     * @param message  a {@link java.lang.String} object.
     */
    public static void assertEquals(byte[] actual, byte[] expected, String message) {
        if (expected != actual) {
            if (null == actual || null == expected) {
                fail(ResourceUtils.getText(Bundle.NAME, "assert.message", Arrays.toString(expected), Arrays.toString(actual), message));
            } else {
                for (int i = 0; i < expected.length; i++) {
                    if (expected[i] != actual[i]) {
                        fail(ResourceUtils.getText(Bundle.NAME, "assert.message", expected[i], actual[i], message));
                    }
                }
            }
        }
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a {@link java.util.Set} object.
     * @param expected a {@link java.util.Set} object.
     */
    public static void assertEquals(Set actual, Set expected) {
        assertEquals(actual, expected, "");
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a {@link java.util.Set} object.
     * @param expected a {@link java.util.Set} object.
     * @param message  a {@link java.lang.String} object.
     */
    public static void assertEquals(Set actual, Set expected, String message) {
        if (actual != expected) {
            if (null == actual || null == expected) {
                fail(ResourceUtils.getText(Bundle.NAME, "assert.message", actual, expected, message));
            } else if (!actual.equals(expected)) {
                fail(ResourceUtils.getText(Bundle.NAME, "assert.message", actual, expected, message));
            }
        }
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a {@link java.util.Map} object.
     * @param expected a {@link java.util.Map} object.
     */
    public static void assertEquals(Map actual, Map expected) {
        assertEquals(actual, expected, "");
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual   a {@link java.util.Map} object.
     * @param expected a {@link java.util.Map} object.
     * @param message  a {@link java.lang.String} object.
     */
    public static void assertEquals(Map actual, Map expected, String message) {
        if (actual != expected) {
            if (null == actual || null == expected) {
                fail(ResourceUtils.getText(Bundle.NAME, "assert.message", actual, expected, message));
            } else if (!actual.equals(expected)) {
                fail(ResourceUtils.getText(Bundle.NAME, "assert.message", actual, expected, message));
            }
        }
    }
}
