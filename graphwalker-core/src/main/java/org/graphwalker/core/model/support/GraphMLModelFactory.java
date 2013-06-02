/*
 * #%L
 * GraphWalker Core
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
package org.graphwalker.core.model.support;

import org.graphwalker.core.Bundle;
import org.graphwalker.core.model.*;
import org.graphwalker.core.utils.Resource;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>GraphMLModelFactory class.</p>
 */
public final class GraphMLModelFactory extends AbstractModelFactory {

    // TODO: Update support for keywords

    private static final String FILE_TYPE = "graphml";
    private CachedElementFactory elementFactory = new CachedElementFactory();

    /**
     * <p>Constructor for GraphmlModelFactory.</p>
     */
    public GraphMLModelFactory() {
    }

    /**
     * <p>getSupportedFileTypes.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<String> getSupportedFileTypes() {
        return Arrays.asList("**/*.graphml");
    }

    /** {@inheritDoc} */
    public boolean accept(String type) {
        return FILE_TYPE.equalsIgnoreCase(type);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p>create.</p>
     */
    public Model create(String id, String filename, String type) {
        return parse(id, Resource.getResourceAsStream(filename));
    }

    private Model parse(String id, InputStream inputStream) {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document;
        Map<String, Vertex> vertices = new HashMap<String, Vertex>();
        List<Edge> edges = new ArrayList<Edge>();

        try {
            document = saxBuilder.build(inputStream);
        } catch (Exception e) {
            throw new ModelException(e);
        }

        Vertex startVertex = null;

        if (null != document) {
            for (Iterator nodeElements = document.getDescendants(new ElementFilter("node")); nodeElements.hasNext(); ) {
                Element nodeElement = (Element) nodeElements.next();
                String text = null;
                for (Iterator nodeLabels = nodeElement.getDescendants(new ElementFilter("NodeLabel")); nodeLabels.hasNext(); ) {
                    Element nodeLabel = (Element) nodeLabels.next();
                    text = nodeLabel.getTextTrim();
                }
                String vertexId = nodeElement.getAttribute("id").getValue();
                Vertex vertex = parseVertex(vertexId, text);
                vertices.put(vertex.getId(), vertex);
                if (Resource.getText(Bundle.NAME, "start.vertex").equalsIgnoreCase(vertex.getName())) {
                    if (null != startVertex) {
                        throw new ModelException(Resource.getText(Bundle.NAME, "exception.duplicate.start.vertex"));
                    }
                    startVertex = vertex;
                }
            }
            for (Iterator edgeElements = document.getDescendants(new ElementFilter("edge")); edgeElements.hasNext(); ) {
                Element edgeElement = (Element) edgeElements.next();
                Vertex source = vertices.get(edgeElement.getAttributeValue("source"));
                Vertex target = vertices.get(edgeElement.getAttributeValue("target"));
                String text = null;
                for (Iterator edgeLabels = edgeElement.getDescendants(new ElementFilter("EdgeLabel")); edgeLabels.hasNext(); ) {
                    Element edgeLabel = (Element) edgeLabels.next();
                    text = edgeLabel.getTextTrim();
                }
                String edgeId = edgeElement.getAttribute("id").getValue();
                edges.add(parseEdge(edgeId, source, target, text));
            }
        }
        return elementFactory.createModel(id, new ArrayList<Vertex>(vertices.values()), edges, startVertex);
    }

    private Vertex parseVertex(String id, String text) {
        String name = null, switchModelId = null, comment = null;
        Boolean blocked = false;
        List<Requirement> requirements = null;
        if (null != text && !"".equals(text)) {
            Tupel<String, String> commentTupel = parseComment(text);
            comment = commentTupel.getValue();
            Tupel<Boolean, String> blockedTupel = parseBlocked(commentTupel.getReminder());
            blocked = blockedTupel.getValue();
            Tupel<String, String> switchModelIdTupel = parseSwitchModelId(commentTupel.getReminder());
            switchModelId = switchModelIdTupel.getValue();
            Tupel<List<Requirement>, String> requirementsTupel = parseRequirements(switchModelIdTupel.getReminder());
            requirements = requirementsTupel.getValue();
            name = getLabel(requirementsTupel.getReminder());
        }
        return elementFactory.createVertex(id, name, blocked, comment, switchModelId, requirements);
    }

    private Tupel<String, String> parseComment(String text) {
        Pattern pattern = Pattern.compile("/\\*.*\\*/", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
        String comment = "";
        if (matcher.find()) {
            comment = matcher.group(0);
        }
        return new Tupel<String, String>(comment, matcher.replaceAll("").trim());
    }

    private Tupel<String, String> parseSwitchModelId(String text) {
        Pattern pattern = Pattern.compile(Resource.getText(Bundle.NAME, "label.switch.model") + "\\s*\\((.*)\\)", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
        String switchModelId = "";
        if (matcher.find()) {
            switchModelId = matcher.group(1);
        }
        return new Tupel<String, String>(switchModelId, matcher.replaceAll("").trim());
    }

    private Tupel<List<Requirement>, String> parseRequirements(String text) {
        Pattern pattern = Pattern.compile(Resource.getText(Bundle.NAME, "label.requirement") + "\\s*\\((.*)\\)", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
        List<Requirement> requirements = new ArrayList<Requirement>();
        while (matcher.find()) {
            String id = matcher.group(1);
            requirements.add(elementFactory.createRequirement(id, id));
        }
        return new Tupel<List<Requirement>, String>(requirements, matcher.replaceAll("").trim());
    }

    private String getLabel(String text) {
        Pattern pattern = Pattern.compile("(\\w+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private Edge parseEdge(String id, Vertex source, Vertex target, String text) {
        String name = null, comment = null;
        Boolean blocked = false;
        Guard guard = null;
        List<Action> actions = null;
        if (null != text && !"".equals(text)) {
            Tupel<String, String> commentTupel = parseComment(text);
            comment = commentTupel.getValue();
            Tupel<Boolean, String> blockedTupel = parseBlocked(commentTupel.getReminder());
            blocked = blockedTupel.getValue();
            Tupel<Guard, String> guardTupel = parseEdgeGuard(blockedTupel.getReminder());
            guard = guardTupel.getValue();
            Tupel<List<Action>, String> actionsTupel = parseEdgeActions(guardTupel.getReminder());
            actions = actionsTupel.getValue();
            name = getLabel(actionsTupel.getReminder());
        }
        return elementFactory.createEdge(id, name, blocked, comment, 1.0, source, target, guard, actions);
    }

    private Tupel<Boolean, String> parseBlocked(String text) {
        Pattern pattern = Pattern.compile(Resource.getText(Bundle.NAME, "label.blocked"), Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
        Boolean blocked = false;
        if (matcher.find()) {
            blocked = true;
        }
        return new Tupel<Boolean, String>(blocked, matcher.replaceAll("").trim());
    }

    private Tupel<Guard, String> parseEdgeGuard(String text) {
        Pattern pattern = Pattern.compile("\\[(.+)\\]");
        Matcher matcher = pattern.matcher(text);
        Guard guard = null;
        if (matcher.find()) {
            String id = matcher.group(1).trim();
            guard = elementFactory.createGuard(id, id);
        }
        return new Tupel<Guard, String>(guard, matcher.replaceAll("").trim());
    }

    private Tupel<List<Action>, String> parseEdgeActions(String text) {
        List<Action> edgeActions = new ArrayList<Action>();
        Pattern pattern = Pattern.compile("/([^\\[]+)");
        Matcher matcher = pattern.matcher(text);
        List<Action> actions = new ArrayList<Action>();
        if (matcher.find()) {
            for (String action : matcher.group(1).split(";")) {
                String id = action.trim();
                actions.add(elementFactory.createAction(id, id));
            }
        }
        return new Tupel<List<Action>, String>(actions, matcher.replaceAll("").trim());
    }

    private class Tupel<V, R> {

        private V value;
        private R reminder;

        private Tupel(V value, R reminder) {
            this.value = value;
            this.reminder = reminder;
        }

        private V getValue() {
            return value;
        }

        public R getReminder() {
            return reminder;
        }
    }

}
