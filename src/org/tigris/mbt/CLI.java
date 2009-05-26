package org.tigris.mbt;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.ws.Endpoint;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;

/**
 * Command Line Interface object, to the org.tigris.mbt package.
 * The object provides a way of working with MBT using a Windows DOS,
 * or a console window in *nix system. The CLI works like the cvs or the subversion
 * command svn. The syntax is:<br><pre>java -jar mbt.jar COMMAND <options></pre>
 * Where mbt.jar is the whole package org.tigris.mbt built using the tool Fat Jar
 * from http://fjep.sourceforge.net/ using org.tigris.mbt.CLI as the main class<br><br>
 * <strong>Example:</strong> Print help for mbt.jar<br>
 * <pre>java -jar mbt.jar help</pre><br>
 * <strong>Example:</strong> Merge graphml files and save the merged result.<br>
 * <pre>java -jar mbt.jar merge -f folder</pre><br>
 * <strong>Example:</strong> Generate offline test sequence, using random walk<br>
 * <pre>java -jar mbt.jar offline -f folder -g RANDOM -s EDGE_COVERAGE:30</pre><br>
 * <strong>Example:</strong> Generate online test sequence, using shortest walk<br>
 * <pre>java -jar mbt.jar online -f folder -g A_STAR -s EDGE_COVERAGE:100</pre><br>
 * <strong>Example:</strong> Print all names of edges and vertices (Sorted, and unique)<br>
 * <pre>java -jar mbt.jar methods -f folder</pre><br>
 * <strong>Example:</strong> When you need to define more complex abstract test cases working with the CLI can sometimes be a burden. For this reason we have added a easier way to structure the abstract test cases, using XML.<br>
 * <pre>java -jar mbt.jar xml -f testcase.xml</pre><br>
 *
 */
public class CLI
{
	public static class VerboseStatisticsLogger extends Thread
	{
		private ModelBasedTesting modelBasedTesting;
		
		VerboseStatisticsLogger( ModelBasedTesting modelBasedTesting )
		{
			this.modelBasedTesting = modelBasedTesting;
		}
		
		public void run() {

			logger.info( modelBasedTesting.getStatisticsVerbose() );
		}
	}

	static Logger logger = Util.setupLogger(CLI.class);
	private ModelBasedTesting mbt = null;
	private Timer timer = null;
	private Options opt = new Options();
	private static Endpoint endpoint = null;

	public CLI(){}
	
	ModelBasedTesting getMbt() 
	{
		if(mbt == null)
			mbt = ModelBasedTesting.getInstance();
		return mbt;
	}

	/**
	 * @param mbt the ModelBasedTesting to set
	 */
	private void setMbt(ModelBasedTesting mbt) {
		this.mbt = mbt;
	}

	public static void main(String[] args)
	{
		CLI cli = new CLI();
		Thread shutDownThread = new CLI.VerboseStatisticsLogger(cli.getMbt());
		try 
		{
			Runtime.getRuntime().addShutdownHook( shutDownThread );
		}
		catch ( IllegalArgumentException e )
		{
			logger.debug( "Could not register ShutdownHook for statistics logger, it has already been registered" );
		}
		try
		{
			cli.run( args );
			endpoint = cli.GetEndpoint();
		}
		catch ( Exception e )
		{
			StringWriter sw = new StringWriter();
		    PrintWriter pw = new PrintWriter( sw );
		    e.printStackTrace( pw );
		    pw.close();	    		    
			logger.error( sw.toString() );
    		System.err.println( e.getMessage() );
		}
		Runtime.getRuntime().removeShutdownHook( shutDownThread );
	}

