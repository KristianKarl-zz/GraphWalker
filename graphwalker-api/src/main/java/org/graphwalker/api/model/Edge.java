package org.graphwalker.api.model;

import java.util.Set;

/**
 * @author Nils Olsson
 */
public interface Edge extends ModelElement {
    <T extends Vertex> T getSource();
    <T extends Vertex> T getTarget();
    <T extends Action> Set<T> getActions();
    <T extends Guard> Set<T> getGuards();
}
