/*
 * #%L
 * GraphWalker Core
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
package org.graphwalker.core;

import org.graphwalker.core.machine.Execution;
import org.graphwalker.core.machine.Machine;
import org.graphwalker.core.model.ModelElement;
import org.graphwalker.core.machine.ExecutionContext;

import java.util.*;

/**
 * <p>GraphWalker class.</p>
 */
public class GraphWalker implements Runnable {

    private final int threads;
    private final Set<Execution> executions;

    public GraphWalker(Set<Execution> executions, int threads) {
        this.threads = threads;
        this.executions = Collections.unmodifiableSet(executions);
    }

    public void run() {

        double y = 0;
        for (int i = 1; i<100000; i++) {
            y = (y*(i-1)+i)/i;
        }

        double x = 4999950000L/(100000-1);

        System.out.println(y);
        System.out.println(x);



        Set<ExecutionContext> executionContexts = new HashSet<ExecutionContext>();
        for (Execution execution: executions) {
            executionContexts.add(new ExecutionContext(execution));
        }
        Machine machine = new Machine(executionContexts);
        machine.run();
    }








    /*
    public void run() {
        if (0<threads) {
            ExecutorService executorService = Executors.newFixedThreadPool(threads);
            for (int i = 0; i<threads; i++) {
                executorService.execute(new GraphWalkerExecutor(this));
            }
            executorService.shutdown();
            try {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    private class GraphWalkerExecutor implements Runnable {

        private final GraphWalker instance;

        GraphWalkerExecutor(GraphWalker instance) {
            this.instance = instance;
        }

        public void run() {
            //ExecutionContext context = new ExecutionContext();



            // en test klass kan innehålla flera modeller, hitta alla @Model annoteringar, Modell klasser ska vi dela mellan trådar
            for (int i = 0; i<100; i++) {
                System.out.println(this);
            }
            // skapa en model context
            // exekvera model context med en machine
            // model contexten måste vara sparad så att vi kan hämta information om exekveringen (byt namn till ExecutionContext)
        }

    }
     */





















/*


    private final AnnotationProcessor annotationProcessor = new AnnotationProcessor();
    private final Map<Model, ExecutionContext> contexts = new HashMap<Model, ExecutionContext>();
    private final Map<Model, Object> implementations = new HashMap<Model, Object>();
    private Machine machine;

    public void addModel(Model model, Object object) {
        ExecutionContext context = new ExecutionContext(model);
        context.setPathGenerator(new RandomPath(new EdgeCoverage(100)));
        contexts.put(model, context);
        implementations.put(model, object);
    }

    public void addModel(Model model, PathGenerator pathGenerator, Object object) {
        ExecutionContext context = new ExecutionContext(model);
        context.setPathGenerator(pathGenerator);
        contexts.put(model, context);
        implementations.put(model, object);
    }

    public void addModel(Model model, PathGenerator pathGenerator, String scriptLanguage, Object object) {
        ExecutionContext context = new ExecutionContext(model);
        context.setPathGenerator(pathGenerator);
        context.setEdgeFilter(new EdgeFilter(scriptLanguage));
        contexts.put(model, context);
        implementations.put(model, object);
    }

    public void execute(Model model) {
        machine = new Machine(new ArrayList<ExecutionContext>(contexts.values()));
        machine.setCurrentExecutionContext(contexts.get(model));
        try {
            processAnnotation(BeforeModel.class, machine, implementations.get(model));
            while (machine.hasMoreSteps()) {
                ModelElement element = machine.getNextStep();
                ExecutionContext context = machine.getExecutionContext();
                if (implementations.containsKey(model)) {
                    processAnnotation(BeforeElement.class, machine, implementations.get(model));
                    ReflectionUtils.execute(implementations.get(context.getModel()), element.getName(), machine.getExecutionContext());
                    processAnnotation(AfterElement.class, machine, implementations.get(model));
                }
            }
            processAnnotation(AfterModel.class, machine, implementations.get(model));
        } catch (Throwable t) {
            processAnnotation(ExceptionHandler.class, machine, implementations.get(model));
        }
    }

    private void processAnnotation(Class<? extends Annotation> annotation, Machine machine, Object object) {
        if (null != object) {
            annotationProcessor.process(annotation, machine, object);
        }
    }

    public boolean isAllModelsDone() {
        return machine.hasMoreSteps();
    }
*/

}
