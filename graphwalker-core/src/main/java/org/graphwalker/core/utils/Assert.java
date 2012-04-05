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

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            fail(Resource.getText(Bundle.NAME, "assert.message", condition, true, message));
        }
    }

    public static void assertTrue(boolean condition) {
        assertTrue(condition, "");
    }

    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            fail(Resource.getText(Bundle.NAME, "assert.message", condition, false, message));
        }
    }

    public static void assertFalse(boolean condition) {
        assertFalse(condition, "");
    }

    public static void fail(String message, Throwable realCause) {
        AssertionError assertionError = new AssertionError(message);
        assertionError.initCause(realCause);
        throw assertionError;
    }

    public static void fail(String message) {
        throw new AssertionError(message);
    }

    public static void fail() {
        fail("");
    }

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
            fail(Resource.getText(Bundle.NAME, "assert.message", actual, expected, message));
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
                        fail(Resource.getText(Bundle.NAME, "assert.message", actual, expected, message));
                    }
                }
                return;
            }
        }
        fail(Resource.getText(Bundle.NAME, "assert.message", actual, expected, message));
    }

    public static void assertEquals(Object actual, Object expected) {
        assertEquals(actual, expected, "");
    }

    public static void assertEquals(String actual, String expected, String message) {
        assertEquals((Object) actual, (Object) expected, message);
    }

    public static void assertEquals(String actual, String expected) {
        assertEquals(actual, expected, "");
    }

    public static void assertEquals(double actual, double expected, double delta, String message) {
        if (Double.isInfinite(expected)) {
            if (!(expected == actual)) {
                fail(Resource.getText(Bundle.NAME, "assert.message", actual, expected, message));
            }
        } else if (!(Math.abs(expected - actual) <= delta)) {
            fail(Resource.getText(Bundle.NAME, "assert.message", actual, expected, message));
        }
    }

    public static void assertEquals(double actual, double expected, double delta) {
        assertEquals(actual, expected, delta, "");
    }

    public static void assertEquals(float actual, float expected, float delta, String message) {
        if (Float.isInfinite(expected)) {
            if (!(expected == actual)) {
                fail(Resource.getText(Bundle.NAME, "assert.message", actual, expected, message));
            }
        } else if (!(Math.abs(expected - actual) <= delta)) {
            fail(Resource.getText(Bundle.NAME, "assert.message", actual, expected, message));
        }
    }

    public static void assertEquals(float actual, float expected, float delta) {
        assertEquals(actual, expected, delta, "");
    }

    public static void assertEquals(long actual, long expected, String message) {
        assertEquals(Long.valueOf(actual), Long.valueOf(expected), message);
    }

    public static void assertEquals(long actual, long expected) {
        assertEquals(actual, expected, "");
    }

    public static void assertEquals(boolean actual, boolean expected, String message) {
        assertEquals(Boolean.valueOf(actual), Boolean.valueOf(expected), message);
    }

    public static void assertEquals(boolean actual, boolean expected) {
        assertEquals(actual, expected, "");
    }

    public static void assertEquals(byte actual, byte expected, String message) {
        assertEquals(Byte.valueOf(actual), Byte.valueOf(expected), message);
    }

    public static void assertEquals(byte actual, byte expected) {
        assertEquals(actual, expected, "");
    }

    public static void assertEquals(char actual, char expected, String message) {
        assertEquals(Character.valueOf(actual), Character.valueOf(expected), message);
    }

    public static void assertEquals(char actual, char expected) {
        assertEquals(actual, expected, "");
    }

    public static void assertEquals(short actual, short expected, String message) {
        assertEquals(Short.valueOf(actual), Short.valueOf(expected), message);
    }

    public static void assertEquals(short actual, short expected) {
        assertEquals(actual, expected, "");
    }

    public static void assertEquals(int actual, int expected, String message) {
        assertEquals(Integer.valueOf(actual), Integer.valueOf(expected), message);
    }

    public static void assertEquals(int actual, int expected) {
        assertEquals(actual, expected, "");
    }

    public static void assertNotNull(Object object) {
        assertNotNull(object, "");
    }

    public static void assertNotNull(Object object, String message) {
        if (null == object) {
            fail(Resource.getText(Bundle.NAME, "assert.message", object, "not null", message));
        }
    }

    public static void assertNull(Object object) {
        assertNull(object, "");
    }

    public static void assertNull(Object object, String message) {
        if (null != object) {
            fail(Resource.getText(Bundle.NAME, "assert.message", object, null, message));
        }
    }

    public static void assertSame(Object actual, Object expected, String message) {
        if (expected != actual) {
            failNotSame(actual, expected, message);
        }
    }

    public static void assertSame(Object actual, Object expected) {
        assertSame(actual, expected, "");
    }

    public static void assertNotSame(Object actual, Object expected, String message) {
        if (expected == actual) {
            failSame(actual, expected, message);
        }
    }

    public static void assertNotSame(Object actual, Object expected) {
        assertNotSame(actual, expected, "");
    }

    private static void failSame(Object actual, Object expected, String message) {
        fail(Resource.getText(Bundle.NAME, "assert.message", actual, expected, message));
    }

    private static void failNotSame(Object actual, Object expected, String message) {
        fail(Resource.getText(Bundle.NAME, "assert.message", actual, expected, message));
    }

    public static void assertEquals(Collection actual, Collection expected) {
        assertEquals(actual, expected, "");
    }

    public static void assertEquals(Collection actual, Collection expected, String message) {
        if (actual != expected) {
            if (actual == null || expected == null) {
                fail(Resource.getText(Bundle.NAME, "assert.message", actual, expected, message));
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

    public static void assertEquals(Object[] actual, Object[] expected, String message) {
        if (actual != expected) {
            if (null == actual || null == expected) {
                fail(Resource.getText(Bundle.NAME, "assert.message", Arrays.toString(expected), Arrays.toString(actual), message));
            } else {
                assertEquals(Arrays.asList(actual), Arrays.asList(expected), message);
            }
        }
    }

    public static void assertEqualsNoOrder(Object[] actual, Object[] expected, String message) {
        if (actual != expected) {
            if (null == actual || null == expected) {
                fail(Resource.getText(Bundle.NAME, "assert.message", Arrays.toString(expected), Arrays.toString(actual), message));
            } else {
                List<Object> actualCollection = new ArrayList<Object>();
                Collections.addAll(actualCollection, actual);
                List<Object> expectedCollection = new ArrayList<Object>();
                Collections.addAll(expectedCollection, expected);
                actualCollection.removeAll(expectedCollection);
                if (actualCollection.size() != 0) {
                    fail(Resource.getText(Bundle.NAME, "assert.message", Arrays.toString(expected), Arrays.toString(actual), message));
                }
            }
        }
    }

    public static void assertEquals(Object[] actual, Object[] expected) {
        assertEquals(actual, expected, "");
    }

    public static void assertEqualsNoOrder(Object[] actual, Object[] expected) {
        assertEqualsNoOrder(actual, expected, "");
    }

    public static void assertEquals(byte[] actual, byte[] expected) {
        assertEquals(actual, expected, "");
    }

    public static void assertEquals(byte[] actual, byte[] expected, String message) {
        if (expected != actual) {
            if (null == actual || null == expected) {
                fail(Resource.getText(Bundle.NAME, "assert.message", Arrays.toString(expected), Arrays.toString(actual), message));
            } else {
                for (int i = 0; i < expected.length; i++) {
                    if (expected[i] != actual[i]) {
                        fail(Resource.getText(Bundle.NAME, "assert.message", expected[i], actual[i], message));
                    }
                }
            }
        }
    }

    public static void assertEquals(Set actual, Set expected) {
        assertEquals(actual, expected, "");
    }

    public static void assertEquals(Set actual, Set expected, String message) {
        if (actual != expected) {
            if (null == actual || null == expected) {
                fail(Resource.getText(Bundle.NAME, "assert.message", actual, expected, message));
            } else if (!actual.equals(expected)) {
                fail(Resource.getText(Bundle.NAME, "assert.message", actual, expected, message));
            }
        }
    }

    public static void assertEquals(Map actual, Map expected) {
        assertEquals(actual, expected, "");
    }

    public static void assertEquals(Map actual, Map expected, String message) {
        if (actual != expected) {
            if (null == actual || null == expected) {
                fail(Resource.getText(Bundle.NAME, "assert.message", actual, expected, message));
            } else if (!actual.equals(expected)) {
                fail(Resource.getText(Bundle.NAME, "assert.message", actual, expected, message));
            }
        }
    }
}
