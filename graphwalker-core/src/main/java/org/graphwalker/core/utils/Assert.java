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

import org.graphwalker.core.Bundle;

import java.lang.reflect.Array;
import java.util.*;

/**
 * <p>Assert class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public final class Assert {

    private Assert() {
    }

    /**
     * <p>assertTrue.</p>
     *
     * @param condition a boolean.
     */
    public static void assertTrue(boolean condition) {
        if (!condition) {
            failNotEquals(condition, Boolean.TRUE);
        }
    }

    /**
     * <p>assertFalse.</p>
     *
     * @param condition a boolean.
     */
    public static void assertFalse(boolean condition) {
        if (condition) {
            failNotEquals(condition, Boolean.FALSE);
        }
    }

    /**
     * <p>fail.</p>
     *
     * @param message a {@link java.lang.String} object.
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
        fail(null);
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual a {@link java.lang.Object} object.
     * @param expected a {@link java.lang.Object} object.
     */
    public static void assertEquals(Object actual, Object expected) {
        if (null == expected && null == actual) {
            return;
        }
        if (null != expected) {
            if (expected.getClass().isArray()) {
                assertArrayEquals(actual, expected);
                return;
            } else if (expected.equals(actual)) {
                return;
            }
        }
        failNotEquals(actual, expected);
    }

    private static void assertArrayEquals(Object actual, Object expected) {
        if (actual.getClass().isArray()) {
            int actualLength = Array.getLength(actual);
            int expectedLength = Array.getLength(expected);
            if (expectedLength == actualLength) {
                for (int i = 0; i < expectedLength; i++) {
                    Object _actual = Array.get(actual, i);
                    Object _expected = Array.get(expected, i);
                    assertEquals(_actual, _expected);
                }
                return;
            } else {
                failNotEquals(actualLength, expectedLength);
            }
        }
        failNotEquals(actual, expected);
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual a double.
     * @param expected a double.
     * @param delta a double.
     */
    public static void assertEquals(double actual, double expected, double delta) {
        if (Double.isInfinite(expected)) {
            if (!(expected == actual)) {
                failNotEquals(actual, expected);
            }
        } else if (!(Math.abs(expected - actual) <= delta)) {
            failNotEquals(actual, expected);
        }
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual a float.
     * @param expected a float.
     * @param delta a float.
     */
    public static void assertEquals(float actual, float expected, float delta) {
        if (Float.isInfinite(expected)) {
            if (!(expected == actual)) {
                failNotEquals(actual, expected);
            }
        } else if (!(Math.abs(expected - actual) <= delta)) {
            failNotEquals(actual, expected);
        }
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual a long.
     * @param expected a long.
     */
    public static void assertEquals(long actual, long expected) {
        assertEquals(Long.valueOf(actual), Long.valueOf(expected));
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual a boolean.
     * @param expected a boolean.
     */
    public static void assertEquals(boolean actual, boolean expected) {
        assertEquals(Boolean.valueOf(actual), Boolean.valueOf(expected));
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual a byte.
     * @param expected a byte.
     */
    public static void assertEquals(byte actual, byte expected) {
        assertEquals(Byte.valueOf(actual), Byte.valueOf(expected));
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual a char.
     * @param expected a char.
     */
    public static void assertEquals(char actual, char expected) {
        assertEquals(Character.valueOf(actual), Character.valueOf(expected));
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual a short.
     * @param expected a short.
     */
    public static void assertEquals(short actual, short expected) {
        assertEquals(Short.valueOf(actual), Short.valueOf(expected));
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual a int.
     * @param expected a int.
     */
    public static void assertEquals(int actual, int expected) {
        assertEquals(Integer.valueOf(actual), Integer.valueOf(expected));
    }

    /**
     * <p>assertNotNull.</p>
     *
     * @param object a {@link java.lang.Object} object.
     */
    public static void assertNotNull(Object object) {
        assertTrue(null != object);
    }

    /**
     * <p>assertNull.</p>
     *
     * @param object a {@link java.lang.Object} object.
     */
    public static void assertNull(Object object) {
        assertTrue(null != object);
    }

    private static void failNotEquals(Object actual, Object expected) {
        fail(format(actual, expected));
    }

    private static String format(Object actual, Object expected) {
        return Resource.getText(Bundle.NAME, "message.assert.equals", expected, actual);
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual a {@link java.util.Collection} object.
     * @param expected a {@link java.util.Collection} object.
     */
    public static void assertEquals(Collection actual, Collection expected) {
        if (actual == expected) {
            return;
        }
        if (null == actual || null == expected) {
            fail(Resource.getText(Bundle.NAME, "message.assert.collection", expected, actual));
        } else {
            assertEquals(actual.size(), expected.size());
            Iterator<?> actualIterator = actual.iterator();
            Iterator<?> expectedIterator = expected.iterator();
            while (actualIterator.hasNext() && expectedIterator.hasNext()) {
                assertEquals(actualIterator.next(), expectedIterator.next());
            }
        }
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual an array of {@link java.lang.Object} objects.
     * @param expected an array of {@link java.lang.Object} objects.
     */
    public static void assertEquals(Object[] actual, Object[] expected) {
        if (actual == expected) {
            return;
        }
        if (null == actual || null == expected) {
            fail(Resource.getText(Bundle.NAME, "assert.message.arrays", Arrays.toString(expected), Arrays.toString(actual)));
        }
        assertEquals(Arrays.asList(actual), Arrays.asList(expected));
    }

    /**
     * <p>assertEqualsNoOrder.</p>
     *
     * @param actual an array of {@link java.lang.Object} objects.
     * @param expected an array of {@link java.lang.Object} objects.
     */
    public static void assertEqualsNoOrder(Object[] actual, Object[] expected) {
        if (actual == expected) {
            return;
        }
        if (null == actual || null == expected) {
            fail(Resource.getText(Bundle.NAME, "assert.message.arrays", Arrays.toString(expected), Arrays.toString(actual)));
        } else if (actual.length != expected.length) {
            fail(Resource.getText(Bundle.NAME, "assert.message.arrays.size", actual.length, expected.length));
        } else {
            List<Object> actualCollection = new ArrayList<Object>();
            Collections.addAll(actualCollection, actual);
            for (Object o : expected) {
                actualCollection.remove(o);
            }
            if (actualCollection.size() != 0) {
                fail(Resource.getText(Bundle.NAME, "assert.message.arrays", Arrays.toString(expected), Arrays.toString(actual)));
            }
        }
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual an array of byte.
     * @param expected an array of byte.
     */
    public static void assertEquals(final byte[] actual, final byte[] expected) {
        if (expected == actual) {
            return;
        }
        if (null == actual || null == expected) {
            fail(Resource.getText(Bundle.NAME, "message.assert.arrays", Arrays.toString(expected), Arrays.toString(actual)));
        } else {
            assertEquals(expected.length, actual.length);
            for (int i = 0; i < expected.length; i++) {
                if (expected[i] != actual[i]) {
                    fail(Resource.getText(Bundle.NAME, "message.assert.equals", expected[i], actual[i]));
                }
            }
        }
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual a {@link java.util.Set} object.
     * @param expected a {@link java.util.Set} object.
     */
    public static void assertEquals(Set<?> actual, Set<?> expected) {
        if (actual == expected) {
            return;
        }
        if (null == actual || null == expected) {
            fail(Resource.getText(Bundle.NAME, "message.assert.sets", expected, actual));
        } else {
            if (!actual.equals(expected)) {
                fail(Resource.getText(Bundle.NAME, "message.assert.sets.differ", expected, actual));
            }
        }
    }

    /**
     * <p>assertEquals.</p>
     *
     * @param actual a {@link java.util.Map} object.
     * @param expected a {@link java.util.Map} object.
     */
    public static void assertEquals(Map<?, ?> actual, Map<?, ?> expected) {
        if (actual == expected) {
            return;
        }
        if (null == actual  || null == expected) {
            fail(Resource.getText(Bundle.NAME, "message.assert.maps", expected, actual));
        } else {
            if (!actual.equals(expected)) {
                fail(Resource.getText(Bundle.NAME, "message.assert.maps.differ", expected, actual));
            }
        }
    }
}
