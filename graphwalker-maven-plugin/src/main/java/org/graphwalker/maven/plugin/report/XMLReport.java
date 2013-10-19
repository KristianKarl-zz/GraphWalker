/*
 * #%L
 * GraphWalker Maven Plugin
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
package org.graphwalker.maven.plugin.report;

import org.codehaus.plexus.util.FileUtils;
import org.graphwalker.core.machine.ExecutionContext;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @author Nils Olsson
 */
public final class XMLReport {

    private final File directory;
    private ObjectFactory factory = new ObjectFactory();

    public XMLReport(File directory) {
        this.directory = directory;
    }

    public void writeReport() {

    }

    public void writeReport(ExecutionContext context, Object implementation) {
        String name = implementation.getClass().getName();
        Testsuite testsuite = factory.createTestsuite();
        testsuite.setName(name);
        // add properties that were used in this context
        /* add requirement status as testcases
        for (Requirement requirement: context.getRequirements()) {
            Testcase testcase = factory.createTestcase();
            testcase.setName(requirement.getName());
            testcase.setStatus();
            testsuite.getTestcase().add(testcase);
        }
        */
        writeReport(testsuite, getOutputStream(name + ".xml"));
    }

    private void writeReport(Testsuite testsuite, OutputStream outputStream) {
        try {
            Marshaller marshaller = JAXBContext.newInstance(ObjectFactory.class).createMarshaller();
            marshaller.marshal(testsuite, outputStream);
        } catch (JAXBException e) {
            throw new XMLReportException(e);
        }
    }

    private OutputStream getOutputStream(String reportName) {
        FileUtils.mkdir(directory.getAbsolutePath());
        try {
            return new FileOutputStream(new File(directory, reportName));
        } catch (FileNotFoundException e) {
            throw new XMLReportException(e);
        }
    }
}
