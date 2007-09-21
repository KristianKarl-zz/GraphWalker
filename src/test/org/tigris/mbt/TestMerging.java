package test.org.tigris.mbt;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.tigris.mbt.CLI;
import org.tigris.mbt.ModelBasedTesting;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;

public class TestMerging extends TestCase
{
	private String  INDEX_KEY = "index";
	StringBuffer stdOutput = new StringBuffer();

	public TestMerging( String testName )
    {
        super(testName);
    }

    public static void main( String args[] )
    {
        String[] testCaseName = { TestMerging.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }

    
    
    public void testRandom10seconds()
    {
		System.out.println( "TEST: testRandom10seconds" );
		System.out.println( "=======================================================================" );
		System.out.println( "Please wait for 10 seconds..." );
		String args[] = new String[ 6 ];
		args[ 0 ] = "dynamic";
		args[ 1 ] = "-r";
		args[ 2 ] = "-t";
		args[ 3 ] = "10";
		args[ 4 ] = "-g";
		args[ 5 ] = "graphml/methods/Main.graphml";
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
    	cli.main( args );
    	System.setOut( oldOutStream );
    	System.setIn( oldInStream );
    	
    	String msg = stdOutput.toString();
		System.out.println( msg );
		Pattern p = Pattern.compile( "End of test. Execution time has ended.", Pattern.MULTILINE );
		Matcher m = p.matcher( msg );
		assertTrue( m.find() );
		System.out.println( "" );
    }

    
        
    public void testCountMethods()
    {
		System.out.println( "TEST: testCountMethods" );
		System.out.println( "=======================================================================" );
		String args[] = new String[ 3 ];
		args[ 0 ] = "methods";
		args[ 1 ] = "-g";
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

    
       
    public void testStopForCulDeSac()
    {
		System.out.println( "TEST: testStopForCulDeSac" );
		System.out.println( "=======================================================================" );
		String args[] = new String[ 5 ];
		args[ 0 ] = "merge";
		args[ 1 ] = "-g";
		args[ 2 ] = "graphml/CulDeSac";
		args[ 3 ] = "-l";
		args[ 4 ] = "graphml/merged/testStopForCulDeSac.graphml";
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
		assertTrue( m.find() );
		System.out.println( "" );
    }

    
    
    public void testContinueForCulDeSac()
    {
		System.out.println( "TEST: testContinueForCulDeSac" );
		System.out.println( "=======================================================================" );
		String args[] = new String[ 6 ];
		args[ 0 ] = "merge";
		args[ 1 ] = "-c";
		args[ 2 ] = "-g";
		args[ 3 ] = "graphml/CulDeSac";
		args[ 4 ] = "-l";
		args[ 5 ] = "graphml/merged/testContinueForCulDeSac.graphml";
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

    
    
    public void test_mergeSubgraphs_01()
    {
		System.out.println( "TEST: test_mergeSubgraphs_01" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/mergeSubgraphs_01" );
	    	mbt.writeGraph( mbt.getGraph(), "graphml/merged/mergeSubgraphs_01.graphml" );
	    	assertTrue( mbt.getGraph().getVertices().size() == 6 );
	    	assertTrue( mbt.getGraph().getEdges().size() == 16 );
	    	verifyIds( mbt.getGraph() );
    	}
    	catch ( RuntimeException e)
    	{
    		System.out.println(e.getMessage());
    		fail( e.getMessage() );
    	}
		System.out.println( "" );
    }

    
    
    public void test_mergeSubgraphs_02()
    {
		System.out.println( "TEST: test_mergeSubgraphs_02" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/mergeSubgraphs_02" );
	    	mbt.writeGraph( mbt.getGraph(), "graphml/merged/mergeSubgraphs_02.graphml" );
	    	assertTrue( mbt.getGraph().getVertices().size() == 16 );
	    	assertTrue( mbt.getGraph().getEdges().size() == 55 );
	    	verifyIds( mbt.getGraph() );
    	}
    	catch ( RuntimeException e)
    	{
    		System.out.println(e.getMessage());
    		fail( e.getMessage() );
    	}
		System.out.println( "" );
    }

    
    
    // Test merging of 2 simple graphs
    public void test01()
    {
		System.out.println( "TEST: test01" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test01" );
	    	mbt.writeGraph( mbt.getGraph(), "graphml/merged/test01.graphml" );
	    	assertTrue( mbt.getGraph().getEdges().size() == 9 );
	    	assertTrue( mbt.getGraph().getVertices().size() == 7 );
	    	verifyIds( mbt.getGraph() );
    	}
    	catch ( RuntimeException e)
    	{
    		System.out.println(e.getMessage());
    		fail( e.getMessage() );
    	}
		System.out.println( "" );
    }
    
    // Test merging of 2 simple graphs, with  nodes containing key word NO_MERGE
    public void test02()
    {
		System.out.println( "TEST: test02" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test02" );
	    	mbt.writeGraph( mbt.getGraph(), "graphml/merged/test02.graphml" );
	    	assertTrue( mbt.getGraph().getEdges().size() == 11 );
	    	assertTrue( mbt.getGraph().getVertices().size() == 8 );
	    	verifyIds( mbt.getGraph() );
    	}
    	catch ( RuntimeException e)
    	{
    		System.out.println(e.getMessage());
    		fail( e.getMessage() );
    	}
		System.out.println( "" );
    }
    
    // Test merging a folder consisting 162 graphs
    public void test03()
    {
		System.out.println( "TEST: test03" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test03" );
	    	mbt.writeGraph( mbt.getGraph(), "graphml/merged/test03.graphml" );
	    	assertTrue( mbt.getGraph().getEdges().size() == 1550 );
	    	assertTrue( mbt.getGraph().getVertices().size() == 788 );
	    	verifyIds( mbt.getGraph() );
    	}
    	catch ( RuntimeException e)
    	{
    		System.out.println(e.getMessage());
    		fail( e.getMessage() );
    	}
		System.out.println( "" );
    }
    
    // Test Generating Tests
    public void test04()
    {
		System.out.println( "TEST: test04" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/merged/test01.graphml" );
    		mbt.generateTests( false, 0 );
	    	verifyIds( mbt.getGraph() );
    	} catch (Exception e) {
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( ".*There is no way to reach.*|.*Found a cul-de-sac.*" ) );
		}
		System.out.println( "" );
    }
    
    // Test Generating Tests
    public void test05()
    {
		System.out.println( "TEST: test05" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/merged/test03.graphml" );
	    	mbt.generateTests( false, 0 );
	    	verifyIds( mbt.getGraph() );
		}
		catch ( Exception e)
		{
    		System.out.println(e.getMessage());
			fail( e.getMessage() );
		}
		System.out.println( "" );
    }
    
    // Test Generate Java Code
    public void test06()
    {
		System.out.println( "TEST: test06" );
		System.out.println( "=======================================================================" );
    	java.io.File file = new java.io.File( "graphml/java/test02.java" );
    	file.delete();
    	file = null;
    	
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test02" );
	    	mbt.generateJavaCode( "graphml/java/test02.java" );	
	    	mbt.generateJavaCode( "graphml/java/test02.java" );	
	    	verifyIds( mbt.getGraph() );
		}
		catch ( RuntimeException e)
		{
    		System.out.println(e.getMessage());
			fail( e.getMessage() );
		}
		System.out.println( "" );
    }
    
    // Test Generate Perl Code
    public void test07()
    {
		System.out.println( "TEST: test07" );
		System.out.println( "=======================================================================" );
    	java.io.File file = new java.io.File( "graphml/perl/test02.pl" );
    	file.delete();
    	file = null;
    	
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test02" );
	    	mbt.generatePerlCode( "graphml/perl/test02.pl" );	
	    	mbt.generatePerlCode( "graphml/perl/test02.pl" );	
	    	verifyIds( mbt.getGraph() );
		}
		catch ( RuntimeException e)
		{
    		System.out.println(e.getMessage());
			fail( e.getMessage() );
		}
		System.out.println( "" );
    }    
    
