package test.org.tigris.mbt;

import junit.framework.TestCase;

import org.tigris.mbt.ModelBasedTesting;

public class TestMerging extends TestCase
{
    public TestMerging( String testName )
    {
        super(testName);
    }

    public static void main( String args[] )
    {
        String[] testCaseName = { TestMerging.class.getName()};
        junit.textui.TestRunner.main(testCaseName);
    }


    // Test merging of 2 simple graphs
    public void test01()
    {
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test01" );
	    	mbt.writeGraph( mbt.getGraph(), "graphml/merged/test01.graphml" );
	    	assertTrue( mbt.getGraph().getEdges().size() == 9 );
	    	assertTrue( mbt.getGraph().getVertices().size() == 7 );
    	}
    	catch ( RuntimeException e)
    	{
    		System.out.println(e.getMessage());
    		fail( e.getMessage() );
    	}
    }
    
    // Test merging of 2 simple graphs, with  nodes containing key word NO_MERGE
    public void test02()
    {
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test02" );
	    	mbt.writeGraph( mbt.getGraph(), "graphml/merged/test02.graphml" );
	    	assertTrue( mbt.getGraph().getEdges().size() == 11 );
	    	assertTrue( mbt.getGraph().getVertices().size() == 8 );
    	}
    	catch ( RuntimeException e)
    	{
    		System.out.println(e.getMessage());
    		fail( e.getMessage() );
    	}
    }
    
    // Test merging a folder consisting 162 graphs
    public void test03()
    {
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test03" );
	    	mbt.writeGraph( mbt.getGraph(), "graphml/merged/test03.graphml" );
	    	assertTrue( mbt.getGraph().getEdges().size() == 1550 );
	    	assertTrue( mbt.getGraph().getVertices().size() == 788 );
    	}
    	catch ( RuntimeException e)
    	{
    		System.out.println(e.getMessage());
    		fail( e.getMessage() );
    	}
    }
    
    // Test Generating Tests
    public void test04()
    {
    	try
    	{
        	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/merged/test01.graphml" );
    		mbt.generateTests();
    	} catch (Exception e) {
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( ".*There is no way to reach.*|.*Found a cul-de-sac, I have to stop now.*" ) );
		}
    }
    
    // Test Generating Tests
    public void test05()
    {
    	try
    	{
        	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/merged/test03.graphml" );
	    	mbt.generateTests();
		}
		catch ( Exception e)
		{
    		System.out.println(e.getMessage());
			fail( e.getMessage() );
		}
    }
    
    // Test Generate Java Code
    public void test06()
    {
    	java.io.File file = new java.io.File( "graphml/java/test02.java" );
    	file.delete();
    	file = null;
    	
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test02" );
	    	mbt.generateJavaCode( "graphml/java/test02.java" );	
	    	mbt.generateJavaCode( "graphml/java/test02.java" );	
		}
		catch ( RuntimeException e)
		{
    		System.out.println(e.getMessage());
			fail( e.getMessage() );
		}
    }
    
    // Test Generate Perl Code
    public void test07()
    {
    	java.io.File file = new java.io.File( "graphml/perl/test02.pl" );
    	file.delete();
    	file = null;
    	
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test02" );
	    	mbt.generatePerlCode( "graphml/perl/test02.pl" );	
	    	mbt.generatePerlCode( "graphml/perl/test02.pl" );	
		}
		catch ( RuntimeException e)
		{
    		System.out.println(e.getMessage());
			fail( e.getMessage() );
		}
    }    
    
    // Verify that mbt reports and exits when an edge without name is found 
    public void test08()
    {
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test08" );
	    	fail( "Missing error message" );
    	}
    	catch ( RuntimeException e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "Label for an edge comming from a non-Start vertex,  'null \\(C -> A\\) [0-9]+\\([0-9]+ -> [0-9]+\\)', must be defined in file: \\\".*graphml.test08.graph1.graphml\\\"" ) );
    	}
    }

    // Verify that mbt checks that subgraphs are unique.
    public void test09()
    {
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test09" );
	    	fail( "Missing error message" );
    	}
    	catch ( RuntimeException e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "Found 2 subgraphs using the same name: 'B', they are defined in files: '.*\\\\graphml.test09.C.graphml', and :'.*graphml.test09.B\\.graphml'" ) );
    	}
    }

    // Verify that mbt reports and exits when an node without name is found 
    public void test10()
    {
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test10" );
	    	fail( "Missing error message" );
    	}
    	catch ( RuntimeException e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "Vertex is missing its label in file: \".*test10.B\\.graphml\"" ) );
    	}
    }

    // Verify that mbt reports and exits when a recursive subgraph situation emerges 
    public void test11()
    {
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test11" );
	    	fail( "Missing error message" );
    	}
    	catch ( RuntimeException e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "Found a subgraph containing a duplicate vertex with name: C, in file: '.*test11.C\\.graphml'" ) );
    	}
    }
    
    // Verify that mbt reports and exits when an edge without name is found 
    public void test12()
    {
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test12" );
	    	fail( "Missing error message" );
    	}
    	catch ( RuntimeException e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "Label for an edge comming from a non-Start vertex,  'null \\(E -> G\\) [0-9]+\\([0-9]+ -> [0-9]+\\)', must be defined in file: \\\".*graphml.test12.C\\.graphml\\\"" ) );
    	}
    }
    
    // Verify that mbt reports and exits when an edge containing a whitespace (tab) is found 
    public void test13()
    {
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test13" );
	    	fail( "Missing error message" );
    	}
    	catch ( RuntimeException e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "Label for an edge comming from a non-Start vertex,  'null \\(E -> G\\) [0-9]+\\([0-9]+ -> [0-9]+\\)', must be defined in file: \\\".*graphml.test13.C\\.graphml\\\"" ) );
    	}
    }
    
    // Verify that mbt reports and exits when a vertex containing a whitespace (tab) is found 
    public void test14()
    {
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test14" );
	    	fail( "Missing error message" );
    	}
    	catch ( RuntimeException e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "Vertex has a label 'Containing a whitespace', containing whitespaces in file: \\\".*graphml.test14.C\\.graphml\\\"" ) );
    	}
    }
    
    // Verify that a single graphml file with an edge comming from the START vertex with an empty label is catched 
    public void test15()
    {
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test15/test15.graphml" );
	    	fail( "Missing error message" );
    	}
    	catch ( Exception e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "Did not find a Start vertex with an out edge with a label." ) );
    	}
    }
    
    // Verify that a folder containing a mother graph file with an edge comming from the
    // START vertex with an empty label is catched
    public void test16()
    {
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test16/" );
	    	fail( "Missing error message" );
    	}
    	catch ( Exception e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "Did not find a Start vertex with an out edge with a label." ) );
    	}
    }
    
    // Verify that a file with 2 Start vertices is catched. Both Start vertices has edges with labels.
    public void test17()
    {
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test17/test17.graphml" );
	    	fail( "Missing error message" );
    	}
    	catch ( Exception e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "Only one Start vertex can exist in one file, see file 'graphml/test17/test17.graphml'" ) );
    	}
    }

    // Verify that a file with 2 Start vertices is catched. One edge has a label, the other has not.
    public void test18()
    {
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test18/test18.graphml" );
	    	fail( "Missing error message" );
    	}
    	catch ( Exception e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "Only one Start vertex can exist in one file, see file 'graphml/test18/test18.graphml'" ) );
    	}
    }

    // Verify that a file with 2 Start vertices is catched. Both edges has no labels.
    public void test19()
    {
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test19/test19.graphml" );
	    	fail( "Missing error message" );
    	}
    	catch ( Exception e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "Only one Start vertex can exist in one file, see file 'graphml/test19/test19.graphml'" ) );
    	}
    }

    // Verify that a file with 2 outedges from the Start vertex is catched.
    public void test20()
    {
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test20/test20.graphml" );
	    	fail( "Missing error message" );
    	}
    	catch ( Exception e)
    	{
    		String msg = e.getMessage();
    		System.out.println(e.getMessage());
    		assertTrue( msg.matches( "A Start vertex can only have one out edge, look in file: graphml/test20/test20.graphml" ) );
    	}
    }

    // Verify that UML notes can be added to the graphml file.
    public void test21()
    {
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test21" );
	    	mbt.generateJavaCode( "graphml/merged/test21.java" );	
    	}
    	catch ( Exception e)
    	{
	    	fail( e.getMessage() );
    	}
    }

    // Verify that a graph containing a Stop vertex is correctly merged.
    public void test22()
    {
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting( "graphml/test22" );
	    	mbt.writeGraph( mbt.getGraph(), "graphml/merged/test22.graphml" );
	    	assertTrue( mbt.getGraph().getEdges().size() == 8 );
	    	assertTrue( mbt.getGraph().getVertices().size() == 8 );
    	}
    	catch ( Exception e)
    	{
    		System.out.println(e.getMessage());
	    	fail( e.getMessage() );
    	}
    }
}