	private void run(String[] args)
	{
		if ( args.length < 1 )
		{
			System.err.println( "Type 'java -jar mbt.jar help' for usage." );
			return;
		}
		else
		{
			if ( args[ 0 ].equals( "help" ) )
			{
				if ( args.length == 1 )
				{
					printGeneralHelpText();
				} else {
					printHelpText(args[ 1 ]);
				}
				return;
			}
			else if ( args[ 0 ].equals( "requirements" ) )
			{
				buildRequirementsCLI();
			}
			else if ( args[ 0 ].equals( "online" ) )
			{
				buildOnlineCLI();
			}
			else if ( args[ 0 ].equals( "offline" ) )
			{
				buildOfflineCLI();
			}
			else if ( args[ 0 ].equals( "methods" ) )
			{
				buildMethodsCLI();
			}
			else if ( args[ 0 ].equals( "merge" ) )
			{
				buildMergeCLI();
			}
			else if ( args[ 0 ].equals( "source" ) )
			{
				buildSourceCLI();
			}
			else if ( args[ 0 ].equals( "xml" ) )
			{
				buildXmlCLI();
			}
			else if ( args[ 0 ].equals( "soap" ) )
			{
				buildSoapCLI();
			}
			else if ( args[ 0 ].equals( "gui" ) )
			{
				buildGuiCLI();
			}
			else if ( args[ 0 ].equals( "-v" ) || args[ 0 ].equals( "--version" ) )
			{
				printVersionInformation();
				return;
			}
			else
			{
				System.err.println( "Unkown command: " + args[ 0 ] );
				System.err.println( "Type 'java -jar mbt.jar help' for usage." );
				return;				
			}
		}
			
		try 
		{			
			timer = new Timer();
			CommandLineParser parser = new PosixParser();
	        CommandLine cl = parser.parse( opt, args );
	        
			/**
			 *  Command: requirements
			 */
			if ( args[ 0 ].equals( "requirements" ) )
			{
				RunCommandRequirements( cl );
			}
			/**
			 *  Command: online
			 */
			if ( args[ 0 ].equals( "online" ) )
			{
				RunCommandOnline( cl );
			}
			/**
			 *  Command: offline
			 */
			else if ( args[ 0 ].equals( "offline" ) )
			{
				RunCommandOffline( cl );
			}
			/**
			 *  Command: methods
			 */
			else if ( args[ 0 ].equals( "methods" ) )
			{
				RunCommandMethods( cl );
			}
			/**
			 *  Command: merge
			 */
			else if ( args[ 0 ].equals( "merge" ) )
			{
				RunCommandMerge( cl );
			}
			/**
			 *  Command: source
			 */
			else if ( args[ 0 ].equals( "source" ) )
			{
				RunCommandSource( cl );
			}
			/**
			 *  Command: xml
			 */
			else if ( args[ 0 ].equals( "xml" ) )
			{
				RunCommandXml( cl );
			}
			/**
			 *  Command: soap
			 */
			else if ( args[ 0 ].equals( "soap" ) )
			{
				RunCommandSoap( cl );
			}
			/**
			 *  Command: gui
			 */
			else if ( args[ 0 ].equals( "gui" ) )
			{
				RunCommandGui( cl );
			}
        }		
		catch ( ArrayIndexOutOfBoundsException e )
        {
			System.err.println( "The arguments for either the generator, or the stop-condition, is incorrect." );
			System.err.println( "Example: java -jar mbt.jar offline -f ../demo/model/UC01.graphml -s EDGE_COVERAGE:100 -g A_STAR" );
        	System.err.println( "Type 'java -jar mbt.jar help " + args[ 0 ] + "' for help." );
        }
		catch ( Exception e )
        {
			StringWriter sw = new StringWriter();
		    PrintWriter pw = new PrintWriter( sw );
		    e.printStackTrace( pw );
		    pw.close();	    		    
			logger.debug( sw.toString() );
			System.err.println( e.getMessage() );
        	System.err.println( "Type 'java -jar mbt.jar help " + args[ 0 ] + "' for help." );
        }
        finally
        {
    		timer.cancel();
        }
	}

	private void printGeneralHelpText()
	{
		System.out.println( "usage: 'java -jar mbt.jar <COMMAND> [OPTION] [ARGUMENT]'\n" );
		System.out.println( "Type 'java -jar mbt.jar help <COMMAND>' to get specific help about a command." );
		System.out.println( "Valid commands are:" );
		System.out.println( "    help" );
		System.out.println( "    online" );
		System.out.println( "    offline" );
		System.out.println( "    requirements" );
		System.out.println( "    methods" );
		System.out.println( "    merge" );
		System.out.println( "    xml" );
		System.out.println( "    soap" );
		System.out.println( "    gui" );
		System.out.println( "    source\n" );
		System.out.println( "Type 'java -jar mbt.jar -v (--version)' for version information." );
	}
	
