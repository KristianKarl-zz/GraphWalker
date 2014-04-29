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
package org.graphwalker.io.factory;

import org.apache.commons.io.FileUtils;
import org.graphwalker.core.Bundle;
import org.graphwalker.core.Model;
import org.graphwalker.core.SimpleModel;
import org.graphwalker.io.common.ResourceException;
import org.graphwalker.io.common.ResourceUtils;
import org.graphwalker.core.model.*;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;

import java.io.InputStream;
import java.nio.file.Path;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nils Olsson
 */
public final class GraphMLModelFactory extends AbstractModelFactory {

    // TODO: Update support for keywords

    private static final String FILE_TYPE = "graphml";
    private Map<String, String> vertexNames = new HashMap<String, String>();
    /**
     * <p>Constructor for GraphmlModelFactory.</p>
     */
    public GraphMLModelFactory() {
        super("**/*.graphml");
    }

    /** {@inheritDoc} */
    public boolean accept(Path path) {
        return path.toFile().toString().endsWith(FILE_TYPE);
    }

    /** {@inheritDoc} */
    public boolean validate(Path path) {
        return true; // TODO: implement
    }

    /**
     * {@inheritDoc}
     * <p/>
     * <p>create.</p>
     */
    public Model create(String filename) {
        try {
            File file = ResourceUtils.getResourceAsFile(filename);
            if (file.isDirectory()) {
                Model model = new SimpleModel(false);
                for (Object fileObject : FileUtils.listFiles(file, new String[]{FILE_TYPE}, true)) {
                    model = model.addModel(parse(new FileInputStream((File) fileObject)), false);
                }
                model.refresh();
                return model;
            } else {
                return parse(new FileInputStream(file));
            }
        } catch (Exception e) {
            try {
                return parse(ResourceUtils.getResourceAsStream(filename));
            } catch (ResourceException x) {
                throw new ModelFactoryException(x);
            }
        }
    }

    private Model parse(InputStream inputStream) {
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document;

        try {
            document = saxBuilder.build(inputStream);
        } catch (Exception e) {
            throw new ModelFactoryException(e);
        }

        Model model = new SimpleModel();

        if (null != document) {
            for (Iterator nodeElements = document.getDescendants(new ElementFilter("node")); nodeElements.hasNext(); ) {
                Element nodeElement = (Element) nodeElements.next();
                String text = null;
                for (Iterator nodeLabels = nodeElement.getDescendants(new ElementFilter("NodeLabel")); nodeLabels.hasNext(); ) {
                    Element nodeLabel = (Element) nodeLabels.next();
                    text = nodeLabel.getTextTrim();
                }
                String vertexId = nodeElement.getAttribute("id").getValue();
                model = parseVertex(model, vertexId, text);
            }
            for (Iterator edgeElements = document.getDescendants(new ElementFilter("edge")); edgeElements.hasNext(); ) {
                Element edgeElement = (Element) edgeElements.next();
                Vertex source = model.getVertex(vertexNames.get(edgeElement.getAttributeValue("source")));
                Vertex target = model.getVertex(vertexNames.get(edgeElement.getAttributeValue("target")));
                String text = null;
                for (Iterator edgeLabels = edgeElement.getDescendants(new ElementFilter("EdgeLabel")); edgeLabels.hasNext(); ) {
                    Element edgeLabel = (Element) edgeLabels.next();
                    text = edgeLabel.getTextTrim();
                }
                String edgeId = edgeElement.getAttribute("id").getValue();
                model = parseEdge(model, edgeId, source, target, text);
            }
        }
        return model;
    }

