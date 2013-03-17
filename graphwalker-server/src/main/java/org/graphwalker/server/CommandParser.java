/*
 * #%L
 * GraphWalker Server
 * %%
 * Copyright (C) 2011 - 2013 GraphWalker
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
package org.graphwalker.server;

import org.apache.commons.cli.*;
import org.graphwalker.core.utils.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.ResourceBundle;

/**
 * <p>CommandParser class.</p>
 *
 * @author nilols
 */
public final class CommandParser {

    private final static Logger logger = LoggerFactory.getLogger(CommandParser.class);
    private final Properties properties;

    /**
     * <p>Constructor for CommandParser.</p>
     *
     * @param args an array of {@link java.lang.String} objects.
     */
    public CommandParser(String[] args, Properties defaultProperties) {
        CommandLine commandLine = parseCommandLine(args, createCommandOptions());
        this.properties = defaultProperties;
        this.properties.putAll(commandLine.getOptionProperties("D"));
        for (String name: properties.stringPropertyNames()) {
            logger.info(String.format("%-20s%20s", name, properties.getProperty(name)));
        }
    }

    /**
     * <p>getProperty.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String getProperty(String name) {
        if (properties.containsKey(name)) {
            return properties.getProperty(name);
        }
        throw new PropertyNotFoundException(Resource.getText(Bundle.NAME, "help.property.missing", name));
    }

    private CommandLine parseCommandLine(String[] args, Options options) {
        CommandLineParser parser = new PosixParser();
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            printCommandHelpAndExit(options);
        }
        return commandLine;
    }

    private void printCommandHelpAndExit(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(Resource.getText(Bundle.NAME, "help.string"), options);
        System.exit(-1);
    }

    private Options createCommandOptions() {
        Options options = new Options();
        options.addOption(createPropertyOption());
        return options;
    }

    private Option createPropertyOption() {
        Option option = new Option("D", Resource.getText(Bundle.NAME, "help.property.description"));
        option.setLongOpt(null);
        option.setRequired(false);
        option.setOptionalArg(true);
        option.setArgs(2);
        option.setType(null);
        option.setValueSeparator('=');
        option.setArgName(Resource.getText(Bundle.NAME, "help.property"));
        return option;
    }
}
