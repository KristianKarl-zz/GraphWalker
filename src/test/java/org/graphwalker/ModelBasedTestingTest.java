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

package org.graphwalker;

import java.awt.GraphicsEnvironment;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graphwalker.Keywords;
import org.graphwalker.ModelBasedTesting;
import org.graphwalker.Util;
import org.graphwalker.exceptions.GeneratorException;
import org.graphwalker.exceptions.InvalidDataException;
import org.graphwalker.exceptions.StopConditionException;
import org.graphwalker.generators.NonOptimizedShortestPath;
import org.jdom.JDOMException;

import junit.framework.TestCase;

/**
 * @author Johan Tejle
 * 
 */
public class ModelBasedTestingTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		ModelBasedTesting.getInstance().reset();
	}

	private InputStream redirectIn() {
		return new InputStream() {
			public int read() throws IOException {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Util.logStackTraceToError(e);
				}
				return '0';
			}
		};
	}

	public void testXmlLoading_Simple() throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
		ModelBasedTesting mbt = Util.loadMbtFromXml(Util.getFile("xml/reqtags/mbt_init.xml"));
		assertEquals("RANDOM{EC>=100}", mbt.toString());
	}

	public void testXmlLoading_Moderate() throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
		ModelBasedTesting mbt = Util.loadMbtFromXml(Util.getFile("xml/reqtags/mbt_init2.xml"));
		assertEquals("RANDOM{((EC>=100 AND SC>=100) OR L=50)}", mbt.toString());
	}

	@SuppressWarnings("static-access")
  public void testXmlLoading_Advanced() throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); 
		if ( ge.isHeadless() )
			return;

		ModelBasedTesting mbt = Util.loadMbtFromXml(Util.getFile("xml/reqtags/mbt_init3.xml"));
		assertEquals("RANDOM{EC>=10}\nRANDOM{(SC>=30 AND EC>=10)}", mbt.toString());
	}

	@SuppressWarnings("static-access")
  public void testXmlLoading_OfflineStub() throws StopConditionException, GeneratorException, IOException, JDOMException,
	    InterruptedException {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); 
		if ( ge.isHeadless() )
			return;

		ModelBasedTesting mbt = Util.loadMbtFromXml(Util.getFile("xml/reqtags/mbt_init4.xml"));
		assertEquals("CODE", mbt.toString());
		File f = new File("mbt_init4.java");
		assertTrue(f.exists());
		assertTrue(f.delete());
		assertFalse(f.exists());
	}

	@SuppressWarnings("static-access")
  public void testXmlLoading_JavaExecution() throws StopConditionException, GeneratorException, IOException, JDOMException,
	    InterruptedException {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); 
		if ( ge.isHeadless() )
			return;

		ModelBasedTesting mbt = Util.loadMbtFromXml(Util.getFile("xml/reqtags/mbt_init5.xml"));
		assertEquals("RANDOM{SC>=40}", mbt.toString());
	}

	public void testXmlLoading_OfflineRequirements() throws StopConditionException, GeneratorException, IOException, JDOMException,
	    InterruptedException {
		PrintStream oldOut = System.out;
		ByteArrayOutputStream innerOut = new ByteArrayOutputStream();

		System.setOut(new PrintStream(innerOut));
		ModelBasedTesting mbt = Util.loadMbtFromXml(Util.getFile("xml/reqtags/mbt_init6.xml"));
		System.setOut(oldOut);

		assertEquals("REQUIREMENTS", mbt.toString());
		assertEquals(6, getNumMatches(Pattern.compile("req[ \\d]+").matcher(innerOut.toString())));
	}

	public void testXmlLoading_OnlineRequirements() throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
		InputStream oldIn = System.in;
		PrintStream oldOut = System.out;
		ByteArrayOutputStream innerOut = new ByteArrayOutputStream();

		System.setOut(new PrintStream(innerOut));
		System.setIn(redirectIn());
		ModelBasedTesting mbt = Util.loadMbtFromXml(Util.getFile("xml/reqtags/mbt_init7.xml"));
		System.setIn(oldIn);
		System.setOut(oldOut);

		assertEquals("REQUIREMENTS", mbt.toString());
		assertEquals(6, getNumMatches(Pattern.compile("req[ \\d]+").matcher(innerOut.toString())));
	}

	@SuppressWarnings("static-access")
  public void testGetdataValue() throws InvalidDataException, StopConditionException, GeneratorException, IOException, JDOMException,
	    InterruptedException {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); 
		if ( ge.isHeadless() )
			return;

		InputStream oldIn = System.in;
		PrintStream oldOut = System.out;
		ByteArrayOutputStream innerOut = new ByteArrayOutputStream();

		System.setOut(new PrintStream(innerOut));
		System.setIn(redirectIn());
		ModelBasedTesting mbt = Util.loadMbtFromXml(Util.getFile("xml/reqtags/mbt_init10.xml"));
		System.setIn(oldIn);
		System.setOut(oldOut);

		System.out.print(mbt.getDataValue("incorrect"));

		assertEquals("0", mbt.getDataValue("incorrect"));
	}

	private int getNumMatches(Matcher m) {
		int numMatches = 0;
		while (m.find() == true)
			numMatches++;
		return numMatches;
	}

	@SuppressWarnings("static-access")
  public void testExecAction() throws InvalidDataException, StopConditionException, GeneratorException, IOException, JDOMException,
	    InterruptedException {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); 
		if ( ge.isHeadless() )
			return;

		InputStream oldIn = System.in;
		PrintStream oldOut = System.out;
		ByteArrayOutputStream innerOut = new ByteArrayOutputStream();

		System.setOut(new PrintStream(innerOut));
		System.setIn(redirectIn());
		ModelBasedTesting mbt = Util.loadMbtFromXml(Util.getFile("xml/reqtags/mbt_init11.xml"));
		System.setIn(oldIn);
		System.setOut(oldOut);

		System.out.print(mbt.execAction("str.toUpperCase()"));

		assertEquals("ABC", mbt.execAction("str.toUpperCase()"));
	}

	@SuppressWarnings("static-access")
  public void testPassRequirement() throws StopConditionException, GeneratorException, IOException, JDOMException, InterruptedException {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); 
		if ( ge.isHeadless() )
			return;

		ModelBasedTesting mbt;
		mbt = Util.loadMbtFromXml(Util.getFile("xml/reqCoverage.xml"));
		mbt.passRequirement(true);
		mbt.passRequirement(false);
		mbt.passRequirement(true);
	}

	public void testNewState() throws StopConditionException, InterruptedException {
		ModelBasedTesting mbt = ModelBasedTesting.getInstance();
		mbt.readGraph("graphml/ModelBasedTestingTest.testNewState.graphml");
		mbt.enableExtended(false);
		mbt.setWeighted(false);
		NonOptimizedShortestPath generator = new NonOptimizedShortestPath();
		generator.setStopCondition(Util.getCondition(mbt.getMachine(), Keywords.CONDITION_EDGE_COVERAGE, "100"));
		mbt.setGenerator(generator);

		String[] pair = mbt.getNextStep();
		assertEquals("e_init", pair[0]);
		assertEquals("v_BrowserStopped", pair[1]);
		assertEquals("e_init", mbt.getCurrentEdgeName());
		assertEquals("v_BrowserStopped", mbt.getCurrentVertexName());

		pair = mbt.getNextStep();
		assertEquals("e_StartBrowser", pair[0]);
		assertEquals("v_BrowserStarted", pair[1]);
		assertEquals("e_StartBrowser", mbt.getCurrentEdgeName());
		assertEquals("v_BrowserStarted", mbt.getCurrentVertexName());

		pair = mbt.getNextStep();
		assertEquals("e_EnterBaseURL", pair[0]);
		assertEquals("v_BaseURL", pair[1]);
		assertEquals("e_EnterBaseURL", mbt.getCurrentEdgeName());
		assertEquals("v_BaseURL", mbt.getCurrentVertexName());

		assertEquals(false, mbt.setCurrentVertex("foobar"));
		assertEquals(false, mbt.setCurrentVertex((String) null));
		pair = mbt.getNextStep();
		assertEquals("e_SearchBook", pair[0]);
		assertEquals("v_SearchResult", pair[1]);
		assertEquals("e_SearchBook", mbt.getCurrentEdgeName());
		assertEquals("v_SearchResult", mbt.getCurrentVertexName());

		assertEquals(false, mbt.setCurrentVertex(""));
		assertEquals("e_SearchBook", pair[0]);
		assertEquals("v_SearchResult", pair[1]);
		assertEquals("e_SearchBook", mbt.getCurrentEdgeName());
		assertEquals("v_SearchResult", mbt.getCurrentVertexName());

		assertEquals(true, mbt.setCurrentVertex("v_BrowserStopped"));
		pair = mbt.getNextStep();
		assertEquals("e_StartBrowser", pair[0]);
		assertEquals("v_BrowserStarted", pair[1]);
		assertEquals("e_StartBrowser", mbt.getCurrentEdgeName());
		assertEquals("v_BrowserStarted", mbt.getCurrentVertexName());

		pair = mbt.getNextStep();
		assertEquals("e_EnterBaseURL", pair[0]);
		assertEquals("v_BaseURL", pair[1]);
		assertEquals("e_EnterBaseURL", mbt.getCurrentEdgeName());
		assertEquals("v_BaseURL", mbt.getCurrentVertexName());
	}
}
