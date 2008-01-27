package test.org.tigris.mbt;

import java.io.FileInputStream;
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
	
	private void runCommand( CLI cli, String args[] )
	{
		stdOutput = new StringBuffer();
		errOutput = new StringBuffer();
		
    	PrintStream outStream = new PrintStream( redirectOut() );
    	PrintStream oldOutStream = System.out; //backup
    	PrintStream errStream = new PrintStream( redirectErr() );
    	PrintStream oldErrStream = System.err; //backup
    	
    	System.setOut( outStream );
    	System.setErr( errStream );
    	
    	cli.main( args );
    	
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
		System.out.println( "TEST: testOfflineRandomEdgeCoverage" );
		System.out.println( "=======================================================================" );
		String args[] = { "offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "RANDOM", "-s", "EDGE_COVERAGE:100" } ;
    	runCommand( new CLI(), args );
		
    	assertTrue( "No error messgaes should occur.", errMsg.trim().equals("") );
    	String[] newLines = outMsg.split("\r\n|\r|\n");
		System.out.println( "Number of lines returned: " + newLines.length );
    	assertTrue( "At least 78, or more lines should be returned.", newLines.length >= 78 );
    }

    /**
     * Test command: offline -f graphml/reqtags/ExtendedMain.graphml -g SHORTEST -s EDGE_COVERAGE:100
     */
    public void testOfflineShortestEdgeCoverage()
    {
		System.out.println( "TEST: testOfflineShortestEdgeCoverage" );
		System.out.println( "=======================================================================" );
		String args[] = { "offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "SHORTEST", "-s", "EDGE_COVERAGE:100" } ;
    	runCommand( new CLI(), args );
		
    	assertTrue( "No error messgaes should occur.", errMsg.trim().equals("")  );
    	String[] newLines = outMsg.split("\r\n|\r|\n");
		System.out.println( "Number of lines returned: " + newLines.length );
    	assertTrue( "At least 78, or more lines should be returned.", newLines.length == 78 );
    }

    /**
     * Test command: offline -f graphml/reqtags/ExtendedMain.graphml -g RANDOM -s STATE_COVERAGE:100
     */
    public void testOfflineRandomStateCoverage()
    {
		System.out.println( "TEST: testOfflineRandomStateCoverage" );
		System.out.println( "=======================================================================" );
		String args[] = { "offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "RANDOM", "-s", "STATE_COVERAGE:100" } ;
    	runCommand( new CLI(), args );
		
    	assertTrue( "No error messgaes should occur.", errMsg.trim().equals("")  );
    	String[] newLines = outMsg.split("\r\n|\r|\n");
		System.out.println( "Number of lines returned: " + newLines.length );
    	assertTrue( "At least 24, or more lines should be returned.", newLines.length >= 24 );
    }

    /**
     * Test command: offline -f graphml/reqtags/ExtendedMain.graphml -g SHORTEST -s STATE_COVERAGE:100
     */
    public void testOfflineShortestStateCoverage()
    {
		System.out.println( "TEST: testOfflineRandomStateCoverage" );
		System.out.println( "=======================================================================" );
		String args[] = { "offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "SHORTEST", "-s", "STATE_COVERAGE:100" } ;
    	runCommand( new CLI(), args );
		
    	assertTrue( "No error messgaes should occur.", errMsg.trim().equals("")  );
    	String[] newLines = outMsg.split("\r\n|\r|\n");
		System.out.println( "Number of lines returned: " + newLines.length );
    	assertTrue( "At least 24, or more lines should be returned.", newLines.length == 24 );
    }

    
    /**
     * Test command: offline -f graphml/reqtags/ExtendedMain.graphml -g shortest -s "REACHED_REQUIREMENT:req 78
     */
    public void testOfflineShortestReachedRequirement()
    {
		System.out.println( "TEST: testOfflineShortestReachedRequirement" );
		System.out.println( "=======================================================================" );
		String args[] = { "offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "SHORTEST", "-s", "REACHED_REQUIREMENT:req 78" } ;
    	runCommand( new CLI(), args );
		
    	assertTrue( "No error messgaes should occur.", errMsg.trim().equals("")  );
    	String[] newLines = outMsg.split("\r\n|\r|\n");
		System.out.println( "Number of lines returned: " + newLines.length );
    	assertTrue( "At least 6, or more lines should be returned.", newLines.length == 6 );
    }

    
    // Check for reserved keywords 
    public void testListReqTags()
    {
		System.out.println( "TEST: testListReqTags" );
		System.out.println( "=======================================================================" );
		String args[] = { "requirements", "-f", "graphml/reqtags/ExtendedMain.graphml" } ;
    	runCommand( new CLI(), args );
    	
    	assertTrue( "No error messgaes should occur.", errMsg.trim().equals("")  );
    	String[] newLines = outMsg.split("\r\n|\r|\n");
		System.out.println( "Number of lines returned: " + newLines.length );
    	assertTrue( "At least 6, or more lines should be returned.", newLines.length == 6 );
    }

    
    
    public void testNoArgs()
    {
		System.out.println( "TEST: testNoArgs" );
		System.out.println( "=======================================================================" );
		String args[] = {  } ;
    	runCommand( new CLI(), args );
    	
		pattern = Pattern.compile( "Type 'java -jar mbt.jar help' for usage.", Pattern.MULTILINE );
		matcher = pattern.matcher( errMsg );
		assertTrue( matcher.find() );
		System.out.println( "" );
    }

    
    
    public void testGenerateCodeFromTemplate()
    {
		System.out.println( "TEST: testGenerateCodeFromTemplate" );
		System.out.println( "=======================================================================" );
		String args[] = { "source", "-f", "graphml/methods/Main.graphml", "-t", "templates/perl.template" } ;
    	runCommand( new CLI(), args );
    	
		pattern = Pattern.compile( " implements the ", Pattern.MULTILINE );
		matcher = pattern.matcher( outMsg );
		assertTrue( matcher.find() );
		System.out.println( "" );
    }

    
    
    public void testNoVerticesWithNoInEdges()
    {
		System.out.println( "TEST: testNoVerticesWithNoInEdges" );
		System.out.println( "=======================================================================" );
		String args[] = { "offline", "-f", "graphml/misc/no_missing_inedges.graphml", "-g", "RANDOM", "-s", "EDGE_COVERAGE:100" } ;
    	runCommand( new CLI(), args );
    	
		System.out.println( outMsg );
		pattern = Pattern.compile( "^No in-edges! The vertex: .* is not reachable.$", Pattern.MULTILINE );
		matcher = pattern.matcher( errMsg );
		assertTrue( matcher.find() );
		System.out.println( "" );
    }

    
    
    public void testVertexWithNoInEdges()
    {
		System.out.println( "TEST: testVertexWithNoInEdges" );
		System.out.println( "=======================================================================" );
		String args[] = { "offline", "-f", "graphml/misc/missing_inedges.graphml", "-g", "RANDOM", "-s", "EDGE_COVERAGE:100" } ;
    	runCommand( new CLI(), args );
    	
		pattern = Pattern.compile( "No in-edges! The vertex: 'v_InvalidKey', INDEX=9 is not reachable.", Pattern.MULTILINE );
		matcher = pattern.matcher( errMsg );
		assertTrue( matcher.find() );
		System.out.println( "" );
    }

    
        
    public void testRandom10seconds()
    {
		System.out.println( "TEST: testRandom10seconds" );
		System.out.println( "=======================================================================" );
		System.out.println( "Please wait for 10 seconds..." );
		String args[] = { "online", "-f", "graphml/methods/Main.graphml", "-g", "RANDOM", "-s", "TEST_DURATION:10" } ;
		
		InputStream	stdin = null;
    	try
	    {
    		stdin = new FileInputStream( "graphml/methods/Redirect.in" );
	    }
    	catch (Exception e)
	    {
		    fail( "Redirect:  Unable to open input file!" );
	    }    	
    	System.setIn( stdin );
		
		
		long startTime = System.currentTimeMillis();
    	runCommand( new CLI(), args );
    	long runTime = System.currentTimeMillis() - startTime;
    	
    	assertTrue( "No error messgaes should occur.", errMsg.trim().equals("")  );
		System.out.println( "Runtime: " + runTime + " ms" );
		assertTrue( ( runTime - 10000 ) < 3000 );
		System.out.println( "" );
    }

    
        
    public void testCountMethods()
    {
		System.out.println( "TEST: testCountMethods" );
		System.out.println( "=======================================================================" );
		String args[] = { "methods", "-f", "graphml/methods/Main.graphml" } ;
    	runCommand( new CLI(), args );
    	
		pattern = Pattern.compile( "e_Cancel\\s+e_CloseApp\\s+e_CloseDB\\s+e_CloseDialog\\s+e_EnterCorrectKey\\s+e_EnterInvalidKey\\s+e_Initialize\\s+e_No\\s+e_Start\\s+e_StartWithDatabase\\s+e_Yes\\s+v_EnterMasterCompositeMasterKey\\s+v_InvalidKey\\s+v_KeePassNotRunning\\s+v_MainWindowEmpty\\s+v_MainWindow_DB_Loaded\\s+v_SaveBeforeCloseLock", Pattern.MULTILINE );
		matcher = pattern.matcher( outMsg );
		assertTrue( matcher.find() );
		System.out.println( "" );
    }

    
       
    public void testContinueForCulDeSac()
    {
		System.out.println( "TEST: testContinueForCulDeSac" );
		System.out.println( "=======================================================================" );
		String args[] = { "merge", "-c", "f", "graphml/CulDeSac" } ;
    	runCommand( new CLI(), args );
    	
		pattern = Pattern.compile( "Found a cul-de-sac. Vertex has no out-edges: '.*', in file: '.*'", Pattern.MULTILINE );
		matcher = pattern.matcher( errMsg );
		assertTrue( matcher.find() );
		System.out.println( "" );
    }
    
    // Check for reserved keywords 
    public void testReservedKeywords()
    {
		System.out.println( "TEST: testReservedKeywords" );
		System.out.println( "=======================================================================" );
		String args[] = { "methods", "-f", "graphml/test24" } ;
    	runCommand( new CLI(), args );
    	
		pattern = Pattern.compile( "Edge has a label 'BACKTRACK', which is a reserved keyword, in file: '.*graphml.test24.(Camera|Time).graphml'", Pattern.MULTILINE );
		matcher = pattern.matcher( errMsg );
		assertTrue( matcher.find() );
		System.out.println( "" );
    }
}
