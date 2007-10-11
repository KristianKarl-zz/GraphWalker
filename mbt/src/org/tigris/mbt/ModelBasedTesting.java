//This file is part of the Model-based Testing java package
//Copyright (C) 2005  Kristian Karl
//
//This program is free software; you can redistribute it and/or
//modify it under the terms of the GNU General Public License
//as published by the Free Software Foundation; either version 2
//of the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

package org.tigris.mbt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.utils.UserData;

/**
 * The object handles the test case generation, both dynamic and static.
 */
public class ModelBasedTesting
{

	private SparseGraph 				_graph          = new SparseGraph();
	private java.util.Random      		_radomGenerator = new Random();

	private Object   			 	_object;
	private org.apache.log4j.Logger _logger;
	private Object[] 			 	_vertices     = null;
	private Object[]             	_edges        = null;
	private DirectedSparseVertex 	_nextVertex   = null;
	private DirectedSparseVertex 	_prevVertex   = null;
	private DirectedSparseEdge 	 	_rejectedEdge = null;
	private DirectedSparseEdge 	 	_currentEdge  = null;
	private Vector 			     	_pathHistory  = new Vector();
	private boolean 				_randomWalk;
	private long 					_executionTime = 0;
	private long				 	_start_time;
	private long				 	_end_time     = 0;
	private boolean				 	_runUntilAllEdgesVisited = false;
	private boolean				 	_backtracking = false;
	private boolean				 	_cul_de_sac = false;
	private List				 	_shortestPathToVertex = null ;
	private int						_latestNumberOfUnvisitedEdges;
 	
	public ModelBasedTesting( String graphmlFileName_,
			  				  Object object_ )
	{
		_object = object_;
		_logger = org.apache.log4j.Logger.getLogger( ModelBasedTesting.class );

		if ( new File( "mbt.properties" ).exists() )
		{
			PropertyConfigurator.configure("mbt.properties");
		}
		else
		{
			SimpleLayout layout = new SimpleLayout();
			WriterAppender writerAppender = null;
	 		try
	 		{
	 			FileOutputStream fileOutputStream = new FileOutputStream( "mbt.log" );
	 			writerAppender = new WriterAppender( layout, fileOutputStream );
	 		} 
	 		catch ( Exception e )
	 		{
				e.printStackTrace();
	 		}
	 
	 		_logger.addAppender( writerAppender );
	 		_logger.setLevel( (Level)Level.ERROR );
		}
		readGraph(graphmlFileName_);
	}

	public ModelBasedTesting()
	{
		this("",null);
	}
	
	public void readGraph( String graphmlFileName_ )
	{
		GraphML graphML = new GraphML();
		graphML.load( graphmlFileName_ );
		_graph = graphML.getModel();
		if ( is_cul_de_sac() )
		{
			searchForCulDeSacs();
		}		
	}
	
	public void initialize( String graphmlFileName, boolean randomWalk, long executionTime )
	{
		_randomWalk      = randomWalk;
		_executionTime   = executionTime;
		_runUntilAllEdgesVisited = !_randomWalk;
		readGraph(graphmlFileName);
	}
	

	/**
	 * @param runningTime
	 * The time, in seconds, to run this test.
	 */
	public void runRandomWalk( long runningTime ) throws FoundNoEdgeException
	{
		reset();

		long startTime = _start_time;
		long currentTime = _start_time;
		runningTime *= 1000;

		// Start the execution which is random.
		while ( ( currentTime - startTime ) < runningTime )
		{
			executeMethod( false, false, false );
			currentTime = System.currentTimeMillis();
			_end_time = currentTime;
		}
	}

	
	/**
	 * This will set the next vertex to vertexId.   
	 */
	public void SetCurrentVertex( Integer vertexIndex ) throws RuntimeException
	{
		_logger.debug( "Searching for vertex with index: " + vertexIndex.intValue() );
		Object[] vertices = _graph.getVertices().toArray();
		for ( int i = 0; i < vertices.length; i++ )
		{
			DirectedSparseVertex v = (DirectedSparseVertex)vertices[ i ];
			Integer index = (Integer)v.getUserDatum( Keywords.INDEX_KEY );
			if ( index.intValue() == vertexIndex.intValue() )
			{
				_shortestPathToVertex = null;
				_nextVertex = v;
				_backtracking = true;
				_logger.debug( "Backtracking to vertex: '" + (String)v.getUserDatum( Keywords.LABEL_KEY ) + "', with index: " + index.intValue() );
				return;
			}
		}
		String msg = new String( "Did not find a vertex with index: " + vertexIndex.intValue() );
		_logger.debug( msg );
		throw new RuntimeException( msg );
	}


