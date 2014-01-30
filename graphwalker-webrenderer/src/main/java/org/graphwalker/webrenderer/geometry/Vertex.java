package org.graphwalker.webrenderer.geometry;

import java.util.*;

/**
 * @author Nils Olsson
 */
public class Vertex {

    private UUID id;
    private String name;
    private int x;
    private int y;
    private int width;
    private int height;
    private Map<String, Edge> edgeMap = new HashMap<>();

    public Vertex(UUID id, String name, int x, int y, int width, int height) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void translate(long dx, long dy) {
        this.x += dx;
        this.y += dy;
    }

    public void addEdge(Edge edge) {
        edgeMap.put(edge.getId().toString(), edge);
    }

    public List<Edge> getEdges() {
        return new ArrayList<>(edgeMap.values());
    }
}
