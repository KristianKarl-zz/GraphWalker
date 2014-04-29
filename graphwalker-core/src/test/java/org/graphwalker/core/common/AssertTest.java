/*
 * #%L
 * 
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 - 2014 GraphWalker
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

import org.junit.Test;

import java.util.*;

/**
 * @author Nils Olsson
 */
public class AssertTest {

    private static final Map<String, String> map1 = new HashMap<>();
    private static final Map<String, String> map2 = new HashMap<>();
    private static final Set<String> set1 = new HashSet<>();
    private static final Set<String> set2 = new HashSet<>();
    private static final List<String> list1 = Arrays.asList("string1", "string2");
    private static final List<String> list2 = Arrays.asList("string2", "string1");
    private static final byte[] array1 = new byte[]{(byte) 1, (byte) 2};
    private static final byte[] array2 = new byte[]{(byte) 2, (byte) 1};
    private static final Object obj1 = new Object();
    private static final Object obj2 = new Object();
    private static final Object[] objects1 = new Object[]{obj1, obj2};
    private static final Object[] objects2 = new Object[]{obj2, obj1};
    private static final Object[] objects3 = new Object[]{obj1};

    static {
        map1.put("key1", "value1");
        map2.put("key2", "value2");
        set1.add("value1");
        set2.add("value2");
    }

    private <T> Iterable<T> cast(List<T> iterable) {
        return iterable;
    }

    @Test
    public void assertions() {
        Assert.assertTrue(true, "message");
        Assert.assertTrue(true);
        Assert.assertFalse(false, "message");
        Assert.assertFalse(false);
        Object object = new Object();
        Assert.assertEquals(object, object, "message");
        Assert.assertEquals(object, object);
        String string = "string";
        Assert.assertEquals(string, string, "message");
        Assert.assertEquals(string, string);
        Assert.assertEquals(1d, 1.1d, 0.5d, "message");
        Assert.assertEquals(1d, 1.1d, 0.5d);
        Assert.assertEquals(1f, 1.1f, 0.5f, "message");
        Assert.assertEquals(1f, 1.1f, 0.5f);
        Assert.assertEquals((long) 1, (long) 1, "message");
        Assert.assertEquals((long) 1, (long) 1);
        Assert.assertEquals(true, true, "message");
        Assert.assertEquals(true, true);
        Assert.assertEquals((byte) 1, (byte) 1, "message");
        Assert.assertEquals((byte) 1, (byte) 1);
        Assert.assertEquals('a', 'a', "message");
        Assert.assertEquals('a', 'a');
        Assert.assertEquals((short) 1, (short) 1, "message");
        Assert.assertEquals((short) 1, (short) 1);
        Assert.assertEquals(1, 1, "message");
        Assert.assertEquals(1, 1);
        Assert.assertNotNull(object);
        Assert.assertNotNull(object, "message");
        Assert.assertNull(null);
        Assert.assertNull(null, "message");
        Assert.assertSame(object, object, "message");
        Assert.assertSame(object, object);
        Assert.assertNotSame(object, string, "message");
        Assert.assertNotSame(object, string);
        Assert.assertTrue(Assert.format("string1", "string2", "message").equals("message expected [string2] but found [string1]"));
        Assert.assertEquals(list1, list1);
        Assert.assertEquals(list1, list1, "message");
        Assert.assertEquals(objects1, objects1, "message");
        Assert.assertEquals(objects1, objects1);
        Assert.assertEquals(list1.iterator(), list1.iterator());
        Assert.assertEquals(list1.iterator(), list1.iterator(), "message");
        Assert.assertEquals(cast(list1), cast(list1));
        Assert.assertEquals(cast(list1), cast(list1), "message");
        Assert.assertEqualsNoOrder(objects1, objects2, "message");
        Assert.assertEqualsNoOrder(objects2, objects1);
        Assert.assertEquals(array1, array1);
        Assert.assertEquals(array1, array1, "message");
        Assert.assertEquals(set1, set1);
        Assert.assertEquals(set1, set1, "message");
        Assert.assertEquals(map1, map1);
        Assert.assertNotEquals(new Object(), "", "message");
        Assert.assertNotEquals(new Object(), "");
        Assert.assertNotEquals("actual1", "actual2", "message");
        Assert.assertNotEquals("actual1", "actual2");
        Assert.assertNotEquals(1L, 2L, "message");
        Assert.assertNotEquals(1L, 2L);
        Assert.assertNotEquals(true, false, "message");
        Assert.assertNotEquals(true, false);
        Assert.assertNotEquals((byte) 1, (byte) 2, "message");
        Assert.assertNotEquals((byte) 1, (byte) 2);
        Assert.assertNotEquals('a', 'b', "message");
        Assert.assertNotEquals('a', 'b');
        Assert.assertNotEquals((short) 1, (short) 2, "message");
        Assert.assertNotEquals((short) 1, (short) 2);
        Assert.assertNotEquals(6500, 2, "message");
        Assert.assertNotEquals(1, 2);
        Assert.assertNotEquals(1f, 2f, 0.5f, "message");
        Assert.assertNotEquals(1f, 2f, 0.5f);
        Assert.assertNotEquals(1d, 2d, 0.5d, "message");
        Assert.assertNotEquals(1d, 2d, 0.5d);
    }