	/**
	 * Put mbt back in initial state, which means that test will begin
	 * at the Start vertex. 
	 */
	public void reset()
	{
		findStartingVertex();
		_start_time = System.currentTimeMillis();
	}


	/**
	 * Run the test until all vertices (nodes) are visited.
	 */
	public void runUntilAllVerticesVisited() throws FoundNoEdgeException
	{
		reset();

		while ( !isAllVerticesVisited() )
		{
			executeMethod( true, false, false );
			_end_time = System.currentTimeMillis();
		}
	}


	/**
	 * Run the test until all edges (arrows, or transitions) are visited.
	 */
	public void runUntilAllEdgesVisited() throws FoundNoEdgeException
	{
		_runUntilAllEdgesVisited = true;
		reset();

		while ( !isAllEdgesVisited() )
		{
			executeMethod( true, false, false );
			_end_time = System.currentTimeMillis();
		}
	}


	/**
	 * Returns a list of names of vertices and edges to be executed.
	 */
	public void getTestSequence()
	{
		_runUntilAllEdgesVisited = true;
		reset();

		try
		{
			while ( !isAllEdgesVisited() )
			{
				executeMethod( true, true, false );
			}
		}
        catch ( Exception e )
		{
			e.printStackTrace();
        }
	}


	public DirectedSparseVertex getCurrentVertex()
	{
		return _nextVertex;
	}
	
	/**
	 * Returns the next edge to be tested. 
	 */
	public DirectedSparseEdge getEdge() throws ExecutionTimeException
	{
		if ( _randomWalk )
		{
			if ( _executionTime > 0 )
			{
				if ( ( System.currentTimeMillis() - _start_time ) < _executionTime * 1000 )
				{
					try
					{
						executeMethod( false, true, true );
					    _logger.debug( "Current edge =   " + Util.getCompleteEdgeName( _currentEdge ) );
						return _currentEdge;
					}
			        catch ( Exception e )
					{
						e.printStackTrace();
			        }
				}
				else
				{
				    _logger.info( "Test has now run for the duration of: " + _executionTime + " seconds, and will now stop" );
				    throw new ExecutionTimeException();
				}
			}
			else
			{
				try
				{
					executeMethod( false, true, true );
				    _logger.debug( "Current edge =   " + Util.getCompleteEdgeName( _currentEdge ) );
					return _currentEdge;
				}
		        catch ( Exception e )
				{
					e.printStackTrace();
		        }
			}
		}
		else
		{
			_runUntilAllEdgesVisited = true;
			try
			{
				executeMethod( true, true, true );
			    _logger.debug( "Current edge =   " + Util.getCompleteEdgeName( _currentEdge ) );
				if ( isAllEdgesVisited() )
				{
					_currentEdge = null;
				}
				return _currentEdge;
			}
	        catch ( Exception e )
			{
				e.printStackTrace();
	        }
		}
        
        return null;
	}


	/**
	 * Run the test until all edges (arrows, or transitions), and
	 * all vertices (nodes) are visited
	 */
	public void runUntilAllVerticesAndEdgesVisited() throws FoundNoEdgeException
	{
		findStartingVertex();
		_runUntilAllEdgesVisited = true;
		_start_time = System.currentTimeMillis();
		while ( !(isAllVerticesVisited() && isAllEdgesVisited()) )
		{
			executeMethod( true, false, false );
			_end_time = System.currentTimeMillis();
		}
	}


	/**
	 * Returns true if all vertices (nodes) are visited
	 */
	private boolean isAllVerticesVisited()
	{
		_vertices = _graph.getVertices().toArray();

		for ( int i = 0; i < _edges.length; i++ )
		{
			DirectedSparseVertex vertex = (DirectedSparseVertex)_vertices[ i ];

			Integer vistited = (Integer)vertex.getUserDatum( Keywords.VISITED_KEY );
			if ( vistited.intValue() == 0 )
			{
				return false;
			}
		}
		return true;
	}


