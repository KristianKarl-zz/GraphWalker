package org.graphwalker.api.event;

import org.graphwalker.api.model.ModelElement;
import org.graphwalker.api.model.Requirement;

/**
 * @author Nils Olsson
 */
public interface EventSink {
    void walking(ModelElement modelElement);
    void stepFailed(ModelElement modelElement);
    void requirementStatusChanged(Requirement requirement);
}
