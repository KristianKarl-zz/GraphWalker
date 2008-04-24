/**
 * 
 */
package org.tigris.mbt.statistics;

import edu.uci.ics.jung.graph.impl.AbstractElement;

/**
 * @author Johan Tejle
 *
 */
public abstract class Statistics {
	public abstract void addProgress(AbstractElement element);
	public abstract int getCurrent();
	public abstract int getMax();
}
