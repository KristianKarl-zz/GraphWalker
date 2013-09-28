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
package org.graphwalker.core.machine;

import org.graphwalker.core.Model;
import org.graphwalker.core.PathGenerator;
import org.graphwalker.core.StopCondition;
import org.graphwalker.core.model.Edge;
import org.graphwalker.core.model.Element;
import org.graphwalker.core.model.Requirement;
import org.graphwalker.core.model.Vertex;
import org.graphwalker.core.script.Context;
import org.graphwalker.core.statistics.ExecutionProfiler;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Nils Olsson                                                            s
 */
public final class ExecutionContext {

    private final Set<Model> models;
    private final PathGenerator pathGenerator;
    private final StopCondition stopCondition;
    private final ScriptEngine scriptEngine;
    private final Context context = new Context();
    private final ExecutionProfiler profiler = new ExecutionProfiler();
    private final Map<Model, Element> currentStep = new HashMap<Model, Element>();

    public ExecutionContext(Set<Model> models, PathGenerator pathGenerator, StopCondition stopCondition, String language) {
        this.models = models;
        this.pathGenerator = pathGenerator;
        this.stopCondition = stopCondition;
        this.scriptEngine = createScriptEngine(language);
    }

    private ScriptEngine createScriptEngine(String language) {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(language);
        scriptEngine.setContext(context);
        return scriptEngine;
    }

    public ExecutionProfiler getProfiler() {
        return profiler;
    }

    public Long getVisitCount() {
        throw new RuntimeException();
    }

    public Set<Edge> getVisitedEdges() {
        throw new RuntimeException();
    }

    public Set<Vertex> getVisitedVertices() {
        throw new RuntimeException();
    }

    public Model getCurrentModel() {
        throw new RuntimeException();
    }

    public Element getCurrentElement() {
        throw new RuntimeException();
    }

    public Set<Requirement> getRequirements(RequirementStatus status) {
        throw new RuntimeException();
    }

    public boolean isVisited(Element element) {
        throw new RuntimeException();
    }

    public Long getVisitCount(Element element) {
        throw new RuntimeException();
    }
}
