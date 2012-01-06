/*
 * #%L
 * GraphWalker CLI
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
package org.graphwalker.cli;

import org.graphwalker.core.util.ResourceException;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GraphWalkerCLITest {

    @Test
    public void testShortHelpCommand() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        GraphWalkerCLI.main(new String[]{"-h"});
        Pattern pattern = Pattern.compile(".*Prints this help.*", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(buffer.toString());
        Assert.assertTrue(matcher.find());
    }

    @Test
    public void testLongHelpCommand() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        GraphWalkerCLI.main(new String[]{"-help"});
        Pattern pattern = Pattern.compile(".*Prints this help.*", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(buffer.toString());
        Assert.assertTrue(matcher.find());
    }

    @Test
    public void testUnknownCommand() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        GraphWalkerCLI.main(new String[]{"-unknown"});
        Pattern pattern = Pattern.compile(".*Prints this help.*", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(buffer.toString());
        Assert.assertTrue(matcher.find());
    }

    @Test(expected = ResourceException.class)
    public void testMissingFileCommand() {
        GraphWalkerCLI.main(new String[] {"-f", "missing"});
    }
    /*
    @Test
    public void testShortFileCommand() {
        GraphWalkerCLI.main(new String[] {"-file", "missing"});
    }

    @Test
    public void testLongFileCommand() {
        GraphWalkerCLI.main(new String[] {"-file", "missing"});
    }
    */
}