    // Verify that mbt checks that subgraphs are unique.
    public void test09()
    {
		System.out.println( "TEST: test09" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test09" );
	    	fail( "Missing error message" );
    	}
    	catch ( RuntimeException e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "Found 2 subgraphs using the same name: 'B', they are defined in files: '.*graphml.test09.C.graphml', and :'.*graphml.test09.B\\.graphml'" ) );
    	}
		System.out.println( "" );
    }

    // Verify that mbt reports and exits when an node without name is found 
    public void test10()
    {
		System.out.println( "TEST: test10" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test10" );
	    	fail( "Missing error message" );
    	}
    	catch ( RuntimeException e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "Vertex is missing its label in file: '.*test10.B\\.graphml'" ) );
    	}
		System.out.println( "" );
    }

    // Verify that mbt reports and exits when a recursive subgraph situation emerges 
    public void test11()
    {
		System.out.println( "TEST: test11" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test11" );
	    	fail( "Missing error message" );
    	}
    	catch ( RuntimeException e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "Found a subgraph containing a duplicate vertex with name: 'C', in file: '.*test11.C\\.graphml'" ) );
    	}
		System.out.println( "" );
    }
    
    
    // Verify that mbt reports and exits when an edge containing a whitespace (tab) is found 
    public void test13()
    {
		System.out.println( "TEST: test13" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test13" );
	    	fail( "Missing error message" );
    	}
    	catch ( RuntimeException e)
    	{
    		String msg = e.getMessage();
    		System.out.println(msg);
    		assertTrue( msg.matches( "Edge has a label '	',  ''null', INDEX=[0-9]+ \\('E', INDEX=[0-9]+ -> 'G', INDEX=[0-9]+\\)', containing whitespaces in file: '.*test13.C\\.graphml'" ) );
    	}
		System.out.println( "" );
    }
    
    // Verify that mbt reports and exits when a vertex containing a whitespace (tab) is found 
    public void test14()
    {
		System.out.println( "TEST: test14" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test14" );
	    	fail( "Missing error message" );
    	}
    	catch ( RuntimeException e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "Vertex has a label 'Containing a whitespace', containing whitespaces in file: '.*graphml.test14.C\\.graphml'" ) );
    	}
		System.out.println( "" );
    }
    
    // Verify that a single graphml file with an edge comming from the START vertex with an empty label is catched 
    public void test15()
    {
		System.out.println( "TEST: test15" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test15" );
	    	fail( "Missing error message" );
    	}
    	catch ( Exception e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "Did not find a Start vertex with an out edge with a label." ) );
    	}
		System.out.println( "" );
    }
    
    // Verify that a file with 2 Start vertices is catched. Both Start vertices has edges with labels.
    public void test17()
    {
		System.out.println( "TEST: test17" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test17/test17.graphml" );
	    	fail( "Missing error message" );
    	}
    	catch ( Exception e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "Only one Start vertex can exist in one file, see file 'graphml/test17/test17.graphml'" ) );
    	}
		System.out.println( "" );
    }

    // Verify that a file with 2 Start vertices is catched. One edge has a label, the other has not.
    public void test18()
    {
		System.out.println( "TEST: test18" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test18/test18.graphml" );
	    	fail( "Missing error message" );
    	}
    	catch ( Exception e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "Only one Start vertex can exist in one file, see file 'graphml/test18/test18.graphml'" ) );
    	}
		System.out.println( "" );
    }

    // Verify that a file with 2 Start vertices is catched. Both edges has no labels.
    public void test19()
    {
		System.out.println( "TEST: test19" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test19/test19.graphml" );
	    	fail( "Missing error message" );
    	}
    	catch ( Exception e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "Only one Start vertex can exist in one file, see file 'graphml/test19/test19.graphml'" ) );
    	}
		System.out.println( "" );
    }

    // Verify that a file with 2 outedges from the Start vertex is catched.
    public void test20()
    {
		System.out.println( "TEST: test20" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test20/test20.graphml" );
	    	fail( "Missing error message" );
    	}
    	catch ( Exception e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "A Start vertex can only have one out edge, look in file: graphml/test20/test20.graphml" ) );
    	}
		System.out.println( "" );
    }

    // Verify that UML notes can be added to the graphml file.
    public void test21()
    {
		System.out.println( "TEST: test21" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test21" );
	    	mbt.generateJavaCode( "graphml/merged/test21.java" );
	    	verifyIds( mbt.getGraph() );
    	}
    	catch ( Exception e)
    	{
	    	fail( e.getMessage() );
    	}
		System.out.println( "" );
    }

    // Verify that a graph containing a Stop vertex is correctly merged.
    public void test22()
    {
		System.out.println( "TEST: test22" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test22" );
	    	mbt.writeGraph( mbt.getGraph(), "graphml/merged/test22.graphml" );
	    	assertTrue( mbt.getGraph().getEdges().size() == 8 );
	    	assertTrue( mbt.getGraph().getVertices().size() == 8 );
	    	verifyIds( mbt.getGraph() );
    	}
    	catch ( Exception e)
    	{
    		System.out.println(e.getMessage());
	    	fail( e.getMessage() );
    	}
		System.out.println( "" );
    }

    // Meging with subgraphs containing Stop vertices 
    public void test23()
    {
		System.out.println( "TEST: test23" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test23" );
	    	mbt.writeGraph( mbt.getGraph(), "graphml/merged/test23.graphml" );
	    	assertTrue( mbt.getGraph().getEdges().size() == 14 );
	    	assertTrue( mbt.getGraph().getVertices().size() == 9 );
	    	verifyIds( mbt.getGraph() );
    	}
    	catch ( Exception e)
    	{
    		System.out.println(e.getMessage());
	    	fail( e.getMessage() );
    	}
		System.out.println( "" );
    }

    // Check for reserved keywords 
    public void test24()
    {
		System.out.println( "TEST: test24" );
		System.out.println( "=======================================================================" );
		String args[] = new String[ 3 ];
		args[ 0 ] = "methods";
		args[ 1 ] = "-g";
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
		Pattern p = Pattern.compile( "Edge has a label 'BACKTRACK', which is a reserved keyword, in file: '.*graphml.test24.Camera.graphml'", Pattern.MULTILINE );
		Matcher m = p.matcher( msg );
		assertTrue( m.find() );
		System.out.println( "" );
    }

    
    // Verify that all vertices and edges has indexes, and that no duplicates exists.
    private void verifyIds( SparseGraph g )
	{
		Object[] vertices1 = g.getVertices().toArray();
		for ( int i = 0; i < vertices1.length; i++ )
		{
			DirectedSparseVertex v1 = (DirectedSparseVertex)vertices1[ i ];
			int hits = 0;
			Integer index1 = (Integer)v1.getUserDatum( INDEX_KEY );
	    	Object[] vertices2 = g.getVertices().toArray();
			for ( int j = 0; j < vertices1.length; j++ )
			{
				DirectedSparseVertex v2 = (DirectedSparseVertex)vertices2[ j ];
				Integer index2 = (Integer)v2.getUserDatum( INDEX_KEY );
				if ( index1.intValue() == index2.intValue() )
				{
					hits++;
		    	}
	    	}
	    	assertTrue( hits == 1 );
		}					

		Object[] edges1 = g.getEdges().toArray();
		for ( int i = 0; i < vertices1.length; i++ )
		{
			DirectedSparseEdge e1 = (DirectedSparseEdge)edges1[ i ];
			int hits = 0;
			Integer index1 = (Integer)e1.getUserDatum( INDEX_KEY );
	    	Object[] edges2 = g.getEdges().toArray();
			for ( int j = 0; j < vertices1.length; j++ )
			{
				DirectedSparseEdge e2 = (DirectedSparseEdge)edges2[ j ];
				Integer index2 = (Integer)e2.getUserDatum( INDEX_KEY );
				if ( index1.intValue() == index2.intValue() )
				{
					hits++;
		    	}
	    	}
	    	assertTrue( hits == 1 );
		}					
	}
}
