package org.tigris.mbt;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class GenerateTests {
	
	static private String graphmlFile;
	static private boolean random;
	static private long length;


	public static void main(String[] args)
	{
		try 
		{
			Options opt = new Options();
			opt.addOption( "h", "help", false, "Print help for this application" );
			opt.addOption( "r", "random", false, "Run the test with a random walk. Can not be combined with --optimize. " + 
												 "This argument also needs the --length to be set." );
			opt.addOption( "o", "optimize", false, "Run the test optimized. Can not be combined with --random." );			
			opt.addOption( OptionBuilder.withArgName( "file" )
                    .withDescription( "The graphml file containing the model of the test" )
                    .hasArg()
                    .withLongOpt( "graphml" )
                    .create( "g" ) );
			opt.addOption( OptionBuilder.withLongOpt( "length" )
					.withArgName( "=length" )
                    .withDescription( "The length of the test sequence. Supply an integer, which tells MBT how many " + 
                    		"vertices a test sequence shall contain." )
                    .withValueSeparator( '=' )
                    .hasArg()
                    .create( "l" ) );
			
			CommandLineParser parser = new PosixParser();
	        CommandLine cl = parser.parse( opt, args );
	        
            if ( cl.hasOption( "h" ) ) 
            {
                HelpFormatter f = new HelpFormatter();
                f.printHelp( "GenerateTests", opt );
            }
            else
            {
	            
	            if ( cl.hasOption( "r" ) && cl.hasOption( "o" ) )
	            {
	            	System.out.println( "Can not set --random and --optimize at the same time." );
	                HelpFormatter f = new HelpFormatter();
	                f.printHelp( "GenerateTests", opt );
	                return;
	            }

	            if ( cl.hasOption( "r" ) ) 
	            {
	            	random = true;
	            	if ( cl.hasOption( "l" ) )
	            	{
	            		length = Integer.valueOf( cl.getOptionValue( "l" ) ).longValue();
	            	}
	            	else
	            	{
		            	System.out.println( "When running in --random mode, the --length must also be set." );
		                HelpFormatter f = new HelpFormatter();
		                f.printHelp( "GenerateTests", opt );
		                return;
	            	}
	            }
	            
	            if ( cl.hasOption( "o" ) ) 
	            {
	            	random = false;
	            }

	            if ( !cl.hasOption( "g" ) )
	            {
	            	System.out.println( "Missing the graphml file, See --graphml or -g" );
	                HelpFormatter f = new HelpFormatter();
	                f.printHelp( "GenerateTests", opt );
	                return;	            	
	            }

            	graphmlFile = cl.getOptionValue( "g" );
            	
            	GenerateTests test = new GenerateTests();
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
			mbt.generateTests( random, length);
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
}
