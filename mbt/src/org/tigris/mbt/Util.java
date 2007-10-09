package org.tigris.mbt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.utils.Pair;

/**
 * This class has some utility functionality used by org.tigris.mbt
 *
 */
public class Util {

	/**
	 * Returns information regarding an edge, including the source and
	 * destination vertices.
	 */
	public static String getCompleteEdgeName( DirectedSparseEdge edge )
	{
		String str = "'" + (String)edge.getUserDatum( Keywords.LABEL_KEY ) + 
		             "', INDEX=" + edge.getUserDatum( Keywords.INDEX_KEY ) + 
		             " ('" + (String)edge.getSource().getUserDatum( Keywords.LABEL_KEY ) + 
		             "', INDEX=" + edge.getSource().getUserDatum( Keywords.INDEX_KEY ) + 
		             " -> '" + (String)edge.getDest().getUserDatum( Keywords.LABEL_KEY ) + 
		             "', INDEX=" + edge.getDest().getUserDatum( Keywords.INDEX_KEY ) +  ")";
		return str;
	}

	/**
	 * Returns information regarding a vertex.
	 */
	public static String getCompleteVertexName( DirectedSparseVertex vertex )
	{
		String str = "'" + (String)vertex.getUserDatum( Keywords.LABEL_KEY ) + 
		             "', INDEX=" + vertex.getUserDatum( Keywords.INDEX_KEY );
		return str;
	}
	