    private Model parseVertex(Model model, String id, String text) {
        String name = null, switchModelId = null, comment = null;
        Boolean blocked = false;
        Set<Requirement> requirements = new HashSet<>();
        if (null != text && !"".equals(text)) {
            Tupel<String, String> commentTupel = parseComment(text);
            comment = commentTupel.getValue();
            Tupel<Boolean, String> blockedTupel = parseBlocked(commentTupel.getReminder());
            blocked = blockedTupel.getValue();
            Tupel<String, String> switchModelIdTupel = parseSwitchModelId(commentTupel.getReminder());
            switchModelId = switchModelIdTupel.getValue();
            Tupel<Set<Requirement>, String> requirementsTupel = parseRequirements(switchModelIdTupel.getReminder());
            requirements = requirementsTupel.getValue();
            name = getLabel(requirementsTupel.getReminder());
        }
        vertexNames.put(id, name);
        return model.addVertex(new Vertex(name, requirements)); //, blocked, comment, switchModelId, ));
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
        Pattern pattern = Pattern.compile(ResourceUtils.getText(Bundle.NAME, "label.switch.model") + "\\s*\\((.*)\\)", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
        String switchModelId = "";
        if (matcher.find()) {
            switchModelId = matcher.group(1);
        }
        return new Tupel<String, String>(switchModelId, matcher.replaceAll("").trim());
    }

    private Tupel<Set<Requirement>, String> parseRequirements(String text) {
        Pattern pattern = Pattern.compile(ResourceUtils.getText(Bundle.NAME, "label.requirement") + "\\s*\\((.*)\\)", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
        Set<Requirement> requirements = new HashSet<Requirement>();
        while (matcher.find()) {
            String id = matcher.group(1);
            requirements.add(new Requirement(id));
        }
        return new Tupel<Set<Requirement>, String>(requirements, matcher.replaceAll("").trim());
    }

    private String getLabel(String text) {
        Pattern pattern = Pattern.compile("(\\w+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private Model parseEdge(Model model, String id, Vertex source, Vertex target, String text) {
        String name = null, comment = null;
        Boolean blocked = false;
        Guard guard = new Guard("true");
        Set<Action> actions = new HashSet<Action>();
        if (null != text && !"".equals(text)) {
            Tupel<String, String> commentTupel = parseComment(text);
            comment = commentTupel.getValue();
            Tupel<Boolean, String> blockedTupel = parseBlocked(commentTupel.getReminder());
            blocked = blockedTupel.getValue();
            Tupel<Guard, String> guardTupel = parseEdgeGuard(blockedTupel.getReminder());
            guard = guardTupel.getValue();
            Tupel<Set<Action>, String> actionsTupel = parseEdgeActions(guardTupel.getReminder());
            actions = actionsTupel.getValue();
            name = getLabel(actionsTupel.getReminder());
        }
        return model.addEdge(new Edge(name, source, target, guard, actions, blocked, 1.0d));
    }

    private Tupel<Boolean, String> parseBlocked(String text) {
        Pattern pattern = Pattern.compile(ResourceUtils.getText(Bundle.NAME, "label.blocked"), Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
        Boolean blocked = false;
        if (matcher.find()) {
            blocked = true;
        }
        return new Tupel<Boolean, String>(blocked, matcher.replaceAll("").trim());
    }

    private Tupel<Guard, String> parseEdgeGuard(String text) {
        Pattern pattern = Pattern.compile("\\[(.+)\\]", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        Guard guard = new Guard("true");
        if (matcher.find()) {
            String script = matcher.group(1).trim();
            guard = new Guard(script);
        }
        return new Tupel<Guard, String>(guard, matcher.replaceAll("").trim());
    }

    private Tupel<Set<Action>, String> parseEdgeActions(String text) {
        Pattern pattern = Pattern.compile("/([^\\[]+)");
        Matcher matcher = pattern.matcher(text);
        Set<Action> actions = new HashSet<Action>();
        if (matcher.find()) {
            for (String action : matcher.group(1).split(";")) {
                String script = action.trim();
                actions.add(new Action(script));
            }
        }
        return new Tupel<Set<Action>, String>(actions, matcher.replaceAll("").trim());
    }

    private class Tupel<V, R> {

        private final V value;
        private final R reminder;

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
