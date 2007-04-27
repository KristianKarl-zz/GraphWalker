package org.tigris.mbt;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;



public class RunPerlScript 
{
	static private String graphmlFile;
	static private String perlScript;
	static private boolean random;
	static private long seconds;
	private String  LABEL_KEY = "label";
	
	public void run()
	{
		ModelBasedTesting mbt = new ModelBasedTesting( graphmlFile );	
		mbt.reset();
		
		while ( true )
		{
			DirectedSparseEdge edge = mbt.getEdgeAndVertex( random, seconds );

			// getEdgeAndVertex caught an exception, and returned null
			if ( edge == null )
			{
				break;
			}

			if ( run_Perl_Subrotine( "perl " + perlScript + " " + edge.getUserDatum( LABEL_KEY ) ) != 0 )
			{
				break;
			}
			if ( run_Perl_Subrotine( "perl " + perlScript + " " + edge.getDest().getUserDatum( LABEL_KEY ) ) != 0 )
			{
				break;
			}
		}
		
		System.out.println( mbt.getStatistics() );
	}
	
	public static void main(String[] args)
	{
		try 
		{
			Options opt = new Options();
			opt.addOption( "h", "help", false, "Print help for this application" );
			opt.addOption( "r", "random", false, "Run the test with a random walk. Can not be combined with --optimize. " + 
												 "This argument also needs the --time to be set." );
			opt.addOption( "o", "optimize", false, "Run the test optimized. Can not be combined with --random." );			
			opt.addOption( OptionBuilder.withArgName( "file" )
                    .withDescription( "The graphml file containing the model of the test" )
                    .hasArg()
                    .withLongOpt( "graphml" )
                    .create( "g" ) );
			opt.addOption( OptionBuilder.withArgName( "file" )
                    .withDescription( "The perl script implementing the model." )
                    .hasArg()
                    .withLongOpt( "perl" )
                    .create( "p" ) );
			opt.addOption( OptionBuilder.withLongOpt( "time" )
					.withArgName( "=seconds" )
                    .withDescription( "The time in seconds for the random walk to run." )
                    .withValueSeparator( '=' )
                    .hasArg()
                    .create( "t" ) );
			
			CommandLineParser parser = new PosixParser();
	        CommandLine cl = parser.parse( opt, args );
	        
            if ( cl.hasOption( "h" ) ) 
            {
                HelpFormatter f = new HelpFormatter();
                f.printHelp( "RunPerlScript", opt );
            }
            else
            {
	            
	            if ( cl.hasOption( "r" ) && cl.hasOption( "o" ) )
	            {
	            	System.out.println( "Can not set --random and --optimize at the same time." );
	                HelpFormatter f = new HelpFormatter();
	                f.printHelp( "RunPerlScript", opt );
	                return;
	            }

	            if ( cl.hasOption( "r" ) ) 
	            {
	            	random = true;
	            	if ( cl.hasOption( "t" ) )
	            	{
	            		seconds = Integer.valueOf( cl.getOptionValue( "t" ) ).longValue();
	            	}
	            	else
	            	{
		            	System.out.println( "When running in --random mode, the --time must also be set." );
		                HelpFormatter f = new HelpFormatter();
		                f.printHelp( "RunPerlScript", opt );
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
	                f.printHelp( "RunPerlScript", opt );
	                return;	            	
	            }

	            if ( !cl.hasOption( "p" ) )
	            {
	            	System.out.println( "Missing the perl script, See --perl or -p" );
	                HelpFormatter f = new HelpFormatter();
	                f.printHelp( "RunPerlScript", opt );
	                return;	            	
	            }

            	graphmlFile = cl.getOptionValue( "g" );
            	perlScript  = cl.getOptionValue( "p" );            	
            	
    			RunPerlScript test = new RunPerlScript();
    			test.run();
			}
        }
        catch ( ParseException e ) 
        {
            e.printStackTrace();
        }
 }
	
	
	/**
	 * This method implemets the edge: e_Initialize
	 */
	public int run_Perl_Subrotine( String command )
	{
		int result = 1;
		// prepare buffers for process output and error streams
		StringBuffer err=new StringBuffer();
		StringBuffer out=new StringBuffer();    
		try
		{
			Process proc=Runtime.getRuntime().exec(command);
			//create thread for reading inputStream (process' stdout)
			StreamReaderThread outThread=new StreamReaderThread(proc.getInputStream(),out);
			//create thread for reading errorStream (process' stderr)
			StreamReaderThread errThread=new StreamReaderThread(proc.getErrorStream(),err);
			//start both threads
			outThread.start();
			errThread.start();
			//wait for process to end
			result=proc.waitFor();
			//finish reading whatever's left in the buffers
			outThread.join();
			errThread.join();
			
			System.out.print(out.toString());
			
			/*if (result!=0) 
			 {
			 System.out.println("Process "+command+ " returned non-zero value:"+result);
			 System.out.println("Process output:\n"+out.toString());
			 System.out.println("Process error:\n"+err.toString());
			 }
			 else
			 {
			 System.out.println("Process "+command+ " executed successfully");
			 System.out.println("Process output:\n"+out.toString());
			 System.out.println("Process error:\n"+err.toString());
			 }*/
		}
		catch (Exception e)
		{
			System.out.println("Error executing "+command);
			e.printStackTrace();
		}
		return result;
	}
}
