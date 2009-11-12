/**
 * 
 */
package org.tigris.mbt.statistics;

import org.tigris.mbt.graph.AbstractElement;

/**
 * @author Johan Tejle
 * 
 */
public abstract class Statistics {
	public abstract void addProgress(AbstractElement element);

	public abstract int getCurrent();

	public abstract int getMax();
}