    @Test(expected = AssertionError.class)
    public void failWithMessageAndThrowable() {
        Assert.fail("message", new RuntimeException());
    }

    @Test(expected = AssertionError.class)
    public void failWithMessage() {
        Assert.fail("message");
    }

    @Test(expected = AssertionError.class)
    public void fail() {
        Assert.fail();
    }

    @Test(expected = AssertionError.class)
    public void fail1() {
        Assert.assertTrue(false, "message");
    }

    @Test(expected = AssertionError.class)
    public void fail2() {
        Assert.assertTrue(false);
    }

    @Test(expected = AssertionError.class)
    public void fail3() {
        Assert.assertFalse(true, "message");
    }

    @Test(expected = AssertionError.class)
    public void fail4() {
        Assert.assertFalse(true);
    }

    @Test(expected = AssertionError.class)
    public void fail5() {
        Assert.assertEquals(new Object(), "object", "message");
    }

    @Test(expected = AssertionError.class)
    public void fail6() {
        Assert.assertEquals(new Object(), "object");
    }

    @Test(expected = AssertionError.class)
    public void fail7() {
        Assert.assertEquals("string1", "string2", "message");
    }

    @Test(expected = AssertionError.class)
    public void fail8() {
        Assert.assertEquals("string1", "string2");
    }

    @Test(expected = AssertionError.class)
    public void fail9() {
        Assert.assertEquals(1d, 2.1d, 0.5d, "message");
    }

    @Test(expected = AssertionError.class)
    public void fail10() {
        Assert.assertEquals(2d, 1.1d, 0.5d);
    }

    @Test(expected = AssertionError.class)
    public void fail11() {
        Assert.assertEquals(1f, 2.1f, 0.5f, "message");
    }

    @Test(expected = AssertionError.class)
    public void fail12() {
        Assert.assertEquals(2f, 1.1f, 0.5f);
    }

    @Test(expected = AssertionError.class)
    public void fail13() {
        Assert.assertEquals((long) 1, (long) 2, "message");
    }

    @Test(expected = AssertionError.class)
    public void fail14() {
        Assert.assertEquals((long) 1, (long) 2);
    }

    @Test(expected = AssertionError.class)
    public void fail15() {
        Assert.assertEquals(true, false, "message");
    }

    @Test(expected = AssertionError.class)
    public void fail16() {
        Assert.assertEquals(false, true);
    }

    @Test(expected = AssertionError.class)
    public void fail17() {
        Assert.assertEquals((byte) 1, (byte) 2, "message");
    }

    @Test(expected = AssertionError.class)
    public void fail18() {
        Assert.assertEquals((byte) 2, (byte) 1);
    }

    @Test(expected = AssertionError.class)
    public void fail19() {
        Assert.assertEquals('a', 'b', "message");
    }

    @Test(expected = AssertionError.class)
    public void fail20() {
        Assert.assertEquals('b', 'a');
    }

    @Test(expected = AssertionError.class)
    public void fail21() {
        Assert.assertEquals((short) 1, (short) 2, "message");
    }

    @Test(expected = AssertionError.class)
    public void fail22() {
        Assert.assertEquals((short) 2, (short) 1);
    }

    @Test(expected = AssertionError.class)
    public void fail23() {
        Assert.assertEquals(1, 2, "message");
    }

    @Test(expected = AssertionError.class)
    public void fail24() {
        Assert.assertEquals(2, 1);
    }

    @Test(expected = AssertionError.class)
    public void fail25() {
        Assert.assertNotNull(null);
    }

    @Test(expected = AssertionError.class)
    public void fail26() {
        Assert.assertNotNull(null, "message");
    }

    @Test(expected = AssertionError.class)
    public void fail27() {
        Assert.assertNull(new Object());
    }

    @Test(expected = AssertionError.class)
    public void fail28() {
        Assert.assertNull(new Object(), "message");
    }

    @Test(expected = AssertionError.class)
    public void fail29() {
        Assert.assertSame("object1", "object2", "message");
    }

    @Test(expected = AssertionError.class)
    public void fail30() {
        Assert.assertSame("object2", "object1");
    }

    @Test(expected = AssertionError.class)
    public void fail31() {
        Assert.assertNotSame("object", "object", "message");
    }

