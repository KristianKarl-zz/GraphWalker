package org.graphwalker.api.model;

import java.util.Set;

/**
 * @author Nils Olsson
 */
public interface Vertex extends ModelElement {
    <T extends Requirement> Set<T> getRequirements();
}
