package org.tigris.mbt;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import jcmdline.BooleanParam;
import jcmdline.CmdLineHandler;
import jcmdline.FileParam;
import jcmdline.HelpCmdLineHandler;
import jcmdline.IntParam;
import jcmdline.Parameter;
import jcmdline.StringParam;
import jcmdline.VersionCmdLineHandler;

/**
 * Command Line Interface object, to the org.tigris.mbt package.
 * The object provides a way of working with MBT using a Windows DOS,
 * or a console window in *nix system. The CLI works like the cvs or the subversion
 * command svn. The syntax is:<br><strong>java -jar mbt.jar COMMAND <options></strong><br>
 * Where mbt.jar is the whole package org.tigris.mbt built using the tool Fat Jar
 * from http://fjep.sourceforge.net/ using org.tigris.mbt.CLI as the main class<br><br>
 * <strong>Example: Print help for mbt.jar</strong><br>
 * java -jar mbt.jar help<br><br>
 * <strong>Example: Merge graphml files and save the merged result.</strong><br>
 * java -jar mbt.jar merge -g folder -l result.graphml<br><br>
 * <strong>Example: Generate offline test sequence, using random walk</strong><br>
 * java -jar mbt.jar offline -r -g folder<br><br>
 * <strong>Example: Generate online test sequence, using random walk</strong><br>
 * java -jar mbt.jar online -r -g folder<br><br>
 * <strong>Example: Print all names of edges and vertices (Sorted, and unique)</strong><br>
 * java -jar mbt.jar methods -g folder<br><br>
 *
 */
public class CLI 
{
	static Logger logger = Logger.getLogger(CLI.class);

	private static String cmdLineSyntax = "java -jar mbt.jar";
	private static String version = "2.0";
	private ModelBasedTesting mbt = new ModelBasedTesting();

	private Timer t = new Timer();
	
