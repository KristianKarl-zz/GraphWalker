/*
 * #%L
 * Maven GraphWalker Plugin
 * %%
 * Copyright (C) 2012 GraphWalker
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
package org.graphwalker.maven.plugin;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.graphwalker.core.GraphWalker;
import org.graphwalker.core.utils.Resource;
import org.graphwalker.maven.plugin.reports.ReportWriter;
import org.graphwalker.maven.plugin.reports.XMLReport;
import org.graphwalker.maven.plugin.utils.TestUtil;

import java.util.*;

/**
 * <p>TestMojo class.</p>
 *
 * @author nilols
 */
@Mojo(name = "test"
        , defaultPhase = LifecyclePhase.TEST
        , requiresDependencyResolution = ResolutionScope.TEST)
@Execute(goal = "test-validate")
public class TestMojo extends AbstractGraphWalkerMojo {

    private List<GraphWalker> graphWalkers = new ArrayList<GraphWalker>();

    private ReportWriter reportWriter = new XMLReport();

    public void executeMojo() {
        if (!skipTests()) {
            displayHeader();
            List<Class<?>> tests = TestUtil.findTests(getIncludes(), getExcludes(), getTestClassesDirectory(), getClassesDirectory());
            displayConfiguration(tests);
            // kör testerna
            /*
            createInstances();
            executeInstances();
            reportExecution();
            */
            displayResult();
        }
    }

    private void displayHeader() {
        getLog().info("------------------------------------------------------------------------");
        getLog().info(" G r a p h W a l k e r                                                  ");
        getLog().info("------------------------------------------------------------------------");
    }

    private void displayConfiguration(List<Class<?>> tests) {
        getLog().info("Arguments:");
        // parametrar/argument, typ: antal trådar, version, timestamp (start)
        getLog().info("");
        getLog().info("Tests:");
        if (0<tests.size()) {
            for (Class<?> test: tests) {
                getLog().info("  " + test.getName());
            }
            getLog().info("");
        } else {
            getLog().info("  No tests found");
        }
        getLog().info("------------------------------------------------------------------------");
    }

    private String convert(List<Class<?>> tests) {
        StringBuilder builder = new StringBuilder();
        for (Class<?> test: tests) {
            builder.append(", ").append(test.getSimpleName());
        }
        return builder.toString();
    }

    private void displayResult() {
        getLog().info("");
        getLog().info(Resource.getText(Bundle.NAME, "result.label"));
        getLog().info("");
        long group = 0, total = 0, completed = 0, failed = 0, notExecuted = 0;
        /*
        List<Model> failedModels = new ArrayList<Model>();
        for (GraphWalker graphWalker : graphWalkers) {
            group++;
            for (Model model : graphWalker.getConfiguration().getModels()) {
                total++;
                switch (model.getModelStatus()) {
                    case COMPLETED: {
                        completed++;
                    }
                    break;
                    case FAILED: {
                        failed++;
                        failedModels.add(model);
                    }
                    break;
                    case NOT_EXECUTED: {
                        notExecuted++;
                    }
                    break;
                }
            }
        }
        if (0 < failedModels.size()) {
            getLog().info("Failed models: ");
            for (Model model : failedModels) {
                getLog().info("  " + model.getId() + " [group = " + model.getGroup() + "]");
            }
            getLog().info("");
        }
        */
        getLog().info(Resource.getText(Bundle.NAME, "result.summary", group, total, completed, failed, notExecuted));
        getLog().info("");
    }





















/*





    private void createInstances() throws MojoExecutionException {
        try {
            Map<String, List<Class<?>>> testGroups = new HashMap<String, List<Class<?>>>();
            List<Class<?>> tests = TestUtil.findTests(testClassesDirectory, updateIncludes(), excludes);
            for (Class<?> test : tests) {
                String group = TestUtil.getGroup(test);
                if (!testGroups.containsKey(group)) {
                    testGroups.put(group, new ArrayList<Class<?>>());
                }
                testGroups.get(group).add(test);
            }

            for (String group : testGroups.keySet()) {
                Configuration configuration = ConfigurationFactory.create(testGroups.get(group));
                graphWalkers.add(GraphWalkerFactory.create(configuration));
            }
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException(Resource.getText(Bundle.NAME, "exception.create.test"));
        }
    }

    private void executeInstances() throws MojoExecutionException {
        if (0< graphWalkers.size()) {
            try {
                ExecutorService executorService = Executors.newFixedThreadPool(graphWalkers.size());
                for (GraphWalker graphWalker : graphWalkers) {
                    for (Model model : graphWalker.getConfiguration().getModels()) {
                        StringBuilder stringBuilder = new StringBuilder("Running [");
                        stringBuilder.append(model.getGroup()).append("] ");
                        stringBuilder.append(model.getImplementation().getClass().getName());
                        getLog().info(stringBuilder.toString());
                    }
                    executorService.execute(new GraphWalkerExecutor(graphWalker));
                }
                executorService.shutdown();
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                throw new MojoExecutionException(Resource.getText(Bundle.NAME, "exception.execution.interrupted"));
            }
        }
    }

    private void reportExecution() throws MojoExecutionException {
        boolean hasExceptions = false;
        for (GraphWalker graphWalker : graphWalkers) {
            reportWriter.writeReport(graphWalker, reportsDirectory, session.getStartTime());
            for (Model model:graphWalker.getConfiguration().getModels()) {
                hasExceptions |= graphWalker.hasExceptions(model);
            }
        }
        if (hasExceptions) {
            throw new MojoExecutionException(Resource.getText(Bundle.NAME, "exception.execution.failed", reportsDirectory.getAbsolutePath()));
        }
    }



 */
}
