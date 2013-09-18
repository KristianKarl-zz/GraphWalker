package org.graphwalker.api.event;

import org.graphwalker.api.graph.Edge;
import org.graphwalker.api.graph.Vertex;

/**
 * @author Nils Olsson
 */
public interface ModelSink extends Sink {
    void edgeAdded(Edge edge);
    void vertexAdded(Vertex vertex);
}
