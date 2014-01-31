package org.graphwalker.webrenderer.geometry.layout;

/*
 * #%L
 * GraphWalker Web Renderer
 * %%
 * Copyright (C) 2011 - 2014 GraphWalker
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

import org.graphwalker.core.Model;
import org.graphwalker.webrenderer.geometry.Edge;
import org.graphwalker.webrenderer.geometry.Label;
import org.graphwalker.webrenderer.geometry.Point;
import org.graphwalker.webrenderer.geometry.Vertex;

import java.util.*;

/**
 * @author Nils Olsson
 */
public class SpringEmbedder {

    private final Model model;
    private final double edgeLength;
    private final double force;
    private final double friction;
    private final double[][] changes;
    private final List<Vertex> vertices;
    private final List<Edge> edges;

    public SpringEmbedder(Model model) {
        this(model, 0.3, 0.01, 0.85);
    }

    public SpringEmbedder(Model model, double edgeLength, double force, double friction) {
        this.model = model;
        this.edgeLength = edgeLength;
        this.force = force;
        this.friction = friction;
        this.changes = new double[model.getVertices().size()][2];
        this.vertices = new ArrayList<>(model.getVertices().size());
        this.edges = new ArrayList<>(model.getEdges().size());
        initialize(model);
        embedSprings();
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    private void initialize(Model model) {
        Random random = new Random();
        Map<org.graphwalker.core.model.Vertex, Vertex> vertexMap = new HashMap<>();
        for (org.graphwalker.core.model.Vertex vertex : model.getVertices()) {
            vertexMap.put(vertex, new Vertex(UUID.randomUUID()
                    , vertex.getName()
                    , 100 + random.nextInt(1000)
                    , 100 + random.nextInt(1000)
                    , 100
                    , 100));
        }
        for (org.graphwalker.core.model.Edge edge : model.getEdges()) {
            Edge newEdge = new Edge(UUID.randomUUID()
                    , new Label(edge.getName(), 0, 0)
                    , vertexMap.get(edge.getSourceVertex())
                    , vertexMap.get(edge.getTargetVertex())
                    , new Point(vertexMap.get(edge.getSourceVertex()).getX(), vertexMap.get(edge.getSourceVertex()).getY())
                    , new Point(vertexMap.get(edge.getTargetVertex()).getX(), vertexMap.get(edge.getTargetVertex()).getY())
                    , new ArrayList<Point>());
            vertexMap.get(edge.getSourceVertex()).addEdge(newEdge);
            vertexMap.get(edge.getTargetVertex()).addEdge(newEdge);
            edges.add(newEdge);
        }
        vertices.addAll(vertexMap.values());
    }

    private void embedSprings() {
        for (int i = 0; i < vertices.size(); i++) {
            Vertex vertex = vertices.get(i);
            for (Edge edge : vertex.getEdges()) {
                double dx = (vertex.getX() - edge.getTarget().getX()); //TODO:
                double dy = (vertex.getY() - edge.getTarget().getY()); //TODO:
                double change = Math.hypot(dx, dy);
                if (change == 0) {
                    dx = 1.0;
                    dy = 0.0;
                } else {
                    dx /= change;
                    dy /= change;
                }
                change = (change - edgeLength) / edgeLength;
                dx *= change * force;
                dy *= change * force;
                changes[i][0] -= dx;
                changes[i][1] -= dy;
            }
        }
        for (int i = 0; i < vertices.size(); i++) {
            for (int j = 0; j < 2; j++) {
                changes[i][j] *= friction;
            }
        }
        for (int i = 0; i < vertices.size(); i++) {
            vertices.get(i).translate(Math.round(changes[i][0]), Math.round(changes[i][1]));
        }
    }

}