	/**
	 * Returns true if all edges (arrows or transitions) are visited
	 */
	private boolean isAllEdgesVisited()
	{
		_edges = _graph.getEdges().toArray();

		for ( int i = 0; i < _edges.length; i++ )
		{
			DirectedSparseEdge edge = (DirectedSparseEdge)_edges[ i ];

			Integer vistited = (Integer)edge.getUserDatum( Keywords.VISITED_KEY );
			if ( vistited.intValue() == 0 )
			{
				return false;
			}
		}
		return true;
	}
	
	private void searchForCulDeSacs()
	{
		Object[] list = _graph.getVertices().toArray();
		for ( int i = 0; i < list.length; i++ )
		{
			DirectedSparseVertex v = (DirectedSparseVertex)list[ i ];
			if ( v.getOutEdges().size() <= 0 )
			{
				throw new RuntimeException( "Found a cul-de-sac. Vertex has no out-edges: '" + 
						                    (String)v.getUserDatum( Keywords.LABEL_KEY ) + "', in file: '" + 
						                    v.getUserDatum( Keywords.FILE_KEY ) + "'" );
			}
		}
	}

	public String getStatistics()
	{
		String stat = new String();
		String new_line = new String( "\n" );

		Object[] vertices = _graph.getVertices().toArray();
		Object[] edges    = _graph.getEdges().toArray();

		int numOfVisitedVertices      = 0;
		int numOfVisitedEdges         = 0;
		int totalNumOfVisitedVertices = 0;
		int totalNumOfVisitedEdges    = 0;

		// Log which edges are not visited.
		for ( int i = 0; i < edges.length; i++ )
		{
			DirectedSparseEdge edge = (DirectedSparseEdge)edges[ i ];

			Integer vistited = (Integer)edge.getUserDatum( Keywords.VISITED_KEY );
			if ( vistited.intValue() == 0 )
			{
				stat += "Not tested (Edge): '" + (String)edge.getUserDatum( Keywords.LABEL_KEY ) + "', from: '" + 
				        (String)edge.getSource().getUserDatum( Keywords.LABEL_KEY ) + "', to: '" + 
				        (String)edge.getDest().getUserDatum( Keywords.LABEL_KEY ) + "'" + new_line;
			}
			else
			{
				numOfVisitedEdges++;
			}
			totalNumOfVisitedEdges += vistited.intValue();
		}

		// Log vertices which has not been visited
		for ( int i = 0; i < vertices.length; i++ )
		{
			DirectedSparseVertex vertex = (DirectedSparseVertex)vertices[ i ];

			Integer vistited = (Integer)vertex.getUserDatum( Keywords.VISITED_KEY );
			if ( vistited.intValue() == 0 )
			{
				stat += "Not tested (Vertex): '" + (String)vertex.getUserDatum( Keywords.LABEL_KEY ) + "'" +new_line;
			}
			else
			{
				numOfVisitedVertices++;
			}
			totalNumOfVisitedVertices += vistited.intValue();
		}
		stat += "Test coverage edges: " + numOfVisitedEdges + "/" + edges.length + " => " +  (numOfVisitedEdges / (float)edges.length * 100) + "%" + new_line;
		stat += "Test coverage vertices: " + numOfVisitedVertices + "/" + vertices.length + " => " + (numOfVisitedVertices / (float)vertices.length * 100)  + "%" + new_line;
		stat += "Number of visited edges: " + totalNumOfVisitedEdges + new_line;
		stat += "Number of visited vertices: " + totalNumOfVisitedVertices + new_line;

		if ( _end_time == 0 )
		{
			_end_time = System.currentTimeMillis();
		}

		stat += "Execution time: " + ( ( _end_time - _start_time ) / 1000 ) + " seconds" + new_line;

		return stat;
	}

	/**
	 * Return the instance of the graph
	 */
	public SparseGraph getGraph() {
		return _graph;
	}

	/**
	 * Return the logger
	 */
	public Logger getLogger() {
		return _logger;
	}

	public void generateTests( boolean random, long length ) throws RuntimeException, FoundNoEdgeException
	{
		findStartingVertex();
		_runUntilAllEdgesVisited = !random;
		if ( random )
		{
			for ( long index = 0; index < length /2; index++  )
			{
				executeMethod( false, true, false );
			}
		}
		else
		{
			while ( !isAllEdgesVisited() )
			{
				executeMethod( true, true, false );
			}
		}
	}

