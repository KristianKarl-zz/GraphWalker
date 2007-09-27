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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.utils.Pair;
import edu.uci.ics.jung.utils.UserData;

/**
 * @author Kristian Karl
 */
public class ModelBasedTesting
{

	private SparseGraph 				_graph          = new SparseGraph();
	private java.util.Vector 			_graphList 		= new Vector();
	private java.util.Random      		_radomGenerator = new Random();

	private String  START_NODE                  = "Start";
	private String  STOP_NODE                   = "Stop";
	private String  ID_KEY                      = "id";
	private String  IMAGE_KEY                   = "image";
	private String  WIDTH_KEY                   = "width";
	private String  HEIGHT_KEY                  = "height";
	private String  FILE_KEY                    = "file";
	private String  LABEL_KEY                   = "label";
	private String  FULL_LABEL_KEY              = "full_label";
	private String  VISITED_KEY                 = "visited";
	private String  WEIGHT_KEY                  = "weight";
	private String  STATE_KEY                   = "state";
	private String  CONDITION_KEY               = "condition";
	private String  VARIABLE_KEY                = "variable";
	private String  INDEX_KEY                   = "index";
	private String  MERGE	                  	= "merge";
	private String  NO_MERGE	          	  	= "no merge";
	private String  MERGED_BY_MBT	          	= "merged by mbt";
	private String  MOTHER_GRAPH_START_VERTEX   = "mother graph start vertex";
	private String  SUBGRAPH_START_VERTEX       = "subgraph start vertex";
	private String  BLOCKED	                    = "BLOCKED";
	private String  BACKTRACK	                = "BACKTRACK";

	private String   				_graphmlFileName;
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
	private int				    	_vertexAndEdgeIndex = 0;
	private Vector 					_reservedKeyWords = new Vector(); 
	
	{
		_reservedKeyWords.add( "BLOCKED" );
		_reservedKeyWords.add( "BACKTRACK" );
		_reservedKeyWords.add( "MERGE" );
		_reservedKeyWords.add( "NO_MERGE" );		
	}
	
	private boolean isKeyWord( String wordToCheck )
	{
        for ( Iterator iter = _reservedKeyWords.iterator(); iter.hasNext(); )
        {
            String keyWord = (String) iter.next();
            if ( keyWord.equals( wordToCheck ) )
            {
            	return true;
            }
        }
        return false;
	}
 	
	private int getNewVertexAndEdgeIndex()
	{
		return ++_vertexAndEdgeIndex;
	}
 	
	public ModelBasedTesting( String graphmlFileName_,
			  				  Object object_ )
	{
		_graphmlFileName = graphmlFileName_;
		_object          = object_;
		_logger          = org.apache.log4j.Logger.getLogger( ModelBasedTesting.class );

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

		readFiles();
	}

	public ModelBasedTesting()
	{
		_object          = null;
		_logger          = org.apache.log4j.Logger.getLogger( ModelBasedTesting.class );
		
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
	}

	
	
	public void readGraph( String graphmlFileName_ )
	{
		_graphmlFileName = graphmlFileName_;
		readFiles();		
	}
	
	
	
	public void initialize( String graphmlFileName, boolean randomWalk, long executionTime )
	{
		_graphmlFileName = graphmlFileName;
		_randomWalk      = randomWalk;
		_executionTime   = executionTime;
		
		if ( _randomWalk )
		{
			_runUntilAllEdgesVisited = false;
		}
		else
		{
			_runUntilAllEdgesVisited = true;
		}

		readFiles();
	}
	
	
	
