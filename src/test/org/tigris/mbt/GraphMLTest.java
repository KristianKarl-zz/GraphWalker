package test.org.tigris.mbt;

import org.tigris.mbt.Keywords;
import org.tigris.mbt.ModelBasedTesting;
import org.tigris.mbt.Util;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;

import junit.framework.TestCase;

public class GraphMLTest extends TestCase {

    public void test_mergeSubgraphs_01()
    {
		System.out.println( "TEST: test_mergeSubgraphs_01" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/mergeSubgraphs_01" );
	    	Util.writeGraphML( mbt.getGraph(), "graphml/merged/mergeSubgraphs_01.graphml" );
	    	assertTrue( mbt.getGraph().getVertices().size() == 6 );
	    	assertTrue( mbt.getGraph().getEdges().size() == 16 );
	    	verifyIds( mbt.getGraph() );
    	}
    	catch ( Exception e)
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
	    	Util.writeGraphML( mbt.getGraph(), "graphml/merged/mergeSubgraphs_02.graphml" );
	    	assertTrue( mbt.getGraph().getVertices().size() == 16 );
	    	assertTrue( mbt.getGraph().getEdges().size() == 55 );
	    	verifyIds( mbt.getGraph() );
    	}
    	catch ( Exception e)
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
	    	Util.writeGraphML( mbt.getGraph(), "graphml/merged/test01.graphml" );
	    	assertTrue( mbt.getGraph().getEdges().size() == 9 );
	    	assertTrue( mbt.getGraph().getVertices().size() == 7 );
	    	verifyIds( mbt.getGraph() );
    	}
    	catch ( Exception e)
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
	    	Util.writeGraphML( mbt.getGraph(), "graphml/merged/test02.graphml" );
	    	assertTrue( mbt.getGraph().getEdges().size() == 11 );
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
    
    // Test merging a folder consisting 162 graphs
    public void test03()
    {
		System.out.println( "TEST: test03" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test03" );
	    	Util.writeGraphML( mbt.getGraph(), "graphml/merged/test03.graphml" );
	    	assertTrue( mbt.getGraph().getEdges().size() == 1550 );
	    	assertTrue( mbt.getGraph().getVertices().size() == 788 );
	    	verifyIds( mbt.getGraph() );
    	}
    	catch ( Exception e)
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
	    	Util.generateJavaCode( mbt.getGraph(), "graphml/java/test02.java" );	
	    	Util.generateJavaCode( mbt.getGraph(), "graphml/java/test02.java" );	
	    	verifyIds( mbt.getGraph() );
		}
		catch ( Exception e)
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
	    	Util.generatePerlCode( mbt.getGraph(), "graphml/perl/test02.pl" );	
	    	Util.generatePerlCode( mbt.getGraph(), "graphml/perl/test02.pl" );	
	    	verifyIds( mbt.getGraph() );
		}
		catch ( Exception e)
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
    		assertTrue( msg.matches( "Found 2 subgraphs using the same name: 'B', they are defined in files: '.*graphml.test09.(C|B).graphml', and :'.*graphml.test09.(B|C).graphml'" ) );
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
	    	Util.generateJavaCode( mbt.getGraph(), "graphml/merged/test21.java" );
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
	    	Util.writeGraphML( mbt.getGraph(), "graphml/merged/test22.graphml" );
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

    // Merging with subgraphs containing Stop vertices 
    public void test23()
    {
		System.out.println( "TEST: test23" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
	    	mbt.readGraph( "graphml/test23" );
	    	Util.writeGraphML( mbt.getGraph(), "graphml/merged/test23.graphml" );
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
    public void testCoverageMethods()
    {
		System.out.println( "TEST: testCoverageMethods" );
		System.out.println( "=======================================================================" );
    	try
    	{
	    	ModelBasedTesting mbt = new ModelBasedTesting();
			mbt.initialize( "graphml/test03", true, 0 );
			mbt.reset();
			
			float edgesCov = 0;
			float verticesCov = 0;
			
			for ( int i = 0; i < 10000; i++ )
			{
				mbt.getEdge();
				float currEdgesCov = mbt.getTestCoverage4Edges();
				float currVerticesCov = mbt.getTestCoverage4Vertices();
				assertTrue( currEdgesCov >= edgesCov );
				assertTrue( currVerticesCov >= verticesCov );
				edgesCov = currEdgesCov;
				verticesCov = currVerticesCov;
			}
    	}
    	catch ( Exception e)
    	{
    		System.out.println(e.getMessage());
    		fail( e.getMessage() );
    	}
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
			Integer index1 = (Integer)v1.getUserDatum( Keywords.INDEX_KEY );
	    	Object[] vertices2 = g.getVertices().toArray();
			for ( int j = 0; j < vertices1.length; j++ )
			{
				DirectedSparseVertex v2 = (DirectedSparseVertex)vertices2[ j ];
				Integer index2 = (Integer)v2.getUserDatum( Keywords.INDEX_KEY );
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
			Integer index1 = (Integer)e1.getUserDatum( Keywords.INDEX_KEY );
	    	Object[] edges2 = g.getEdges().toArray();
			for ( int j = 0; j < vertices1.length; j++ )
			{
				DirectedSparseEdge e2 = (DirectedSparseEdge)edges2[ j ];
				Integer index2 = (Integer)e2.getUserDatum( Keywords.INDEX_KEY );
				if ( index1.intValue() == index2.intValue() )
				{
					hits++;
		    	}
	    	}
	    	assertTrue( hits == 1 );
		}					
	}
}
