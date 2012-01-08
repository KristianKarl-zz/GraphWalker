/*
 * #%L
 * GraphWalker CLI
 * %%
 * Copyright (C) 2011 - 2012 GraphWalker
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
import org.graphwalker.core.util.Resource;

import java.io.File;

public class GraphWalkerCLI {

    public static final String BUNDLE = "cli";

    private final Options myOptions = new Options();
    private final CommandLineParser myParser = new PosixParser();

    GraphWalkerCLI() {
        createOptions();
    }

    private void createOptions() {
        myOptions.addOption(Resource.getText(BUNDLE, "option.file.mnemonic")
                , Resource.getText(BUNDLE, "option.file.label")
                , true
                , Resource.getText(BUNDLE, "option.file.description"));

        myOptions.addOption(Resource.getText(BUNDLE, "option.help.mnemonic")
                , Resource.getText(BUNDLE, "option.help.label")
                , false
                , Resource.getText(BUNDLE, "option.help.description"));
    }

    private void parse(String[] arguments) {
        try {
            CommandLine commandLine = myParser.parse(myOptions, arguments);
            if (commandLine.hasOption(Resource.getText(BUNDLE, "option.file.mnemonic"))) {
                executeFile(commandLine.getOptionValue(Resource.getText(BUNDLE, "option.file.mnemonic")));
            } else {
                printHelp();
            }
        } catch (ParseException e) {
            printHelp();
        }
    }

    private void executeFile(String filename) {
        File file = Resource.getFile(filename);

    }
    
    private void printHelp() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(Resource.getText(BUNDLE, "options.label"), myOptions);
    }

    public static void main(String[] arguments) {
        GraphWalkerCLI graphWalkerCLI = new GraphWalkerCLI();
        graphWalkerCLI.parse(arguments);
    }
}
