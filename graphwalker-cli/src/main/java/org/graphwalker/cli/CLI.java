/*
 * #%L
 * GraphWalker Command Line Interface
 * %%
 * Copyright (C) 2011 - 2014 GraphWalker
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
package org.graphwalker.cli;

import org.apache.commons.cli.*;
import org.apache.commons.vfs2.FileSystemException;
import org.graphwalker.core.*;
import org.graphwalker.core.condition.BaseStopCondition;
import org.graphwalker.core.condition.EdgeCoverage;
import org.graphwalker.core.condition.VertexCoverage;
import org.graphwalker.core.generator.AStarPath;
import org.graphwalker.core.generator.RandomPath;
import org.graphwalker.core.machine.ExecutionContext;
import org.graphwalker.core.model.Element;
import org.graphwalker.io.factory.GraphMLModelFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLI {
  static Logger logger = Logger.getAnonymousLogger();
  private Options opt = new Options();

  public static void main(String[] args) {
    CLI cli = new CLI();
    try {
      cli.run(args);
    } catch (Exception e) {
      System.err.println(e);
      System.err.println(printGeneralHelpText());
      logger.log(Level.ALL, e.toString(), e.getStackTrace());
    }
  }

  private void run(String[] args) {
    if (args.length < 1) {
      System.err.println("Type 'java -jar graphwalker.jar help' for usage.");
    } else {
      if (args[0].equals("help")) {
        if (args.length == 1) {
          printGeneralHelpText();
        } else {
          printHelpText(args[1]);
        }
      } else if (args[0].equalsIgnoreCase("offline")) {
        buildOfflineCLI();
      } else if (args[0].equals("-v") || args[0].equals("--version")) {
        System.out.println(printVersionInformation());
      } else if (args[0].equals("-h") || args[0].equals("--help")) {
        System.out.println(printGeneralHelpText());
      } else {
        System.err.println("Unknown command: " + args[0]);
        System.err.println("Type 'java -jar graphwalker.jar help' for usage.");
      }
    }

    CommandLineParser parser = new PosixParser();
    CommandLine cl = null;

    try {
      cl = parser.parse(opt, args);

      /**
       * Command: offline
       */
      if (args[0].equalsIgnoreCase("offline")) {
        RunCommandOffline(cl);
      }
    } catch (Exception e) {
      e.printStackTrace();
      logger.log(Level.ALL, printGeneralHelpText(), e.getStackTrace());
      System.err.println("Parsing of the command line failed");
      System.err.println(e.getMessage());
      System.err.println("Type 'java -jar graphwalker.jar help " + args[0] + "' for help.");
    }
  }

  private void RunCommandOffline(CommandLine cl) throws FileSystemException {
    if (helpNeeded("offline", !cl.hasOption("f"), "Missing the input graphml file (folder), See -f (--input_graphml)")
      || helpNeeded("offline", !cl.hasOption("s"), "A stop condition must be supplied, See option -s")
      || helpNeeded("offline", !cl.hasOption("g"), "Missing the generator, See option -g")) return;

    GraphMLModelFactory factory = new GraphMLModelFactory();
    Model model = factory.create(cl.getOptionValue("f"));

    StopCondition condition = null;
    if ( cl.getOptionValue("s").equalsIgnoreCase("EDGE_COVERAGE") ) {
      condition = new EdgeCoverage();
    } else if ( cl.getOptionValue("s").equalsIgnoreCase("VERTEX_COVERAGE") ) {
      condition = new VertexCoverage();
    } else {
      throw new IllegalArgumentException("No generator called: " + cl.getOptionValue("g"));
    }

    PathGenerator pathGenerator = null;
    if ( cl.getOptionValue("g").equalsIgnoreCase("RANDOM") ) {
      pathGenerator = new RandomPath(condition);
    } else if ( cl.getOptionValue("g").equalsIgnoreCase("A_STAR") ) {
      pathGenerator = new AStarPath(condition);
    } else {
      throw new IllegalArgumentException("No generator called: " + cl.getOptionValue("g"));
    }

    ExecutionContext context = new ExecutionContext(model, pathGenerator);
    Machine machine = new SimpleMachine(context);
    while (machine.hasNextStep()) {
      Element e = machine.getNextStep();
      System.out.println(e.getName());
    }
  }

  private String printVersionInformation() {
    String version = "org.graphwalker version: " + getVersionString() + System.getProperty("line.separator");
    version +=  System.getProperty("line.separator");

    version += "org.graphwalker is open source software licensed under MIT license" + System.getProperty("line.separator");
    version += "The software (and it's source) can be downloaded from http://graphwalker.org" + System.getProperty("line.separator");
    version += "For a complete list of this package software dependencies, see TO BE DEFINED" + System.getProperty("line.separator");

    return version;
  }

  private String getVersionString() {
    Properties properties = new Properties();
    InputStream inputStream = null;
    try {
      inputStream = getClass().getResourceAsStream("/org/graphwalker/resources/version.properties");
      properties.load(inputStream);
      inputStream.close();
    } catch (IOException e) {
      logger.log(Level.ALL, printGeneralHelpText(), e.getStackTrace());
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (Exception e) {
          // ignore all exceptions
        }
      }
    }
    StringBuilder stringBuilder = new StringBuilder();
    if (properties.containsKey("version.major")) {
      stringBuilder.append(properties.getProperty("version.major"));
    }
    if (properties.containsKey("version.minor")) {
      stringBuilder.append(".");
      stringBuilder.append(properties.getProperty("version.minor"));
    }
    if (properties.containsKey("version.fix")) {
      stringBuilder.append(".");
      stringBuilder.append(properties.getProperty("version.fix"));
    }
    if (properties.containsKey("version.git.commit")) {
      stringBuilder.append(", git commit ");
      stringBuilder.append(properties.getProperty("version.git.commit"));
    }
    return stringBuilder.toString();
  }


  private static String printGeneralHelpText() {
    String text = "";
    text += "usage: 'java -jar graphwalker.jar <COMMAND> [OPTION] [ARGUMENT]" + System.getProperty("line.separator");
    text += System.getProperty("line.separator");

    text += "Type 'java -jar graphwalker.jar help <COMMAND>' to get specific help about a command." + System.getProperty("line.separator");
    text += "Valid commands are:" + System.getProperty("line.separator");
    text += "    help" + System.getProperty("line.separator");
    text += "    offline" + System.getProperty("line.separator");
    text += System.getProperty("line.separator");

    text += "Type 'java -jar graphwalker.jar -v (--version)' for version information." + System.getProperty("line.separator");

    return text;
  }

  private void printHelpText(String helpSection) {
    String header;

    if (helpSection.equalsIgnoreCase("offline")) {
      buildOfflineCLI();
      header = "Generate a test sequence offline. The sequence is printed to the standard output";
    } else {
      System.err.println("Type 'java -jar graphwalker.jar help' for usage.");
      return;
    }

    HelpFormatter f = new HelpFormatter();
    f.printHelp(100, "java -jar graphwalker.jar " + helpSection.toLowerCase(), header, opt, "", true);
  }

  @SuppressWarnings("static-access")
  private void buildOfflineCLI() {
    opt.addOption("x", false, "Use an extended finite state machine to handle the model.");
    opt.addOption(OptionBuilder
            .isRequired()
            .withArgName("stop-condition")
            .withDescription(
                    "Defines the stop condition(s)." + System.getProperty("line.separator") +
                            "Halts the generation after the specified stop-conditon(s) has been met. "
                            + "At least 1 condition must be given. If more than 1 is given, the condition that meets "
                            + "it's stop-condition first, will cause the generation to halt. "
                            + "To separate multiple conditions, the separator pipe-character | is used. "
                            + "A list of valid stop-conditions are:" + System.getProperty("line.separator")
                            + " -------------------" + System.getProperty("line.separatr")
                            + generateListOfValidStopConditions()
                            + " -------------------" + System.getProperty("line.separator")
                            + "For more extensive examples, " + "see http://mbt.tigris.org/wiki/All_about_stop_conditions").hasArg()
            .create("s"));
    opt.addOption(OptionBuilder
            .isRequired()
            .withArgName("generator")
            .withDescription(
                    "The generator to be used when traversing the model. At least 1 generator must be given. "
                            + "To separate multiple generators, the separator pipe-character | is used. "
                            + "A list of valid generators are:" + System.getProperty("line.separator")
                            + " -------------------" + System.getProperty("line.separator")
                            + generateListOfValidGenerators()
                            + " -------------------" + System.getProperty("line.separator")
                            + "For more extensive examples, " + "see http://graphwalker.org/syntax/generators/").hasArg().create("g"));
    opt.addOption(OptionBuilder.isRequired().withArgName("file|folder").withDescription("The file (or folder) containing graphml formatted files.")
            .hasArg().create("f"));
    opt.addOption(OptionBuilder
            .withArgName("seconds")
            .withDescription(
                    "Prints the test coverage of the graph during execution every <n second>. The printout goes to the logger file defined in "
                            + "mbt.properties, and only, if at least INFO level is set in " + "that same file.").hasArg().create("o"));
    opt.addOption("t", "report-template", true, "Optional report template to use. (Also requires option -r) (To be better documented)");
    opt.addOption("r", "report-output", true, "Optional report filename to save report to. (Also requires option -t)  (To be better documented)");
    opt.addOption("w", "weighted", false, "Use weighted values if they exist in the model, and the generator is RANDOM.");
  }

  private String generateListOfValidGenerators() {
    return "";
  }

  private String generateListOfValidStopConditions() {
    return "";
  }

  private boolean helpNeeded(String module, boolean condition, String message) {
    if (condition) {
      System.out.println(message);
      System.out.println("Type 'java -jar graphwalker.jar help " + module + "' for help.");
    }
    return condition;
  }
}