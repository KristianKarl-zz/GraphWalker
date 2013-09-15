package org.graphwalker.api.model;

import java.util.Deque;

/**
 * @author Nils Olsson
 */
public interface Path<T extends ModelElement> extends Deque<T> {
}
