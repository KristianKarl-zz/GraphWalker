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

	StringBuffer stdOutput = new StringBuffer();
        
    
    // Check for reserved keywords 
    public void testListReqTags()
    {
		System.out.println( "TEST: testListReqTags" );
		System.out.println( "=======================================================================" );
		String args[] = new String[ 3 ];
		args[ 0 ] = "requirements";
		args[ 1 ] = "-f";
		args[ 2 ] = "graphml/reqtags/ExtendedMain.graphml";
    	CLI cli = new CLI();
    	
    	
    	OutputStream out = new OutputStream() {
    		public void write(int b) throws IOException {
    			stdOutput.append( Character.toString((char) b) );
    		}
   		};
    	PrintStream stream = new PrintStream( out );
    	PrintStream oldStream = System.out; //backup
    	System.setOut( stream );
    	cli.main( args );
    	System.setOut( oldStream );
    	
    	String msg = stdOutput.toString();
		System.out.println( msg );
		Pattern p = Pattern.compile( "req 23", Pattern.MULTILINE );
		Matcher m = p.matcher( msg );
		assertTrue( m.find() );
		System.out.println( "" );
    }

    
    
    public void testNoArgs()
    {
		System.out.println( "TEST: testNoArgs" );
		System.out.println( "=======================================================================" );
		String args[] = new String[ 0 ];
    	CLI cli = new CLI();
    	
    	OutputStream out = new OutputStream() {
    		public void write(int b) throws IOException {
    			stdOutput.append( Character.toString((char) b) );
    		}
   		};
    	PrintStream stream = new PrintStream( out );
    	PrintStream oldOutStream = System.out; //backup
    	System.setOut( stream );
    	cli.main( args );
    	System.setOut( oldOutStream );
    	
    	String msg = stdOutput.toString();
		System.out.println( msg );
		Pattern p = Pattern.compile( "Type 'java -jar mbt.jar help' for usage.", Pattern.MULTILINE );
		Matcher m = p.matcher( msg );
		assertTrue( m.find() );
		System.out.println( "" );
    }

    
    
    public void testGenerateCodeFromTemplate()
    {
		System.out.println( "TEST: testGenerateCodeFromTemplate" );
		System.out.println( "=======================================================================" );
		String args[] = new String[ 5 ];
		args[ 0 ] = "source";
		args[ 1 ] = "-f";
		args[ 2 ] = "graphml/methods/Main.graphml";
		args[ 3 ] = "-t";
		args[ 4 ] = "templates/perl.template";
    	CLI cli = new CLI();
    	
    	OutputStream out = new OutputStream() {
    		public void write(int b) throws IOException {
    			stdOutput.append( Character.toString((char) b) );
    		}
   		};
    	PrintStream stream = new PrintStream( out );
    	PrintStream oldOutStream = System.out; //backup
    	System.setOut( stream );
    	cli.main( args );
    	System.setOut( oldOutStream );
    	
    	String msg = stdOutput.toString();
		System.out.println( msg );
		Pattern p = Pattern.compile( " implements the ", Pattern.MULTILINE );
		Matcher m = p.matcher( msg );
		assertTrue( m.find() );
		System.out.println( "" );
    }

    
    
    public void testNoVerticesWithNoInEdges()
    {
		System.out.println( "TEST: testNoVerticesWithNoInEdges" );
		System.out.println( "=======================================================================" );
		String args[] = new String[ 7 ];
		args[ 0 ] = "offline";
		args[ 1 ] = "-e";
		args[ 2 ] = "100";
		args[ 3 ] = "-g";
		args[ 4 ] = "random";
		args[ 5 ] = "-f";
		args[ 6 ] = "graphml/misc/no_missing_inedges.graphml";
    	CLI cli = new CLI();
    	
    	OutputStream out = new OutputStream() {
    		public void write(int b) throws IOException {
    			stdOutput.append( Character.toString((char) b) );
    		}
   		};
    	PrintStream stream = new PrintStream( out );
    	PrintStream oldErrStream = System.err; //backup
    	System.setErr( stream );
    	cli.main( args );
    	System.setErr( oldErrStream );
    	
    	String msg = stdOutput.toString();
		System.out.println( msg );
		
		if ( msg != null )
		{		
			Pattern p = Pattern.compile( "^No in-edges! The vertex: .* is not reachable.$", Pattern.MULTILINE );
			Matcher m = p.matcher( msg );
			assertTrue( !m.find() );
	
			p = Pattern.compile( "null", Pattern.MULTILINE );
			m = p.matcher( msg );
			assertFalse( m.find() );
		}

		System.out.println( "" );
    }

    
    
    public void testVertexWithNoInEdges()
    {
		System.out.println( "TEST: testVertexWithNoInEdges" );
		System.out.println( "=======================================================================" );
		String args[] = new String[ 7 ];
		args[ 0 ] = "offline";
		args[ 1 ] = "-e";
		args[ 2 ] = "100";
		args[ 3 ] = "-g";
		args[ 4 ] = "random";
		args[ 5 ] = "-f";
		args[ 6 ] = "graphml/misc/missing_inedges.graphml";
    	CLI cli = new CLI();
    	
    	OutputStream out = new OutputStream() {
    		public void write(int b) throws IOException {
    			stdOutput.append( Character.toString((char) b) );
    		}
   		};
    	PrintStream stream = new PrintStream( out );
    	PrintStream oldErrStream = System.err; //backup
    	System.setErr( stream );
    	cli.main( args );
    	System.setErr( oldErrStream );
    	
    	String msg = stdOutput.toString();
		System.out.println( msg );
		Pattern p = Pattern.compile( "No in-edges! The vertex: 'v_InvalidKey', INDEX=9 is not reachable.", Pattern.MULTILINE );
		Matcher m = p.matcher( msg );
		assertTrue( m.find() );
		System.out.println( "" );
    }

    
        
    public void testRandom10seconds()
    {
		System.out.println( "TEST: testRandom10seconds" );
		System.out.println( "=======================================================================" );
		System.out.println( "Please wait for 10 seconds..." );
		String args[] = new String[ 7 ];
		args[ 0 ] = "online";
		args[ 1 ] = "-g";
		args[ 2 ] = "random";
		args[ 3 ] = "-u";
		args[ 4 ] = "10";
		args[ 5 ] = "-f";
		args[ 6 ] = "graphml/methods/Main.graphml";
    	CLI cli = new CLI();
    	
   		OutputStream out = new OutputStream() {
    		public void write(int b) throws IOException {
    			stdOutput.append( Character.toString((char) b) );
    			try {
					Thread.sleep( 50 );
				} catch (InterruptedException e) {
				}
    		}
   		};

   		PrintStream stream = new PrintStream( out );
    	PrintStream oldOutStream = System.out; //backup
    	InputStream oldInStream = System.in; //backup
    	
    	InputStream	stdin	= null;
    	try
	    {
    		stdin = new FileInputStream( "graphml/methods/Redirect.in" );
	    }
    	catch (Exception e)
	    {
		    fail( "Redirect:  Unable to open input file!" );
	    }
    	
    	System.setIn( stdin );
    	System.setOut( stream );
    	
    	long startTime = System.currentTimeMillis();
    	cli.main( args );
    	long stopTime = System.currentTimeMillis();
    	System.setOut( oldOutStream );
    	System.setIn( oldInStream );
    	
    	String msg = stdOutput.toString();
		System.out.println( msg );
		long runTime = stopTime - startTime;
		System.out.println(  runTime / 1000 + " seconds" );
		// Should finish within 10-13 seconds
		assertTrue( ( runTime - 10000 ) < 3000 );
		System.out.println( "" );
    }

    
        
    public void testCountMethods()
    {
		System.out.println( "TEST: testCountMethods" );
		System.out.println( "=======================================================================" );
		String args[] = new String[ 3 ];
		args[ 0 ] = "methods";
		args[ 1 ] = "-f";
		args[ 2 ] = "graphml/methods/Main.graphml";
    	CLI cli = new CLI();
    	
    	OutputStream out = new OutputStream() {
    		public void write(int b) throws IOException {
    			stdOutput.append( Character.toString((char) b) );
    		}
   		};
    	PrintStream stream = new PrintStream( out );
    	PrintStream oldStream = System.out; //backup
    	System.setOut( stream );
    	cli.main( args );
    	System.setOut( oldStream );
    	
    	String msg = stdOutput.toString();
		System.out.println( msg );
	    Pattern p = Pattern.compile( "e_Cancel\\s+e_CloseApp\\s+e_CloseDB\\s+e_CloseDialog\\s+e_EnterCorrectKey\\s+e_EnterInvalidKey\\s+e_Initialize\\s+e_No\\s+e_Start\\s+e_StartWithDatabase\\s+e_Yes\\s+v_EnterMasterCompositeMasterKey\\s+v_InvalidKey\\s+v_KeePassNotRunning\\s+v_MainWindowEmpty\\s+v_MainWindow_DB_Loaded\\s+v_SaveBeforeCloseLock", Pattern.MULTILINE );
		Matcher m = p.matcher( msg );
		assertTrue( m.find() );
		System.out.println( "" );
    }

    
       
    public void testContinueForCulDeSac()
    {
		System.out.println( "TEST: testContinueForCulDeSac" );
		System.out.println( "=======================================================================" );
		String args[] = new String[ 4 ];
		args[ 0 ] = "merge";
		args[ 1 ] = "-c";
		args[ 2 ] = "-f";
		args[ 3 ] = "graphml/CulDeSac";
    	CLI cli = new CLI();
    	
    	
    	OutputStream out = new OutputStream() {
    		public void write(int b) throws IOException {
    			stdOutput.append( Character.toString((char) b) );
    		}
   		};
    	PrintStream stream = new PrintStream( out );
    	PrintStream oldStream = System.err; //backup
    	System.setErr( stream );
    	cli.main( args );
    	System.setErr( oldStream );
    	
    	String msg = stdOutput.toString();
		System.out.println( msg );
		Pattern p = Pattern.compile( "Found a cul-de-sac. Vertex has no out-edges: '.*', in file: '.*'", Pattern.MULTILINE );
		Matcher m = p.matcher( msg );
		assertTrue( !m.find() );
		System.out.println( "" );
    }
    
    // Check for reserved keywords 
    public void testReservedKeywords()
    {
		System.out.println( "TEST: testReservedKeywords" );
		System.out.println( "=======================================================================" );
		String args[] = new String[ 3 ];
		args[ 0 ] = "methods";
		args[ 1 ] = "-f";
		args[ 2 ] = "graphml/test24";
    	CLI cli = new CLI();
    	
    	
    	OutputStream out = new OutputStream() {
    		public void write(int b) throws IOException {
    			stdOutput.append( Character.toString((char) b) );
    		}
   		};
    	PrintStream stream = new PrintStream( out );
    	PrintStream oldStream = System.err; //backup
    	System.setErr( stream );
    	cli.main( args );
    	System.setErr( oldStream );
    	
    	String msg = stdOutput.toString();
		System.out.println( msg );
		Pattern p = Pattern.compile( "Edge has a label 'BACKTRACK', which is a reserved keyword, in file: '.*graphml.test24.(Camera|Time).graphml'", Pattern.MULTILINE );
		Matcher m = p.matcher( msg );
		assertTrue( m.find() );
		System.out.println( "" );
    }
}
