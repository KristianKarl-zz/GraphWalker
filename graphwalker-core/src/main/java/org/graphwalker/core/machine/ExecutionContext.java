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
import org.graphwalker.core.script.ScriptContext;
import org.graphwalker.core.statistics.ExecutionProfiler;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.*;

/**
 * @author Nils Olsson                                                            s
 */
public final class ExecutionContext {

    private final static String DEFAULT_SCRIPT_LANGUAGE = "JavaScript";

    private final Set<Model> models;
    private final PathGenerator pathGenerator;
    private final ScriptEngine scriptEngine;
    private final ScriptContext context = new ScriptContext();
    private final ExecutionProfiler profiler = new ExecutionProfiler();
    private final Map<Model, Element> currentSteps = new HashMap<Model, Element>();
    private final Set<Requirement> requirements;
    private Map<Requirement, RequirementStatus> requirementStatus = new HashMap<Requirement, RequirementStatus>();
    private final Map<Element, Long> elementVisitCount = new HashMap<Element, Long>();
    private Long totalVisitCount = 0l;
    private Set<Edge> visitedEdges = new HashSet<Edge>();
    private Set<Vertex> visitedVertices = new HashSet<Vertex>();

    public ExecutionContext(Model model, PathGenerator pathGenerator) {
        this(new HashSet<Model>(Arrays.asList(model)), pathGenerator);
    }

    public ExecutionContext(Model model, PathGenerator pathGenerator, String language) {
        this(new HashSet<Model>(Arrays.asList(model)), pathGenerator, language);
    }

    public ExecutionContext(Set<Model> models, PathGenerator pathGenerator) {
        this(models, pathGenerator, DEFAULT_SCRIPT_LANGUAGE);
    }

    public ExecutionContext(Set<Model> models, PathGenerator pathGenerator, String language) {
        this.models = models;
        this.pathGenerator = pathGenerator;
        this.scriptEngine = createScriptEngine(language);
        this.requirements = aggregateRequirements();
    }

    private Set<Requirement> aggregateRequirements() {
        Set<Requirement> requirements = new HashSet<Requirement>();
        for (Model model: models) {
            requirements.addAll(model.getRequirements());
            for (Requirement requirement: model.getRequirements()) {
                requirementStatus.put(requirement, RequirementStatus.NOT_COVERED);
            }
        }
        return requirements;
    }

    private ScriptEngine createScriptEngine(String language) {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(language);
        scriptEngine.setContext(context);
        return scriptEngine;
    }

    public PathGenerator getPathGenerator() {
        return pathGenerator;
    }

    public StopCondition getStopCondition() {
        return pathGenerator.getStopCondition();
    }

    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    public ScriptContext getScriptContext() {
        return context;
    }

    public ExecutionProfiler getProfiler() {
        return profiler;
    }

    public Model getCurrentModel() {
        throw new RuntimeException();
    }

    public Element getCurrentElement() {
        throw new RuntimeException();
    }

    public List<Requirement> getRequirements() {
        return new ArrayList<Requirement>(requirements);
    }

    public List<Requirement> getRequirements(RequirementStatus status) {
        Set<Requirement> requirements = new HashSet<Requirement>();
        for (Requirement requirement: getRequirements()) {
            if (requirementStatus.get(requirement).equals(status)) {
                requirements.add(requirement);
            }
        }
        return new ArrayList<Requirement>(requirements);
    }

    public RequirementStatus getRequirementStatus(Requirement requirement) {
        return requirementStatus.get(requirement);
    }

    public void setRequirementStatus(Requirement requirement, RequirementStatus status) {
        requirementStatus.put(requirement, status);
    }

    public boolean isVisited(Element element) {
        return 0 < getVisitCount(element);
    }

    public Long getVisitCount(Element element) {
        return elementVisitCount.get(element);
    }

    public Long getVisitCount() {
        return totalVisitCount;
    }

    public List<Edge> getVisitedEdges() {
        return new ArrayList<Edge>(visitedEdges);
    }

    public List<Vertex> getVisitedVertices() {
        return new ArrayList<Vertex>(visitedVertices);
    }

    public void visit(Element element) {
        this.totalVisitCount++;
        this.elementVisitCount.put(element, getVisitCount(element)+1);
        if (element instanceof Edge) {
            visit((Edge)element);
        } else {
            visit((Vertex)element);
        }
    }

    private void visit(Edge edge) {
        if (!visitedEdges.contains(edge)) {
            visitedEdges.add(edge);
        }
    }

    private void visit(Vertex vertex) {
        if (!visitedVertices.contains(vertex)) {
            visitedVertices.add(vertex);
        }
    }
}