	private void printHelpText(String helpSection)
	{
		String header = "";
		if(helpSection.equalsIgnoreCase("online")) {
			buildOnlineCLI();
			header = "Run the test online.\n" +			
					"MBT will return a test sequence, one line at a time to standard output, " +
					"it will wait until a line is fed back via standard input. The data fed back can be:\n" +
					"  '0' which means, continue the test as normal\n" +
					"  '1' which means go back to previous vertex (backtracking)\n" +
					"  '2' will end the test normally\n" +
					"anything else will abort the execution.\n" ;					
		} else if(helpSection.equalsIgnoreCase("requirements")){
			buildRequirementsCLI();
			header = "Print a list of requiremnts found in the model.\n" ;					
		} else if(helpSection.equalsIgnoreCase("offline")){
			buildOfflineCLI();
			header = "Generate a test sequence offline. The sequence is printed to the standard output\n" ;					
		} else if(helpSection.equalsIgnoreCase("methods")){
			buildMethodsCLI();
			header = "Generate all methods, or tests existing in the model.\n" +			
								"MBT will parse the graph(s), and return all methods (or tests) that" +
								" exists in the graph(s). The list will onyl print out a unique name once." +
								" The list is printed to stdout.\n" ;					
		} else if(helpSection.equalsIgnoreCase("merge")){
			buildMergeCLI();
			header = "Merge several graphml files into one single graphml file.\n" +			
								"The files to be merged, shall all exist in a single folder.\n" ;					
		} else if(helpSection.equalsIgnoreCase("source")){
			buildSourceCLI();
			header = "Generate code from a template.\n" +			
								"Will generate code using a template. The code generated will contain all lables/names " +
								"defined by the vertices and edges. This enables the user to write templates for a " +
								"multitude of scripting or programming languages. " +
								"The result will be printed to stdout. " +
								"There is 1 variable in the template, that will be replaced: {LABEL} ->Will be replace " +
								"by the actual name of the edge or vertex." ;
		} else if(helpSection.equalsIgnoreCase("xml")){
			buildXmlCLI();
			header = "Setup mbt engine from xml.\n" +
					"Will setup and execute an engine based on xml specified criterias.";
		} else if(helpSection.equalsIgnoreCase("soap")){
			buildSoapCLI();
			header = "Run MBT as a Web Services (SOAP) server.\n" +
					"To see the services, see the WSDL file at: http://localhost:8080/mbt-services?WSDL";
		} else if(helpSection.equalsIgnoreCase("gui")){
			buildGuiCLI();
			header = "Run MBT in a GUI mode.\n" +
					"All other commands are available in this mode.";
		} else {
			System.err.println( "Type 'java -jar mbt.jar help' for usage." );
			return;
		}

		HelpFormatter f = new HelpFormatter();
        f.printHelp( "java -jar mbt.jar "+ helpSection.toLowerCase(), header, opt , "", true);
	}
	
	private void buildRequirementsCLI()
	{
		opt.addOption( OptionBuilder.withArgName( "file" )
                .withDescription( "The file (or folder) containing graphml formatted files." )
                .hasArg()
                .withLongOpt( "input_graphml" )
                .create( "f" ) );
	}

	private String generateListOfValidStopConditions()
	{
		String list = "";
		Set<String> stopConditions = Keywords.getStopConditions();
		for (Iterator<String> i = stopConditions.iterator(); i.hasNext();) {
			list += i.next() + "\n";
		}
		return list;
	}
	
	private String generateListOfValidGenerators()
	{
		String list = "";
		Set<String> generators = Keywords.getGenerators();
		for (Iterator<String> i = generators.iterator(); i.hasNext();) {
			list += i.next() + "\n";
		}
		return list;
	}
	