	public CLI()
	{
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				logger.info( mbt.getStatisticsVerbose() );
			}
		});
	}
	
	public static void main(String[] args)
	{
		try
		{
			CLI cli = new CLI();
			cli.run( args );
		}
		catch(RuntimeException e)
		{
			logger.fatal(e.getMessage());
			System.exit(-1);
		}

	}

	public void run(String[] args)
	{
        FileParam modelParam =
            new FileParam("model",
                          "the GraphML model or directory of GraphML files to be processed",
                          ( FileParam.IS_FILE | FileParam.IS_DIR ) & FileParam.IS_READABLE,
                          FileParam.REQUIRED,
                          FileParam.MULTI_VALUED);
        StringParam generatorParam =
        	new StringParam("generator",
        					"Possible generators are:\n"+
        					"  random    Use a randomized path algorithm\n"+
        					"  dijkstra  Use Dijkstras shortest path algorithm\n"+
        					"  list      Generate all unique available Edge and State names\n"+
        					"  stub      Generate code stubs using a template.\n",
        					new String[]{"random", "dijkstra", "list", "stub"},
        					StringParam.REQUIRED);
        
        FileParam mergeOpt =
            new FileParam("output-graphml",
            		"Merge all loaded graphml files into one single graphml file",
                          FileParam.DOESNT_EXIST | FileParam.IS_WRITEABLE,
                          FileParam.OPTIONAL,
                          FileParam.SINGLE_VALUED);
        
        FileParam outputOpt =
            new FileParam("output-file",
            		"Save generated content to a file, insead of to stdout.",
                          FileParam.DOESNT_EXIST | FileParam.IS_WRITEABLE,
                          FileParam.OPTIONAL,
                          FileParam.SINGLE_VALUED);

        // command line options
        FileParam templateOpt =
            new FileParam("template",
            		"Template file to use when generating stub functions.\n" +
            		"The code generated will contain all lables/names defined " +
            		"by the vertices and edges. This enables the user to write " +
            		"templates for a multitude of scripting or programming languages. " +
            		"Any occurance of {LABEL} in the template will be replaced " +
            		"by the actual name of the edge or vertex.",
                          FileParam.EXISTS & FileParam.IS_FILE & FileParam.IS_READABLE,
                          FileParam.OPTIONAL,
                          FileParam.SINGLE_VALUED);

        
        BooleanParam statisticsOpt = 
            new BooleanParam("statistics", "Display coverage statistics at the end of the run.");
        BooleanParam backtrackOpt = 
            new BooleanParam("backtrack", "Enable backtracking in the model.");
        BooleanParam extendedOpt = 
            new BooleanParam("extended", "Use an extended finite state machine to handle the model.");
        BooleanParam onlineOpt = 
            new BooleanParam("online", "Run the test interactively. The test sequence will be generated " + 
            		"two lines at a time (transaction and verification) and then wait until a response is fed back via standard input. " +
            		"The data fed back can be:\n" +
					"  '0' which means, continue the test as normal\n" +
					"  '1' which means go back to previous vertex (backtracking)\n" +
					"  '2' will end the test normally\n" +
					"anything else will be ignored.");
        
        StringParam reachedEdgeConditionOpt =
        	new StringParam("end-edge", "Halts generation after the specified edge has been traversed.");
        StringParam reachedStateConditionOpt =
        	new StringParam("end-state", "Halts generation after the specified state has been reached.");
        IntParam edgeCoverageConditionOpt =
        	new IntParam("end-edge-coverage", "Halts generation after the specified edge-coverage has been met.",0,100);
        IntParam stateCoverageConditionOpt =
        	new IntParam("end-state-coverage", "Halts generation after the specified state-coverage has been met.",0,100);
        IntParam testLengthConditionOpt =
        	new IntParam("end-length", "Halts generation after the specified script length has been reached.",0,Integer.MAX_VALUE);
        IntParam testDurationConditionOpt =
        	new IntParam("end-duration", "Halts generation after the specified duration in seconds has been reached.",0,Integer.MAX_VALUE);
        IntParam logCoverageOpt =
        	new IntParam("log-coverage", "Prints the test coverage of the graph during execution every <n>. " +
        			"The printout goes to the log file defined in mbt.properties, " + 
        			"and only, if at least INFO level is set in that same file.",0,Integer.MAX_VALUE );

        CmdLineHandler commandLine =
            new VersionCmdLineHandler(version,
            new HelpCmdLineHandler(
            	"This program prints to stdout an abstract script generated " + 
  				"from an abstract finite state model model.\n\n" +

  				// TODO Insert more CLI usage text and add examples.
  				"<INSERT MORE USAGE TEXT AND EXAMPLES HERE>\n" +
  				"<INSERT MORE USAGE TEXT AND EXAMPLES HERE>\n" +
  				"<INSERT MORE USAGE TEXT AND EXAMPLES HERE>\n\n" +

  				"org.tigris.mbt is open source software licensed under GPL\n" +
  				"The software (and it's source) can be downloaded at http://mbt.tigris.org/\n\n" +
  				"This package contains following software packages:\n" +
  				"  crimson-1.1.3.jar            http://xml.apache.org/crimson/\n" +
  				"  commons-collections-3.1.jar  http://jakarta.apache.org/commons/collections/\n" +
  				"  jdom-1.0.jar                 http://www.jdom.org/\n" +
  				"  log4j-1.2.8.jar              http://logging.apache.org/log4j/\n" +
  				"  commons-cli-1.0.jar          http://jakarta.apache.org/commons/cli/\n" +
  				"  colt-1.2.jar                 http://dsd.lbl.gov/~hoschek/colt/\n" +
  				"  jung-1.7.6.jar               http://jung.sourceforge.net/\n" +
  				"  bsh-core-2.0b4.jar           http://www.beanshell.org/\n",
                cmdLineSyntax,
                "Generate test sequences from an abstract model.",
                new Parameter[] { 
            		reachedEdgeConditionOpt, 
            		reachedStateConditionOpt, 
            		edgeCoverageConditionOpt, 
            		stateCoverageConditionOpt,  
            		testLengthConditionOpt, 
            		testDurationConditionOpt, 
            		logCoverageOpt, statisticsOpt, 
            		templateOpt, outputOpt, extendedOpt, 
            		onlineOpt, backtrackOpt, mergeOpt},
                new Parameter[] { generatorParam, modelParam } ));
        
        commandLine.parse(args);
        
        for(Iterator i = modelParam.getValues().iterator();i.hasNext();)
        {
        	String fileName = (String) i.next();
        	mbt.readGraph(fileName);
        }

		logger.info("Setting extended: " + extendedOpt.isTrue());
        mbt.enableExtended(extendedOpt.isTrue());
       
		logger.info("Adding stop conditions.");
		if(reachedEdgeConditionOpt.isSet())
		{
			mbt.addCondition(Keywords.CONDITION_REACHED_EDGE, reachedEdgeConditionOpt.getValue()); 
		}
		if(reachedStateConditionOpt.isSet())
		{
			mbt.addCondition(Keywords.CONDITION_REACHED_STATE, reachedStateConditionOpt.getValue()); 
		}
		if(edgeCoverageConditionOpt.isSet())
		{
			mbt.addCondition(Keywords.CONDITION_EDGE_COVERAGE, edgeCoverageConditionOpt.getValue()); 
		}
		if(stateCoverageConditionOpt.isSet())
		{
			mbt.addCondition(Keywords.CONDITION_STATE_COVERAGE, stateCoverageConditionOpt.getValue()); 
		}
		if(testLengthConditionOpt.isSet())
		{
			mbt.addCondition(Keywords.CONDITION_TEST_LENGTH, testLengthConditionOpt.getValue()); 
		}
		if(testDurationConditionOpt.isSet())
		{
			mbt.addCondition(Keywords.CONDITION_TEST_DURATION, testDurationConditionOpt.getValue()); 
		}
		
		String generator = generatorParam.getValue();

		logger.info("Setting generator: " + generator);
		if(generator.equals("list"))
		{
			mbt.setGenerator(Keywords.GENERATOR_LIST);
		}
		else if(generator.equals("random")) 
		{
			mbt.setGenerator(Keywords.GENERATOR_RANDOM);
		}
		else if(generator.equals("dijkstra")) 
		{
			mbt.setGenerator(Keywords.GENERATOR_SHORTEST);
		}
		else if(generator.equals("stub")) 
		{
			mbt.setGenerator(Keywords.GENERATOR_STUB);
		}

		if(templateOpt.isSet())
		{
			logger.info("Set code template: " + templateOpt.getValue());
			mbt.setTemplate(templateOpt.getValue());
		}

		PrintStream oldSystemOut = null;
		if(outputOpt.isSet())
		{
			logger.info("Diverting System.out to file: " + outputOpt.getValue());
			oldSystemOut = System.out;
			try {
				System.setOut( new PrintStream(new FileOutputStream(outputOpt.getValue())));
			} catch (FileNotFoundException e) {
				throw new RuntimeException( "File output error: " + e.getMessage() );
			}
		}
		logger.info("Allow backtracking: " + backtrackOpt.isTrue());
		mbt.enableBacktrack(backtrackOpt.isTrue());
		
		if(logCoverageOpt.isSet())
		{
			logger.info("Append coverage to log every: "+ logCoverageOpt.getValue() +" seconds");
			t.schedule(	new TimerTask()	{
				public void run() {
					logger.info(mbt.getStatisticsCompact());
				}
			}, 500, logCoverageOpt.intValue() * 1000);
		}
		
		if(mergeOpt.isSet())
		{
			logger.info("Write merged model to: "+ mergeOpt.getValue());
			mbt.writeModel(mergeOpt.getValue());
		}
		
		if(onlineOpt.isTrue())
		{
			logger.info("Use interactive/online mode");
			while(mbt.hasNextStep())
			{
				char input = getInput(); 
				logger.debug("Recieved: '"+ input+"'");
				if(input == '2')
				{
					break;
				}
				if(input == '1')
				{
					mbt.backtrack();
				}
				String[] stepPair = mbt.getNextStep();
				System.out.println(stepPair[0]);
				logger.debug("Execute: " + stepPair[0]);
				System.out.println(stepPair[1]);
				logger.debug("Verify: " + stepPair[1]);
			}
		}
		else
		{
			logger.info("Use offline mode");
			while(mbt.hasNextStep())
			{
				String[] stepPair = mbt.getNextStep();
				System.out.println(stepPair[0]);
				logger.debug("Execute: " + stepPair[0]);
				System.out.println(stepPair[1]);
				logger.debug("Verify: " + stepPair[1]);
			}
		}
		
		if(oldSystemOut != null)
		{
			logger.info("Restoring old System.out");
			System.setOut(oldSystemOut);
		}

		if(statisticsOpt.isTrue())
		{
			System.out.println(mbt.getStatisticsString());
		}
		t.cancel();
	}

	private static char getInput() 
	{
		char c = 0; 
		try 
		{
			while(c != '0' && c != '1' && c != '2')
			{
				int tmp = System.in.read ();
				c = (char) tmp;
			}
		}
		catch (IOException e) {}
		return c;
	}
}