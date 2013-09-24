package org.graphwalker.core.event;

/**
 * @author Nils Olsson
 */
public interface EventSource<T extends EventSink> {
    void addSink(T sink);
    void removeSink(T sink);
}