	private void buildOnlineCLI()
	{
		opt.addOption( "a", "statistics", false, "Prints the statistics of the test, at the end of the run." );
		opt.addOption( "x", "extended",   false, "Use an extended finite state machine to handle the model." );
		opt.addOption( "b", "backtrack",  false, "Enable backtracking in the model." );
		opt.addOption( OptionBuilder.withArgName( "stop-condition" )
                .withDescription( "Stop condition. Halts generation after the specified stop-conditon(s) has been met. " +
		                          "At least 1 condition can be given. If more are given, the condition that meets" +
		                          "it's stop-condition first, will cause the generation to halt.\n" +
		                  		"Each stop condition must be followed with a : and then an integer between 1-100, representing the" + 
		                		" percentage that the stop-condition should reach. Every stop-condition is seperated with a |. " +
		                		"A list of valid stop-conditions are:\n" + generateListOfValidStopConditions() )
                .hasArg()
                .create( "s" ) );
		opt.addOption( OptionBuilder.withArgName( "generator" )
                .withDescription( "The generator to be used when traversing the model. At least 1 generator must be given. " +
                		"Every generator is seperated with a |.\nA list of valid generators are:\n" + generateListOfValidGenerators() )
                .hasArg()
                .create( "g" ) );
		opt.addOption( OptionBuilder.withArgName( "file" )
                .withDescription( "The file (or folder) containing graphml formatted files." )
                .hasArg()
                .withLongOpt( "input_graphml" )
                .create( "f" ) );
		opt.addOption( OptionBuilder.withArgName( "log-coverage" )
				.withDescription( "Prints the test coverage of the graph during execution every " +
			              "<n>. The printout goes to the log file defined in " +
			              "mbt.properties, and only, if at least INFO level is set in " +
			              "that same file." )
                .hasArg()
                .withLongOpt( "log-coverage" )
                .create( "o" ) );
		opt.addOption( "c", "class_name", true, "Optional class name to use for test execution." );
		opt.addOption( "t", "report-template", true, "Optional report template to use." );
		opt.addOption( "r", "report-output", true, "Optional report filename to save report to." );
		opt.addOption( "w", "weighted", false, "Use weighted values if they exists in the model, and the generator is RANDOM." );
		opt.addOption( "d", "dry-run", false, "Will execute a dry-run of the model. Dialog will pop up for every edge and vertex." );
	}

	/**
	 * Build the command offline command line parser
	 */
	private void buildOfflineCLI()
	{
		opt.addOption( "a", false, "Prints the statistics of the test, at the end of the run." );
		opt.addOption( "x", false, "Use an extended finite state machine to handle the model." );
		opt.addOption( OptionBuilder.withArgName( "stop-condition" )
                .withDescription( "Stop condition. Halts generation after the specified stop-conditon(s) has been met. " +
		                          "At least 1 condition can be given. If more are given, the condition that meets" +
		                          "it's stop-condition first, will cause the generation to halt.\n" +
		                  		"Each stop condition must be followed with a : and then an integer between 1-100, representing the" + 
		                		" percentage that the stop-condition should reach. Every stop-condition is seperated with a |. " +
		                		"A list of valid stop-conditions are:\n" + generateListOfValidStopConditions() )
                .hasArg()
                .create( "s" ) );
		opt.addOption( OptionBuilder.withArgName( "generator" )
                .withDescription( "The generator to be used when traversing the model. At least 1 generator must be given. " +
                		"Every generator is seperated with a |.\nA list of valid generators are:\n" + generateListOfValidGenerators() )
                .hasArg()
                .create( "g" ) );
		opt.addOption( OptionBuilder.withArgName( "file" )
                .withDescription( "The file (or folder) containing graphml formatted files." )
                .hasArg()
                .create( "f" ) );
		opt.addOption( OptionBuilder.withArgName( "log-coverage" )
				.withDescription( "Prints the test coverage of the graph during execution every " +
			              "<n>. The printout goes to the log file defined in " +
			              "mbt.properties, and only, if at least INFO level is set in " +
			              "that same file." )
                .hasArg()
                .create( "o" ) );
		opt.addOption( "t", "report-template", true, "Optional report template to use." );
		opt.addOption( "r", "report-output", true, "Optional report filename to save report to." );
		opt.addOption( "w", "weighted", false, "Use weighted values if they exists in the model, and the generator is RANDOM." );
	}
	
