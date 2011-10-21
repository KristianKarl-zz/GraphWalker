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

package org.graphwalker.core;

import org.graphwalker.core.Keywords;
import org.graphwalker.core.exceptions.GeneratorException;
import org.graphwalker.core.exceptions.StopConditionException;

import junit.framework.TestCase;

public class KeywordsTest extends TestCase {

	public void testKeywords() {
		assertEquals(true, Keywords.isKeyWord("BACKTRACK"));
		assertEquals(true, Keywords.isKeyWord("backtrack"));
		assertEquals(true, Keywords.isKeyWord("BLOCKED"));
		assertEquals(true, Keywords.isKeyWord("blocked"));
		assertEquals(true, Keywords.isKeyWord("MERGE"));
		assertEquals(true, Keywords.isKeyWord("merge"));
		assertEquals(true, Keywords.isKeyWord("NO_MERGE"));
		assertEquals(true, Keywords.isKeyWord("no_merge"));
		assertEquals(true, Keywords.isKeyWord("SWITCH_MODEL"));
		assertEquals(true, Keywords.isKeyWord("switch_model"));
	}

	public void testNonKeywords() {
		assertEquals(false, Keywords.isKeyWord("BACKTRACKING"));
		assertEquals(false, Keywords.isKeyWord("BLOCK"));
		assertEquals(false, Keywords.isKeyWord("MERGED"));
		assertEquals(false, Keywords.isKeyWord("NO_MERGED"));
		assertEquals(false, Keywords.isKeyWord("REQTAG"));
		assertEquals(false, Keywords.isKeyWord(""));
	}

	public void testGetStopCondition() throws StopConditionException {
		assertTrue(Keywords.getStopCondition("REACHED_EDGE") != -1);
		assertTrue(Keywords.getStopCondition("REACHED_VERTEX") != -1);
		assertTrue(Keywords.getStopCondition("EDGE_COVERAGE") != -1);
		assertTrue(Keywords.getStopCondition("VERTEX_COVERAGE") != -1);
		assertTrue(Keywords.getStopCondition("TEST_LENGTH") != -1);
		assertTrue(Keywords.getStopCondition("TEST_DURATION") != -1);
		assertTrue(Keywords.getStopCondition("REQUIREMENT_COVERAGE") != -1);
		assertTrue(Keywords.getStopCondition("REACHED_REQUIREMENT") != -1);

		try {
			assertTrue(Keywords.getStopCondition("REACHEDREQUIREMENT") == -1);
			assertTrue(Keywords.getStopCondition("") == -1);
			assertTrue(Keywords.getStopCondition(null) == -1);
		} catch (StopConditionException e) {
		}

		assertTrue(Keywords.getStopConditions().size() > 0);
	}

	public void testGetGenerator() throws GeneratorException {
		assertTrue(Keywords.getGenerator("RANDOM") != -1);
		assertTrue(Keywords.getGenerator("A_STAR") != -1);
		assertTrue(Keywords.getGenerator("LIST") != -1);
		assertTrue(Keywords.getGenerator("STUB") != -1);
		assertTrue(Keywords.getGenerator("REQUIREMENTS") != -1);

		try {
			assertTrue(Keywords.getGenerator("RASNDOM") == -1);
			assertTrue(Keywords.getGenerator("") == -1);
			assertTrue(Keywords.getGenerator(null) == -1);
		} catch (GeneratorException e) {
		}

		assertTrue(Keywords.getGenerators().size() > 0);
	}
}