	private void findStartingVertex()
	{
		_vertices = _graph.getVertices().toArray();
		_logger.info( "Number of vertices = " + _vertices.length );

		_edges = _graph.getEdges().toArray();
		_logger.info( "Number of edges = " + _edges.length );


		_nextVertex = null;
		for ( int i = 0; i < _vertices.length; i++ )
		{
			DirectedSparseVertex vertex = (DirectedSparseVertex)_vertices[ i ];

			// Find Start vertex
			if ( vertex.getUserDatum( Keywords.LABEL_KEY ).equals( Keywords.START_NODE ) )
			{
				_nextVertex = vertex;
				vertex.setUserDatum( Keywords.VISITED_KEY, new Integer( 1 ), UserData.SHARED );
				break;
			}
		}

		if ( _nextVertex == null )
		{
			String msg = "Did not find the starting vertex in the graph.";
			_logger.error( msg );
			throw new RuntimeException( msg );
		}
	}

	
	private void executeMethod( boolean optimize, boolean dryRun, boolean suppressPrintout ) throws FoundNoEdgeException
	{
		DirectedSparseEdge edge 	= null;
		Object[] 		   outEdges = null;

		if ( _nextVertex.containsUserDatumKey( Keywords.MOTHER_GRAPH_START_VERTEX ) && dryRun == false )
		{
			_pathHistory.clear();   
		}

		_logger.debug( "Vertex = '" + (String)_nextVertex.getUserDatum( Keywords.LABEL_KEY ) + "'" );

		outEdges = _nextVertex.getOutEdges().toArray();
		_logger.debug( "Number of outgoing edges = " + outEdges.length );

		if ( outEdges.length == 0 )
		{
			_logger.error( "Vertex has no out-edges: '" + (String)_nextVertex.getUserDatum( Keywords.LABEL_KEY ) + "'" );
			throw new RuntimeException( "Found a cul-de-sac: '" + (String)_nextVertex.getUserDatum( Keywords.LABEL_KEY ) + "' I have to stop now..." );
		}

		outEdges = shuffle( outEdges );

		if ( _shortestPathToVertex == null && _runUntilAllEdgesVisited == true )
		{
			Vector unvisitedEdges = returnUnvisitedEdge();
			if ( unvisitedEdges.size() == 0)
			{
				_logger.debug( "All edges has been visited!" );
				return;
			}
			_logger.debug( "Found " + unvisitedEdges.size() + " unvisited edges (" + _graph.getEdges().size() + ")" );

			DirectedSparseEdge e = null;
			do
			{
				_logger.debug( "Number of unvisited edges:        " + unvisitedEdges.size() );
				_logger.debug( "Latest number of unvisited edges: " + _latestNumberOfUnvisitedEdges );
				if ( e == null && _backtracking == false )
				{
					_logger.info( "Strategy: Optimized - Get an unvisited edge, selected randomly from the graph." );
					Object[] shuffledList = shuffle( unvisitedEdges.toArray() );
					e = (DirectedSparseEdge)shuffledList[ 0 ];
				}
				else if ( unvisitedEdges.size() == _latestNumberOfUnvisitedEdges || _backtracking == true )
				{
					_logger.info( "Strategy: Random from vertex - Get a random out edge from the currrent vertex: '" + _prevVertex.getUserDatum( Keywords.LABEL_KEY ) + "'" );
					Object[] shuffledList = shuffle( _prevVertex.getOutEdges().toArray() );
					e = (DirectedSparseEdge)shuffledList[ 0 ];					
				}
				// We have tried the unvisited edges, but did not get a reachable path. So we try anything now.
				else
				{
					_logger.info( "Strategy: Truly random - Get any edge, selected randomly from the graph." );
					Object[] shuffledList = shuffle( _graph.getEdges().toArray() );
					e = (DirectedSparseEdge)shuffledList[ 0 ];
				}
				
				if ( e == null )
				{
					throw new RuntimeException( "Found an empty edge!" );
				}
				_logger.info( "Selecting edge: " + Util.getCompleteEdgeName( e ) );
				_shortestPathToVertex = new DijkstraShortestPath( _graph ).getPath( _nextVertex, e.getSource() );
	
				// DijkstraShortestPath.getPath returns 0 if there is no way to reach the destination. But,
				// DijkstraShortestPath.getPath also returns 0 paths if the the source and destination vertex are the same, even if there is
				// an edge there (self-loop). So we have to check for that.
				if ( _shortestPathToVertex.size() == 0 )
				{
					if ( _nextVertex.getUserDatum( Keywords.INDEX_KEY ) != e.getSource().getUserDatum( Keywords.INDEX_KEY ) )
					{
						String msg = "There is no way to reach: " + Util.getCompleteEdgeName( e ) + ", from: '" + _nextVertex.getUserDatum( Keywords.LABEL_KEY ) + "' " + _nextVertex.getUserDatum( Keywords.INDEX_KEY );
						_logger.warn( msg );
					}
				}
			} while ( _shortestPathToVertex.size() == 0 && ( _nextVertex.getUserDatum( Keywords.INDEX_KEY ) != e.getSource().getUserDatum( Keywords.INDEX_KEY ) ) ); 

			_latestNumberOfUnvisitedEdges = unvisitedEdges.size(); 
			_shortestPathToVertex.add( e );
			_logger.info( "Intend to take the shortest (" + _shortestPathToVertex.size() + " hops) path between: '" + _nextVertex.getUserDatum( Keywords.LABEL_KEY ) + "' and '" + (String)e.getDest().getUserDatum( Keywords.LABEL_KEY ) + "' (from: '" + e.getSource().getUserDatum( Keywords.LABEL_KEY ) + "')" );

			String paths = "The route is: ";
			for (Iterator iter = _shortestPathToVertex.iterator(); iter.hasNext();)
			{
				DirectedSparseEdge item = (DirectedSparseEdge) iter.next();
				paths += Util.getCompleteEdgeName( item ) + " ==> ";
			}
			paths += " Done!";
			_logger.info( paths );
		}

		if ( _shortestPathToVertex != null && _shortestPathToVertex.size() > 0 )
		{
			edge = (DirectedSparseEdge)_shortestPathToVertex.get( 0 );
			_shortestPathToVertex.remove( 0 );
			_logger.debug( "Removed edge: " + Util.getCompleteEdgeName( edge ) + " from the shortest path list, " + _shortestPathToVertex.size() + " hops remaining." );

			if ( _shortestPathToVertex.size() == 0 )
			{
				_shortestPathToVertex = null;
			}
		}
		else if ( optimize )
		{
			// Look for an edge that has not been visited yet.
			for ( int i = 0; i < outEdges.length; i++ )
			{
				edge = (DirectedSparseEdge)outEdges[ i ];

				Integer vistited = (Integer)edge.getUserDatum( Keywords.VISITED_KEY );
				if ( vistited.intValue() == 0 )
				{
					if ( _rejectedEdge == edge )
					{
						// This edge has been rejected, because some condition was not fullfilled.
						// Try with the next edge in the for-loop.
						// _rejectedEdge has to be set to null, because it can be valid next time.
						_rejectedEdge = null;
					}
					else
					{
						_logger.debug( "Found an edge which has not been visited yet: " + Util.getCompleteEdgeName( edge ) );
						break;
					}
				}
				edge = null;
			}
			if ( edge == null )
			{
				_logger.debug( "All edges has been visited (" + outEdges.length + ")" );
				edge = getWeightedEdge( _nextVertex );
			}
		}
		else
		{
			edge = getWeightedEdge( _nextVertex );
		}

		if ( edge == null  )
		{
			throw new RuntimeException( "Did not find any edge." );
		}
		_logger.debug( "Edge = '" + Util.getCompleteEdgeName( edge ) + "'" );

		_prevVertex = _nextVertex;
		_nextVertex = (DirectedSparseVertex)edge.getDest();
		
		try
		{
			String label = (String)edge.getUserDatum( Keywords.LABEL_KEY );
			_logger.debug( "Invoke method for edge: '" + label + "' and index: " + edge.getUserDatum( Keywords.INDEX_KEY ) );
			invokeMethod( label, dryRun, suppressPrintout );
			Integer vistited = (Integer)edge.getUserDatum( Keywords.VISITED_KEY );
			vistited = new Integer( vistited.intValue() + 1 );
			edge.setUserDatum( Keywords.VISITED_KEY, vistited, UserData.SHARED );

			label = (String)edge.getDest().getUserDatum( Keywords.LABEL_KEY );
			_logger.debug( "Invoke method for vertex: '" + label + "' and index: " +  edge.getDest().getUserDatum( Keywords.INDEX_KEY ) );
			invokeMethod( label, dryRun, suppressPrintout );
			vistited = (Integer)edge.getDest().getUserDatum( Keywords.VISITED_KEY );
			vistited = new Integer( vistited.intValue() + 1 );
			edge.getDest().setUserDatum( Keywords.VISITED_KEY, vistited, UserData.SHARED );
			
			_currentEdge = edge;
			_backtracking = false;
		}
		catch( GoBackToPreviousVertexException e )
		{
			_logger.info( "The edge: " + Util.getCompleteEdgeName( edge ) + " can not be run due to unfullfilled conditions." );
			_logger.info( "Trying from vertex: '" + (String)_prevVertex.getUserDatum( Keywords.LABEL_KEY ) + "' again." );
			_rejectedEdge = edge;
			_nextVertex   = _prevVertex;

			if ( _runUntilAllEdgesVisited )
			{
				_shortestPathToVertex = null;
				_runUntilAllEdgesVisited = false;
			}
		}
	}

