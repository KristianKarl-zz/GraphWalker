package org.graphwalker.api.event;

/**
 * @author Nils Olsson
 */
public interface EventSource {
    void addEventSink(EventSink eventSink);
    void removeEventSink(EventSink eventSink);
}
