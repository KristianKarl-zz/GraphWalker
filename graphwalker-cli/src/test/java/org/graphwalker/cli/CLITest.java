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
// This file is part of the GraphWalker java package
// The MIT License
//
// Copyright (c) 2010 graphwalker.org
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package org.graphwalker.cli;

import static org.graphwalker.RegexMatcher.matches;
import static org.hamcrest.CoreMatchers.*;

import org.graphwalker.cli.CLI;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.core.Is.is;


public class CLITest {

  Pattern pattern;
  Matcher matcher;
  StringBuffer stdOutput;
  StringBuffer errOutput;
  String outMsg;
  String errMsg;
  static Logger logger = Logger.getAnonymousLogger();
  private CLI commandLineInterface;

  private OutputStream redirectOut() {
    return new OutputStream() {
      @Override
      public void write(int b) throws IOException {
        stdOutput.append(Character.toString((char) b));
      }
    };
  }

  private OutputStream redirectErr() {
    return new OutputStream() {
      @Override
      public void write(int b) throws IOException {
        errOutput.append(Character.toString((char) b));
      }
    };
  }

  private InputStream redirectIn() {
    return new InputStream() {
      @Override
      public int read() throws IOException {
        try {
          Thread.sleep(300);
        } catch (InterruptedException e) {
          logger.log(Level.ALL, "Unit testing was interrupted", e.getStackTrace());
        }
        return '0';
      }
    };
  }

  private void runCommand(String args[]) {
    stdOutput = new StringBuffer();
    errOutput = new StringBuffer();

    PrintStream outStream = new PrintStream(redirectOut());
    PrintStream oldOutStream = System.out; // backup
    PrintStream errStream = new PrintStream(redirectErr());
    PrintStream oldErrStream = System.err; // backup

    System.setOut(outStream);
    System.setErr(errStream);

    commandLineInterface = new CLI();
    commandLineInterface.main(args);

    System.setOut(oldOutStream);
    System.setErr(oldErrStream);

    outMsg = stdOutput.toString();
    errMsg = errOutput.toString();
    logger.log(Level.FINER, "stdout: " + outMsg);
    logger.log(Level.FINER, "stderr: " + errMsg);
  }

  private void moveMbtPropertiesFile() {
    File mbt_properties = new File("graphwalker.properties");
    if (mbt_properties.exists()) {
      mbt_properties.renameTo(new File("graphwalker.properties.bak"));
    }
    Assert.assertFalse(new File("graphwalker.properties").exists());
  }

  private void restoreMbtPropertiesFile() {
    File mbt_properties = new File("graphwalker.properties.bak");
    if (mbt_properties.exists()) {
      mbt_properties.renameTo(new File("graphwalker.properties"));
    }
    Assert.assertFalse(new File("graphwalker.properties.bak").exists());
  }

  /**
   * Simulates
   * java -jar graphwalker.jar
   */
  @Test
  public void testNoArgs() {
    String args[] = {};
    runCommand(args);
    String output = "^Usage: java -jar graphwalker.jar .*";
    Assert.assertThat( outMsg, matches(output));
    Assert.assertThat( "Nothing should be written to standard output", errMsg, is(""));
  }

  /**
   * Simulates
   * java -jar graphwalker.jar -v
   */
  @Test
  public void testVersion() {
    String args[] = {"-v"};
    runCommand(args);
    Assert.assertThat( "No error messages should occur", errMsg, is(""));
    Assert.assertThat( "Expected output", outMsg, matches("^org.graphwalker version: [0-9]+\\.[0-9]+\\.[0-9]+"));
  }

  /**
   * Simulates
   * java -jar graphwalker.jar
   */
  @Test
  public void testNoMbtPropertiesFile() {
    String args[] = {};
    moveMbtPropertiesFile();
    runCommand(args);
    restoreMbtPropertiesFile();
    String output = "Type 'java -jar graphwalker.jar help' for usage." + System.getProperty("line.separator");
    Assert.assertThat( "Expected output", errMsg, is(output));
    Assert.assertThat( "Nothing should be written to standard output", outMsg, is(""));
  }

  /**
   * Simulates
   * java -jar graphwalker.jar sputnik
   */
  @Test
  public void testUnknownCommand() {
    String args[] = {"sputnik"};
    runCommand(args);
    Assert.assertThat( "Expected output", errMsg, containsString("Expected a command, got sputnik"));
    Assert.assertThat( "Nothing should be written to standard output", outMsg, is(""));
  }

  /**
   * Simulates
   * java -jar graphwalker.jar offline -f graphml/UC01.graphml -g A_STAR -s EDGE_COVERAGE:100
   */
  @Test
  public void testOfflineRandomEdgeCoverage100percent() {
    String args[] = {"offline", "-f", "graphml/EFSM_with_REQTAGS.graphml", "-g", "A_STAR", "-s", "EDGE_COVERAGE:100"};
    runCommand(args);
    Assert.assertThat( "No error messages should occur", errMsg, is(""));
  }

