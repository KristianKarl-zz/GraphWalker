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

/**
 * This class has some utility functionality used by org.tigris.mbt
 * The functionality is:<br>
 * * Getting names with extra info for vertices and edges<br>
 * * Setting up the logger for classes<br>
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
