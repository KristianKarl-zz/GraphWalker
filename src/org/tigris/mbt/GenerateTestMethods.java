package org.tigris.mbt;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class GenerateTestMethods {
	static private String graphmlFile;


	public static void main(String[] args)
	{
		try 
		{
			Options opt = new Options();
			opt.addOption( "h", "help", false, "Print help for this application" );
			opt.addOption( OptionBuilder.withArgName( "file" )
                    .withDescription( "The graphml file containing the model of the test" )
                    .hasArg()
                    .withLongOpt( "graphml" )
                    .create( "g" ) );
			
			CommandLineParser parser = new PosixParser();
	        CommandLine cl = parser.parse( opt, args );
	        
            if ( cl.hasOption( "h" ) ) 
            {
                HelpFormatter f = new HelpFormatter();
                f.printHelp( "GenerateTestMethods", opt );
            }
            else
            {
	            if ( !cl.hasOption( "g" ) )
	            {
	            	System.out.println( "Missing the graphml file, See --graphml or -g" );
	                HelpFormatter f = new HelpFormatter();
	                f.printHelp( "GenerateTestMethods", opt );
	                return;	            	
	            }

            	graphmlFile = cl.getOptionValue( "g" );
            	
            	GenerateTestMethods test = new GenerateTestMethods();
    			test.run();
			}
        }
        catch ( ParseException e ) 
        {
            e.printStackTrace();
        }
	}

	public void run()
	{
		try
		{
			ModelBasedTesting mbt = new ModelBasedTesting( graphmlFile );	
			Vector testSequence = mbt.generateTests( false, 0);
			SortedSet set = new TreeSet();
			Iterator iterTestSequence = testSequence.iterator();
		    while ( iterTestSequence.hasNext() ) 
		    {
				String element = (String) iterTestSequence.next();
				set.add( element );
		    }

			StringBuffer strBuff = new StringBuffer();
		    Iterator setIterator = set.iterator();
		    while ( setIterator.hasNext() ) 
		    {
				String element = (String) setIterator.next();
				strBuff.append( element + "\n" );
		    }
			System.out.println( strBuff.toString() );
		}		
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
}