	private void invokeMethod( String method, boolean dryRun, boolean suppressPrintout ) throws GoBackToPreviousVertexException
	{
		Class cls = null;
		
		if ( dryRun == false )
		{
			cls = _object.getClass();
			_pathHistory.add( method );
		}

		if ( method == null )
		{
			_pathHistory.add( "" );
			return;
		}


		try
		{
			if ( method.compareTo( "" ) != 0 )
			{
				if ( dryRun )
				{
					if ( suppressPrintout == false )
					{
						System.out.println( method );
					}
				}
				else
				{
					Method meth = cls.getMethod( method, null );
					meth.invoke( _object, null  );
				}
			}
		}
		catch( NoSuchMethodException e )
		{
			_logger.error( e );
			_logger.error( "Try to invoke method: " + method );
			throw new RuntimeException( "The method is not defined: " + method );
		}
		catch( java.lang.reflect.InvocationTargetException e )
		{
			if ( e.getTargetException().getClass() == GoBackToPreviousVertexException.class )
			{
				throw new GoBackToPreviousVertexException();
			}

			_logger.error( e.getCause().getMessage() );
			e.getCause().printStackTrace();
			throw new RuntimeException( e.getCause().getMessage() );
		}
		catch( Exception e )
		{
			_logger.error( e );
			e.printStackTrace();
			throw new RuntimeException( "Abrupt end of execution: " + e.getMessage() );
		}
	}

