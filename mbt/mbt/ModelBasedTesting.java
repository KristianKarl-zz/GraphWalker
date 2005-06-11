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

package mbt;

import java.lang.reflect.Method;
import java.util.*;
import java.io.*;
import org.apache.log4j.*;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.filter.*;
import org.jdom.*;

import java.util.regex.*;
import edu.uci.ics.jung.graph.impl.*;
import edu.uci.ics.jung.utils.*;

/**
 * @author Kristian Karl
 */
public class ModelBasedTesting
{
	private static SparseGraph _graph          = new SparseGraph();
	private static SAXBuilder  _parser         = new SAXBuilder();
	private static Random      _radomGenerator = new Random();

	private static String  START_NODE           = "Start";
	private static String  ID_KEY               = "id";
	private static String  LABEL_KEY            = "label";
	private static String  VISITED_KEY          = "visited";
	private static String  WEIGHT_KEY           = "weight";
	private static String  STATE_KEY            = "state";
	private static String  CONDITION_KEY        = "condition";
	private static String  VARIABLE_KEY         = "variable";
	private static String  BACK_KEY             = "back";
	private static String  NO_HISTORY	        = "no history";

	private Document 			 _doc;
	private String   			 _graphmlFileName;
	private Object   			 _object;
	private Logger   			 _logger;
	private Object[] 			 _vertices     = null;
	private Object[]             _edges        = null;
	private DirectedSparseVertex _nextVertex   = null;
	private DirectedSparseVertex _prevVertex   = null;
	private DirectedSparseEdge 	 _rejectedEdge = null;
	private LinkedList 			 _history      = new LinkedList();
	private long				 _start_time;
	private long				 _end_time;

	public ModelBasedTesting( String graphmlFileName_,
							  Object object_,
							  Logger logger_ )
	{
		_graphmlFileName = graphmlFileName_;
		_object          = object_;
		_logger          = logger_;

		parseFile();
	}

	public ModelBasedTesting( String graphmlFileName_,
							  Logger logger_ )
	{
		_graphmlFileName = graphmlFileName_;
		_object          = null;
		_logger          = logger_;

		parseFile();
	}

	/**
	 * @param runningTime
	 * The time, in seconds, to run this test.
	 */
	public void runRandomWalk( long runningTime )
	{
		findStartingVertex();

		long startTime = System.currentTimeMillis();
		long currentTime = startTime;
		_start_time = startTime;


		runningTime *= 1000;

		// Start the run. The run is a ranomized walk through the graph.
		while ( ( currentTime - startTime ) < runningTime )
		{
			executeMethod( false );
			currentTime = System.currentTimeMillis();
			_end_time = currentTime;
		}
	}


