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

import org.graphwalker.core.conditions.support.EdgeCoverage;
import org.graphwalker.core.filter.EdgeFilter;
import org.graphwalker.core.generators.PathGenerator;
import org.graphwalker.core.generators.support.RandomPath;
import org.graphwalker.core.machine.Machine;
import org.graphwalker.core.model.Model;
import org.graphwalker.core.model.ModelElement;
import org.graphwalker.core.machine.Context;
import org.graphwalker.core.annotations.*;
import org.graphwalker.core.utils.Reflection;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <p>GraphWalker class.</p>
 */
public class GraphWalker implements Runnable {

    private final Object object;

    public GraphWalker(Class<?> testClass) {
        this.object = Reflection.newInstance(testClass);
    }

    public void run(int threads) {
        if (0<threads) {
            ExecutorService executorService = Executors.newFixedThreadPool(threads);
            executorService.execute(this);
            executorService.shutdown();
            try {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    public void run() {
        // en test klass kan innehålla flera modeller, hitta alla @Model annoteringar, Modell klasser ska vi dela mellan trådar
        int i = 0;
        // skapa en model context
        // exekvera model context med en machine
        // model contexten måste vara sparad så att vi kan hämta information om exekveringen (byt namn till Context)
    }


























    private final AnnotationProcessor annotationProcessor = new AnnotationProcessor();
    private final Map<Model, Context> contexts = new HashMap<Model, Context>();
    private final Map<Model, Object> implementations = new HashMap<Model, Object>();
    private Machine machine;

    /**
     * <p>addModel.</p>
     *
     * @param model a {@link org.graphwalker.core.model.Model} object.
     * @param object a {@link java.lang.Object} object.
     */
    public void addModel(Model model, Object object) {
        Context context = new Context(model);
        context.setPathGenerator(new RandomPath(new EdgeCoverage(100)));
        contexts.put(model, context);
        implementations.put(model, object);
    }

    /**
     * <p>addModel.</p>
     *
     * @param model a {@link org.graphwalker.core.model.Model} object.
     * @param pathGenerator a {@link org.graphwalker.core.generators.PathGenerator} object.
     * @param object a {@link java.lang.Object} object.
     */
    public void addModel(Model model, PathGenerator pathGenerator, Object object) {
        Context context = new Context(model);
        context.setPathGenerator(pathGenerator);
        contexts.put(model, context);
        implementations.put(model, object);
    }

    /**
     * <p>addModel.</p>
     *
     * @param model a {@link org.graphwalker.core.model.Model} object.
     * @param pathGenerator a {@link org.graphwalker.core.generators.PathGenerator} object.
     * @param scriptLanguage a {@link java.lang.String} object.
     * @param object a {@link java.lang.Object} object.
     */
    public void addModel(Model model, PathGenerator pathGenerator, String scriptLanguage, Object object) {
        Context context = new Context(model);
        context.setPathGenerator(pathGenerator);
        context.setEdgeFilter(new EdgeFilter(scriptLanguage));
        contexts.put(model, context);
        implementations.put(model, object);
    }

    /**
     * <p>execute.</p>
     *
     * @param model a {@link org.graphwalker.core.model.Model} object.
     */
    public void execute(Model model) {
        machine = new Machine(new ArrayList<Context>(contexts.values()));
        machine.setCurrentContext(contexts.get(model));
        try {
            processAnnotation(BeforeModel.class, machine, implementations.get(model));
            while (machine.hasMoreSteps()) {
                ModelElement element = machine.getNextStep();
                Context context = machine.getCurrentContext();
                if (implementations.containsKey(model)) {
                    processAnnotation(BeforeElement.class, machine, implementations.get(model));
                    Reflection.execute(implementations.get(context.getModel()), element.getName(), machine.getCurrentContext());
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

    /**
     * <p>isAllModelsDone.</p>
     *
     * @return a boolean.
     */
    public boolean isAllModelsDone() {
        return machine.hasMoreSteps();
    }


}