  /**
   * Simulates
   * java -jar graphwalker.jar offline -f graphml/UC01.graphml -g A_STAR -s EDGE_COVERAGE:100
   */
  @Test
  public void testOfflineA_StarEdgeCoverage100percent() {
    String args[] = {"offline", "-f", "graphml/UC01.graphml", "-g", "A_STAR", "-s", "EDGE_COVERAGE:100"};
    runCommand(args);
    Assert.assertThat( "No error messages should occur", errMsg, is(""));
    Assert.assertEquals("Expected 38 lines beginning with v_" , 38, getNumMatches(Pattern.compile("v_").matcher(outMsg)));
    Assert.assertEquals("Expected 38 lines beginning with e_" , 38, getNumMatches(Pattern.compile("e_").matcher(outMsg)));
  }

  private int getNumMatches(Matcher m) {
    int numMatches = 0;
    while (m.find() == true)
      numMatches++;
    return numMatches;
  }

//  /**
//   * Simulates
//   * java -jar graphwalker.jar offline -f graphml/reqtags/ExtendedMain.graphml -g RANDOM -s EDGE_COVERAGE:100
//   */
//  @Test
//  public void testOfflineRandomEdgeCoverage() {
//    String args[] = {"offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "RANDOM", "-s", "EDGE_COVERAGE:100"};
//    runCommand(args);
//    Assert.assertTrue("No error messgaes should occur: " + errMsg, errMsg.isEmpty());
//    Assert.assertTrue("Expected at least 78 lines, got: " + outMsg.split("\r\n|\r|\n").length, outMsg.split("\r\n|\r|\n").length >= 78);
//  }
//
//  /**
//   * Simulates
//   * java -jar graphwalker.jar offline -f graphml/reqtags/ExtendedMain.graphml -g RANDOM -s VERTEX_COVERAGE:100
//   */
//  @Test
//  public void testOfflineRandomStateCoverage() {
//    String args[] = {"offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "RANDOM", "-s", "VERTEX_COVERAGE:100"};
//    runCommand(args);
//    Assert.assertTrue("No error messages should occur: " + errMsg, errMsg.isEmpty());
//    Assert.assertTrue("Expected at least 24 lines, got: " + outMsg.split("\r\n|\r|\n").length, outMsg.split("\r\n|\r|\n").length >= 24);
//  }
//
//  /**
//   * Simulates
//   * java -jar graphwalker.jar offline -f graphml/reqtags/ExtendedMain.graphml -g A_STAR -s VERTEX_COVERAGE:100
//   */
//  @Test
//  public void testOfflineA_StarStateCoverage() {
//    String args[] = {"offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "A_STAR", "-s", "VERTEX_COVERAGE:100"};
//    runCommand(args);
//    Assert.assertEquals("No error messages should occur.", "", errMsg);
//    Assert.assertTrue(outMsg, getNumMatches(Pattern.compile("INFO").matcher(outMsg)) <= 21);
//  }
//
//  /**
//   * Simulates
//   * java -jar graphwalker.jar offline -f graphml/reqtags/ExtendedMain.graphml -g a_star -s "REACHED_REQUIREMENT:req 78
//   */
//  @Test
//  public void testOfflineA_StarReachedRequirement() {
//    String args[] = {"offline", "-f", "graphml/reqtags/ExtendedMain.graphml", "-g", "a_star", "-s", "REACHED_REQUIREMENT:req 78"};
//    runCommand(args);
//    Assert.assertEquals("No error messages should occur.", "", errMsg);
//    Assert.assertEquals(0, getNumMatches(Pattern.compile("INFO").matcher(outMsg)));
//  }
//
//
//  /**
//   * Simulates
//   * java -jar graphwalker.jar offline -f graphml/misc/missing_inedges.graphml -g RANDOM -s EDGE_COVERAGE:100
//   */
//  @Test
//  public void testNoVerticesWithNoInEdges() {
//    String args[] = {"offline", "-f", "graphml/misc/missing_inedges.graphml", "-g", "RANDOM", "-s", "EDGE_COVERAGE:100"};
//    runCommand(args);
//    pattern = Pattern.compile("^No in-edges! Vertex: .* is not reachable, from file: 'graphml.misc.missing_inedges.graphml'$", Pattern.MULTILINE);
//    matcher = pattern.matcher(errMsg);
//    Assert.assertTrue(matcher.find());
//  }
//
//  /**
//   * Simulates
//   * java -jar graphwalker.jar offline -f graphml/misc/missing_inedges.graphml -g RANDOM -s EDGE_COVERAGE:100
//   */
//  @Test
//  public void testVertexWithNoInEdges() {
//    String args[] = {"offline", "-f", "graphml/misc/missing_inedges.graphml", "-g", "RANDOM", "-s", "EDGE_COVERAGE:100"};
//    runCommand(args);
//    pattern = Pattern.compile("No in-edges! Vertex: 'v_InvalidKey', INDEX=9 is not reachable.", Pattern.MULTILINE);
//    matcher = pattern.matcher(errMsg);
//    Assert.assertTrue(matcher.find());
//  }
}
