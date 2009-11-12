package test.org.tigris.mbt.bugs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.tigris.mbt.CLI;
import org.tigris.mbt.ModelBasedTesting;
import org.tigris.mbt.Util;

import test.org.tigris.mbt.CLITest;
import junit.framework.TestCase;

public class Issue_9 extends TestCase {

	Pattern pattern;
	Matcher matcher;
	StringBuffer stdOutput;
	StringBuffer errOutput;
	String outMsg;
	String errMsg;
	static Logger logger = Util.setupLogger(CLITest.class);

	protected void setUp() throws Exception {
		super.setUp();
		ModelBasedTesting.getInstance().reset();
	}

	private OutputStream redirectOut() {
		return new OutputStream() {
			public void write(int b) throws IOException {
				// Redirect to nothing, else we'll get an OutOfMemoryException
				// stdOutput.append( Character.toString((char) b) );
			}
		};
	}

	private OutputStream redirectErr() {
		return new OutputStream() {
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
		String args[] = { "xml", "-f", "xml/test.org.tigris.mbt.bugs/Issue_9.xml" };
		runCommand(args);
		assertTrue("No error messages should occur.", errMsg.isEmpty());
	}
}
