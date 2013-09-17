package org.graphwalker.api;

import org.graphwalker.api.graph.DirectedGraph;
import org.graphwalker.api.graph.Element;
import org.graphwalker.api.graph.Path;
import org.graphwalker.api.model.State;
import org.graphwalker.api.model.Transition;

import java.util.Set;

/**
 * @author Nils Olsson
 */
public interface Model extends DirectedGraph {
    <S extends State> S addState(S state);
    <S extends State> S getState(String name);
    <S extends State> Set<S> getStates();
    <T extends Transition> T addTransition(T transition);
    <T extends Transition> T getTransition(String name);
    <T extends Transition> T getTransitions();
    <E extends Element> Set<E> getConnectedComponent(E element);
    <E extends Element> int getShortestDistance(E source, E target);
    <E extends Element> int getMaximumDistance(E target);
    <P extends Path, E extends Element> P getShortestPath(E source, E target);
}