	private void buildMethodsCLI()
	{
		opt.addOption( OptionBuilder.withArgName( "file" )
                .withDescription( "The file (or folder) containing graphml formatted files." )
                .hasArg()
                .withLongOpt( "input_graphml" )
                .create( "f" ) );
	}
	
	/**
	 * Build the command merge command line parser
	 */
	private void buildMergeCLI()
	{
		opt.addOption( OptionBuilder.withArgName( "file" )
                .withDescription( "The file (or folder) containing graphml formatted files." )
                .hasArg()
                .withLongOpt( "input_graphml" )
                .create( "f" ) );
	}
	
	/**
	 * Build the command source command line parser
	 */
	private void buildSourceCLI()
	{
		opt.addOption( OptionBuilder.withArgName( "file" )
                .withDescription( "The file (or folder) containing graphml formatted files." )
                .hasArg()
                .withLongOpt( "input_graphml" )
                .create( "f" ) );
		opt.addOption( OptionBuilder.withArgName( "file" )
                .withDescription( "The template file" )
                .hasArg()
                .withLongOpt( "template" )
                .create( "t" ) );
	}

	/**
	 * Build the command xml command line parser
	 */
	private void buildXmlCLI() {
		opt.addOption( "a", false, "Prints the statistics of the test, at the end of the run." );
		opt.addOption( OptionBuilder.withArgName( "file" )
                .withDescription( "The xml file containing the mbt settings." )
                .hasArg()
                .create( "f" ) );
		opt.addOption( OptionBuilder.withArgName( "log-coverage" )
				.withDescription( "Prints the test coverage of the graph during execution every " +
			              "<n>. The printout goes to the log file defined in " +
			              "mbt.properties, and only, if at least INFO level is set in " +
			              "that same file." )
                .hasArg()
                .create( "o" ) );
		opt.addOption( "d", "dry-run", false, "Will execute a dry-run of the model. Dialog will pop up for every edge and vertex." );
	}
	
	/**
	 * Build the command soap command line parser
	 */
	private void buildSoapCLI() {
		opt.addOption( OptionBuilder.withArgName( "file" )
                .withDescription( "The xml file containing the mbt model and settings." )
                .hasArg()
                .create( "f" ) );
		opt.addOption( OptionBuilder.withArgName( "interface" )
                .withDescription( "The network interface to which mbt should bind to. " +
                		"If not given, the default is 0.0.0.0" )
                .hasArg()
                .create( "i" ) );
		opt.addOption( OptionBuilder.withArgName( "port" )
                .withDescription( "The port to which mbt should listen to. " +
                		"If not given, the default is 9090" )
                .hasArg()
                .create( "p" ) );
	}
	
	/**
	 * Build the command gui command line parser
	 */
	private void buildGuiCLI() {
	}
	
	/**
	 * Print version information
	 */
	private void printVersionInformation()
	{
		System.out.println( "org.tigris.mbt version 2.2 (revision 663) Beta 4\n" );
		System.out.println( "org.tigris.mbt is open source software licensed under GPL" );
		System.out.println( "The software (and it's source) can be downloaded from http://mbt.tigris.org/\n" );
		System.out.println( "This package contains following software packages:" );
		System.out.println( "  crimson-1.1.3.jar              http://xml.apache.org/crimson/" );
		System.out.println( "  commons-collections-3.2.1.jar  http://jakarta.apache.org/commons/collections/" );
		System.out.println( "  jdom-1.0.jar                   http://www.jdom.org/" );
		System.out.println( "  log4j-1.2.15.jar               http://logging.apache.org/log4j/" );
		System.out.println( "  commons-cli-1.1.jar            http://commons.apache.org/cli/" );
		System.out.println( "  colt-1.2.jar                   http://dsd.lbl.gov/~hoschek/colt/" );
		System.out.println( "  jung-XXX-2.0.jar                 http://jung.sourceforge.net/" );
		System.out.println( "  bsh-2.0b4.jar                  http://www.beanshell.org/" );
		System.out.println( "  commons-configuration-1.5.jar  http://commons.apache.org/configuration/" );
		System.out.println( "  commons-lang-2.4.jar           http://commons.apache.org/lang/" );
		System.out.println( "  commons-logging-1.1.1.jar      http://commons.apache.org/logging/" );
	}
	
