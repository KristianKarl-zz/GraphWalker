package org.graphwalker.api.event;

import org.graphwalker.api.graph.Element;

/**
 * @author Nils Olsson
 */
public interface MachineSink extends Sink {
    void walking(Element element);
}
