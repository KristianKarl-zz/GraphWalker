package test.org.tigris.mbt;

import java.io.File;
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

	private void moveMbtPropertiesFile()
	{
		File mbt_properties = new File( "mbt.properties" );
		if ( mbt_properties.exists() )
		{
			mbt_properties.renameTo( new File( "mbt.properties.bak" ) );
		}
		else
		{
			fail( "Expected mbt.properties to exist!" );
		}
		assertFalse( new File( "mbt.properties" ).exists() );
	}

	private void restoreMbtPropertiesFile()
	{
		File mbt_properties = new File( "mbt.properties.bak" );
		if ( mbt_properties.exists() )
		{
			mbt_properties.renameTo( new File( "mbt.properties" ) );
		}
		else
		{
			fail( "Expected mbt.properties.bak to exist!" );
		}
		assertFalse( new File( "mbt.properties.bak" ).exists() );
	}

    /**
     * Test command: java -jar mbt.jar 
     */
    public void testNoArgs()
    {
		String args[] = {  };
    	runCommand( args );
		pattern = Pattern.compile( "Type 'java -jar mbt.jar help' for usage.", Pattern.MULTILINE );
		matcher = pattern.matcher( errMsg );
		assertTrue( matcher.find() );
		assertTrue( "Nothing should be written to standard output.", outMsg.isEmpty() );
    }

    /**
     * Test command: java -jar mbt.jar -v
     */
    public void testVersion()
    {
		String args[] = { "-v" };
    	runCommand( args );
		assertTrue( "No error messages should occur.", errMsg.isEmpty() );
		pattern = Pattern.compile( "^org\\.tigris\\.mbt version 2\\.[\\.0-9]+ \\(revision [0-9]+\\)", Pattern.MULTILINE );
		matcher = pattern.matcher( outMsg );
		assertTrue( matcher.find() );
    }

    /**
     * Test command: java -jar mbt.jar 
     */
    public void testNoMbtPropertiesFile()
    {
		String args[] = {  };
		moveMbtPropertiesFile();
    	runCommand( args );
		restoreMbtPropertiesFile();
		pattern = Pattern.compile( "Type 'java -jar mbt.jar help' for usage.", Pattern.MULTILINE );
		matcher = pattern.matcher( errMsg );
		assertTrue( matcher.find() );
		assertTrue( "Nothing should be written to standard output.", outMsg.isEmpty() );
    }

    /**
     * Test command: java -jar mbt.jar offline -f graphml/reqtags/ExtendedMain.graphml -g RANDOM -s TEST_LENGTH:10
     */
    public void testNoMbtPropertiesFileOffline()
    {
		String args[] = { "offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "RANDOM", "-s", "TEST_LENGTH:10" };
		moveMbtPropertiesFile();
    	runCommand( args );
		restoreMbtPropertiesFile();
		System.out.println( errMsg );
    	assertEquals( "No error messages should occur.", "", errMsg);
    	assertEquals( 20, outMsg.split("\r\n|\r|\n").length); // 20 rows as 1 step = 1 edge
    }

    /**
     * Test command: java -jar mbt.jar sputnik
     */
    public void testUnkownCommand()
    {
		String args[] = { "sputnik" };
    	runCommand( args );
		pattern = Pattern.compile( "^Unkown command: .*\\s+", Pattern.MULTILINE );
		matcher = pattern.matcher( errMsg );
		assertTrue( matcher.find() );
		assertTrue( "Nothing should be written to standard output.", outMsg.isEmpty() );
    }

    /**
     * Test command: java -jar mbt.jar offline -f graphml/reqtags/ExtendedMain.graphml -g SHORTEST -s EDGE_COVERAGE:100
     */
    public void testOfflineShortestEdgeCoverage()
    {
		String args[] = { "offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "SHORTEST", "-s", "EDGE_COVERAGE:100" };
    	runCommand( args );
    	assertEquals( "No error messages should occur.", "", errMsg);
    	assertTrue( outMsg.split("\r\n|\r|\n").length <= 110 );
    }

    /**
     * Test command: java -jar mbt.jar offline -f graphml/reqtags/ExtendedMain.graphml -g RANDOM -s EDGE_COVERAGE:100
     */
    public void testOfflineRandomEdgeCoverage()
    {
		String args[] = { "offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "RANDOM", "-s", "EDGE_COVERAGE:100" };
    	runCommand( args );
    	assertTrue( "No error messgaes should occur.", errMsg.isEmpty() );
    	assertTrue( "Expected at least 78 lines, got: " + outMsg.split("\r\n|\r|\n").length, outMsg.split("\r\n|\r|\n").length >= 78 );
    }

    /**
     * Test command: java -jar mbt.jar offline -f graphml/reqtags/ExtendedMain.graphml -g RANDOM -s STATE_COVERAGE:100
     */
    public void testOfflineRandomStateCoverage()
    {
		String args[] = { "offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "RANDOM", "-s", "STATE_COVERAGE:100" };
    	runCommand( args );
    	assertTrue( "No error messages should occur.", errMsg.isEmpty() );
    	assertTrue( "Expected at least 24 lines, got: " + outMsg.split("\r\n|\r|\n").length, outMsg.split("\r\n|\r|\n").length >= 24 );
    }

    /**
     * Test command: java -jar mbt.jar offline -f graphml/reqtags/ExtendedMain.graphml -g SHORTEST -s STATE_COVERAGE:100
     */
    public void testOfflineShortestStateCoverage()
    {
		String args[] = { "offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "SHORTEST", "-s", "STATE_COVERAGE:100" };
    	runCommand( args );
    	assertEquals( "No error messages should occur.", "", errMsg );
    	assertTrue( outMsg.split("\r\n|\r|\n").length <= 32 );
    }
    
    /**
     * Test command: java -jar mbt.jar offline -f graphml/reqtags/ExtendedMain.graphml -g shortest -s "REACHED_REQUIREMENT:req 78
     */
    public void testOfflineShortestReachedRequirement()
    {
		String args[] = { "offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "SHORTEST", "-s", "REACHED_REQUIREMENT:req 78" };
    	runCommand( args );
    	assertEquals( "No error messages should occur.", "", errMsg );
    	assertEquals( 6, outMsg.split("\r\n|\r|\n").length );
    }
    
    /**
     * Test command: java -jar mbt.jar requirements -f graphml/reqtags/ExtendedMain.graphml
     */
    public void testListReqTags()
    {
		String args[] = { "requirements", "-f", "graphml/reqtags/ExtendedMain.graphml" };
    	runCommand( args );
    	assertTrue( "No error messages should occur.", errMsg.isEmpty() );
    	assertEquals( 6, outMsg.split("\r\n|\r|\n").length );
    }

    /**
     * Test command: java -jar mbt.jar source -f graphml/methods/Main.graphml -t templates/perl.template
     */
    public void testGenerateCodeFromTemplate()
    {
		String args[] = { "source", "-f", "graphml/methods/Main.graphml", "-t", "templates/perl.template" };
    	runCommand( args );
    	assertTrue( "No error messages should occur.", errMsg.isEmpty() );
		pattern = Pattern.compile( " implements the ", Pattern.MULTILINE );
		matcher = pattern.matcher( outMsg );
		assertTrue( matcher.find() );
    }

    /**
     * Test command: java -jar mbt.jar offline -f graphml/misc/missing_inedges.graphml -g RANDOM -s EDGE_COVERAGE:100
     */
    public void testNoVerticesWithNoInEdges()
    {
		String args[] = { "offline", "-f", "graphml/misc/missing_inedges.graphml", "-g", "RANDOM", "-s", "EDGE_COVERAGE:100" };
    	runCommand( args );
		pattern = Pattern.compile( "^No in-edges! The vertex: .* is not reachable, from file: 'graphml.misc.missing_inedges.graphml'$", Pattern.MULTILINE );
		matcher = pattern.matcher( errMsg );
		assertTrue( matcher.find() );
    }

    /**
     * Test command: java -jar mbt.jar offline -f graphml/misc/missing_inedges.graphml -g RANDOM -s EDGE_COVERAGE:100
     */
    public void testVertexWithNoInEdges()
    {
		String args[] = { "offline", "-f", "graphml/misc/missing_inedges.graphml", "-g", "RANDOM", "-s", "EDGE_COVERAGE:100" };
    	runCommand( args );
		pattern = Pattern.compile( "No in-edges! The vertex: 'v_InvalidKey', INDEX=9 is not reachable.", Pattern.MULTILINE );
		matcher = pattern.matcher( errMsg );
		assertTrue( matcher.find() );
    }

    /**
     * Test command: java -jar mbt.jar online -f graphml/methods/Main.graphml -g RANDOM -s TEST_DURATION:10
     */
    public void testRandom10seconds()
    {
		String args[] = { "online", "-f", "graphml/methods/Main.graphml", "-g", "RANDOM", "-s", "TEST_DURATION:10", "-o", "1" };
    	InputStream oldInputStream = System.in; //backup
    	System.setIn( redirectIn() );
		long startTime = System.currentTimeMillis();
    	runCommand( args );
    	long runTime = System.currentTimeMillis() - startTime;
    	System.setIn( oldInputStream );
    	assertEquals( "No error messages should occur.", "", errMsg );
		assertTrue( ( runTime - 10000 ) < 3000 );
    }
         
    /**
     * Test command: java -jar mbt.jar methods -f graphml/methods/Main.graphml
     */
    public void testCountMethods()
    {
		String args[] = { "methods", "-f", "graphml/methods/Main.graphml" };
    	runCommand( args );
    	assertTrue( "No error messages should occur.", errMsg.isEmpty() );
		pattern = Pattern.compile( "e_Cancel\\s+e_CloseApp\\s+e_CloseDB\\s+e_CloseDialog\\s+e_EnterCorrectKey\\s+e_EnterInvalidKey\\s+e_Initialize\\s+e_No\\s+e_Start\\s+e_StartWithDatabase\\s+e_Yes\\s+v_EnterMasterCompositeMasterKey\\s+v_InvalidKey\\s+v_KeePassNotRunning\\s+v_MainWindowEmpty\\s+v_MainWindow_DB_Loaded\\s+v_SaveBeforeCloseLock", Pattern.MULTILINE );
		matcher = pattern.matcher( outMsg );
		assertTrue( matcher.find() );
    }
      
    /**
     * Check for reserved keywords
     * Test command: java -jar mbt.jar methods -f graphml/test24
     */
    public void testReservedKeywords()
    {
		String args[] = { "methods", "-f", "graphml/test24" };
    	runCommand( args );
		pattern = Pattern.compile( "Edge has a label 'BACKTRACK', which is a reserved keyword, in file: '.*graphml.test24.(Camera|Time).graphml'", Pattern.MULTILINE );
		matcher = pattern.matcher( errMsg );
		assertTrue( matcher.find() );
    }
    
    /**
     * Check for reserved keywords
     * Test command: java -jar mbt.jar methods -f graphml/test24
     */
    public void testXmlSetup()
    {
		String args[] = { "xml", "-f", "graphml/reqtags/mbt_init6.xml" };
    	runCommand( args );
    	assertTrue( "No error messages should occur.", errMsg.isEmpty() );
    	assertEquals( 6, outMsg.split("\r\n|\r|\n").length );
    }
    
}