	/**
	 * Run the offline command
	 */
	private void RunCommandOffline( CommandLine cl )
	{
		/**
		 * Get the model from the graphml file (or folder)
		 */
		if( helpNeeded("offline", !cl.hasOption( "f" ), "Missing the input graphml file (folder), See -f (--input_graphml)") || 
			helpNeeded("offline", !cl.hasOption( "s" ), "A stop condition must be supplied, See option -s") || 
			helpNeeded("offline", cl.hasOption( "t" ) && !cl.hasOption( "r" ), "A report output file must be set, See -t, when using a report template") || 
			helpNeeded("offline", !cl.hasOption( "t" ) && cl.hasOption( "r" ), "A report template must be set, See -r, when using a report output file") || 
			helpNeeded("offline", !cl.hasOption( "g" ), "Missing the generator, See option -g") ) 
			return;

        getMbt().readGraph( cl.getOptionValue( "f" ) );
        getMbt().enableExtended( cl.hasOption( "x" ) );
		getMbt().setWeighted( cl.hasOption( "w" ) );

        /*
         * Set the stop-conditions(s)
         */
		String[] stopConditions = cl.getOptionValue( "s" ).split( "\\|" );
		for (int i = 0; i < stopConditions.length; i++) {
			String[] sc = stopConditions[ i ].trim().split( ":" );
			getMbt().addAlternativeCondition( Keywords.getStopCondition( 
				sc[ 0 ].trim() ), // Stop condition 
				(sc.length==1?"":sc[ 1 ].trim()) ); // Optional condition parameter					
		}

        /*
         * Set the generators(s)
         */
		String[] genrators = cl.getOptionValue( "g" ).split( "\\|" );
		for (int i = 0; i < genrators.length; i++) {
			getMbt().setGenerator( Keywords.getGenerator( genrators[ 0 ].trim() ) );
		}
		
		if( cl.hasOption( "o" ) )
		{
			long seconds = Integer.valueOf( cl.getOptionValue( "o" ) ).longValue();

			logger.info( "Append coverage to log every: " + seconds + " seconds" );

			TimerTask logTask;
			if(cl.hasOption( "t" ) && cl.hasOption( "r" ))
			{
				getMbt().getStatisticsManager().setReportTemplate(cl.getOptionValue('t'));
				final String reportName = cl.getOptionValue('r');
				logTask = new TimerTask()	
				{
					public void run() 
					{
						try {
							getMbt().getStatisticsManager().writeFullReport(new PrintStream(reportName));
						} catch (FileNotFoundException e) {
							throw new RuntimeException("Could not open or write report file '"+reportName+"'", e);
						}
					}
				};
			} else {
				logTask = new TimerTask()	
				{
					public void run() 
					{
						logger.info( getMbt().getStatisticsCompact() );
					}
				};
			}
			timer = new Timer();
			timer.schedule(	logTask, 500, seconds * 1000 );
		}

		getMbt().writePath();
		
		if( cl.hasOption( "a" ) )
		{
			System.out.println( getMbt().getStatisticsString() );
		}
		if( cl.hasOption( "o" ) )
		{
			logger.info( mbt.getStatisticsCompact() );
		}
		if( cl.hasOption( "t" ) && cl.hasOption( "r" ) )
		{
			getMbt().getStatisticsManager().setReportTemplate(cl.getOptionValue('t'));
			getMbt().getStatisticsManager().writeFullReport(cl.getOptionValue('r'));
		}

	}

