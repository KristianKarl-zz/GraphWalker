package org.tigris.mbt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.utils.UserData;

/**
 * This class has some utility functionality used by org.tigris.mbt
 * The functionality is:<br>
 * * Getting names with extra info for vertices and edges<br>
 * * Setting up the logger for classes<br>
 */
public class Util {
    /**
     * Retries information regarding an edge, and returns it as a String.
     * This method is for logging purposes.
     * 
     * @param edge The edge about which information shall be retrieved.
     * @return Returns a String with information regarding the edge, including the source and
     *  destination vertices. The format is:<br>
     *  <pre>'&lt;EDGE LABEL&gt;', INDEX=x ('&lt;SOURCE VERTEX LABEL&gt;', INDEX=y -&gt; '&lt;DEST VERTEX LABEL&gt;', INDEX=z)</pre>
     *  Where x, y and n are the unique indexes for the edge, the source vertex and the destination vertex.<br>
     *  Please note that the label of an edge can be either null, or empty ("");
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
     * Retries information regarding a vertex, and returns it as a String.
     * This method is for logging purposes.
     *
     * @param vertex The vertex about which information shall be retrieved.
     * @return Returns a String with information regarding the vertex. The format is:<br>
     *  <pre>'&lt;VERTEX LABEL&gt;', INDEX=n</pre>
     *  Where is the unique index for the vertex.
     */
    public static String getCompleteVertexName( DirectedSparseVertex vertex )
    {
            String str = "'" + (String)vertex.getUserDatum( Keywords.LABEL_KEY ) + 
                         "', INDEX=" + vertex.getUserDatum( Keywords.INDEX_KEY );
            return str;
    }

	public static void AbortIf(boolean bool, String message)
	{
		if(bool)
		{
			throw new RuntimeException( message );
		}
	}

	public static Logger setupLogger( Class classParam ) 
	{
		Logger logger = Logger.getLogger( classParam  );
		if ( new File( "mbt.properties" ).exists() )
		{
			PropertyConfigurator.configure("mbt.properties");
		}
		else
		{
	 		try
	 		{
	 			WriterAppender writerAppender = new WriterAppender( 
	 					new SimpleLayout(), 
	 					new FileOutputStream( "logs/mbt.log" ) );
		 		logger.addAppender( writerAppender );
		 		logger.setLevel( (Level)Level.ERROR );
	 		} 
	 		catch ( Exception e )
	 		{
				throw new RuntimeException(e.getMessage());
	 		}
	 
		}
		return logger;
	}

	public static DirectedSparseVertex addVertexToGraph(
			SparseGraph graph, 
			String strLabel)
	{
		DirectedSparseVertex retur = new DirectedSparseVertex();
		retur.setUserDatum(Keywords.INDEX_KEY, new Integer(graph.numEdges()+graph.numVertices()+1), UserData.SHARED);
		if(strLabel != null) retur.setUserDatum(Keywords.LABEL_KEY, strLabel, UserData.SHARED);
		return (DirectedSparseVertex) graph.addVertex(retur);
	}
	
	public static DirectedSparseEdge addEdgeToGraph(
			SparseGraph graph, 
			DirectedSparseVertex vertexFrom, 
			DirectedSparseVertex vertexTo, 
			String strLabel,
			String strParameter,
			String strGuard,
			String strAction)
	{
		DirectedSparseEdge retur = new DirectedSparseEdge(vertexFrom, vertexTo);
		retur.setUserDatum(Keywords.INDEX_KEY, new Integer(graph.numEdges()+graph.numVertices()+1), UserData.SHARED);
		if(strLabel != null) retur.setUserDatum(Keywords.LABEL_KEY, strLabel, UserData.SHARED);
		if(strParameter != null) retur.setUserDatum(Keywords.PARAMETER_KEY, strParameter, UserData.SHARED);
		if(strGuard != null) retur.setUserDatum(Keywords.GUARD_KEY, strGuard, UserData.SHARED);
		if(strAction != null) retur.setUserDatum(Keywords.ACTIONS_KEY, strAction, UserData.SHARED);
		return (DirectedSparseEdge) graph.addEdge(retur);
	}
	
	/**
	 * @deprecated use execute from {@link ModelBasedTesting} instead.
	 */
	public static boolean RunTest( Object ref, Logger log, String method ) 
	{
		Class cls = null;
		cls = ref.getClass();
		try
		{
			Method meth = cls.getMethod( method, null );
			meth.invoke( ref, null  );
		}
		catch( Exception e )
		{
			StringWriter sw = new StringWriter();
		    PrintWriter pw = new PrintWriter( sw );
		    e.printStackTrace( pw );
		    pw.close();	    		    
			log.error( sw.toString() );
			if ( e.getCause().getMessage() != null )
			{
				System.err.println( e.getCause().getMessage() );
			}
			if ( e.getMessage() != null )
			{
				System.err.println( e.getMessage() );
			}
			return false;
		}
		return true;
	}
}
