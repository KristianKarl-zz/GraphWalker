package test.org.tigris.mbt;

import org.tigris.mbt.Keywords;
import org.tigris.mbt.ModelBasedTesting;
import org.tigris.mbt.Util;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;

import junit.framework.TestCase;

public class UtilTest extends TestCase {

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