	/**
	 * Run the online command
	 */
	private void RunCommandOnline( CommandLine cl )
	{
		/**
		 * Get the model from the graphml file (or folder)
		 */
		if( helpNeeded("online", !cl.hasOption( "f" ), "Missing the input graphml file (folder), See -f (--input_graphml)") || 
				helpNeeded("online", !cl.hasOption( "s" ), "A stop condition must be supplied, See option -s") || 
				helpNeeded("online", cl.hasOption( "t" ) && !cl.hasOption( "r" ), "A report output file must be set, See -t, when using a report template") || 
				helpNeeded("online", !cl.hasOption( "t" ) && cl.hasOption( "r" ), "A report template must be set, See -r, when using a report output file") || 
				helpNeeded("online", !cl.hasOption( "g" ), "Missing the generator, See option -g") ) 
				return;

        getMbt().readGraph( cl.getOptionValue( "f" ) );
        getMbt().enableExtended( cl.hasOption( "x" ) );
		getMbt().setWeighted( cl.hasOption( "w" ) );

        /*
         * Set the stop-conditions(s)
         */
		String[] stopConditions = cl.getOptionValue( "s" ).split( "\\|" );
		for (int i = 0; i < stopConditions.length; i++) {
			String[] stopCondition = stopConditions[ i ].trim().split( ":" );
			if ( stopCondition.length == 1 )
			{
				getMbt().addAlternativeCondition( Keywords.getStopCondition( stopCondition[ 0 ].trim() ), "" );					
			}
			else
			{
				getMbt().addAlternativeCondition( Keywords.getStopCondition( stopCondition[ 0 ].trim() ), stopCondition[ 1 ].trim() );
			}
		}

        /*
         * Set the generators(s)
         */
		String[] genrators = cl.getOptionValue( "g" ).split( "\\|" );
		for (int i = 0; i < genrators.length; i++) {
			getMbt().setGenerator( Keywords.getGenerator( genrators[ 0 ].trim() ) );
		}
        
        /**
		 * Set backtracking
		 */
        getMbt().enableBacktrack( cl.hasOption( "b" ) );

        /**
		 * Set dry-run
		 */
        getMbt().setDryRun( cl.hasOption( "d" ) );

		if( cl.hasOption( "o" ) )
		{
			long seconds = Integer.valueOf( cl.getOptionValue( "o" ) ).longValue();

			logger.info( "Append coverage to log every: " + seconds + " seconds" );

			TimerTask logTask;
			if(cl.hasOption( "t" ) && cl.hasOption( "r" ))
			{
				getMbt().getStatisticsManager().setReportTemplate(cl.getOptionValue('t'));
				final String reportName = cl.getOptionValue('r');
				logTask = new TimerTask()	
				{
					public void run() 
					{
						try {
							getMbt().getStatisticsManager().writeFullReport(new PrintStream(reportName));
						} catch (FileNotFoundException e) {
							throw new RuntimeException("Could not open or write report file '"+reportName+"'", e);
						}
					}
				};
			} else {
				logTask = new TimerTask()	
				{
					public void run() 
					{
						logger.info( getMbt().getStatisticsCompact() );
					}
				};
			}
			timer = new Timer();
			timer.schedule(	logTask, 500, seconds * 1000 );
		}
	
		if( cl.hasOption( "c" ) )
		{
			getMbt().executePath( cl.getOptionValue( "c" ) );
		}
		else if( cl.hasOption( "d" ) )
		{
			getMbt().executePath( (String)null );
		}
		else
		{
			getMbt().interractivePath();
		}

		if( cl.hasOption( "a" ) )
		{
			writeStatisticsVerbose(System.out);
		}
		if( cl.hasOption( "o" ) )
		{
			logger.info( mbt.getStatisticsCompact() );
		}
		if( cl.hasOption( "t" ) && cl.hasOption( "r" ) )
		{
			getMbt().getStatisticsManager().setReportTemplate(cl.getOptionValue('t'));
			getMbt().getStatisticsManager().writeFullReport(cl.getOptionValue('r'));
		}
		
	}

	private void writeStatisticsVerbose(PrintStream out) {
		out.println( getMbt().getStatisticsString() );
	}

	/**
	 * Run the source command
	 */
	private void RunCommandSource( CommandLine cl )
	{
		if( helpNeeded("source", !cl.hasOption( "f" ), "Missing the input graphml file (folder), See -f (--input_graphml)") || 
			helpNeeded("source", !cl.hasOption( "t" ), "Missing the template file. See -t (--template)") ) 
			return;

		getMbt().readGraph( cl.getOptionValue( "f" ) );
		getMbt().setTemplate( cl.getOptionValue( "t" ) );
		getMbt().setGenerator( Keywords.GENERATOR_STUB );
		getMbt().writePath(System.out);
	}

