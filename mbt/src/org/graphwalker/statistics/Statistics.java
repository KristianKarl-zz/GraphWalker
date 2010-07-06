/**
 * 
 */
package org.graphwalker.statistics;

import org.graphwalker.graph.AbstractElement;

/**
 * @author Johan Tejle
 * 
 */
public abstract class Statistics {
	public abstract void addProgress(AbstractElement element);

	public abstract int getCurrent();

	public abstract int getMax();
}
