package test.org.tigris.mbt;

import org.tigris.mbt.Keywords;
import org.tigris.mbt.exceptions.GeneratorException;
import org.tigris.mbt.exceptions.StopConditionException;

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