	/**
	 * Run the requirements command
	 */
	private void RunCommandRequirements( CommandLine cl )
	{
		if ( helpNeeded("requirements", !cl.hasOption( "f" ), 
				"Missing the input graphml file (folder), See -f (--input_graphml)") ) 
			return;

		getMbt().readGraph( cl.getOptionValue( "f" ) );
		getMbt().setGenerator( Keywords.GENERATOR_REQUIREMENTS );
		getMbt().writePath(System.out);
	}

	/**
	 * Run the methods command
	 */
	private void RunCommandMethods( CommandLine cl )
	{
		if ( helpNeeded("methods", !cl.hasOption( "f" ), 
				"Missing the input graphml file (folder), See -f (--input_graphml)") ) 
			return;

		getMbt().readGraph( cl.getOptionValue( "f" ) );
		getMbt().setTemplate(new String[]{"","{LABEL}",""});
		getMbt().setGenerator( Keywords.GENERATOR_STUB );
		getMbt().writePath(System.out);
	}

	/**
	 * Run the merge command
	 */
	private void RunCommandMerge( CommandLine cl )
	{
		if ( helpNeeded("merge", !cl.hasOption( "f" ), 
				"Missing the input graphml file (folder), See -f (--input_graphml)") ) 
			return;
		
		getMbt().readGraph( cl.getOptionValue( "f" ) );
		getMbt().writeModel( System.out );
	}
	
	/**
	 * Run the xml command
	 */
	private void RunCommandXml(CommandLine cl) {
		if( helpNeeded("xml", !cl.hasOption( "f" ), "Missing the input xml file, See  option -f") ) 
			return;

        setMbt( Util.loadMbtFromXml( cl.getOptionValue( "f" ), cl.hasOption( "d" ) ) );
		
		if( cl.hasOption( "a" ) )
		{
			logger.info( getMbt().getStatisticsString() );
			writeStatisticsVerbose( System.out );
		}
	}

	/**
	 * Run the soap command
	 * @throws UnknownHostException 
	 */
	private void RunCommandSoap(CommandLine cl) throws UnknownHostException  {
		String port = null;
		String nicAddr = null;
		if( cl.hasOption( "p" ) ) {
			port = cl.getOptionValue( "p" );
		}
		else {
			port =  Util.readWSPort();
		}
		
		if( cl.hasOption( "i" ) ) {
			nicAddr = Util.getInternetAddr( cl.getOptionValue( "i" ) ).getCanonicalHostName();
		}
		else {
			nicAddr = "0.0.0.0";
		}
				
		String wsURL = "http://" + nicAddr + ":" + port + "/mbt-services";
		endpoint = Endpoint.publish( wsURL, new SoapServices( cl.getOptionValue( "f" ) ) );

		if ( nicAddr.equals( "0.0.0.0" ) ) {
			wsURL = wsURL.replace( "0.0.0.0", InetAddress.getLocalHost().getHostName() );
		}
		
		System.out.println( "Now running as a SOAP server. For the WSDL file, see: " + wsURL + "?WSDL" );
		System.out.println( "Press Ctrl+C to quit" );
		System.out.println( "" );
	}

	/**
	 * Run the gui command
	 * @throws IOException 
	 */
	private void RunCommandGui(CommandLine cl) throws IOException {
		new org.tigris.mbt.GUI.mbt();
	}

	private boolean helpNeeded(String module, boolean condition, String message)
	{
		if(condition)
		{
			System.out.println(message);
			System.out.println( "Type 'java -jar mbt.jar help "+module+"' for help." );
		}
		return condition;
	}
	
	public Endpoint GetEndpoint()
	{
		return endpoint;
	}
	
	public void StopSOAP()
	{
		if ( endpoint != null )
		{
		    logger.debug("Stopping the SOAP service");
			endpoint.stop();
		}
	}
}