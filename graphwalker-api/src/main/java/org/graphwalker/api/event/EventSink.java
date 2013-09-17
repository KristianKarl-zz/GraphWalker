package org.graphwalker.api.event;

import org.graphwalker.api.model.Edge;
import org.graphwalker.api.model.ModelElement;
import org.graphwalker.api.model.Requirement;
import org.graphwalker.api.model.Vertex;

/**
 * @author Nils Olsson
 */
public interface EventSink {
    void walking(ModelElement element);
    void stepFailed(ModelElement element, Throwable cause);
    void statusChanged(Requirement requirement);
    void statusChanged(ModelElement element);
    void edgeAdded(Edge edge);
    void edgeRemoved(Edge edge);
    void vertexAdded(Vertex vertex);
    void vertexRemoved(Vertex vertex);
}