    @Test(expected = AssertionError.class)
    public void fail32() {
        Assert.assertNotSame("object", "object");
    }

    @Test(expected = AssertionError.class)
    public void fail33() {
        Assert.assertEquals(set1, set2);
    }

    @Test(expected = AssertionError.class)
    public void fail34() {
        Assert.assertEquals(set2, set1, "message");
    }

    @Test(expected = AssertionError.class)
    public void fail35() {
        Assert.assertEquals(map1, map2);
    }

    @Test(expected = AssertionError.class)
    public void fail36() {
        Assert.assertNotEquals("object", "object", "message");
    }

    @Test(expected = AssertionError.class)
    public void fail37() {
        Assert.assertNotEquals("object", "object");
    }

    @Test(expected = AssertionError.class)
    public void fail38() {
        Assert.assertNotEquals("actual1", "actual1", "message");
    }

    @Test(expected = AssertionError.class)
    public void fail39() {
        Assert.assertNotEquals("actual2", "actual2");
    }

    @Test(expected = AssertionError.class)
    public void fail40() {
        Assert.assertNotEquals(1L, 1L, "message");
    }

    @Test(expected = AssertionError.class)
    public void fail41() {
        Assert.assertNotEquals(1L, 1L);
    }

    @Test(expected = AssertionError.class)
    public void fail42() {
        Assert.assertNotEquals(true, true, "message");
    }

    @Test(expected = AssertionError.class)
    public void fail43() {
        Assert.assertNotEquals(false, false);
    }

    @Test(expected = AssertionError.class)
    public void fail44() {
        Assert.assertNotEquals((byte) 1, (byte) 1, "message");
    }

    @Test(expected = AssertionError.class)
    public void fail45() {
        Assert.assertNotEquals((byte) 1, (byte) 1);
    }

    @Test(expected = AssertionError.class)
    public void fail46() {
        Assert.assertNotEquals('a', 'a', "message");
    }

    @Test(expected = AssertionError.class)
    public void fail47() {
        Assert.assertNotEquals('a', 'a');
    }

    @Test(expected = AssertionError.class)
    public void fail48() {
        Assert.assertNotEquals((short) 1, (short) 1, "message");
    }

    @Test(expected = AssertionError.class)
    public void fail49() {
        Assert.assertNotEquals((short) 1, (short) 1);
    }

    @Test(expected = AssertionError.class)
    public void fail50() {
        Assert.assertNotEquals(1, 1, "message");
    }

    @Test(expected = AssertionError.class)
    public void fail51() {
        Assert.assertNotEquals(1, 1);
    }

    @Test(expected = AssertionError.class)
    public void fail52() {
        Assert.assertNotEquals(1f, 1f, 0.5f, "message");
    }

    @Test(expected = AssertionError.class)
    public void fail53() {
        Assert.assertNotEquals(1f, 1f, 0.5f);
    }

    @Test(expected = AssertionError.class)
    public void fail54() {
        Assert.assertNotEquals(1d, 1d, 0.5d, "message");
    }

    @Test(expected = AssertionError.class)
    public void fail55() {
        Assert.assertNotEquals(1d, 1d, 0.5d);
    }

    @Test(expected = AssertionError.class)
    public void fail56() {
        Assert.assertEquals(list1, list2);
    }

    @Test(expected = AssertionError.class)
    public void fail57() {
        Assert.assertEquals(list2, list1, "message");
    }

    @Test(expected = AssertionError.class)
    public void fail58() {
        Assert.assertEquals(list1.iterator(), list2.iterator());
    }

    @Test(expected = AssertionError.class)
    public void fail59() {
        Assert.assertEquals(list2.iterator(), list1.iterator(), "message");
    }

    @Test(expected = AssertionError.class)
    public void fail60() {
        Assert.assertEquals(cast(list1), cast(list2));
    }

    @Test(expected = AssertionError.class)
    public void fail61() {
        Assert.assertEquals(cast(list2), cast(list1), "message");
    }

    @Test(expected = AssertionError.class)
    public void fail62() {
        Assert.assertEquals(objects1, objects2);
    }

    @Test(expected = AssertionError.class)
    public void fail63() {
        Assert.assertEquals(objects1, objects3, "message");
    }

    @Test(expected = AssertionError.class)
    public void fail64() {
        Assert.assertEqualsNoOrder(null, objects2, "message");
    }

    @Test(expected = AssertionError.class)
    public void fail65() {
        Assert.assertEqualsNoOrder(objects1, objects3);
    }

    @Test(expected = AssertionError.class)
    public void fail66() {
        Assert.assertEquals(array1, array2);
    }

    @Test(expected = AssertionError.class)
    public void fail67() {
        Assert.assertEquals(array2, array1, "message");
    }
}