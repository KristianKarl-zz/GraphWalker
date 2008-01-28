package test.org.tigris.mbt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tigris.mbt.CLI;

import junit.framework.TestCase;

public class CLITest extends TestCase {

	Pattern pattern;
	Matcher matcher;
	StringBuffer stdOutput;
	StringBuffer errOutput;
	String outMsg;
	String errMsg;

	
	private OutputStream redirectOut()
	{
		return new OutputStream() {
			public void write(int b) throws IOException {
				stdOutput.append( Character.toString((char) b) );
			}
		};
	}

	private OutputStream redirectErr()
	{
		return new OutputStream() {
			public void write(int b) throws IOException {
				errOutput.append( Character.toString((char) b) );
			}
		};
	}
	
	private InputStream redirectIn()
	{
		return new InputStream() {
			public int read() throws IOException {
				try {
					Thread.sleep( 300 );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return '0';
			}
		};
	}
	
	private void runCommand( String args[] )
	{
		stdOutput = new StringBuffer();
		errOutput = new StringBuffer();
		
    	PrintStream outStream = new PrintStream( redirectOut() );
    	PrintStream oldOutStream = System.out; //backup
    	PrintStream errStream = new PrintStream( redirectErr() );
    	PrintStream oldErrStream = System.err; //backup
    	
    	System.setOut( outStream );
    	System.setErr( errStream );
    	
    	CLI.main( args );
    	
    	System.setOut( oldOutStream );
    	System.setErr( oldErrStream );		

    	outMsg = stdOutput.toString();
    	errMsg = errOutput.toString();
	}

    
    /**
     * Test command: offline -f graphml/reqtags/ExtendedMain.graphml -g RANDOM -s EDGE_COVERAGE:100
     */
    public void testOfflineRandomEdgeCoverage()
    {
		String args[] = { "offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "RANDOM", "-s", "EDGE_COVERAGE:100" } ;
    	runCommand( args );
    	assertTrue( "No error messgaes should occur.", errMsg.isEmpty()  );
    	String[] newLines = outMsg.split("\r\n|\r|\n");
    	assertTrue( "At least 78, or more lines should be returned.", newLines.length >= 78 );
    }

    /**
     * Test command: offline -f graphml/reqtags/ExtendedMain.graphml -g SHORTEST -s EDGE_COVERAGE:100
     */
    public void testOfflineShortestEdgeCoverage()
    {
		String args[] = { "offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "SHORTEST", "-s", "EDGE_COVERAGE:100" } ;
    	runCommand( args );
    	assertTrue( "No error messages should occur.", errMsg.isEmpty()  );
    	String[] newLines = outMsg.split("\r\n|\r|\n");
    	assertTrue( "At least 78, or more lines should be returned.", newLines.length == 78 );
    }

    /**
     * Test command: offline -f graphml/reqtags/ExtendedMain.graphml -g RANDOM -s STATE_COVERAGE:100
     */
    public void testOfflineRandomStateCoverage()
    {
		String args[] = { "offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "RANDOM", "-s", "STATE_COVERAGE:100" } ;
    	runCommand( args );
    	assertTrue( "No error messages should occur.", errMsg.isEmpty()  );
    	String[] newLines = outMsg.split("\r\n|\r|\n");
    	assertTrue( "At least 24, or more lines should be returned.", newLines.length >= 24 );
    }

    /**
     * Test command: offline -f graphml/reqtags/ExtendedMain.graphml -g SHORTEST -s STATE_COVERAGE:100
     */
    public void testOfflineShortestStateCoverage()
    {
		String args[] = { "offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "SHORTEST", "-s", "STATE_COVERAGE:100" } ;
    	runCommand( args );
    	assertTrue( "No error messages should occur.", errMsg.isEmpty()  );
    	String[] newLines = outMsg.split("\r\n|\r|\n");
    	assertTrue( "At least 24, or more lines should be returned.", newLines.length == 24 );
    }

    
    /**
     * Test command: offline -f graphml/reqtags/ExtendedMain.graphml -g shortest -s "REACHED_REQUIREMENT:req 78
     */
    public void testOfflineShortestReachedRequirement()
    {
		String args[] = { "offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "SHORTEST", "-s", "REACHED_REQUIREMENT:req 78" } ;
    	runCommand( args );
    	assertTrue( "No error messages should occur.", errMsg.isEmpty()  );
    	String[] newLines = outMsg.split("\r\n|\r|\n");
    	assertTrue( "At least 6, or more lines should be returned.", newLines.length == 6 );
    }

    
    /**
     * Test command: requirements -f graphml/reqtags/ExtendedMain.graphml
     */
    public void testListReqTags()
    {
		String args[] = { "requirements", "-f", "graphml/reqtags/ExtendedMain.graphml" } ;
    	runCommand( args );
    	assertTrue( "No error messages should occur.", errMsg.isEmpty()  );
    	String[] newLines = outMsg.split("\r\n|\r|\n");
    	assertTrue( "At least 6, or more lines should be returned.", newLines.length == 6 );
    }

    
    
    /**
     * Test command: 
     */
    public void testNoArgs()
    {
		String args[] = {  } ;
    	runCommand( args );
		pattern = Pattern.compile( "Type 'java -jar mbt.jar help' for usage.", Pattern.MULTILINE );
		matcher = pattern.matcher( errMsg );
		assertTrue( matcher.find() );
    }

    
    
    /**
     * Test command: source -f graphml/methods/Main.graphml -t templates/perl.template
     */
    public void testGenerateCodeFromTemplate()
    {
		String args[] = { "source", "-f", "graphml/methods/Main.graphml", "-t", "templates/perl.template" } ;
    	runCommand( args );
    	assertTrue( "No error messages should occur.", errMsg.isEmpty()  );
		pattern = Pattern.compile( " implements the ", Pattern.MULTILINE );
		matcher = pattern.matcher( outMsg );
		assertTrue( matcher.find() );
    }

    
    
    /**
     * Test command: offline -f graphml/misc/missing_inedges.graphml -g RANDOM -s EDGE_COVERAGE:100
     */
    public void testNoVerticesWithNoInEdges()
    {
		String args[] = { "offline", "-f", "graphml/misc/missing_inedges.graphml", "-g", "RANDOM", "-s", "EDGE_COVERAGE:100" } ;
    	runCommand( args );
		pattern = Pattern.compile( "^No in-edges! The vertex: .* is not reachable, from file: 'graphml.misc.missing_inedges.graphml'$", Pattern.MULTILINE );
		matcher = pattern.matcher( errMsg );
		assertTrue( matcher.find() );
    }

    
    
    /**
     * Test command: offline -f graphml/misc/missing_inedges.graphml -g RANDOM -s EDGE_COVERAGE:100
     */
    public void testVertexWithNoInEdges()
    {
		String args[] = { "offline", "-f", "graphml/misc/missing_inedges.graphml", "-g", "RANDOM", "-s", "EDGE_COVERAGE:100" } ;
    	runCommand( args );
		pattern = Pattern.compile( "No in-edges! The vertex: 'v_InvalidKey', INDEX=9 is not reachable.", Pattern.MULTILINE );
		matcher = pattern.matcher( errMsg );
		assertTrue( matcher.find() );
    }

    
        
    /**
     * Test command: online -f graphml/methods/Main.graphml -g RANDOM -s TEST_DURATION:10
     */
    public void testRandom10seconds()
    {
		String args[] = { "online", "-f", "graphml/methods/Main.graphml", "-g", "RANDOM", "-s", "TEST_DURATION:10" } ;
    	InputStream oldInputStream = System.in; //backup
    	System.setIn( redirectIn() );
		long startTime = System.currentTimeMillis();
    	runCommand( args );
    	long runTime = System.currentTimeMillis() - startTime;
    	System.setIn( oldInputStream );
    	assertTrue( "No error messages should occur.", errMsg.isEmpty()  );
		assertTrue( ( runTime - 10000 ) < 3000 );
    }

    
        
    /**
     * Test command: methods -f graphml/methods/Main.graphml
     */
    public void testCountMethods()
    {
		String args[] = { "methods", "-f", "graphml/methods/Main.graphml" } ;
    	runCommand( args );
    	assertTrue( "No error messages should occur.", errMsg.isEmpty()  );
		pattern = Pattern.compile( "e_Cancel\\s+e_CloseApp\\s+e_CloseDB\\s+e_CloseDialog\\s+e_EnterCorrectKey\\s+e_EnterInvalidKey\\s+e_Initialize\\s+e_No\\s+e_Start\\s+e_StartWithDatabase\\s+e_Yes\\s+v_EnterMasterCompositeMasterKey\\s+v_InvalidKey\\s+v_KeePassNotRunning\\s+v_MainWindowEmpty\\s+v_MainWindow_DB_Loaded\\s+v_SaveBeforeCloseLock", Pattern.MULTILINE );
		matcher = pattern.matcher( outMsg );
		assertTrue( matcher.find() );
    }

    
       
    /**
     * Check for reserved keywords
     * Test command: methods -f graphml/test24
     */
    public void testReservedKeywords()
    {
		String args[] = { "methods", "-f", "graphml/test24" } ;
    	runCommand( args );
		pattern = Pattern.compile( "Edge has a label 'BACKTRACK', which is a reserved keyword, in file: '.*graphml.test24.(Camera|Time).graphml'", Pattern.MULTILINE );
		matcher = pattern.matcher( errMsg );
		assertTrue( matcher.find() );
    }
}
