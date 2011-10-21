/*
 * #%L
 * GraphWalker Core
 * %%
 * Copyright (C) 2011 GraphWalker
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
//This file is part of the GraphWalker java package
//The MIT License
//
//Copyright (c) 2010 graphwalker.org
//
//Permission is hereby granted, free of charge, to any person obtaining a copy
//of this software and associated documentation files (the "Software"), to deal
//in the Software without restriction, including without limitation the rights
//to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//copies of the Software, and to permit persons to whom the Software is
//furnished to do so, subject to the following conditions:
//
//The above copyright notice and this permission notice shall be included in
//all copies or substantial portions of the Software.
//
//THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//THE SOFTWARE.

package org.graphwalker.cli.bugs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.graphwalker.cli.CLI;
import org.graphwalker.cli.CLITest;
import org.graphwalker.core.ModelBasedTesting;
import org.graphwalker.core.Util;

import junit.framework.TestCase;

public class Issue_9 extends TestCase {

	Pattern pattern;
	Matcher matcher;
	StringBuffer stdOutput;
	StringBuffer errOutput;
	String outMsg;
	String errMsg;
	static Logger logger = Util.setupLogger(CLITest.class);

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ModelBasedTesting.getInstance().reset();
	}

	private OutputStream redirectOut() {
		return new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				// Redirect to nothing, else we'll get an OutOfMemoryException
				// stdOutput.append( Character.toString((char) b) );
			}
		};
	}

	private OutputStream redirectErr() {
		return new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				errOutput.append(Character.toString((char) b));
			}
		};
	}

	private void runCommand(String args[]) {
		stdOutput = new StringBuffer();
		errOutput = new StringBuffer();

		PrintStream outStream = new PrintStream(redirectOut());
		PrintStream oldOutStream = System.out; // backup
		PrintStream errStream = new PrintStream(redirectErr());
		PrintStream oldErrStream = System.err; // backup

		System.setOut(outStream);
		System.setErr(errStream);

		CLI.main(args);

		System.setOut(oldOutStream);
		System.setErr(oldErrStream);

		outMsg = stdOutput.toString();
		errMsg = errOutput.toString();
		logger.debug("stdout: " + outMsg);
		logger.debug("stderr: " + errMsg);
	}

	// Test will excute during 35 seconds
	public void testIssue_9() {
		System.out.println("Test will excute during 35 seconds");
		String args[] = { "xml", "-f", "xml/bugs/Issue_9.xml" };
		runCommand(args);
		assertTrue(errMsg, errMsg.isEmpty());
	}
}