	/**
	 * Returns a random edge from the vertex's list of outgoing edges.
	 * If any edge is weighted, this will be taken in consideration.
	 *
	 * @param DirectedSparseVertex
	 * @return DirectedSparseEdge
	 */
	private DirectedSparseEdge getWeightedEdge( DirectedSparseVertex vertex ) throws FoundNoEdgeException
	{
		Object[] edges = vertex.getOutEdges().toArray();
		DirectedSparseEdge edge = null;
		float probabilities[]   = new float[ edges.length ];
		int   numberOfZeros     = 0;
		float sum               = 0;

		for ( int i = 0; i < edges.length; i++ )
		{
			edge = (DirectedSparseEdge)edges[ i ];

			if ( edge.containsUserDatumKey( Keywords.WEIGHT_KEY ) )
			{
				Float weight = (Float)edge.getUserDatum( Keywords.WEIGHT_KEY );
				probabilities[ i ] = weight.floatValue();
				sum += probabilities[ i ];
			}
			else
			{
				numberOfZeros++;
				probabilities[ i ] = 0;
			}
		}

		if ( sum > 1 )
		{
			throw new RuntimeException( "The sum of all weight from vertex: '" + (String)vertex.getUserDatum( Keywords.LABEL_KEY ) + "' adds to more than 1" );
		}

		float rest = ( 1 - sum ) / numberOfZeros;
		int index = _radomGenerator.nextInt( 100 );
		_logger.debug( "Randomized integer index = " + index );

		float weight = 0;
		for ( int i = 0; i < edges.length; i++ )
		{
			if ( probabilities[ i ] == 0 )
			{
				probabilities[ i ] = rest;
			}
			_logger.debug( "The edge: '" + (String)((DirectedSparseEdge)edges[ i ]).getUserDatum( Keywords.LABEL_KEY ) + "' is given the probability of " + probabilities[ i ] * 100 + "%"  );

			weight = weight + probabilities[ i ] * 100;
			_logger.debug( "Current weight is: " + weight  );
			if ( index < weight )
			{
				edge = (DirectedSparseEdge)edges[ i ];
				_logger.debug( "Selected edge is: " + Util.getCompleteEdgeName( edge ) );
				break;
			}
		}

		if ( edge == null )
		{
			_logger.error( "Vertex: '" + (String)vertex.getUserDatum( Keywords.LABEL_KEY ) + "', has no out edges. Test ends here!" );
			throw new FoundNoEdgeException();
		}

		return edge;
	}