	/**
	 * Writes the graph to file, using GraphML format
	 */
	public static void writeGraphML( SparseGraph g, String mergedGraphml )
	{
		StringBuffer sourceFile = new StringBuffer();
		try {
			FileWriter file = new FileWriter( mergedGraphml );

			sourceFile.append( "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" );
			sourceFile.append( "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns/graphml\"  " +
					            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
					            "xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns/graphml " +
					            "http://www.yworks.com/xml/schema/graphml/1.0/ygraphml.xsd\" " +
					            "xmlns:y=\"http://www.yworks.com/xml/graphml\">\n" );
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

				if ( v.containsUserDatumKey( Keywords.IMAGE_KEY ) )
				{
					sourceFile.append( "        <y:ImageNode >\n" );
					sourceFile.append( "          <y:Geometry  x=\"241.875\" y=\"158.701171875\" width=\"" +
							                        v.getUserDatum( Keywords.WIDTH_KEY ) + "\" height=\"" +
							                        v.getUserDatum( Keywords.HEIGHT_KEY ) + "\"/>\n" );
				}
				else
				{
					sourceFile.append( "        <y:ShapeNode >\n" );
					sourceFile.append( "          <y:Geometry  x=\"241.875\" y=\"158.701171875\" width=\"95.0\" height=\"30.0\"/>\n" );
				}
				
				sourceFile.append( "          <y:Fill color=\"#CCCCFF\"  transparent=\"false\"/>\n" );
				sourceFile.append( "          <y:BorderStyle type=\"line\" width=\"1.0\" color=\"#000000\" />\n" );
				sourceFile.append( "          <y:NodeLabel x=\"1.5\" y=\"5.6494140625\" width=\"92.0\" height=\"18.701171875\" " +
						                         "visible=\"true\" alignment=\"center\" fontFamily=\"Dialog\" fontSize=\"12\" " +
						                         "fontStyle=\"plain\" textColor=\"#000000\" modelName=\"internal\" modelPosition=\"c\" " +
						                         "autoSizePolicy=\"content\">" + v.getUserDatum( Keywords.FULL_LABEL_KEY ) + 
						                         "&#xA;INDEX=" + v.getUserDatum( Keywords.INDEX_KEY ) + "</y:NodeLabel>\n" );
				
				if ( v.containsUserDatumKey( Keywords.IMAGE_KEY ) )
				{
					sourceFile.append( "          <y:Image href=\"" + v.getUserDatum( Keywords.IMAGE_KEY ) + "\"/>\n" );
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
	            
	            if ( e.getUserDatum( Keywords.FULL_LABEL_KEY ) != null )
	            {
	            	sourceFile.append( "          <y:EdgeLabel x=\"-148.25\" y=\"30.000000000000014\" width=\"169.0\" height=\"18.701171875\" " +
	            			                         "visible=\"true\" alignment=\"center\" fontFamily=\"Dialog\" fontSize=\"12\" " +
	            			                         "fontStyle=\"plain\" textColor=\"#000000\" modelName=\"free\" modelPosition=\"anywhere\" " +
	            			                         "preferredPlacement=\"on_edge\" distance=\"2.0\" ratio=\"0.5\">" + e.getUserDatum( Keywords.FULL_LABEL_KEY ) + 
	            			                         "&#xA;INDEX=" + e.getUserDatum( Keywords.INDEX_KEY ) + "</y:EdgeLabel>\n" );
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
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void generateJavaCode( SparseGraph g, String fileName )
	{
		Object[] vertices = g.getVertices().toArray();
		Object[] edges    = g.getEdges().toArray();

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
		}
		catch ( IOException e )
		{
			e.printStackTrace();
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
				e.printStackTrace();
			}
		}

		for ( int i = 0; i < vertices.length; i++ )
		{
			DirectedSparseVertex vertex = (DirectedSparseVertex)vertices[ i ];

			// Do not write the Start vertex to file, it's only used as
			// an entry point.
			if ( vertex.getUserDatum( Keywords.LABEL_KEY ).equals( Keywords.START_NODE ) )
			{
				continue;
			}

			boolean duplicated = false;
			for ( Iterator iter = writtenVertices.iterator(); iter.hasNext(); )
			{
				String str = (String) iter.next();
				if ( str.equals( (String)vertex.getUserDatum( Keywords.LABEL_KEY ) ) == true )
				{
					duplicated = true;
					break;
				}
			}

			if ( duplicated == false )
			{
				Pattern p = Pattern.compile( "public void " + (String)vertex.getUserDatum( Keywords.LABEL_KEY ) + "\\(\\)(.|[\\n\\r])*?\\{(.|[\\n\\r])*?\\}", Pattern.MULTILINE );
				Matcher m = p.matcher( sourceFile );

				if ( m.find() == false )
				{
					sourceFile.append( "/**\n" );
					sourceFile.append( " * This method implements the verification of the vertex: '" + 
							            (String)vertex.getUserDatum( Keywords.LABEL_KEY ) + "'\n" );
					sourceFile.append( " */\n" );
					sourceFile.append( "public void " + (String)vertex.getUserDatum( Keywords.LABEL_KEY ) + "()\n" );
					sourceFile.append( "{\n" );
					sourceFile.append( "	_logger.info( \"Vertex: " + (String)vertex.getUserDatum( Keywords.LABEL_KEY ) + "\" );\n" );
					sourceFile.append( "	throw new RuntimeException( \"Not implemented. This line can be removed.\" );\n" );
					sourceFile.append( "}\n\n" );
				}
			}

			writtenVertices.add( (String)vertex.getUserDatum( Keywords.LABEL_KEY ) );
		}

		for ( int i = 0; i < edges.length; i++ )
		{
			DirectedSparseEdge edge = (DirectedSparseEdge)edges[ i ];
			if ( edge.getUserDatum( Keywords.LABEL_KEY ) == null )
			{
				continue;
			}

			boolean duplicated = false;
			for ( Iterator iter = writtenEdges.iterator(); iter.hasNext(); )
			{
				String str = (String) iter.next();
				if ( str.equals( edge.getUserDatum( Keywords.LABEL_KEY ) ) == true )
				{
					duplicated = true;
					break;
				}
			}

			if ( duplicated == false )
			{
				Pattern p = Pattern.compile( "public void " + (String)edge.getUserDatum( Keywords.LABEL_KEY ) + 
						            "\\(\\)(.|[\\n\\r])*?\\{(.|[\\n\\r])*?\\}", Pattern.MULTILINE );
				Matcher m = p.matcher( sourceFile );

				if ( m.find() == false )
				{
					sourceFile.append( "/**\n" );
					sourceFile.append( " * This method implemets the edge: '" + (String)edge.getUserDatum( Keywords.LABEL_KEY ) + "'\n" );
					sourceFile.append( " */\n" );
					sourceFile.append( "public void " + (String)edge.getUserDatum( Keywords.LABEL_KEY ) + "()\n" );
					sourceFile.append( "{\n" );
					sourceFile.append( "	_logger.info( \"Edge: " + (String)edge.getUserDatum( Keywords.LABEL_KEY ) + "\" );\n" );
					sourceFile.append( "	throw new RuntimeException( \"Not implemented\" );\n" );
					sourceFile.append( "}\n\n" );
				}
			}

			writtenEdges.add( (String)edge.getUserDatum( Keywords.LABEL_KEY ) );
		}

		try
		{
			FileWriter file = new FileWriter( fileName );
			file.write( sourceFile.toString() );
			file.flush();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
	}

	public static void generatePerlCode( SparseGraph g, String fileName )
	{
		Object[] vertices = g.getVertices().toArray();
		Object[] edges    = g.getEdges().toArray();

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
		}
		catch ( IOException e )
		{
			e.printStackTrace();
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
				e.printStackTrace();
			}
		}

		for ( int i = 0; i < vertices.length; i++ )
		{
			DirectedSparseVertex vertex = (DirectedSparseVertex)vertices[ i ];

			// Do not write the Start vertex to file, it's only used as
			// an entry point.
			if ( vertex.getUserDatum( Keywords.LABEL_KEY ).equals( Keywords.START_NODE ) )
			{
				continue;
			}

			boolean duplicated = false;
			for ( Iterator iter = writtenVertices.iterator(); iter.hasNext(); )
			{
				String str = (String) iter.next();
				if ( str.equals( (String)vertex.getUserDatum( Keywords.LABEL_KEY ) ) == true )
				{
					duplicated = true;
					break;
				}
			}

			if ( duplicated == false )
			{
				Pattern p = Pattern.compile( "sub " + (String)vertex.getUserDatum( Keywords.LABEL_KEY ) + "\\(\\)(.|[\\n\\r])*?\\{(.|[\\n\\r])*?\\}", Pattern.MULTILINE );
				Matcher m = p.matcher( sourceFile );

				if ( m.find() == false )
				{
					sourceFile.append( "#\n" );
					sourceFile.append( "# This method implements the verification of the vertex: '" + (String)vertex.getUserDatum( Keywords.LABEL_KEY ) + "'\n" );
					sourceFile.append( "#\n" );
					sourceFile.append( "sub " + (String)vertex.getUserDatum( Keywords.LABEL_KEY ) + "()\n" );
					sourceFile.append( "{\n" );
					sourceFile.append( "	print \"Vertex: " + (String)vertex.getUserDatum( Keywords.LABEL_KEY ) + "\\n\";\n" );
					sourceFile.append( "	print \"Not implemented.\\n\";\n" );
					sourceFile.append( "	exit 1;\n" );
					sourceFile.append( "}\n\n" );
				}
			}

			writtenVertices.add( (String)vertex.getUserDatum( Keywords.LABEL_KEY ) );
		}

		for ( int i = 0; i < edges.length; i++ )
		{
			DirectedSparseEdge edge = (DirectedSparseEdge)edges[ i ];

			boolean duplicated = false;
			for ( Iterator iter = writtenEdges.iterator(); iter.hasNext(); )
			{
				String str = (String) iter.next();
				if ( str.equals( (String)edge.getUserDatum( Keywords.LABEL_KEY ) ) == true )
				{
					duplicated = true;
					break;
				}
			}

			if ( duplicated == false )
			{
				Pattern p = Pattern.compile( "sub " + (String)edge.getUserDatum( Keywords.LABEL_KEY ) + "\\(\\)(.|[\\n\\r])*?\\{(.|[\\n\\r])*?\\}", Pattern.MULTILINE );
				Matcher m = p.matcher( sourceFile );

				if ( m.find() == false )
				{
					sourceFile.append( "#\n" );
					sourceFile.append( "# This method implemets the edge: '" + (String)edge.getUserDatum( Keywords.LABEL_KEY ) + "'\n" );
					sourceFile.append( "#\n" );
					sourceFile.append( "sub " + (String)edge.getUserDatum( Keywords.LABEL_KEY ) + "()\n" );
					sourceFile.append( "{\n" );
					sourceFile.append( "	print \"Edge: " + (String)edge.getUserDatum( Keywords.LABEL_KEY ) + "\\n\";\n" );
					sourceFile.append( "	print \"Not implemented.\\n\";\n" );
					sourceFile.append( "	exit 1;\n" );
					sourceFile.append( "}\n\n" );
				}
			}

			writtenEdges.add( (String)edge.getUserDatum( Keywords.LABEL_KEY ) );
		}

		try
		{
			FileWriter file = new FileWriter( fileName );
			file.write( sourceFile.toString() );
			file.flush();
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
	}
}
