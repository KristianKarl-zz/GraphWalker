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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.graphwalker.core.common.ResourceUtils;
import org.graphwalker.core.machine.Execution;
import org.graphwalker.core.machine.Machine;
import org.graphwalker.maven.plugin.test.Configuration;
import org.graphwalker.maven.plugin.test.Group;
import org.graphwalker.maven.plugin.test.Manager;
import org.graphwalker.maven.plugin.test.Scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <p>TestMojo class.</p>
 *
 */
@Mojo(name = "test", defaultPhase = LifecyclePhase.TEST, requiresDependencyResolution = ResolutionScope.TEST)
@Execute(phase = LifecyclePhase.TEST, lifecycle = "graphwalker")
public final class TestMojo extends AbstractTestMojo {

    @Override
    protected void executeMojo() throws MojoExecutionException, MojoFailureException {
        if (!getSkipTests()) {
            ClassLoader classLoader = switchClassLoader(createClassLoader());
            Properties properties = switchProperties(createProperties());
            displayHeader();
            Configuration configuration = createConfiguration();
            Scanner scanner = new Scanner();
            Manager manager = new Manager(configuration, scanner.scan(getTestClassesDirectory(), getClassesDirectory()));
            displayConfiguration(manager);
            //executeTests(manager);
            //reportResults(manager);
            //displayResult(manager);
            switchProperties(properties);
            switchClassLoader(classLoader);
        }


        /*
        if (!getSkipTests()) {
            displayHeader();
            Configuration configuration = new Configuration();
            configuration.setIncludes(getIncludes());
            configuration.setExcludes(getExcludes());
            configuration.setTest(getTest());
            configuration.setSkipTests(getSkipTests());   // behöver vi ta med denna?
            configuration.setClassesDirectory(getClassesDirectory());
            configuration.setTestClassesDirectory(getTestClassesDirectory());
            configuration.setReportsDirectory(getReportsDirectory());
            configuration.setGroups(getGroups());
            Scanner scanner = new Scanner(configuration);
            Manager manager = new Manager(configuration, scanner.scan(getTestClassesDirectory(), getClassesDirectory()));
            displayConfiguration(manager);
            executeTests(manager);
            reportResults(manager);
            displayResult(manager);
        }   */
    }

    private void displayHeader() {
        if (getLog().isInfoEnabled()) {
            getLog().info("------------------------------------------------------------------------");
            getLog().info(" G r a p h W a l k e r                                                  ");
            getLog().info("------------------------------------------------------------------------");
        }
    }

    private Configuration createConfiguration() {
        Configuration configuration = new Configuration();
        configuration.setIncludes(getIncludes());
        configuration.setExcludes(getExcludes());
        configuration.setTest(getTest());
        configuration.setClassesDirectory(getClassesDirectory());
        configuration.setTestClassesDirectory(getTestClassesDirectory());
        configuration.setReportsDirectory(getReportsDirectory());
        configuration.setGroups(getGroups());
        return configuration;
    }

    private void displayConfiguration(Manager manager) {
        getLog().info("Configuration:");
        getLog().info("          Test = "+manager.getConfiguration().getTest());
        getLog().info("       Include = "+manager.getConfiguration().getIncludes());
        getLog().info("       Exclude = "+manager.getConfiguration().getExcludes());
        getLog().info("        Groups = "+manager.getConfiguration().getGroups());
        getLog().info("  Threads/Test = "+1); // TODO: gör så att man kan låta flera trådar köra samma test (kunna utföra lasttest)
        getLog().info("");
        getLog().info("Tests:");

        if (0<manager.getGroups().size()) {
            for (Group group: manager.getGroups()) {
                getLog().info("  [" + group.getName()+"]");
                for (Execution execution: group.getExecutions()) {
                    getLog().info("    "+execution.getName());
                }
                getLog().info("");
            }
        } else {
            getLog().info("  No tests found");
        }
        getLog().info("------------------------------------------------------------------------");
    }

    private void executeTests(Manager manager) {
        if (0<manager.getGroups().size()) {
            List<Machine> machines = new ArrayList<Machine>();
            for (Group group: manager.getGroups()) {
                machines.add(new Machine(group.getExecutions()));
            }
            try {
                ExecutorService executorService = Executors.newFixedThreadPool(machines.size());
                for (Machine machine : machines) {
                    executorService.execute(machine);
                }
                executorService.shutdown();
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                throw new RuntimeException(ResourceUtils.getText(Bundle.NAME, "exception.execution.interrupted")); // TODO: byt exception
            }
        }
    }

    private void reportResults(Manager manager) {

    }

    private void displayResult(Manager manager) {
        getLog().info("------------------------------------------------------------------------");
        getLog().info("");
        getLog().info(ResourceUtils.getText(Bundle.NAME, "result.label"));
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
        getLog().info(ResourceUtils.getText(Bundle.NAME, "result.summary", group, total, completed, failed, notExecuted));
        getLog().info("");
    }





















/*

    private String convert(List<Class<?>> tests) {
        StringBuilder builder = new StringBuilder();
        for (Class<?> test: tests) {
            builder.append(", ").append(test.getSimpleName());
        }
        return builder.toString();
    }



    private void createInstances() throws MojoExecutionException {
        try {
            Map<String, List<Class<?>>> testGroups = new HashMap<String, List<Class<?>>>();
            List<Class<?>> tests = Scanner.findTests(testClassesDirectory, updateIncludes(), excludes);
            for (Class<?> test : tests) {
                String group = Scanner.getGroup(test);
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
            throw new MojoExecutionException(ResourceUtils.getText(Bundle.NAME, "exception.create.test"));
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
                throw new MojoExecutionException(ResourceUtils.getText(Bundle.NAME, "exception.execution.interrupted"));
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
            throw new MojoExecutionException(ResourceUtils.getText(Bundle.NAME, "exception.execution.failed", reportsDirectory.getAbsolutePath()));
        }
    }



 */
}
