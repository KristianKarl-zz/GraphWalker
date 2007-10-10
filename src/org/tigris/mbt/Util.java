package org.tigris.mbt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.utils.Pair;

/**
 * This class has some utility functionality used by org.tigris.mbt
 * The functionality is:<br>
 * * Generating Java code<br>
 * * Generating Perl code<br>
 * * Getting names with extra info for vertices and edges<br>
 * * Writing a graph back to a file using the GraphML format<br>
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
	 * Writes the graph to file, using GraphML format.
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


	/**
	 * Will generate code using a template. The code generated will contain all lables/names
	 * defined by the vertices and edges. This enables the user to write templates for a 
	 * multitude of scripting or programming languages.<br><br>
	 * The result will be printed to stdout.<br><br>
	 * There is 1 variable in the template, that will be replaced:<br>
	 * <strong>{LABEL}</strong> Will be replace by the actual name of the edge or vertex.<br><
	 * <strong>{EDGE_VERTEX}</strong> Will be replace by the word 'Edge' or 'Vertex'.<br><br>
	 * <strong>Below is an example of a template.</strong>
	 * <pre>
	 * /**
	 * * This method implements the {EDGE_VERTEX} '{LABEL}'
	 * * /
	 * public void {LABEL}()
	 * {
	 *    log.info( "{EDGE_VERTEX}: {LABEL}" );
	 *    throw new RuntimeException( "Not implemented" );
	 * }
	 * </pre>
	 * @throws IOException 
	 */
	public static void generateCodeByTemplate( SparseGraph g, String templateFile ) throws IOException
	{
		Object[] vertices = g.getVertices().toArray();
		Object[] edges    = g.getEdges().toArray();

		StringBuffer templateBuffer = new StringBuffer();

		/**
		 * Read the original file first. If the methods already are defined in the file,
		 * leave those methods alone.
		 */
		BufferedReader input = null;
		input = new BufferedReader( new FileReader( templateFile ) );
		String line = null;
		while ( ( line = input.readLine() ) != null )
		{
			templateBuffer.append( line );
			templateBuffer.append( System.getProperty( "line.separator" ) );
		}
		input.close();
		
		SortedSet set  = new TreeSet();
		Hashtable hash = new Hashtable();

		for (int i = 0; i < vertices.length; i++) 
		{
			DirectedSparseVertex vertex = (DirectedSparseVertex)vertices[ i ];
			String element = (String) vertex.getUserDatum( Keywords.LABEL_KEY );
			if ( element != null )
			{
				if ( !element.equals( "Start" ) )
				{
					set.add( element );
					hash.put( element, "Vertex" );
				}
			}
		}
		
		for (int i = 0; i < edges.length; i++) 
		{
			DirectedSparseEdge edge = (DirectedSparseEdge)edges[ i ];
			String element = (String) edge.getUserDatum( Keywords.LABEL_KEY );
			if ( element != null )
			{
				set.add( element );
				hash.put( element, "Edge" );
			}
		}

		String template = templateBuffer.toString();
	    Iterator setIterator = set.iterator();
	    while ( setIterator.hasNext() ) 
	    {
			String element = (String)setIterator.next();
			String type = (String)hash.get( element );
			String tmpStr = template.replaceAll( "\\{LABEL\\}", element );
			System.out.print( tmpStr.replaceAll( "\\{EDGE_VERTEX\\}", type ) );			
			System.out.println();
	    }
	}
}
