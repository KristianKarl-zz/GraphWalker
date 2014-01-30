package org.graphwalker.webrenderer.geometry;

import java.util.List;
import java.util.UUID;

/**
 * @author Nils Olsson
 */
public class Edge {

    private UUID id;
    private Label label;
    private Vertex source;
    private Vertex target;
    private Point pathSource;
    private Point pathTarget;
    private List<Point> pathPoints;

    public Edge(UUID id, Label label, Vertex source, Vertex target, Point pathSource, Point pathTarget, List<Point> pathPoints) {
        this.id = id;
        this.label = label;
        this.source = source;
        this.target = target;
        this.pathSource = pathSource;
        this.pathTarget = pathTarget;
        this.pathPoints = pathPoints;
    }

    public UUID getId() {
        return id;
    }

    public Label getLabel() {
        return label;
    }

    public Vertex getSource() {
        return source;
    }

    public Vertex getTarget() {
        return target;
    }

    public Point getPathSource() {
        return pathSource;
    }

    public Point getPathTarget() {
        return pathTarget;
    }

    public List<Point> getPathPoints() {
        return pathPoints;
    }
}