	public void writeGraph( SparseGraph g, String mergedGraphml_ )
	{
		StringBuffer sourceFile = new StringBuffer();
		try {
			FileWriter file = new FileWriter( mergedGraphml_ );

			sourceFile.append( "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" );
			sourceFile.append( "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns/graphml\"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns/graphml http://www.yworks.com/xml/schema/graphml/1.0/ygraphml.xsd\" xmlns:y=\"http://www.yworks.com/xml/graphml\">\n" );
			sourceFile.append( "  <key id=\"d0\" for=\"node\" yfiles.type=\"nodegraphics\"/>\n" );
			sourceFile.append( "  <key id=\"d1\" for=\"edge\" yfiles.type=\"edgegraphics\"/>\n" );
			sourceFile.append( "  <graph id=\"G\" edgedefault=\"directed\">\n" );

	        int numVertices = g.getVertices().size();
	        edu.uci.ics.jung.graph.decorators.Indexer id = edu.uci.ics.jung.graph.decorators.Indexer.getAndUpdateIndexer( g );
	        for ( int i = 0; i < numVertices; i++ )
	        {
	            Vertex v = (Vertex) id.getVertex(i);
	            int vId = i+1;

				sourceFile.append( "    <node id=\"n" + vId + "\">\n" );
				sourceFile.append( "      <data key=\"d0\" >\n" );

				if ( v.containsUserDatumKey( IMAGE_KEY ) )
				{
					sourceFile.append( "        <y:ImageNode >\n" );
					sourceFile.append( "          <y:Geometry  x=\"241.875\" y=\"158.701171875\" width=\"" + v.getUserDatum( WIDTH_KEY ) + "\" height=\"" + v.getUserDatum( HEIGHT_KEY ) + "\"/>\n" );
				}
				else
				{
					sourceFile.append( "        <y:ShapeNode >\n" );
					sourceFile.append( "          <y:Geometry  x=\"241.875\" y=\"158.701171875\" width=\"95.0\" height=\"30.0\"/>\n" );
				}
				
				sourceFile.append( "          <y:Fill color=\"#CCCCFF\"  transparent=\"false\"/>\n" );
				sourceFile.append( "          <y:BorderStyle type=\"line\" width=\"1.0\" color=\"#000000\" />\n" );
				sourceFile.append( "          <y:NodeLabel x=\"1.5\" y=\"5.6494140625\" width=\"92.0\" height=\"18.701171875\" visible=\"true\" alignment=\"center\" fontFamily=\"Dialog\" fontSize=\"12\" fontStyle=\"plain\" textColor=\"#000000\" modelName=\"internal\" modelPosition=\"c\" autoSizePolicy=\"content\">" + v.getUserDatum( FULL_LABEL_KEY ) + "&#xA;INDEX=" + v.getUserDatum( INDEX_KEY ) + "</y:NodeLabel>\n" );
				
				if ( v.containsUserDatumKey( IMAGE_KEY ) )
				{
					sourceFile.append( "          <y:Image href=\"" + v.getUserDatum( IMAGE_KEY ) + "\"/>\n" );
					sourceFile.append( "        </y:ImageNode>\n" );
				}
				else
				{
					sourceFile.append( "          <y:Shape type=\"rectangle\"/>\n" );
					sourceFile.append( "        </y:ShapeNode>\n" );
				}
				
				sourceFile.append( "      </data>\n" );
				sourceFile.append( "    </node>\n" );
			}

	        int i = 0;
	        for ( Iterator edgeIterator = g.getEdges().iterator(); edgeIterator.hasNext(); )
	        {
	            Edge e = (Edge) edgeIterator.next();
	            Pair p = e.getEndpoints();
	            Vertex src = (Vertex) p.getFirst();
	            Vertex dest = (Vertex) p.getSecond();
	            int srcId = id.getIndex(src)+1;
	            int destId = id.getIndex(dest)+1;
	            int nId = ++i;

	            sourceFile.append( "    <edge id=\"" + nId + "\" source=\"n" + srcId + "\" target=\"n" + destId + "\">\n" );
	            sourceFile.append( "      <data key=\"d1\" >\n" );
	            sourceFile.append( "        <y:PolyLineEdge >\n" );
	            sourceFile.append( "          <y:Path sx=\"-23.75\" sy=\"15.0\" tx=\"-23.75\" ty=\"-15.0\">\n" );
	            sourceFile.append( "            <y:Point x=\"273.3125\" y=\"95.0\"/>\n" );
	            sourceFile.append( "            <y:Point x=\"209.5625\" y=\"95.0\"/>\n" );
	            sourceFile.append( "            <y:Point x=\"209.5625\" y=\"143.701171875\"/>\n" );
	            sourceFile.append( "            <y:Point x=\"265.625\" y=\"143.701171875\"/>\n" );
	            sourceFile.append( "          </y:Path>\n" );
	            sourceFile.append( "          <y:LineStyle type=\"line\" width=\"1.0\" color=\"#000000\" />\n" );
	            sourceFile.append( "          <y:Arrows source=\"none\" target=\"standard\"/>\n" );
	            
	            if ( e.getUserDatum( FULL_LABEL_KEY ) != null )
	            {
	            	sourceFile.append( "          <y:EdgeLabel x=\"-148.25\" y=\"30.000000000000014\" width=\"169.0\" height=\"18.701171875\" visible=\"true\" alignment=\"center\" fontFamily=\"Dialog\" fontSize=\"12\" fontStyle=\"plain\" textColor=\"#000000\" modelName=\"free\" modelPosition=\"anywhere\" preferredPlacement=\"on_edge\" distance=\"2.0\" ratio=\"0.5\">" + e.getUserDatum( FULL_LABEL_KEY ) + "&#xA;INDEX=" + e.getUserDatum( INDEX_KEY ) + "</y:EdgeLabel>\n" );
	            }
	            
	            sourceFile.append( "          <y:BendStyle smoothed=\"false\"/>\n" );
	            sourceFile.append( "        </y:PolyLineEdge>\n" );
	            sourceFile.append( "      </data>\n" );
	            sourceFile.append( "    </edge>\n" );

	        }

	        sourceFile.append( "  </graph>\n" );
	        sourceFile.append( "</graphml>\n" );

			file.write( sourceFile.toString() );
			file.flush();
			_logger.info( "Wrote: " +  mergedGraphml_ );
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void readFiles()
	{
		File file = new File( _graphmlFileName );
		if ( file.isFile() )
		{
	    	_graphList.add( parseFile( _graphmlFileName ) );
		}
		else if ( file.isDirectory() )
		{
		    // Only accpets files which suffix is .graphml
		    FilenameFilter filter = new FilenameFilter()
		    {
		        public boolean accept( File dir, String name )
		        {
		            return name.endsWith( ".graphml" );
		        }
		    };

		    File [] allChildren = file.listFiles( filter );
		    for ( int i = 0; i < allChildren.length; ++i )
		    {
		    	_graphList.add( parseFile( allChildren[ i ].getAbsolutePath() ) );
		    }
		}
		else
		{
			throw new RuntimeException( "'" + _graphmlFileName + "' is not a file or a directory. Please specify a valid .graphml file or a directory containing .graphml files" );
		}
	    mergeAllGraphs();
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
			Integer index = (Integer)v.getUserDatum( INDEX_KEY );
			if ( index.intValue() == vertexIndex.intValue() )
			{
				_shortestPathToVertex = null;
				_nextVertex = v;
				_backtracking = true;
				_logger.debug( "Backtracking to vertex: '" + (String)v.getUserDatum( LABEL_KEY ) + "', with index: " + index.intValue() );
				return;
			}
		}
		String msg = new String( "Did not find a vertex with index: " + vertexIndex.intValue() );
		_logger.debug( msg );
		throw new RuntimeException( msg );
	}


	/**
	 * Put mbt back in inital state, which means that test will begin
	 * at the Start vertex. 
	 */
	public void reset()
	{
		findStartingVertex();
		_start_time = System.currentTimeMillis();
	}


	/**
	 * Run the test untill all vertices (nodes) are visited.
	 */
	public void runUntilAllVerticesVisited() throws FoundNoEdgeException
	{
		reset();

		while ( true )
		{
			executeMethod( true, false, false );
			_end_time = System.currentTimeMillis();
			if ( isAllVerticesVisited() )
			{
				break;
			}
		}
	}


	/**
	 * Run the test untill all edges (arrows, or transistions) are visited.
	 */
	public void runUntilAllEdgesVisited() throws FoundNoEdgeException
	{
		_runUntilAllEdgesVisited = true;
		reset();

		while ( true )
		{
			executeMethod( true, false, false );
			_end_time = System.currentTimeMillis();
			if ( isAllEdgesVisited() )
			{
				break;
			}
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
			while ( true )
			{
				executeMethod( true, true, false );
				if ( isAllEdgesVisited() )
				{
					break;
				}
			}
			
			return;
		}
        catch ( Exception e )
		{
			e.printStackTrace();
        }
        
        return;
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
					    _logger.debug( "Current edge =   " + getCompleteEdgeName( _currentEdge ) );
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
				    _logger.debug( "Current edge =   " + getCompleteEdgeName( _currentEdge ) );
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
			    _logger.debug( "Current edge =   " + getCompleteEdgeName( _currentEdge ) );
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
	 * Run the test untill all edges (arrows, or transistions), and
	 * all vertices (nodes) are visited
	 */
	public void runUntilAllVerticesAndEdgesVisited() throws FoundNoEdgeException
	{
		findStartingVertex();
		_runUntilAllEdgesVisited = true;
		_start_time = System.currentTimeMillis();
		while ( true )
		{
			executeMethod( true, false, false );
			_end_time = System.currentTimeMillis();
			if ( isAllVerticesVisited() && isAllEdgesVisited() )
			{
				break;
			}
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

			Integer vistited = (Integer)vertex.getUserDatum( VISITED_KEY );
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

			Integer vistited = (Integer)edge.getUserDatum( VISITED_KEY );
			if ( vistited.intValue() == 0 )
			{
				return false;
			}
		}
		return true;
	}


	/**
	 * Parses the graphml file, and load into the internal graph structure _graph
	 */
	private SparseGraph parseFile( String fileName )
	{
		SparseGraph graph = new SparseGraph();
		graph.addUserDatum( FILE_KEY, fileName, UserData.SHARED );
		SAXBuilder parser = new SAXBuilder( "org.apache.crimson.parser.XMLReaderImpl", false );		
				
		try
		{
			_logger.info( "Parsing file: " + fileName );
			Document doc = parser.build( fileName );

			// Parse all vertices (nodes)
			Iterator iter_node = doc.getDescendants( new org.jdom.filter.ElementFilter( "node" ) );
			while ( iter_node.hasNext() )
			{
				Object o = iter_node.next();
				if ( o instanceof org.jdom.Element )
				{
					org.jdom.Element element = (org.jdom.Element)o;
					if ( element.getAttributeValue( "yfiles.foldertype" ) != null )
					{
						_logger.debug( "  Excluded node: " + element.getAttributeValue( "yfiles.foldertype" ) );
						continue;
					}
					Iterator iterUMLNoteIter = element.getDescendants( new org.jdom.filter.ElementFilter( "UMLNoteNode" ) );
					if ( iterUMLNoteIter.hasNext() )
					{
						_logger.debug( "  Excluded node: UMLNoteNode" );
						continue;
					}
					_logger.debug( "  id: " + element.getAttributeValue( "id" ) );

					// Used to remember which vertext to store the image location.
					DirectedSparseVertex currentVertex = null;
					
					Iterator iterNodeLabel = element.getDescendants( new org.jdom.filter.ElementFilter( "NodeLabel" ) );
					while ( iterNodeLabel.hasNext() )
					{
						Object o2 = iterNodeLabel.next();
						if ( o2 instanceof org.jdom.Element )
						{
							org.jdom.Element nodeLabel = (org.jdom.Element)o2;
							_logger.debug( "  Full name: '" + nodeLabel.getQualifiedName() + "'" );
							_logger.debug( "  Name: '" + nodeLabel.getTextTrim() + "'" );

							DirectedSparseVertex v = (DirectedSparseVertex) graph.addVertex( new DirectedSparseVertex() );
							currentVertex = v;

							v.addUserDatum( ID_KEY, 	 	element.getAttributeValue( "id" ),         UserData.SHARED );
							v.addUserDatum( VISITED_KEY, 	new Integer( 0 ), 				           UserData.SHARED );
							v.addUserDatum( FILE_KEY, 	 	fileName, 						   		   UserData.SHARED );
							v.addUserDatum( FULL_LABEL_KEY, nodeLabel.getTextTrim(), 		           UserData.SHARED );
							v.addUserDatum( INDEX_KEY,      new Integer( getNewVertexAndEdgeIndex() ), UserData.SHARED );

							String str = nodeLabel.getTextTrim();
							Pattern p = Pattern.compile( "(.*)", Pattern.MULTILINE );
							Matcher m = p.matcher( str );
							String label;
							if ( m.find() )
							{
								label = m.group( 1 );
								if ( label.length() <= 0 )
								{
									throw new RuntimeException( "Vertex is missing its label in file: '" + fileName + "'" );
								}
								if ( label.matches( ".*[\\s].*" ) )
								{
									throw new RuntimeException( "Vertex has a label '" + label  + "', containing whitespaces in file: '" + fileName + "'" );
								}
								if ( isKeyWord( label ) )
								{
									throw new RuntimeException( "Vertex has a label '" + label  + "', which is a reserved keyword, in file: '" + fileName + "'" );
								}
								v.addUserDatum( LABEL_KEY, label, UserData.SHARED );
							}
							else
							{
								throw new RuntimeException( "Label must be defined in file: '" + fileName + "'" );
							}
							_logger.debug( "  Added vertex: '" + v.getUserDatum( LABEL_KEY ) + "', with id: " + v.getUserDatum( INDEX_KEY ) );





							// If merge is defined, find it...
							// If defined, it means that the node will be merged with all other nodes wit the same name,
							// but not replaced by any subgraphs
							p = Pattern.compile( "\\n(MERGE)", Pattern.MULTILINE );
							m = p.matcher( str );
							if ( m.find() )
							{
								v.addUserDatum( MERGE, m.group( 1 ), UserData.SHARED );
								_logger.debug( "  Found MERGE for vertex: " + label );
							}



							// If no merge is defined, find it...
							// If defined, it means that when merging graphs, this specific vertex will not be merged
							// or replaced by any subgraphs
							p = Pattern.compile( "\\n(NO_MERGE)", Pattern.MULTILINE );
							m = p.matcher( str );
							if ( m.find() )
							{
								v.addUserDatum( NO_MERGE, m.group( 1 ), UserData.SHARED );
								_logger.debug( "  Found NO_MERGE for vertex: " + label );
							}



							// If BLOCKED is defined, find it...
							// If defined, it means that this vertex will not be added to the graph
							// Sometimes it can be useful during testing to mark vertcies as BLOCKED
							// due to bugs in the system you test. When the bug is removed, the BLOCKED
							// tag can be removed.
							p = Pattern.compile( "\\n(BLOCKED)", Pattern.MULTILINE );
							m = p.matcher( str );
							if ( m.find() )
							{
								_logger.debug( "  Found BLOCKED. This vetex will be removed from the graph: " + label );
								v.addUserDatum( BLOCKED, BLOCKED, UserData.SHARED );
							}



							// If INDEX is defined, find it...
							// If defined, it means that this vertex has already a unique id gnerated
							// by mbt before, so we use this instead..
							p = Pattern.compile( "\\n(INDEX=(.*))", Pattern.MULTILINE );
							m = p.matcher( str );
							if ( m.find() )
							{
								String index_key = m.group( 2 );
								_logger.debug( "  Found INDEX. This vertex will use the INDEX key: " + index_key );
								v.setUserDatum( INDEX_KEY, new Integer( index_key ), UserData.SHARED );
							}
						}
					}
					
					// Using the yEd editor, an image can be used to depict the vertex. When merging multiple
					// graphs into one, the code below, stores the image location, which will be used when
					// writing that merged graphml file.
					Iterator iterImage = element.getDescendants( new org.jdom.filter.ElementFilter( "Image" ) );
					while ( iterImage.hasNext() && currentVertex != null)
					{
						Object o2 = iterImage.next();
						if ( o2 instanceof org.jdom.Element )
						{
							org.jdom.Element image = (org.jdom.Element)o2;
							_logger.debug( "  Image: '" + image.getAttributeValue( "href" ) + "'" );
							currentVertex.addUserDatum( IMAGE_KEY, image.getAttributeValue( "href" ), UserData.SHARED );
						}
					}
					Iterator iterGeometry = element.getDescendants( new org.jdom.filter.ElementFilter( "Geometry" ) );
					while ( iterGeometry.hasNext() && currentVertex != null)
					{
						Object o2 = iterGeometry.next();
						if ( o2 instanceof org.jdom.Element )
						{
							org.jdom.Element geometry = (org.jdom.Element)o2;
							_logger.debug( "  width: '" + geometry.getAttributeValue( "width" ) + "'" );
							_logger.debug( "  height: '" + geometry.getAttributeValue( "height" ) + "'" );
							currentVertex.addUserDatum( WIDTH_KEY, geometry.getAttributeValue( "width" ), UserData.SHARED );							
							currentVertex.addUserDatum( HEIGHT_KEY, geometry.getAttributeValue( "height" ), UserData.SHARED );
						}
					}
				}
			}

			Object[] vertices = graph.getVertices().toArray();

			// Parse all edges (arrows or transtitions)
			Iterator iter_edge = doc.getDescendants( new org.jdom.filter.ElementFilter( "edge" ) );
			while ( iter_edge.hasNext() )
			{
				Object o = iter_edge.next();
				if ( o instanceof org.jdom.Element )
				{
					org.jdom.Element element = (org.jdom.Element)o;
					_logger.debug( "  id: " + element.getAttributeValue( "id" ) );

					Iterator iter2 = element.getDescendants( new org.jdom.filter.ElementFilter( "EdgeLabel" ) );
					org.jdom.Element edgeLabel = null;
					if ( iter2.hasNext() )
					{
						Object o2 = iter2.next();
						if ( o2 instanceof org.jdom.Element )
						{
							edgeLabel = (org.jdom.Element)o2;
							_logger.debug( "  Full name: '" + edgeLabel.getQualifiedName() + "'" );
							_logger.debug( "  Name: '" + edgeLabel.getTextTrim() + "'" );
						}
					}
					_logger.debug( "  source: " + element.getAttributeValue( "source" ) );
					_logger.debug( "  target: " + element.getAttributeValue( "target" ) );

					DirectedSparseVertex source = null;
					DirectedSparseVertex dest = null;

					for ( int i = 0; i < vertices.length; i++ )
					{
						DirectedSparseVertex vertex = (DirectedSparseVertex)vertices[ i ];

						// Find source vertex
						if ( vertex.getUserDatum( ID_KEY ).equals( element.getAttributeValue( "source" ) ) &&
							 vertex.getUserDatum( FILE_KEY ).equals( fileName ) )
						{
							source = vertex;
						}
						if ( vertex.getUserDatum( ID_KEY ).equals( element.getAttributeValue( "target" ) ) &&
							 vertex.getUserDatum( FILE_KEY ).equals( fileName ) )
						{
							dest = vertex;
						}
					}
					if ( source == null )
					{
						String msg = "Could not find starting node for edge. Name: '" + element.getAttributeValue( "source" ) + "' In file '" + fileName + "'";
						_logger.error( msg );
						throw new RuntimeException( msg );
					}
					if ( dest == null )
					{
						String msg = "Could not find end node for edge. Name: '" + element.getAttributeValue( "target" ) + "' In file '" + fileName + "'";
						_logger.error( msg );
						throw new RuntimeException( msg );
					}


					DirectedSparseEdge e = new DirectedSparseEdge( source, dest );
					graph.addEdge( e );
					e.addUserDatum( ID_KEY,    element.getAttributeValue( "id" ),         UserData.SHARED );
					e.addUserDatum( FILE_KEY,  fileName, 						          UserData.SHARED );
					e.addUserDatum( INDEX_KEY, new Integer( getNewVertexAndEdgeIndex() ), UserData.SHARED );


					if ( edgeLabel != null )
					{
						String str = edgeLabel.getText();
						e.addUserDatum( FULL_LABEL_KEY, str, UserData.SHARED );
						Pattern p = Pattern.compile( "(.*)", Pattern.MULTILINE );
						Matcher m = p.matcher( str );
						String label = null;
						if ( m.find() )
						{
							label = m.group( 1 );
							if ( label.length() > 0 )
							{
								if ( label.matches( ".*[\\s].*" ) )
								{
									throw new RuntimeException( "Edge has a label '" + label + "',  '" + getCompleteEdgeName( e ) + "', containing whitespaces in file: '" + fileName + "'" );
								}
								if ( isKeyWord( label ) )
								{
									throw new RuntimeException( "Edge has a label '" + label  + "', which is a reserved keyword, in file: '" + fileName + "'" );
								}
								e.addUserDatum( LABEL_KEY, label, UserData.SHARED );
								_logger.debug( " Found label = '" + label + "' for edge id: " + edgeLabel.getQualifiedName() );
							}
						}
						else
						{
							throw new RuntimeException( "Label for edge must be defined in file '" + fileName + "'" );
						}



						// If weight is defined, find it...
						// weight must be associated with a value, which depicts the probability for the edge
						// to be executed.
						// A value of 0.05 is the same as 5% chance of going down this road.
						p = Pattern.compile( "\\n(weight=(.*))", Pattern.MULTILINE );
						m = p.matcher( str );
						if ( m.find() )
						{
							Float weight;
							String value = m.group( 2 );
							try
							{
								weight = Float.valueOf( value.trim() );
								_logger.debug( "  Found weight= " + weight + " for edge: " + label );
							}
							catch ( NumberFormatException error )
							{
								throw new RuntimeException( "For label: " + label + ", weight is not a correct float value: " + error.toString() + " In file '" + fileName + "'" );
							}
							e.addUserDatum( WEIGHT_KEY, weight, UserData.SHARED );
						}



						// If BLOCKED is defined, find it...
						// If defined, it means that this edge will not be added to the graph
						// Sometimes it can be useful during testing to mark edges as BLOCKED
						// due to bugs in the system you test. When the bug is removed, the BLOCKED
						// tag can be removed.
						p = Pattern.compile( "\\n(BLOCKED)", Pattern.MULTILINE );
						m = p.matcher( str );
						if ( m.find() )
						{
							_logger.debug( "  Found BLOCKED. This edge will be removed from the graph: " + label );
							e.addUserDatum( BLOCKED, BLOCKED, UserData.SHARED );
						}



						// If BACKTRACK is defined, find it...
						// If defined, it means that executing this edge may not lead to the desired
						// vertex. So, if that happens, the model gives the user a chance to try an
						// other edge from the previous vertex (the source vertex of the edge defined
						// with BACKTRACK)
						p = Pattern.compile( "\\n(BACKTRACK)", Pattern.MULTILINE );
						m = p.matcher( str );
						if ( m.find() )
						{
							_logger.debug( "  Found BACKTRACK for edge: " + label );
							e.addUserDatum( BACKTRACK, BACKTRACK, UserData.SHARED );
						}



						// If condition used defined, find it...
						p = Pattern.compile( "\\n(if: (.*)=(.*))", Pattern.MULTILINE );
						m = p.matcher( str );
						HashMap conditions = null;
						while ( m.find( ) )
						{
							if ( conditions == null )
							{
								conditions = new HashMap();
							}
							String variable = m.group( 2 );
							Boolean state   = Boolean.valueOf( m.group( 3 ) );
							conditions.put( variable, state );
							_logger.debug( "  Condition: " + variable + " = " +  state );
						}
						if ( conditions != null )
						{
							e.addUserDatum( CONDITION_KEY, conditions, UserData.SHARED );
						}



						// If state are defined, find them...
						HashMap states = null;
						p = Pattern.compile( "\\n(state: (.*)=(.*))", Pattern.MULTILINE );
						m = p.matcher( str );
						while ( m.find( ) )
						{
							if ( states == null )
							{
								states = new HashMap();
							}
							String variable = m.group( 2 );
							Boolean state   = Boolean.valueOf( m.group( 3 ) );
							states.put( variable, state );
							_logger.debug( "  State: " + variable + " = " +  state );
						}
						if ( states != null )
						{
							e.addUserDatum( STATE_KEY, states, UserData.SHARED );
						}

						
						
						// If INDEX is defined, find it...
						// If defined, it means that this edge has already a unique id generated
						// by mbt before, so we use this instead..
						p = Pattern.compile( "\\n(INDEX=(.*))", Pattern.MULTILINE );
						m = p.matcher( str );
						if ( m.find() )
						{
							String index_key = m.group( 2 );
							_logger.debug( "  Found INDEX. This edge will use the INDEX key: " + index_key );
							e.setUserDatum( INDEX_KEY, new Integer( index_key ), UserData.SHARED );
						}



						// If string variables are defined, find them...
						HashMap variables = null;
						p = Pattern.compile( "\\n(string: (.*)=(.*))", Pattern.MULTILINE );
						m = p.matcher( str );
						while ( m.find( ) )
						{
							if ( variables == null )
								{
									variables = new HashMap();
								}
								String variableLabel = m.group( 2 );
								String variable = m.group( 3 );
								variables.put( variableLabel, variable );
								_logger.debug( "  String variable: " + variableLabel + " = " +  variable );
							}



							// If integer variables are defined, find them...
							p = Pattern.compile( "\\n(integer: (.*)=(.*))", Pattern.MULTILINE );
							m = p.matcher( str );
							while ( m.find( ) )
							{
								if ( variables == null )
								{
									variables = new HashMap();
								}
								String variableLabel = m.group( 2 );
								Integer variable   = Integer.valueOf( m.group( 3 ) );
								variables.put( variableLabel, variable );
								_logger.debug( "  Integer variable: " + variableLabel + " = " +  variable );
							}



							// If integer variables are defined, find them...
							p = Pattern.compile( "\\n(float: (.*)=(.*))", Pattern.MULTILINE );
							m = p.matcher( str );
							while ( m.find( ) )
							{
								if ( variables == null )
								{
									variables = new HashMap();
								}
								String variableLabel = m.group( 2 );
								Float variable   = Float.valueOf( m.group( 3 ) );
								variables.put( variableLabel, variable );
								_logger.debug( "  Float variable: " + variableLabel + " = " +  variable );
							}
							if ( variables != null )
							{
								e.addUserDatum( VARIABLE_KEY, variables, UserData.SHARED );
							}
						}

						e.addUserDatum( VISITED_KEY, new Integer( 0 ), UserData.SHARED );
						_logger.debug( "  Added edge: '" + e.getUserDatum( LABEL_KEY ) + "', with id: " + e.getUserDatum( INDEX_KEY ) );
					}
				}
		}
		catch ( JDOMException e )
		{
			_logger.error( e );
			throw new RuntimeException( "Could not parse file: '" + fileName + "'" );
		}
		catch ( IOException e )
		{
			e.printStackTrace();
			throw new RuntimeException( "Could not parse file: '" + fileName + "'" );
		}

		removeBlockedEntities( graph );

		return graph;
	}


	private void removeBlockedEntities( SparseGraph graph )
	{
		Object[] vertices = graph.getVertices().toArray();
		for ( int i = 0; i < vertices.length; i++ )
		{
			DirectedSparseVertex v = (DirectedSparseVertex)vertices[ i ];
			if ( v.containsUserDatumKey(BLOCKED) )
			{
				_logger.debug( "Removing this vertex because it is BLOCKED: '" + v.getUserDatum( LABEL_KEY ) + "'" );
				graph.removeVertex( v );
			}
		}
		Object[] edges = graph.getEdges().toArray();
		for ( int i = 0; i < edges.length; i++ )
		{
			DirectedSparseEdge e = (DirectedSparseEdge)edges[ i ];
			if ( e.containsUserDatumKey(BLOCKED) )
			{
				_logger.debug( "Removing this edge because it is BLOCKED: '" + e.getUserDatum( LABEL_KEY ) + "'" );
				graph.removeEdge( e );
			}
		}
	}

	
	// Merge all file graphs into one graph.
	private void mergeAllGraphs()
	{
		findMotherAndSubgraphs();
		checkForDuplicateVerticesInSubgraphs();
		mergeSubgraphs();
		mergeVerticesMarked_MERGE();
		
		if ( is_cul_de_sac() )
		{
			searchForCulDeSacs();
		}
		
		checkForVerticesWithZeroInEdges();

		_logger.info( "Done merging" );		
	}
	
	
	private void mergeSubgraphs()
	{
		for ( int i = 0; i < _graphList.size(); i++ )
		{
			SparseGraph g = (SparseGraph)_graphList.elementAt( i );

			if ( _graph.hashCode() == g.hashCode() )
			{
				continue;
			}
			_logger.debug( "Analysing graph in file: " + g.getUserDatum( FILE_KEY ) );

			Object[] vertices = _graph.getVertices().toArray();
			for ( int j = 0; j < vertices.length; j++ )
			{
				DirectedSparseVertex v1 = (DirectedSparseVertex)vertices[ j ];
				_logger.debug( "Investigating vertex(" + v1.getUserDatum( INDEX_KEY ) + "): '" + v1.getUserDatum( LABEL_KEY ) + "'" );

				if ( v1.getUserDatum( LABEL_KEY ).equals( g.getUserDatum( LABEL_KEY ) ) )
				{
					if ( v1.containsUserDatumKey( MERGE ) )
					{
						_logger.debug( "The vertex is marked MERGE, and will not be replaced by a subgraph.");
						continue;
					}
					if ( v1.containsUserDatumKey( NO_MERGE ) )
					{
						_logger.debug( "The vertex is marked NO_MERGE, and will not be replaced by a subgraph.");
						continue;
					}
					if ( v1.containsUserDatumKey( MERGED_BY_MBT ) )
					{
						_logger.debug( "The vertex is marked MERGED_BY_MBT, and will not be replaced by a subgraph.");
						continue;
					}

					_logger.debug( "A subgraph'ed vertex: '" + v1.getUserDatum( LABEL_KEY ) +
							       "' in graph: " + g.getUserDatum( FILE_KEY )  +
							       ", equals a node in the graph in file: '" +
							       _graph.getUserDatum( FILE_KEY ) + "'" );

					appendGraph( _graph, g );
					copySubGraphs( _graph, g, v1 );

					vertices = _graph.getVertices().toArray();
					i = -1;
					j = -1;
				}
			}
		}
	}
	
	
	private void searchForCulDeSacs()
	{
		Object[] list = _graph.getVertices().toArray();
		for ( int i = 0; i < list.length; i++ )
		{
			DirectedSparseVertex v = (DirectedSparseVertex)list[ i ];
			if ( v.getOutEdges().size() <= 0 )
			{
				throw new RuntimeException( "Found a cul-de-sac. Vertex has no out-edges: '" + (String)v.getUserDatum( LABEL_KEY ) + "', in file: '" + v.getUserDatum( FILE_KEY ) + "'" );
			}
		}
	}

	
	// Merge all vertices marked MERGE
	private void mergeVerticesMarked_MERGE()
	{
		Object[] list1 = _graph.getVertices().toArray();
		for ( int i = 0; i < list1.length; i++ )
		{
			DirectedSparseVertex v1 = (DirectedSparseVertex)list1[ i ];

			if ( v1.containsUserDatumKey( MERGE ) == false )
			{
				continue;
			}

			Object[] list2 = _graph.getVertices().toArray();
			Vector mergedVertices = new Vector();
			for ( int j = 0; j < list2.length; j++ )
			{
				DirectedSparseVertex v2 = (DirectedSparseVertex)list2[ j ];

				if ( v1.getUserDatum( LABEL_KEY ).equals( v2.getUserDatum( LABEL_KEY ) ) == false )
				{
					continue;
				}
				if ( v2.containsUserDatumKey( NO_MERGE ) )
				{
					continue;
				}
				if ( v1.getUserDatum( INDEX_KEY ) == v2.getUserDatum( INDEX_KEY ) )
				{
					continue;
				}
				if ( mergedVertices.contains( v1 ) )
				{
					continue;
				}

				_logger.debug( "Merging vertex(" + v1.getUserDatum( INDEX_KEY ) + "): '" + v1.getUserDatum( LABEL_KEY ) +
						       "' with vertex (" + v2.getUserDatum( INDEX_KEY ) + ")" );

				Object[] inEdges = v1.getInEdges().toArray();
				for (int x = 0; x < inEdges.length; x++)
				{
					DirectedSparseEdge edge = (DirectedSparseEdge)inEdges[ x ];
					DirectedSparseEdge new_edge = (DirectedSparseEdge)_graph.addEdge( new DirectedSparseEdge( edge.getSource(), v2 ) );
					new_edge.importUserData( edge );
					new_edge.setUserDatum( INDEX_KEY, new Integer( getNewVertexAndEdgeIndex() ), UserData.SHARED );
				}
				Object[] outEdges = v1.getOutEdges().toArray();
				for (int x = 0; x < outEdges.length; x++)
				{
					DirectedSparseEdge edge = (DirectedSparseEdge)outEdges[ x ];
					DirectedSparseEdge new_edge = (DirectedSparseEdge)_graph.addEdge( new DirectedSparseEdge( v2, edge.getDest() ) );
					new_edge.importUserData( edge );
					new_edge.setUserDatum( INDEX_KEY, new Integer( getNewVertexAndEdgeIndex() ), UserData.SHARED );
				}
				mergedVertices.add( v1 );
			}

			if ( mergedVertices.isEmpty() == false )
			{
				_logger.debug( "Remvoing merged vertex(" + v1.getUserDatum( INDEX_KEY ) + ")" );
				_graph.removeVertex( v1 );
			}
		}		
	}
	
	// Look for duplicated vertices in each sub-graph. If a vertex is found, which represents
	// the name of the sub-graph (the vertex which the Start vertex points to) is duplicated
	// in the same sub-graph, this will lead to an infinit recursive loop.
	private void checkForDuplicateVerticesInSubgraphs()
	{
		for ( int i = 0; i < _graphList.size(); i++ )
		{
			SparseGraph g = (SparseGraph)_graphList.elementAt( i );

			// Exclude the mother graph
			if ( _graph.hashCode() == g.hashCode() )
			{
				continue;
			}

			_logger.debug( "Looking for infinit recursive loop in file: " + g.getUserDatum( FILE_KEY ) );

			String subgraph_label = (String)g.getUserDatum( LABEL_KEY );
			Object[] vertices = g.getVertices().toArray();
			for ( int j = 0; j < vertices.length; j++ )
			{
				DirectedSparseVertex v = (DirectedSparseVertex)vertices[ j ];
				String label = (String)v.getUserDatum( LABEL_KEY );
				if ( label.equals( subgraph_label ) )
				{
					if ( v.containsUserDatumKey( SUBGRAPH_START_VERTEX ) )
					{
						continue;
					}
					if ( v.containsUserDatumKey( NO_MERGE ) )
					{
						continue;
					}
					
					_logger.error( "Vertex: " + label + ", with id: " + v.getUserDatum( INDEX_KEY ) + ", is a duplicate in a subgraph" );
					throw new RuntimeException( "Found a subgraph containing a duplicate vertex with name: '" + 
		                    					v.getUserDatum( LABEL_KEY ) + 
		                    					"', in file: '" + 
		                    					g.getUserDatum( FILE_KEY ) + "'" );
					
				}
			}
			_logger.debug( "Nope! Did not find any infinit recursive loops." );
		}
	}
	
	// Search for the mother graph, and all subgraphs
	private void findMotherAndSubgraphs()
	{	
		boolean foundMotherStartGraph = false;
		boolean foundSubStartGraph = false;
		_graph = null;
		
		for ( Iterator iter = _graphList.iterator(); iter.hasNext(); )
		{
			SparseGraph g = (SparseGraph) iter.next();
			foundSubStartGraph = false;
	
			_logger.debug( "Analyzing graph: " + g.getUserDatum( FILE_KEY ) );
	
			Object[] vertices = g.getVertices().toArray();
			for ( int i = 0; i < vertices.length; i++ )
			{
				DirectedSparseVertex v = (DirectedSparseVertex)vertices[ i ];
	
				// Find all vertices that are start nodes (START_NODE)
				if ( v.getUserDatum( LABEL_KEY ).equals( START_NODE ) )
				{
					Object[] edges = v.getOutEdges().toArray();
					if ( edges.length != 1 )
					{
						throw new RuntimeException( "A Start vertex can only have one out edge, look in file: " + g.getUserDatum( FILE_KEY ) );
					}
					DirectedSparseEdge edge = (DirectedSparseEdge)edges[ 0 ];
					if ( edge.containsUserDatumKey( LABEL_KEY ) )
					{
						if ( foundMotherStartGraph )
						{
							if ( _graph.getUserDatum( FILE_KEY ).equals( g.getUserDatum( FILE_KEY ) ) )
							{
								throw new RuntimeException( "Only one Start vertex can exist in one file, see file '" +
					                    _graph.getUserDatum( FILE_KEY )+ "'" );
							}
							else
							{
								throw new RuntimeException( "Only one Start vertex can exist in one file, see files " +
					                    _graph.getUserDatum( FILE_KEY )+ ", and " + g.getUserDatum( FILE_KEY ) );								
							}
						}
						if ( foundSubStartGraph == true )
						{
							throw new RuntimeException( "Only one Start vertex can exist in one file, see file '" +
				                    g.getUserDatum( FILE_KEY )+ "'" );		
						}
	
						foundMotherStartGraph = true;
						_graph = g;
						edge.getDest().addUserDatum( MOTHER_GRAPH_START_VERTEX, MOTHER_GRAPH_START_VERTEX, UserData.SHARED );
						_logger.debug( "Found the mother graph in the file: " +  _graph.getUserDatum( FILE_KEY ) );
					}
					else
					{
						if ( foundSubStartGraph == true )
						{
							throw new RuntimeException( "Only one Start vertex can exist in one file, see file '" +
				                    g.getUserDatum( FILE_KEY )+ "'" );		
						}
						
						// Verify that current subgraph is not already defined
						for ( Iterator iter_g = _graphList.iterator(); iter_g.hasNext(); )
						{
							if ( iter.hashCode() == iter_g.hashCode() )
							{
								continue;
							}
							
							SparseGraph tmp_graph = (SparseGraph) iter_g.next();
							if ( tmp_graph.containsUserDatumKey( LABEL_KEY ) )
							{
								String name = (String) tmp_graph.getUserDatum( LABEL_KEY );
								if ( name.equals( (String)edge.getDest().getUserDatum( LABEL_KEY ) ) )
								{
									throw new RuntimeException( "Found 2 subgraphs using the same name: '" + 
											                    edge.getDest().getUserDatum( LABEL_KEY ) + 
											                    "', they are defined in files: '" + 
											                    g.getUserDatum( FILE_KEY ) + "', and :'"+
											                    tmp_graph.getUserDatum( FILE_KEY ) + "'" );
								}
							}
						}
						
						if ( foundMotherStartGraph == true )
						{
							if ( _graph.getUserDatum( FILE_KEY ).equals( g.getUserDatum( FILE_KEY ) ) )
							{
								throw new RuntimeException( "Only one Start vertex can exist in one file, see file '" +
					                    _graph.getUserDatum( FILE_KEY )+ "'" );
							}
						}
	
						// Since the edge does not contain a label, this is a subgraph
						// Mark the destination node of the edge to a subgraph starting node
						foundSubStartGraph = true;
						edge.getDest().addUserDatum( SUBGRAPH_START_VERTEX, SUBGRAPH_START_VERTEX, UserData.SHARED );
						g.addUserDatum( LABEL_KEY, edge.getDest().getUserDatum( LABEL_KEY ), UserData.SHARED );
						_logger.debug( "Found sub-graph: '" + g.getUserDatum( LABEL_KEY ) + "', in file '" + g.getUserDatum( FILE_KEY ) + "'" );
						_logger.debug( "Added SUBGRAPH_START_VERTEX to vertex: " + edge.getDest().getUserDatum( INDEX_KEY ) );
					}
				}
			}
		}
		
		if ( _graph == null )
		{
			throw new RuntimeException( "Did not find a Start vertex with an out edge with a label." );		
		}
	}
	
	

	// Copies the graph src, into the graph dst
	private void appendGraph( SparseGraph dst, SparseGraph src )
	{
		HashMap map = new HashMap();
		Object[] vertices = src.getVertices().toArray();
		for ( int i = 0; i < vertices.length; i++ )
		{
			DirectedSparseVertex v = (DirectedSparseVertex)vertices[ i ];
			if ( v.getUserDatum( LABEL_KEY ).equals( START_NODE ) )
			{
				continue;
			}
			DirectedSparseVertex new_v = new DirectedSparseVertex();
			new_v.importUserData( v );
			new_v.setUserDatum( INDEX_KEY, new Integer( getNewVertexAndEdgeIndex() ), UserData.SHARED );
			dst.addVertex( new_v );
			_logger.debug("Associated vertex: " + v + " to new vertex: " + new_v );
			map.put( (Integer)v.getUserDatum( INDEX_KEY ), new_v );
		}
		Object[] edges = src.getEdges().toArray();
		for ( int i = 0; i < edges.length; i++ )
		{
			DirectedSparseEdge e = (DirectedSparseEdge)edges[ i ];
			DirectedSparseVertex v1 = (DirectedSparseVertex)map.get( (Integer)e.getSource().getUserDatum( INDEX_KEY ) );
			DirectedSparseVertex v2 = (DirectedSparseVertex)map.get( (Integer)e.getDest().getUserDatum( INDEX_KEY ) );
			if ( v1 == null || v2 == null )
			{
				continue;
			}
			DirectedSparseEdge new_e = new DirectedSparseEdge( v1, v2 );
			new_e.importUserData( e );
			new_e.setUserDatum( INDEX_KEY, new Integer( getNewVertexAndEdgeIndex() ), UserData.SHARED );
			dst.addEdge( new_e );
		}
	}


	// Replaces the vertex targetVertex and all its in and out edges, in the graph g, with all
	// other vertices with the same name.
	private void copySubGraphs( SparseGraph mainGraph, SparseGraph subGraph, DirectedSparseVertex targetVertex )
	{
		// Save the target vertex out-edge list
		Vector targetVertexOutEdgeList = new Vector();
		_logger.debug( "Target vertex (" + getCompleteVertexName( targetVertex ) + ") out-edge list" );
		for (Iterator iter = targetVertex.getOutEdges().iterator(); iter.hasNext();)
		{
			DirectedSparseEdge element = (DirectedSparseEdge) iter.next();
			_logger.debug( "  " + getCompleteEdgeName( element ) );
			targetVertexOutEdgeList.add( element );
		}

		DirectedSparseVertex sourceVertex = null;
		Object[] vertices = mainGraph.getVertices().toArray();
		for ( int i = 0; i < vertices.length; i++ )
		{
			DirectedSparseVertex v = (DirectedSparseVertex)vertices[ i ];
			if ( v.getUserDatum( LABEL_KEY ).equals( targetVertex.getUserDatum( LABEL_KEY ) ) )
			{
				if ( v.containsUserDatumKey( SUBGRAPH_START_VERTEX ) == false )
				{
					continue;
				}
				if ( v.containsUserDatumKey( MERGE ) )
				{
					continue;
				}
				if ( v.containsUserDatumKey( NO_MERGE ) )
				{
					continue;
				}
				if ( v.containsUserDatumKey( MERGED_BY_MBT ) )
				{
					continue;
				}
				if ( v.getUserDatum( INDEX_KEY ) == targetVertex.getUserDatum( INDEX_KEY ) )
				{
					continue;
				}

				sourceVertex = v;
				break;
			}
		}

		if ( sourceVertex == null )
		{
			return;
		}

		_logger.debug( "Start merging target vertex: " + getCompleteVertexName( targetVertex ) + " with source vertex: " + getCompleteVertexName( sourceVertex ) );

		Object[] inEdges = sourceVertex.getInEdges().toArray();
		for (int i = 0; i < inEdges.length; i++)
		{
			DirectedSparseEdge edge = (DirectedSparseEdge)inEdges[ i ];
			DirectedSparseEdge new_edge = (DirectedSparseEdge)mainGraph.addEdge( new DirectedSparseEdge( edge.getSource(), targetVertex ) );
			new_edge.importUserData( edge );
			new_edge.setUserDatum( INDEX_KEY, new Integer( getNewVertexAndEdgeIndex() ), UserData.SHARED );
		}
		Object[] outEdges = sourceVertex.getOutEdges().toArray();
		for (int i = 0; i < outEdges.length; i++)
		{
			DirectedSparseEdge edge = (DirectedSparseEdge)outEdges[ i ];
			DirectedSparseEdge new_edge = (DirectedSparseEdge)mainGraph.addEdge( new DirectedSparseEdge( targetVertex, edge.getDest() ) );
			new_edge.importUserData( edge );
			new_edge.setUserDatum( INDEX_KEY, new Integer( getNewVertexAndEdgeIndex() ), UserData.SHARED );
		}
		_logger.debug( "Remvoing source vertex: " + getCompleteVertexName( sourceVertex) );
		mainGraph.removeVertex( sourceVertex );
		targetVertex.addUserDatum( MERGED_BY_MBT, MERGED_BY_MBT, UserData.SHARED );

		
		// Check if there exists a Stop vertex.
		// Also check if there is only one.
		DirectedSparseVertex stopVertex = null;
		vertices = mainGraph.getVertices().toArray();
		for ( int i = 0; i < vertices.length; i++ )
		{
			DirectedSparseVertex v = (DirectedSparseVertex)vertices[ i ];
			if ( v.getUserDatum( LABEL_KEY ).equals( STOP_NODE ) )
			{
				if ( stopVertex != null )
				{
					throw new RuntimeException( "Found more than 1 Stop vertex in file (Only one Stop vertex per file is allowed): '" + 
							mainGraph.getUserDatum( FILE_KEY ) + "'" );					
				}
				stopVertex = v;
			}
		}
		
		
		// All edges going to the Stop vertex, needs to be merged to the destination vertex.
		// The destination vertex, is pointed to by the vertex which is expanded by the sub graph.
		if ( stopVertex != null )
		{
			Vector edgesToBeRemoved = new Vector();
			inEdges = stopVertex.getInEdges().toArray();
			
			_logger.debug( "Stop vertex in-edge list" );
			for (Iterator iter = stopVertex.getInEdges().iterator(); iter.hasNext();)
			{
				DirectedSparseEdge element = (DirectedSparseEdge) iter.next();
				_logger.debug( "  " + getCompleteEdgeName( element ) );
			}
			_logger.debug( "Target vertex (" + getCompleteVertexName( targetVertex ) + ") out-edge list" );
			for (Iterator iter = targetVertexOutEdgeList.iterator(); iter.hasNext();)
			{
				DirectedSparseEdge element = (DirectedSparseEdge) iter.next();
				_logger.debug( "  " + getCompleteEdgeName( element ) );
			}

			Vector mergeList = MergeList( targetVertexOutEdgeList.toArray(), inEdges );
			Object[] mergeListArray = mergeList.toArray();
			
			for ( int i = 0; i < mergeListArray.length; i++ )
			{
				Pair pair = (Pair)mergeListArray[ i ];
				MergeOutEdgeAndInEdge( (DirectedSparseEdge)pair.getFirst(), (DirectedSparseEdge)pair.getSecond(), edgesToBeRemoved, mainGraph );
			}									

			
			// Now remove the edges that has been copied.
			for (Iterator iter = edgesToBeRemoved.iterator(); iter.hasNext();)
			{
				DirectedSparseEdge element = (DirectedSparseEdge) iter.next();
				try {
					mainGraph.removeEdge( element );
					_logger.debug( "Removing edge: " + getCompleteEdgeName( element ) );
					_logger.debug( getCompleteEdgeName( element ) + ", was found and removed from graph,: '" + mainGraph.getUserDatum( FILE_KEY ) + "'");
				} catch (java.lang.IllegalArgumentException e) {
					_logger.debug( getCompleteEdgeName( element ) + ", was not found in graph: '" + mainGraph.getUserDatum( FILE_KEY ) + "', this is ok, since it probably been removed before. (I know, not ver good progamming practice here)");
				}
			}
			_logger.debug( "Removing the Stop vertex: " + stopVertex.getUserDatum( INDEX_KEY )  );
			mainGraph.removeVertex( stopVertex );
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

			Integer vistited = (Integer)edge.getUserDatum( VISITED_KEY );
			if ( vistited.intValue() == 0 )
			{
				stat += "Not tested (Edge): '" + (String)edge.getUserDatum( LABEL_KEY ) + "', from: '" + (String)edge.getSource().getUserDatum( LABEL_KEY ) + "', to: '" + (String)edge.getDest().getUserDatum( LABEL_KEY ) + "'" + new_line;
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

			Integer vistited = (Integer)vertex.getUserDatum( VISITED_KEY );
			if ( vistited.intValue() == 0 )
			{
				stat += "Not tested (Vertex): '" + (String)vertex.getUserDatum( LABEL_KEY ) + "'" +new_line;
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
		if ( random == true )
		{
			_runUntilAllEdgesVisited = false;
			long calculatedLength = length /2;
			for ( long index = 0; index < calculatedLength; index++  )
			{
				executeMethod( false, true, false );
			}
		}
		else
		{
			_runUntilAllEdgesVisited = true;
			while ( true )
			{
				executeMethod( true, true, false );
				if ( isAllEdgesVisited() )
				{
					break;
				}
			}
		}
		
		return;
	}
	
	public void generateJavaCode( String fileName )
	{
		_vertices = _graph.getVertices().toArray();
		_edges    = _graph.getEdges().toArray();

		ArrayList writtenVertices = new ArrayList();
		ArrayList writtenEdges    = new ArrayList();

		StringBuffer sourceFile = new StringBuffer();

		/**
		 * Read the original file first. If the methods already are defined in the file,
		 * leave those methods alone.
		 */
		BufferedReader input = null;
		try
		{
			_logger.debug( "Try to open file: " + fileName );
			input = new BufferedReader( new FileReader( fileName ) );
			String line = null;
			while ( ( line = input.readLine() ) != null )
			{
				sourceFile.append( line );
				sourceFile.append( System.getProperty( "line.separator" ) );
			}
		}
		catch ( FileNotFoundException e )
		{
			_logger.error( "File not found exception: " + e.getMessage() );
		}
		catch ( IOException e )
		{
			_logger.error( "IO exception: " + e.getMessage() );
		}
		finally
		{
			try
			{
				if ( input != null )
				{
					input.close();
				}
			}
			catch ( IOException e )
			{
				_logger.error( "IO exception: " + e.getMessage() );
			}
		}

		_logger.debug( sourceFile.toString() );


		for ( int i = 0; i < _vertices.length; i++ )
		{
			DirectedSparseVertex vertex = (DirectedSparseVertex)_vertices[ i ];

			// Do not write the Start vertex to file, it's only used as
			// an entry point.
			if ( vertex.getUserDatum( LABEL_KEY ).equals( START_NODE ) )
			{
				continue;
			}

			boolean duplicated = false;
			for ( Iterator iter = writtenVertices.iterator(); iter.hasNext(); )
			{
				String str = (String) iter.next();
				if ( str.equals( (String)vertex.getUserDatum( LABEL_KEY ) ) == true )
				{
					duplicated = true;
					break;
				}
			}

			if ( duplicated == false )
			{
				Pattern p = Pattern.compile( "public void " + (String)vertex.getUserDatum( LABEL_KEY ) + "\\(\\)(.|[\\n\\r])*?\\{(.|[\\n\\r])*?\\}", Pattern.MULTILINE );
				Matcher m = p.matcher( sourceFile );

				if ( m.find() == false )
				{
					sourceFile.append( "/**\n" );
					sourceFile.append( " * This method implements the verification of the vertex: '" + (String)vertex.getUserDatum( LABEL_KEY ) + "'\n" );
					sourceFile.append( " */\n" );
					sourceFile.append( "public void " + (String)vertex.getUserDatum( LABEL_KEY ) + "()\n" );
					sourceFile.append( "{\n" );
					sourceFile.append( "	_logger.info( \"Vertex: " + (String)vertex.getUserDatum( LABEL_KEY ) + "\" );\n" );
					sourceFile.append( "	throw new RuntimeException( \"Not implemented. This line can be removed.\" );\n" );
					sourceFile.append( "}\n\n" );
				}
			}

			writtenVertices.add( (String)vertex.getUserDatum( LABEL_KEY ) );
		}

		for ( int i = 0; i < _edges.length; i++ )
		{
			DirectedSparseEdge edge = (DirectedSparseEdge)_edges[ i ];
			if ( edge.getUserDatum( LABEL_KEY ) == null )
			{
				continue;
			}

			boolean duplicated = false;
			for ( Iterator iter = writtenEdges.iterator(); iter.hasNext(); )
			{
				String str = (String) iter.next();
				if ( str.equals( edge.getUserDatum( LABEL_KEY ) ) == true )
				{
					duplicated = true;
					break;
				}
			}

			if ( duplicated == false )
			{
				Pattern p = Pattern.compile( "public void " + (String)edge.getUserDatum( LABEL_KEY ) + "\\(\\)(.|[\\n\\r])*?\\{(.|[\\n\\r])*?\\}", Pattern.MULTILINE );
				Matcher m = p.matcher( sourceFile );

				if ( m.find() == false )
				{
					sourceFile.append( "/**\n" );
					sourceFile.append( " * This method implemets the edge: '" + (String)edge.getUserDatum( LABEL_KEY ) + "'\n" );
					sourceFile.append( " */\n" );
					sourceFile.append( "public void " + (String)edge.getUserDatum( LABEL_KEY ) + "()\n" );
					sourceFile.append( "{\n" );
					sourceFile.append( "	_logger.info( \"Edge: " + (String)edge.getUserDatum( LABEL_KEY ) + "\" );\n" );

					if ( edge.containsUserDatumKey( STATE_KEY ) )
					{
						HashMap map = (HashMap)edge.getUserDatum( STATE_KEY );
						Set variables = map.keySet();
						for ( Iterator iter = variables.iterator(); iter.hasNext();)
						{
							String  variable = (String) iter.next();
							Boolean value	 = (Boolean)map.get( variable );
							sourceFile.append( "	boolean " + variable + " = " + value + ";\n" );
						}
					}

					if ( edge.containsUserDatumKey( CONDITION_KEY ) )
					{
						HashMap map = (HashMap)edge.getUserDatum( CONDITION_KEY );
						Set variables = map.keySet();
						for ( Iterator iter = variables.iterator(); iter.hasNext();)
						{
							String  variable = (String) iter.next();
							Boolean value	 = (Boolean)map.get( variable );
							sourceFile.append( "	if ( " + variable + " != " + value + " )\n" );
							sourceFile.append( "	{\n" );
							sourceFile.append( "	  _logger.info( \"Not a valid path until condition is fullfilled\" );\n" );
							sourceFile.append( "	  throw new GoBackToPreviousVertexException();\n" );
							sourceFile.append( "	}\n" );
						}
					}

					if ( edge.containsUserDatumKey( VARIABLE_KEY ) )
					{
						HashMap map = (HashMap)edge.getUserDatum( VARIABLE_KEY );
						Set variables = map.keySet();
						for ( Iterator iter = variables.iterator(); iter.hasNext();)
						{
							String variable = (String) iter.next();
							Object object = map.get( variable );
							if ( object == null )
							{
								throw new RuntimeException( "An object in the hash map was null!" );
							}
							if ( object instanceof String )
							{
								String value  = (String)map.get( variable );
								sourceFile.append( "	String " + variable + " = \"" + value + "\";\n" );
							}
							if ( object instanceof Integer )
							{
								Integer value = (Integer)map.get( variable );
								sourceFile.append( "	String " + variable + " = " + value + ";\n" );
							}
							if ( object instanceof Float )
							{
								Float value	= (Float)map.get( variable );
								sourceFile.append( "	String " + variable + " = " + value + ";\n" );
							}
						}
					}

					sourceFile.append( "	throw new RuntimeException( \"Not implemented\" );\n" );
					sourceFile.append( "}\n\n" );
				}
			}

			writtenEdges.add( (String)edge.getUserDatum( LABEL_KEY ) );
		}

		try
		{
			FileWriter file = new FileWriter( fileName );
			file.write( sourceFile.toString() );
			file.flush();
		}
		catch ( IOException e )
		{
			_logger.error( e.getMessage() );
		}
	}

	public void generatePerlCode( String fileName )
	{
		_vertices = _graph.getVertices().toArray();
		_edges    = _graph.getEdges().toArray();

		ArrayList writtenVertices = new ArrayList();
		ArrayList writtenEdges    = new ArrayList();

		StringBuffer sourceFile = new StringBuffer();

		/**
		 * Read the original file first. If the methods already are defined in the file,
		 * leave those methods alone.
		 */
		BufferedReader input = null;
		try
		{
			_logger.debug( "Try to open file: " + fileName );
			input = new BufferedReader( new FileReader( fileName ) );
			String line = null;
			while ( ( line = input.readLine() ) != null )
			{
				sourceFile.append( line );
				sourceFile.append( System.getProperty( "line.separator" ) );
			}
		}
		catch ( FileNotFoundException e )
		{
			_logger.error( "File not found exception: " + e.getMessage() );
		}
		catch ( IOException e )
		{
			_logger.error( "IO exception: " + e.getMessage() );
		}
		finally
		{
			try
			{
				if ( input != null )
				{
					input.close();
				}
			}
			catch ( IOException e )
			{
				_logger.error( "IO exception: " + e.getMessage() );
			}
		}

		_logger.debug( sourceFile.toString() );


		for ( int i = 0; i < _vertices.length; i++ )
		{
			DirectedSparseVertex vertex = (DirectedSparseVertex)_vertices[ i ];

			// Do not write the Start vertex to file, it's only used as
			// an entry point.
			if ( vertex.getUserDatum( LABEL_KEY ).equals( START_NODE ) )
			{
				continue;
			}

			boolean duplicated = false;
			for ( Iterator iter = writtenVertices.iterator(); iter.hasNext(); )
			{
				String str = (String) iter.next();
				if ( str.equals( (String)vertex.getUserDatum( LABEL_KEY ) ) == true )
				{
					duplicated = true;
					break;
				}
			}

			if ( duplicated == false )
			{
				Pattern p = Pattern.compile( "sub " + (String)vertex.getUserDatum( LABEL_KEY ) + "\\(\\)(.|[\\n\\r])*?\\{(.|[\\n\\r])*?\\}", Pattern.MULTILINE );
				Matcher m = p.matcher( sourceFile );

				if ( m.find() == false )
				{
					sourceFile.append( "#\n" );
					sourceFile.append( "# This method implements the verification of the vertex: '" + (String)vertex.getUserDatum( LABEL_KEY ) + "'\n" );
					sourceFile.append( "#\n" );
					sourceFile.append( "sub " + (String)vertex.getUserDatum( LABEL_KEY ) + "()\n" );
					sourceFile.append( "{\n" );
					sourceFile.append( "	print \"Vertex: " + (String)vertex.getUserDatum( LABEL_KEY ) + "\\n\";\n" );
					sourceFile.append( "	print \"Not implemented.\\n\";\n" );
					sourceFile.append( "	exit 1;\n" );
					sourceFile.append( "}\n\n" );
				}
			}

			writtenVertices.add( (String)vertex.getUserDatum( LABEL_KEY ) );
		}

		for ( int i = 0; i < _edges.length; i++ )
		{
			DirectedSparseEdge edge = (DirectedSparseEdge)_edges[ i ];

			boolean duplicated = false;
			for ( Iterator iter = writtenEdges.iterator(); iter.hasNext(); )
			{
				String str = (String) iter.next();
				if ( str.equals( (String)edge.getUserDatum( LABEL_KEY ) ) == true )
				{
					duplicated = true;
					break;
				}
			}

			if ( duplicated == false )
			{
				Pattern p = Pattern.compile( "sub " + (String)edge.getUserDatum( LABEL_KEY ) + "\\(\\)(.|[\\n\\r])*?\\{(.|[\\n\\r])*?\\}", Pattern.MULTILINE );
				Matcher m = p.matcher( sourceFile );

				if ( m.find() == false )
				{
					sourceFile.append( "#\n" );
					sourceFile.append( "# This method implemets the edge: '" + (String)edge.getUserDatum( LABEL_KEY ) + "'\n" );
					sourceFile.append( "#\n" );
					sourceFile.append( "sub " + (String)edge.getUserDatum( LABEL_KEY ) + "()\n" );
					sourceFile.append( "{\n" );
					sourceFile.append( "	print \"Edge: " + (String)edge.getUserDatum( LABEL_KEY ) + "\\n\";\n" );
					sourceFile.append( "	print \"Not implemented.\\n\";\n" );
					sourceFile.append( "	exit 1;\n" );
					sourceFile.append( "}\n\n" );
				}
			}

			writtenEdges.add( (String)edge.getUserDatum( LABEL_KEY ) );
		}

		try
		{
			FileWriter file = new FileWriter( fileName );
			file.write( sourceFile.toString() );
			file.flush();
		}
		catch ( IOException e )
		{
			_logger.error( e.getMessage() );
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
			if ( vertex.getUserDatum( LABEL_KEY ).equals( START_NODE ) )
			{
				_nextVertex = vertex;
				vertex.setUserDatum( VISITED_KEY, new Integer( 1 ), UserData.SHARED );
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

		if ( _nextVertex.containsUserDatumKey( MOTHER_GRAPH_START_VERTEX ) && dryRun == false )
		{
			_pathHistory.clear();
		}

		_logger.debug( "Vertex = '" + (String)_nextVertex.getUserDatum( LABEL_KEY ) + "'" );

		outEdges = _nextVertex.getOutEdges().toArray();
		_logger.debug( "Number of outgoing edges = " + outEdges.length );

		if ( outEdges.length == 0 )
		{
			_logger.error( "Vertex has no out-edges: '" + (String)_nextVertex.getUserDatum( LABEL_KEY ) + "'" );
			throw new RuntimeException( "Found a cul-de-sac: '" + (String)_nextVertex.getUserDatum( LABEL_KEY ) + "' I have to stop now..." );
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
					_logger.info( "Strategy: Random from vertex - Get a random out edge from the currrent vertex: '" + _prevVertex.getUserDatum( LABEL_KEY ) + "'" );
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
				_logger.info( "Selecting edge: " + getCompleteEdgeName( e ) );
				_shortestPathToVertex = new DijkstraShortestPath( _graph ).getPath( _nextVertex, e.getSource() );
	
				// DijkstraShortestPath.getPath returns 0 if there is no way to reach the destination. But,
				// DijkstraShortestPath.getPath also returns 0 paths if the the source and destination vertex are the same, even if there is
				// an edge there (self-loop). So we have to check for that.
				if ( _shortestPathToVertex.size() == 0 )
				{
					if ( _nextVertex.getUserDatum( INDEX_KEY ) != e.getSource().getUserDatum( INDEX_KEY ) )
					{
						String msg = "There is no way to reach: " + getCompleteEdgeName( e ) + ", from: '" + _nextVertex.getUserDatum( LABEL_KEY ) + "' " + _nextVertex.getUserDatum( INDEX_KEY );
						_logger.warn( msg );
					}
				}
			} while ( _shortestPathToVertex.size() == 0 && ( _nextVertex.getUserDatum( INDEX_KEY ) != e.getSource().getUserDatum( INDEX_KEY ) ) ); 

			_latestNumberOfUnvisitedEdges = unvisitedEdges.size(); 
			_shortestPathToVertex.add( e );
			_logger.info( "Intend to take the shortest (" + _shortestPathToVertex.size() + " hops) path between: '" + _nextVertex.getUserDatum( LABEL_KEY ) + "' and '" + (String)e.getDest().getUserDatum( LABEL_KEY ) + "' (from: '" + e.getSource().getUserDatum( LABEL_KEY ) + "')" );

			String paths = "The route is: ";
			for (Iterator iter = _shortestPathToVertex.iterator(); iter.hasNext();)
			{
				DirectedSparseEdge item = (DirectedSparseEdge) iter.next();
				paths += getCompleteEdgeName( item ) + " ==> ";
			}
			paths += " Done!";
			_logger.info( paths );
		}

		if ( _shortestPathToVertex != null && _shortestPathToVertex.size() > 0 )
		{
			edge = (DirectedSparseEdge)_shortestPathToVertex.get( 0 );
			_shortestPathToVertex.remove( 0 );
			_logger.debug( "Removed edge: " + getCompleteEdgeName( edge ) + " from the shortest path list, " + _shortestPathToVertex.size() + " hops remaining." );

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

				Integer vistited = (Integer)edge.getUserDatum( VISITED_KEY );
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
						_logger.debug( "Found an edge which has not been visited yet: " + getCompleteEdgeName( edge ) );
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
		_logger.debug( "Edge = '" + getCompleteEdgeName( edge ) + "'" );

		_prevVertex = _nextVertex;
		_nextVertex = (DirectedSparseVertex)edge.getDest();
		
		try
		{
			String label = (String)edge.getUserDatum( LABEL_KEY );
			_logger.debug( "Invoke method for edge: '" + label + "' and index: " + edge.getUserDatum( INDEX_KEY ) );
			invokeMethod( label, dryRun, suppressPrintout );
			Integer vistited = (Integer)edge.getUserDatum( VISITED_KEY );
			vistited = new Integer( vistited.intValue() + 1 );
			edge.setUserDatum( VISITED_KEY, vistited, UserData.SHARED );

			label = (String)edge.getDest().getUserDatum( LABEL_KEY );
			_logger.debug( "Invoke method for vertex: '" + label + "' and index: " +  edge.getDest().getUserDatum( INDEX_KEY ) );
			invokeMethod( label, dryRun, suppressPrintout );
			vistited = (Integer)edge.getDest().getUserDatum( VISITED_KEY );
			vistited = new Integer( vistited.intValue() + 1 );
			edge.getDest().setUserDatum( VISITED_KEY, vistited, UserData.SHARED );
			
			_currentEdge = edge;
			_backtracking = false;
		}
		catch( GoBackToPreviousVertexException e )
		{
			_logger.info( "The edge: " + getCompleteEdgeName( edge ) + " can not be run due to unfullfilled conditions." );
			_logger.info( "Trying from vertex: '" + (String)_prevVertex.getUserDatum( LABEL_KEY ) + "' again." );
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

			if ( edge.containsUserDatumKey( WEIGHT_KEY ) )
			{
				Float weight = (Float)edge.getUserDatum( WEIGHT_KEY );
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
			throw new RuntimeException( "The sum of all weight from vertex: '" + (String)vertex.getUserDatum( LABEL_KEY ) + "' adds to more than 1" );
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
			_logger.debug( "The edge: '" + (String)((DirectedSparseEdge)edges[ i ]).getUserDatum( LABEL_KEY ) + "' is given the probability of " + probabilities[ i ] * 100 + "%"  );

			weight = weight + probabilities[ i ] * 100;
			_logger.debug( "Current weight is: " + weight  );
			if ( index < weight )
			{
				edge = (DirectedSparseEdge)edges[ i ];
				_logger.debug( "Selected edge is: " + getCompleteEdgeName( edge ) );
				break;
			}
		}

		if ( edge == null )
		{
			_logger.error( "Vertex: '" + (String)vertex.getUserDatum( LABEL_KEY ) + "', has no out edges. Test ends here!" );
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

			Integer vistited = (Integer)edge.getUserDatum( VISITED_KEY );
			if ( vistited.intValue() == 0 )
			{
				edgesNotVisited.add( edge );
				_logger.debug( "Unvisited: " +  getCompleteEdgeName( edge ) );
			}
		}

		return edgesNotVisited;
	}

	private String getCompleteEdgeName( DirectedSparseEdge edge )
	{
		String str = "'" + (String)edge.getUserDatum( LABEL_KEY ) + "', INDEX=" + edge.getUserDatum( INDEX_KEY ) + " ('" + (String)edge.getSource().getUserDatum( LABEL_KEY ) + "', INDEX=" + edge.getSource().getUserDatum( INDEX_KEY ) + " -> '" + (String)edge.getDest().getUserDatum( LABEL_KEY ) + "', INDEX=" + edge.getDest().getUserDatum( INDEX_KEY ) +  ")";
		return str;
	}

	private String getCompleteVertexName( DirectedSparseVertex vertex )
	{
		String str = "'" + (String)vertex.getUserDatum( LABEL_KEY ) + "', INDEX=" + vertex.getUserDatum( INDEX_KEY );
		return str;
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

	
	private void MergeOutEdgeAndInEdge( DirectedSparseEdge outEdge, DirectedSparseEdge inEdge, Vector edgesToBeRemoved, SparseGraph graph )
	{
		_logger.debug( "MergeOutEdgeAndInEdge" );
		
		if ( outEdge == null )
		{
			throw new RuntimeException( "Internal progamming error" );
		}
		if ( inEdge == null )
		{
			throw new RuntimeException( "Internal progamming error" );
		}

		_logger.debug( "  outEdge: " + getCompleteEdgeName( outEdge ) );
		_logger.debug( "  inEdge: " + getCompleteEdgeName( inEdge ) );

		DirectedSparseEdge new_edge = (DirectedSparseEdge)graph.addEdge( new DirectedSparseEdge( (DirectedSparseVertex)inEdge.getSource(), outEdge.getDest() ) );

		merge_user_data( new_edge, outEdge, inEdge  );
		
		new_edge.setUserDatum( INDEX_KEY, new Integer( getNewVertexAndEdgeIndex() ), UserData.SHARED );
		_logger.debug( "  Replacing the target vertex out-edge: " + getCompleteEdgeName( outEdge ) + " (old) with: " + getCompleteEdgeName( new_edge ) + "(new), using: " + getCompleteEdgeName( inEdge ) );
		
		edgesToBeRemoved.add( inEdge );
		edgesToBeRemoved.add( outEdge );
	}

	
	
	private Vector MergeList( Object[] array_A, Object[] array_B )
	{
		_logger.debug( "Vector twoLists( Object[] array_A, Object[] array_B )" );
		Vector matches = new Vector();
		_logger.debug( "  Looking for exact matches" );
		for ( int i = 0; i < array_A.length; i++ )
		{
			DirectedSparseEdge a = (DirectedSparseEdge)array_A[ i ];			
			String aLabel = (String)a.getUserDatum( LABEL_KEY );
			for ( int j = 0; j < array_B.length; j++ )
			{
				DirectedSparseEdge b = (DirectedSparseEdge)array_B[ j ];
				String bLabel = (String)b.getUserDatum( LABEL_KEY );
				if ( aLabel != null && aLabel.length() == 0 )
				{
					aLabel = null;
				}
				if ( bLabel != null && bLabel.length() == 0 )
				{
					bLabel = null;
				}
				if ( aLabel == null && bLabel == null )
				{
					_logger.debug( "    adding: " + getCompleteEdgeName( a ) + " and " + getCompleteEdgeName( b ) );
					matches.add( new Pair( a, b ) );
				}
				else if ( aLabel != null && bLabel != null )
				{
					if ( aLabel.equals( bLabel ) )
					{
						_logger.debug( "    adding: " + getCompleteEdgeName( a ) + " and " + getCompleteEdgeName( b ) );
						matches.add( new Pair( a, b ) );
					}
				}
			}		
		}
		
		_logger.debug( "  Matching nulls from the first list with non-matched items in the second list" );
		for ( int i = 0; i < array_A.length; i++ )
		{
			DirectedSparseEdge a = (DirectedSparseEdge)array_A[ i ];			
			String aLabel = (String)a.getUserDatum( LABEL_KEY );
			if ( aLabel == null || aLabel.length() == 0 )
			{
				for ( int j = 0; j < array_B.length; j++ )
				{
					DirectedSparseEdge b = (DirectedSparseEdge)array_B[ j ];
					String bLabel = (String)b.getUserDatum( LABEL_KEY );
					if ( bLabel != null )
					{
						boolean alreadyMatched = false;
						for (Iterator iter = matches.iterator(); iter.hasNext();)
						{
							Pair element = (Pair) iter.next();
							if ( b.equals( element.getSecond() ) )
							{
								alreadyMatched = true;
								break;
							}
						}
			
						if ( alreadyMatched == false )
						{
							_logger.debug( "    adding: " + getCompleteEdgeName( a ) + " and " + getCompleteEdgeName( b ) );
							matches.add( new Pair( a, b ) );
						}
					}
				}
			}
		}
		
		_logger.debug( "  Matching nulls from the second list with non-matched items in the first list" );
		for ( int i = 0; i < array_B.length; i++ )
		{
			DirectedSparseEdge b = (DirectedSparseEdge)array_B[ i ];			
			String bLabel = (String)b.getUserDatum( LABEL_KEY );
			if ( bLabel == null || bLabel.length() == 0 )
			{
				for ( int j = 0; j < array_A.length; j++ )
				{
					DirectedSparseEdge a = (DirectedSparseEdge)array_A[ j ];
					String aLabel = (String)a.getUserDatum( LABEL_KEY );
					if ( aLabel != null )
					{
						boolean alreadyMatched = false;
						for (Iterator iter = matches.iterator(); iter.hasNext();)
						{
							Pair element = (Pair) iter.next();
							if ( a.equals( element.getFirst() ) )
							{
								alreadyMatched = true;
								break;
							}
						}
			
						if ( alreadyMatched == false )
						{
							_logger.debug( "    adding: " + getCompleteEdgeName( a ) + " and " + getCompleteEdgeName( b ) );
							matches.add( new Pair( a, b ) );
						}
					}
				}
			}
		}
		return matches;
	}
	
	
	private void merge_user_data( DirectedSparseEdge dst_edge, DirectedSparseEdge src_edge_A , DirectedSparseEdge src_edge_B )
	{
		_logger.debug( "    merge_user_data" );
		for (Iterator iter = src_edge_A.getUserDatumKeyIterator(); iter.hasNext();)
		{
			String element = (String) iter.next();
			if ( dst_edge.containsUserDatumKey( element ) == false )
			{
				_logger.debug( "      addUserDatum: " + element + " = " + src_edge_A.getUserDatum( element ) );
				dst_edge.addUserDatum( element, src_edge_A.getUserDatum( element ), UserData.SHARED );
			}
		}
		for (Iterator iter = src_edge_B.getUserDatumKeyIterator(); iter.hasNext();)
		{
			String element = (String) iter.next();
			if ( dst_edge.containsUserDatumKey( element ) == false )
			{
				_logger.debug( "      addUserDatum: " + element + " = " + src_edge_B.getUserDatum( element ) );
				dst_edge.addUserDatum( element, src_edge_B.getUserDatum( element ), UserData.SHARED );
			}
		}
		String fullLabel_A = (String)src_edge_A.getUserDatum( FULL_LABEL_KEY ); 
		String fullLabel_B = (String)src_edge_B.getUserDatum( FULL_LABEL_KEY );
		
		if ( fullLabel_A != null && fullLabel_B != null )
		{
			if ( fullLabel_A.length() > fullLabel_B.length() )
			{
				_logger.debug( "      full label: " +  src_edge_A.getUserDatum( FULL_LABEL_KEY ) );
				dst_edge.setUserDatum( FULL_LABEL_KEY, src_edge_A.getUserDatum( FULL_LABEL_KEY ), UserData.SHARED );
			}
			else
			{
				_logger.debug( "      full label: " +  src_edge_B.getUserDatum( FULL_LABEL_KEY ) );
				dst_edge.setUserDatum( FULL_LABEL_KEY, src_edge_B.getUserDatum( FULL_LABEL_KEY ), UserData.SHARED );
			}
		}
		else if ( fullLabel_A != null  && fullLabel_B == null )
		{
			_logger.debug( "      full label: " +  src_edge_A.getUserDatum( FULL_LABEL_KEY ) );
			dst_edge.setUserDatum( FULL_LABEL_KEY, src_edge_A.getUserDatum( FULL_LABEL_KEY ), UserData.SHARED );
		}
		else if ( fullLabel_A == null  && fullLabel_B != null )
		{
			_logger.debug( "      full label: " +  src_edge_B.getUserDatum( FULL_LABEL_KEY ) );
			dst_edge.setUserDatum( FULL_LABEL_KEY, src_edge_B.getUserDatum( FULL_LABEL_KEY ), UserData.SHARED );
		}
	}

	private boolean is_cul_de_sac() {
		return _cul_de_sac;
	}

	public void set_cul_de_sac(boolean _cul_de_sac) {
		this._cul_de_sac = _cul_de_sac;
	}
	
	private void checkForVerticesWithZeroInEdges()
	{
		Object[] vs = _graph.getVertices().toArray();
		for ( int i = 0; i < vs.length; i++ )
		{
			DirectedSparseVertex v = (DirectedSparseVertex)vs[ i ];
			if ( !v.getUserDatum( LABEL_KEY ).equals( START_NODE ) )
			{
				if ( v.getInEdges().toArray().length == 0 )
				{
					String msg = "No in-edges! The vertex: " + getCompleteVertexName( v ) + " is not reachable.";
					_logger.error( msg );
					throw new RuntimeException( msg );
				}
			}
		}
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

			Integer vistited = (Integer)edge.getUserDatum( VISITED_KEY );
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

			Integer vistited = (Integer)vertex.getUserDatum( VISITED_KEY );
			if ( vistited.intValue() != 0 )
			{
				numOfVisitedVertices++;
			}
		}
		return (numOfVisitedVertices / (float)vertices.length * 100);
	}
}
