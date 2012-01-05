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
package org.graphwalker.core.model;

import org.graphwalker.core.util.Resource;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>ModelFactory class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class ModelFactory {

    private ModelFactory() {
    }

    /**
     * <p>create.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @param filename a {@link java.lang.String} object.
     * @return a {@link org.graphwalker.core.model.Model} object.
     */
    public static Model create(String id, String filename) {
        return new ModelFactory().parse(id, Resource.getFile(filename));
    }

    private Model parse(String id, File file) {
        Model model = new ModelImpl(id);
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document;

        try {
            document = saxBuilder.build(file);
        } catch (Exception e) {
            throw new ModelException(e);
        }

        if (null != document) {
            for (Iterator nodeElements = document.getDescendants(new ElementFilter("node")); nodeElements.hasNext();) {
                Element nodeElement = (Element)nodeElements.next();
                String text = null;
                for (Iterator nodeLabels = nodeElement.getDescendants(new ElementFilter("NodeLabel")); nodeLabels.hasNext();) {
                    Element nodeLabel = (Element)nodeLabels.next();
                    text = nodeLabel.getTextTrim();
                }
                Vertex vertex = new Vertex();
                vertex.setId(nodeElement.getAttribute("id").getValue());
                if (null != text && !"".equals(text)) {
                    parseName(vertex, getLabel(text));
                    parseSwitchModelId(vertex, text);
                    parseRequirements(vertex, text);
                }
                model.addVertex(vertex);
            }
            for (Iterator edgeElements = document.getDescendants(new ElementFilter("edge")); edgeElements.hasNext();) {
                Element edgeElement = (Element)edgeElements.next();
                Vertex source = model.getVertexById(edgeElement.getAttributeValue("source"));
                Vertex target = model.getVertexById(edgeElement.getAttributeValue("target"));
                String text = null;
                for (Iterator edgeLabels = edgeElement.getDescendants(new ElementFilter("EdgeLabel")); edgeLabels.hasNext();) {
                    Element edgeLabel = (Element)edgeLabels.next();
                    text = edgeLabel.getTextTrim();
                }
                Edge edge = new Edge();
                edge.setId(edgeElement.getAttribute("id").getValue());
                if (null != text && !"".equals(text)) {
                    parseName(edge, getLabel(text));
                    parseEdgeGuard(edge, text);
                    parseEdgeActions(edge, text);
                    parseBlocked(edge, text);
                }
                model.addEdge(edge, source, target);
            }
        }
        return model;
    }

    private void parseName(Vertex vertex, String name) {
        if (null != name && !"".equals(name)) {
            vertex.setName(name);
        }
    }

    private void parseName(Edge edge, String name) {
        if (null != name && !"".equals(name)) {
            edge.setName(name);
        }
    }

    private String getLabel(String text) {
        Pattern pattern = Pattern.compile("^(\\w+).*");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String label = matcher.group(1);
            if (!"BLOCKED".equals(label)) {  //TODO: Get label from resource
                return label;
            }
        }
        return null;
    }

    private void parseSwitchModelId(Vertex vertex, String text) {
        Pattern pattern = Pattern.compile("SWITCH_MODEL\\((.*)\\)", Pattern.MULTILINE);  //TODO: Get label from resource
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            vertex.setSwitchModelId(matcher.group(1));
        }
    }
    
    private void parseRequirements(Vertex vertex, String text) {
        Pattern pattern = Pattern.compile("REQ\\((.*)\\)", Pattern.MULTILINE);  //TODO: Get label from resource
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            vertex.addRequirement(new Requirement(matcher.group(1)));
        }
    }

    private void parseEdgeGuard(Edge edge, String text) {
        Pattern pattern = Pattern.compile("^.*\\[(.+)\\].*$");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            edge.setEdgeGuard(new Guard(matcher.group(1)));    
        }
    }
    
    private void parseEdgeActions(Edge edge, String text) {
        List<Action> edgeActions = new ArrayList<Action>();
        Pattern pattern = Pattern.compile("^.*/(.*)$");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            for (String action:  matcher.group(1).split(":")) {
                edgeActions.add(new Action(action));
            }
        }
        edge.setEdgeActions(edgeActions);
    }
    
    private void parseBlocked(Edge edge, String text) {
        Pattern pattern = Pattern.compile("BLOCKED", Pattern.MULTILINE);   //TODO: Get label from resource
        Matcher matcher = pattern.matcher(text);
        edge.setBlocked(matcher.find());
    }

}
