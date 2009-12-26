/**
 * 
 */
package test.org.tigris.mbt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.tigris.mbt.Keywords;
import org.tigris.mbt.ModelBasedTesting;
import org.tigris.mbt.Util;
import org.tigris.mbt.exceptions.GeneratorException;
import org.tigris.mbt.exceptions.InvalidDataException;
import org.tigris.mbt.exceptions.StopConditionException;
import org.tigris.mbt.generators.NonOptimizedShortestPath;

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

	public void testXmlLoading_Simple() throws StopConditionException, GeneratorException, IOException {
		ModelBasedTesting mbt = Util.loadMbtFromXml("graphml/reqtags/mbt_init.xml");
		assertEquals("RANDOM{EC>=100}", mbt.toString());
	}

	public void testXmlLoading_Moderate() throws StopConditionException, GeneratorException, IOException {
		ModelBasedTesting mbt = Util.loadMbtFromXml("graphml/reqtags/mbt_init2.xml");
		assertEquals("RANDOM{((EC>=100 AND SC>=100) OR L=50)}", mbt.toString());
	}

	public void testXmlLoading_Advanced() throws StopConditionException, GeneratorException, IOException {
		ModelBasedTesting mbt = Util.loadMbtFromXml("graphml/reqtags/mbt_init3.xml");
		assertEquals("RANDOM{EC>=10}\nRANDOM{(SC>=30 AND EC>=10)}", mbt.toString());
	}

	public void testXmlLoading_OfflineStub() throws StopConditionException, GeneratorException, IOException {
		ModelBasedTesting mbt = Util.loadMbtFromXml("graphml/reqtags/mbt_init4.xml");
		assertEquals("CODE", mbt.toString());
		File f = new File("mbt_init4.java");
		assertTrue(f.exists());
		assertTrue(f.delete());
		assertFalse(f.exists());
	}

	public void testXmlLoading_JavaExecution() throws StopConditionException, GeneratorException, IOException {
		ModelBasedTesting mbt = Util.loadMbtFromXml("graphml/reqtags/mbt_init5.xml");
		assertEquals("RANDOM{SC>=40}", mbt.toString());
	}

	public void testXmlLoading_OfflineRequirements() throws StopConditionException, GeneratorException, IOException {
		PrintStream oldOut = System.out;
		ByteArrayOutputStream innerOut = new ByteArrayOutputStream();

		System.setOut(new PrintStream(innerOut));
		ModelBasedTesting mbt = Util.loadMbtFromXml("graphml/reqtags/mbt_init6.xml");
		System.setOut(oldOut);

		assertEquals("REQUIREMENTS", mbt.toString());
		assertEquals(6, innerOut.toString().trim().split("\r\n|\r|\n").length);
	}

	public void testXmlLoading_OnlineRequirements() throws StopConditionException, GeneratorException, IOException {
		InputStream oldIn = System.in;
		PrintStream oldOut = System.out;
		ByteArrayOutputStream innerOut = new ByteArrayOutputStream();

		System.setOut(new PrintStream(innerOut));
		System.setIn(redirectIn());
		ModelBasedTesting mbt = Util.loadMbtFromXml("graphml/reqtags/mbt_init7.xml");
		System.setIn(oldIn);
		System.setOut(oldOut);

		assertEquals("REQUIREMENTS", mbt.toString());
		assertEquals(6, innerOut.toString().trim().split("\r\n|\r|\n").length);
	}

	public void testGetdataValue() throws InvalidDataException, StopConditionException, GeneratorException, IOException {
		InputStream oldIn = System.in;
		PrintStream oldOut = System.out;
		ByteArrayOutputStream innerOut = new ByteArrayOutputStream();

		System.setOut(new PrintStream(innerOut));
		System.setIn(redirectIn());
		ModelBasedTesting mbt = Util.loadMbtFromXml("graphml/reqtags/mbt_init10.xml");
		System.setIn(oldIn);
		System.setOut(oldOut);

		System.out.print(mbt.getDataValue("incorrect"));

		assertEquals("0", mbt.getDataValue("incorrect"));
	}

	public void testExecAction() throws InvalidDataException, StopConditionException, GeneratorException, IOException {
		InputStream oldIn = System.in;
		PrintStream oldOut = System.out;
		ByteArrayOutputStream innerOut = new ByteArrayOutputStream();

		System.setOut(new PrintStream(innerOut));
		System.setIn(redirectIn());
		ModelBasedTesting mbt = Util.loadMbtFromXml("graphml/reqtags/mbt_init11.xml");
		System.setIn(oldIn);
		System.setOut(oldOut);

		System.out.print(mbt.execAction("str.toUpperCase()"));

		assertEquals("ABC", mbt.execAction("str.toUpperCase()"));
	}

	public void testPassRequirement() throws StopConditionException, GeneratorException, IOException {
		ModelBasedTesting mbt;
		mbt = Util.loadMbtFromXml("xml/reqCoverage.xml");
		mbt.passRequirement(true);
		mbt.passRequirement(false);
		mbt.passRequirement(true);
	}

	public void testNewState() throws StopConditionException {
		ModelBasedTesting mbt = ModelBasedTesting.getInstance();
		mbt.readGraph("graphml/test.org.tigris.mbt.unittest/ModelBasedTestingTest.testNewState.graphml");
		mbt.enableExtended(false);
		mbt.setWeighted(false);
		NonOptimizedShortestPath generator = new NonOptimizedShortestPath();
		generator.setStopCondition(Util.getCondition(Keywords.CONDITION_EDGE_COVERAGE, "100"));
		mbt.setGenerator(generator);

		String[] pair = mbt.getNextStep();
		assertEquals("e_init", pair[0]);
		assertEquals("v_BrowserStopped", pair[1]);

		pair = mbt.getNextStep();
		assertEquals("e_StartBrowser", pair[0]);
		assertEquals("v_BrowserStarted", pair[1]);

		pair = mbt.getNextStep();
		assertEquals("e_EnterBaseURL", pair[0]);
		assertEquals("v_BaseURL", pair[1]);

		assertEquals(false, mbt.setCurrentVertex("foobar"));
		assertEquals(true, mbt.setCurrentVertex(null));
		pair = mbt.getNextStep();
		assertEquals("e_init", pair[0]);
		assertEquals("v_BrowserStopped", pair[1]);

		pair = mbt.getNextStep();
		assertEquals("e_StartBrowser", pair[0]);
		assertEquals("v_BrowserStarted", pair[1]);

		pair = mbt.getNextStep();
		assertEquals("e_EnterBaseURL", pair[0]);
		assertEquals("v_BaseURL", pair[1]);

		assertEquals(true, mbt.setCurrentVertex(""));
		pair = mbt.getNextStep();
		assertEquals("e_init", pair[0]);
		assertEquals("v_BrowserStopped", pair[1]);

		pair = mbt.getNextStep();
		assertEquals("e_StartBrowser", pair[0]);
		assertEquals("v_BrowserStarted", pair[1]);

		pair = mbt.getNextStep();
		assertEquals("e_EnterBaseURL", pair[0]);
		assertEquals("v_BaseURL", pair[1]);

		assertEquals(true, mbt.setCurrentVertex("v_BrowserStopped"));
		pair = mbt.getNextStep();
		assertEquals("e_StartBrowser", pair[0]);
		assertEquals("v_BrowserStarted", pair[1]);

		pair = mbt.getNextStep();
		assertEquals("e_EnterBaseURL", pair[0]);
		assertEquals("v_BaseURL", pair[1]);
	}
}