	/**
	 * Run the test untill all vertices (nodes) are visited.
	 */
	public void runUntilAllVerticesVisited()
	{
		findStartingVertex();

		_start_time = System.currentTimeMillis();
		while ( true )
		{
			executeMethod( true );
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
	public void runUntilAllEdgesVisited()
	{
		findStartingVertex();

		_start_time = System.currentTimeMillis();
		while ( true )
		{
			executeMethod( true );
			_end_time = System.currentTimeMillis();
			if ( isAllEdgesVisited() )
			{
				break;
			}
		}
	}


	/**
	 * Run the test untill all edges (arrows, or transistions), and
	 * all vertices (nodes) are visited
	 */
	public void runUntilAllVerticesAndEdgesVisited()
	{
		findStartingVertex();

		_start_time = System.currentTimeMillis();
		while ( true )
		{
			executeMethod( true );
			_end_time = System.currentTimeMillis();
			if ( isAllVerticesVisited() && isAllEdgesVisited() )
			{
				break;
			}
		}
	}


	private void goBackToPreviousVertex()
	{
		if ( _prevVertex == null  )
		{
			throw new RuntimeException( "goBackToPreviousVertex(): _prevVertex is null" );
		}
		_nextVertex = _prevVertex;
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
	private void parseFile()
	{
		try
		{
			_logger.info( "Parsing file: " + _graphmlFileName );
			_doc = _parser.build( _graphmlFileName );

			// Parse all vertices (nodes)
			Iterator iter = _doc.getDescendants( new ElementFilter( "node" ) );
			while ( iter.hasNext() )
			{
				Object o = iter.next();
				if ( o instanceof Element )
				{
					Element element = (Element)o;
					if ( element.getAttributeValue( "yfiles.foldertype" ) != null )
					{
						_logger.debug( "Excluded node: " + element.getAttributeValue( "yfiles.foldertype" ) );
						continue;
					}
					_logger.debug( "id: " + element.getAttributeValue( "id" ) );

					Iterator iter2 = element.getDescendants( new ElementFilter( "NodeLabel" ) );
					while ( iter2.hasNext() )
					{
						Object o2 = iter2.next();
						if ( o2 instanceof Element )
						{
							Element nodeLabel = (Element)o2;
							_logger.debug( "Full name: " + nodeLabel.getQualifiedName() );
							_logger.debug( "Name: " + nodeLabel.getTextTrim() );

							DirectedSparseVertex v = (DirectedSparseVertex) _graph.addVertex( new DirectedSparseVertex() );

							v.addUserDatum( ID_KEY, 	 element.getAttributeValue( "id" ), UserData.SHARED );
							v.addUserDatum( VISITED_KEY, new Integer( 0 ), 					UserData.SHARED );

							String str = nodeLabel.getTextTrim();
							Pattern p = Pattern.compile( "(.*)", Pattern.MULTILINE );
							Matcher m = p.matcher( str );
							String label;
							if ( m.find( ))
							{
								label = m.group( 1 );
								v.addUserDatum( LABEL_KEY, label, UserData.SHARED );
							}
							else
							{
								throw new RuntimeException( "Label must be defined." );
							}
							_logger.debug( "Added node: " + v.getUserDatum( LABEL_KEY ) );


							// NOTE: Only for html applications
							// In browsers, the usage of the 'Back'-button can be used.
							// If defined, with a value value, which depicts the probability for the edge
							// to be executed, tha back-button will be pressed in the browser.
							// A value of 0.05 is the same as 5% chance of going down this road.
							p = Pattern.compile( "(back=(.*))", Pattern.MULTILINE );
							m = p.matcher( str );
							if ( m.find( ) )
							{
								Float probability;
								String value = m.group( 2 );
								try
								{
									probability = Float.valueOf( value.trim() );
								}
								catch ( NumberFormatException error )
								{
									throw new RuntimeException( "For label: " + label + ", back is not a correct float value: " + error.toString() );
								}
								v.addUserDatum( BACK_KEY, probability, UserData.SHARED );
							}
						}
					}
				}
			}

			Object[] vertices = _graph.getVertices().toArray();

			// Parse all edges (arrows or transtitions)
			iter = _doc.getDescendants( new ElementFilter( "edge" ) );
			while ( iter.hasNext() )
			{
				Object o = iter.next();
				if ( o instanceof Element )
				{
					Element element = (Element)o;
					_logger.debug( "id: " + element.getAttributeValue( "id" ) );

					Iterator iter2 = element.getDescendants( new ElementFilter( "EdgeLabel" ) );
					while ( iter2.hasNext() )
					{
						Object o2 = iter2.next();
						if ( o2 instanceof Element )
						{
							Element edgeLabel = (Element)o2;
							_logger.debug( "Full name: " + edgeLabel.getQualifiedName() );
							_logger.debug( "Name: " + edgeLabel.getTextTrim() );
							_logger.debug( "source: " + element.getAttributeValue( "source" ) );
							_logger.debug( "target: " + element.getAttributeValue( "target" ) );

							DirectedSparseVertex source = null;
							DirectedSparseVertex dest = null;

							for ( int i = 0; i < vertices.length; i++ )
							{
								DirectedSparseVertex vertex = (DirectedSparseVertex)vertices[ i ];

								// Find source vertex
								if ( vertex.getUserDatum( ID_KEY ).equals( element.getAttributeValue( "source" ) ) )
								{
									source = vertex;
								}
								if ( vertex.getUserDatum( ID_KEY ).equals( element.getAttributeValue( "target" ) ) )
								{
									dest = vertex;
								}
							}
							if ( source == null )
							{
								String msg = "Could not find starting node for edge. Name: " + element.getAttributeValue( "source" );
								_logger.error( msg );
								throw new RuntimeException( msg );
							}
							if ( dest == null )
							{
								String msg = "Could not find end node for edge. Name: " + element.getAttributeValue( "target" );
								_logger.error( msg );
								throw new RuntimeException( msg );
							}

							String str = edgeLabel.getTextTrim();

							DirectedSparseEdge e = new DirectedSparseEdge( source, dest );
							_graph.addEdge( e );
							e.addUserDatum( ID_KEY, element.getAttributeValue( "id" ), UserData.SHARED );

							Pattern p = Pattern.compile( "(.*)", Pattern.MULTILINE );
							Matcher m = p.matcher( str );
							String label;
							if ( m.find() )
							{
								label = m.group( 1 );
								e.addUserDatum( LABEL_KEY, label, UserData.SHARED );
								_logger.debug( "Found label= " + label + " for edge id: " + edgeLabel.getQualifiedName() );
							}
							else
							{
								throw new RuntimeException( "Label must be defined." );
							}



							// If weight is defined, find it...
							// weight must be associated with a value, which depicts the probability for the edge
							// to be executed.
							// A value of 0.05 is the same as 5% chance of going down this road.
							p = Pattern.compile( "(weight=(.*))", Pattern.MULTILINE );
							m = p.matcher( str );
							if ( m.find() )
							{
								Float weight;
								String value = m.group( 2 );
								try
								{
									weight = Float.valueOf( value.trim() );
									_logger.debug( "Found weight= " + weight + " for edge: " + label );
								}
								catch ( NumberFormatException error )
								{
									throw new RuntimeException( "For label: " + label + ", weight is not a correct float value: " + error.toString() );
								}
								e.addUserDatum( WEIGHT_KEY, weight, UserData.SHARED );
							}



							// If No_history is defined, find it...
							// If defined, it means that when executing this edge, it shall not
							// be added to the history list of passed edgses.
							p = Pattern.compile( "(No_history)", Pattern.MULTILINE );
							m = p.matcher( str );
							if ( m.find() )
							{
								e.addUserDatum( NO_HISTORY, m.group( 1 ), UserData.SHARED );
								_logger.debug( "Found No_history for edge: " + label );
							}



							// If condition used defined, find it...
							p = Pattern.compile( "(if: (.*)=(.*))", Pattern.MULTILINE );
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
								_logger.debug( "Condition: " + variable + " = " +  state );
							}
							if ( conditions != null )
							{
								e.addUserDatum( CONDITION_KEY, conditions, UserData.SHARED );
							}



							// If state are defined, find them...
							HashMap states = null;
							p = Pattern.compile( "(state: (.*)=(.*))", Pattern.MULTILINE );
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
								_logger.debug( "State: " + variable + " = " +  state );
							}
							if ( states != null )
							{
								e.addUserDatum( STATE_KEY, states, UserData.SHARED );
							}



							// If string variables are defined, find them...
							HashMap variables = null;
							p = Pattern.compile( "(string: (.*)=(.*))", Pattern.MULTILINE );
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
								_logger.debug( "String variable: " + variableLabel + " = " +  variable );
							}



							// If integer variables are defined, find them...
							p = Pattern.compile( "(integer: (.*)=(.*))", Pattern.MULTILINE );
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
								_logger.debug( "Integer variable: " + variableLabel + " = " +  variable );
							}



							// If integer variables are defined, find them...
							p = Pattern.compile( "(float: (.*)=(.*))", Pattern.MULTILINE );
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
								_logger.debug( "Float variable: " + variableLabel + " = " +  variable );
							}
							if ( variables != null )
							{
								e.addUserDatum( VARIABLE_KEY, variables, UserData.SHARED );
							}


							e.addUserDatum( VISITED_KEY, new Integer( 0 ), UserData.SHARED );
							_logger.debug( "Adderade edge: " + e.getUserDatum( LABEL_KEY ) );
						}
					}
				}
			}
		}
		catch ( JDOMException e )
		{
			_logger.error( e );
			throw new RuntimeException( "Kunde inte skanna filen: " + _graphmlFileName );
		}
		catch ( IOException e )
		{
			_logger.error( e );
			throw new RuntimeException( "Kunde inte skanna filen: " + _graphmlFileName );
		}
	}

	public String get_statistics()
	{
		String stat = new String();
		String new_line = new String( "<br>" );

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
				stat += "Not tested (Edge): " + (String)edge.getUserDatum( ID_KEY ) + ", " + (String)edge.getUserDatum( LABEL_KEY ) + new_line;
			}
			else
			{
				numOfVisitedEdges++;
			}
			totalNumOfVisitedEdges += vistited.intValue();
		}

		// Logga vilka noder som inte är besökta.
		for ( int i = 0; i < vertices.length; i++ )
		{
			DirectedSparseVertex vertex = (DirectedSparseVertex)vertices[ i ];

			Integer vistited = (Integer)vertex.getUserDatum( VISITED_KEY );
			if ( vistited.intValue() == 0 )
			{
				stat += "Not tested (Vertex): " + (String)vertex.getUserDatum( ID_KEY ) + ", " + (String)vertex.getUserDatum( LABEL_KEY ) + new_line;
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
		stat += "Execution time: " + ( ( _end_time - _start_time ) / 1000 ) + " seconds" + new_line;

		return stat;
	}

	/**
	 * Return the instance of the graph
	 */
	public static SparseGraph get_graph() {
		return _graph;
	}

	public void generateJavaCode_XDE( String fileName )
	{
		boolean _existBack = false;

		_vertices = _graph.getVertices().toArray();
		_edges    = _graph.getEdges().toArray();

		ArrayList writtenVertices = new ArrayList();
		ArrayList writtenEdges    = new ArrayList();

		StringBuffer sourceFile = new StringBuffer();

		try
		{
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

			_logger.info( sourceFile.toString() );


			FileWriter file = new FileWriter( fileName );

			for ( int i = 0; i < _vertices.length; i++ )
			{
				DirectedSparseVertex vertex = (DirectedSparseVertex)_vertices[ i ];

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

				if ( _existBack == false )
				{
					_existBack = true;

					Pattern p = Pattern.compile( "public void PressBackButton\\(\\)(.|[\\n\\r])*?\\{(.|[\\n\\r])*?\\}", Pattern.MULTILINE );
					Matcher m = p.matcher( sourceFile );

					if ( m.find() == false )
					{
						sourceFile.append( "/**\n" );
						sourceFile.append( " * This method implements the edge: PressBackButton\n" );
						sourceFile.append( " */\n" );
						sourceFile.append( "public void PressBackButton()\n" );
						sourceFile.append( "{\n" );
						sourceFile.append( "	logInfo( \"Edge: PressBackButton\" );\n" );
						sourceFile.append( "	throw new RuntimeException( \"Not implemented. This line can be removed.\" );\n" );
						sourceFile.append( "}\n\n" );
					}
				}

				if ( duplicated == false )
				{
					Pattern p = Pattern.compile( "public void " + (String)vertex.getUserDatum( LABEL_KEY ) + "\\(\\)(.|[\\n\\r])*?\\{(.|[\\n\\r])*?\\}", Pattern.MULTILINE );
					Matcher m = p.matcher( sourceFile );

					if ( m.find() == false )
					{
						sourceFile.append( "/**\n" );
						sourceFile.append( " * This method implements the verification of the vertex: " + (String)vertex.getUserDatum( LABEL_KEY ) + "\n" );
						sourceFile.append( " */\n" );
						sourceFile.append( "public void " + (String)vertex.getUserDatum( LABEL_KEY ) + "()\n" );
						sourceFile.append( "{\n" );
						sourceFile.append( "	logInfo( \"Vertex: " + (String)vertex.getUserDatum( LABEL_KEY ) + "\" );\n" );
						sourceFile.append( "	throw new RuntimeException( \"Not implemented. This line can be removed.\" );\n" );
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
					Pattern p = Pattern.compile( "public void " + (String)edge.getUserDatum( LABEL_KEY ) + "\\(\\)(.|[\\n\\r])*?\\{(.|[\\n\\r])*?\\}", Pattern.MULTILINE );
					Matcher m = p.matcher( sourceFile );

					if ( m.find() == false )
					{
						sourceFile.append( "/**\n" );
						sourceFile.append( " * This method implemets the edge: " + (String)edge.getUserDatum( LABEL_KEY ) + "\n" );
						sourceFile.append( " */\n" );
						sourceFile.append( "public void " + (String)edge.getUserDatum( LABEL_KEY ) + "()\n" );
						sourceFile.append( "{\n" );
						sourceFile.append( "	logInfo( \"Edge: " + (String)edge.getUserDatum( LABEL_KEY ) + "\" );\n" );

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
								sourceFile.append( "	  logInfo( \"Not a valid path until condition is fullfilled\" );\n" );
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

						sourceFile.append( "	throw new RuntimeException( \"Ej implementerat\" );\n" );
						sourceFile.append( "}\n\n" );
					}
				}

				writtenEdges.add( (String)edge.getUserDatum( LABEL_KEY ) );
			}
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
			String msg = "Did not found the starting vertex in the graph.";
			_logger.error( msg );
			throw new RuntimeException( msg );
		}
	}

	private void executeMethod( boolean optimize )
	{
		DirectedSparseEdge edge 	= null;
		Object[] 		   outEdges = null;

		if ( _nextVertex.containsUserDatumKey( BACK_KEY ) && _history. size() >= 3 )
		{
			Float probability = (Float)_nextVertex.getUserDatum( BACK_KEY );
			int index = _radomGenerator.nextInt( 100 );
			if ( index < ( probability.floatValue() * 100 ) )
			{
				String str =  (String)_history.removeLast();
				_logger.debug( "Remove from history: " + str );
				String  nodeLabel = (String)_history.getLast();
				_logger.debug( "Reversing a vertex. From: " + (String)_nextVertex.getUserDatum( LABEL_KEY ) + ", to: " + nodeLabel );

				Object[] vertices = _graph.getVertices().toArray();
				for ( int i = 0; i < vertices.length; i++ )
				{
					DirectedSparseVertex vertex = (DirectedSparseVertex)vertices[ i ];
					if ( nodeLabel == (String)vertex.getUserDatum( LABEL_KEY ) )
					{
						try
						{
							_nextVertex = vertex;
							String label = "PressBackButton";
							_logger.debug( "Invoke method for edge: " + label );
							invokeMethod( label );

							label = nodeLabel;
							_logger.debug( "Invoke method for vertex: " + label );
							invokeMethod( label );
						}
						catch( GoBackToPreviousVertexException e )
						{
							throw new RuntimeException( "An GoBackToPreviousVertexException was thrown where it should not be thrown." );
						}

						return;
					}
				}
				throw new RuntimeException( "An attempt was made to reverse to vertex: " + nodeLabel + ", and did not find it." );
			}
		}


		_logger.debug( "Vertex = " + (String)_nextVertex.getUserDatum( LABEL_KEY ) );

		outEdges = _nextVertex.getOutEdges().toArray();
		_logger.debug( "Number of outgoing edges = " + outEdges.length );

		outEdges = shuffle( outEdges );

		if ( optimize )
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
						_logger.debug( "Found an edge which has not been visited yet: " + (String)edge.getUserDatum( LABEL_KEY ) );
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
		_logger.debug( "Edge = " + (String)edge.getUserDatum( LABEL_KEY ) );

		_prevVertex = _nextVertex;
		_nextVertex = (DirectedSparseVertex)edge.getDest();

		try
		{
			String label = (String)edge.getUserDatum( LABEL_KEY );
			_logger.debug( "Invoke method for edge: " + label );
			invokeMethod( label );
			Integer vistited = (Integer)edge.getUserDatum( VISITED_KEY );
			vistited = new Integer( vistited.intValue() + 1 );
			edge.setUserDatum( VISITED_KEY, vistited, UserData.SHARED );

			label = (String)edge.getDest().getUserDatum( LABEL_KEY );
			_logger.debug( "Invoke method for vertex: " + label );
			invokeMethod( label );
			vistited = (Integer)edge.getDest().getUserDatum( VISITED_KEY );
			vistited = new Integer( vistited.intValue() + 1 );
			edge.getDest().setUserDatum( VISITED_KEY, vistited, UserData.SHARED );

			if ( ((String)edge.getDest().getUserDatum( LABEL_KEY )).equals( "Stop" ) )
			{
				_logger.debug( "Clearing the history" );
				_history.clear();
			}
			if ( edge.containsUserDatumKey( NO_HISTORY ) == false )
			{
				_logger.debug( "Add to history: " +  (String)edge.getDest().getUserDatum( LABEL_KEY ) );
				_history.add( (String)edge.getDest().getUserDatum( LABEL_KEY ) );
			}
		}
		catch( GoBackToPreviousVertexException e )
		{
			_logger.debug( "The edge: " + (String)edge.getUserDatum( LABEL_KEY ) + " can not be run due to unfullfilled conditions." );
			_logger.debug( "Trying from vertex: " + (String)_prevVertex.getUserDatum( LABEL_KEY ) + " again." );
			_rejectedEdge = edge;
			_nextVertex   = _prevVertex;
		}
	}

	private void invokeMethod( String method ) throws GoBackToPreviousVertexException
	{
		Class cls = _object.getClass();

		try
		{
			Method meth = cls.getMethod( method, null );
			meth.invoke( _object, null  );
		}
		catch( NoSuchMethodException e )
		{
			_logger.error( e );
			_logger.error( "Try to invoke method: " + method );
			throw new RuntimeException( "The methoden is not defined: " + method );
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
	DirectedSparseEdge getWeightedEdge( DirectedSparseVertex vertex )
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
			throw new RuntimeException( "The sum of all weight from vertex: " + (String)vertex.getUserDatum( LABEL_KEY ) + " adds to more than 1" );
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
			_logger.debug( "The edge: " + (String)((DirectedSparseEdge)edges[ i ]).getUserDatum( LABEL_KEY ) + " is given the probability of " + probabilities[ i ] * 100 + "%"  );

			weight = weight + probabilities[ i ] * 100;
			_logger.debug( "Current weight is: " + weight  );
			if ( index < weight )
			{
				edge = (DirectedSparseEdge)edges[ i ];
				_logger.debug( "Selected edge is: " + (String)edge.getUserDatum( LABEL_KEY ) );
				break;
			}
		}

		if ( edge == null )
		{
			throw new RuntimeException( "Did not find any edge." );
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
}