	/**
	 * This functions shuffle the array, and returns the shuffled array
	 * @param array
	 * @return
	 */
	private Object[] shuffle( Object[] array )
	{
		for ( int i = 0; i < array.length; i++ )
		{
			Object leftObject = array[ i ];
			int index = _radomGenerator.nextInt( array.length );
			Object rightObject = array[ index ];

			array[ i ]     = rightObject;
			array[ index ] = leftObject;
		}
		return array;
	}



	/**
	 * This functions returns a list of edges, which has not yet been visited
	 * @return DirectedSparseEdge
	 */
	private Vector returnUnvisitedEdge()
	{
		Vector edgesNotVisited = new Vector();

		for ( int i = 0; i < _edges.length; i++ )
		{
			DirectedSparseEdge edge = (DirectedSparseEdge)_edges[ i ];

			Integer vistited = (Integer)edge.getUserDatum( Keywords.VISITED_KEY );
			if ( vistited.intValue() == 0 )
			{
				edgesNotVisited.add( edge );
				_logger.debug( "Unvisited: " + Util.getCompleteEdgeName( edge ) );
			}
		}

		return edgesNotVisited;
	}

	public void writeWalkedPath( String fileName )
	{
		try
		{
			FileWriter file = new FileWriter( fileName );

			StringBuffer strBuff = new StringBuffer();

			for (Iterator iter = _pathHistory.iterator(); iter.hasNext();)
			{
				String element = (String) iter.next();
				strBuff.append( element + "\n" );
			}

			file.write( strBuff.toString() );
			file.flush();
		}
		catch ( IOException e )
		{
			_logger.error( e.getMessage() );
		}
	}

	private boolean is_cul_de_sac() {
		return _cul_de_sac;
	}

	public void set_cul_de_sac(boolean _cul_de_sac) {
		this._cul_de_sac = _cul_de_sac;
	}
	
	// Returns, in percent, how many edges has been visited.
	public float getTestCoverage4Edges()
	{
		Object[] edges    = _graph.getEdges().toArray();
		if ( edges.length == 0 )
		{
			return 0;
		}

		int numOfVisitedEdges         = 0;

		for ( int i = 0; i < edges.length; i++ )
		{
			DirectedSparseEdge edge = (DirectedSparseEdge)edges[ i ];

			Integer vistited = (Integer)edge.getUserDatum( Keywords.VISITED_KEY );
			if ( vistited.intValue() != 0 )
			{
				numOfVisitedEdges++;
			}
		}

		return ( numOfVisitedEdges / (float)edges.length * 100 );
	}

	// Returns, in percent, how many vertices has been visited.
	public float getTestCoverage4Vertices()
	{
		Object[] vertices = _graph.getVertices().toArray();
		if ( vertices.length == 0 )
		{
			return 0;
		}

		int numOfVisitedVertices      = 0;

		for ( int i = 0; i < vertices.length; i++ )
		{
			DirectedSparseVertex vertex = (DirectedSparseVertex)vertices[ i ];

			Integer vistited = (Integer)vertex.getUserDatum( Keywords.VISITED_KEY );
			if ( vistited.intValue() != 0 )
			{
				numOfVisitedVertices++;
			}
		}
		return (numOfVisitedVertices / (float)vertices.length * 100);
	}
}
