/**
 * 
 */
package org.tigris.mbt;

import org.tigris.mbt.statistics.*;

import java.util.Enumeration;
import java.util.Hashtable;

import edu.uci.ics.jung.graph.impl.AbstractElement;

/**
 * @author Johan Tejle
 *
 */
public class StatisticsManager {

	Hashtable counters;
	/**
	 * 
	 */
	public StatisticsManager() {
		this.counters = new Hashtable();
	}
	
	public void addStatisicsCounter(String name, Statistics statisicsCounter)
	{
		counters.put(name, statisicsCounter);
	}
	
	public void addProgress(AbstractElement element)
	{
		for(Enumeration e=counters.keys();e.hasMoreElements();)
		{
			String key = (String)e.nextElement();
			Statistics stats = (Statistics) counters.get(key);
			stats.addProgress(element);
		}
	}

	public int[] getStatistics(String key)
	{
		Statistics stats = (Statistics) counters.get(key);
		int[] retur = {stats.getCurrent(),stats.getMax()};
		return retur;
	}
	
	public String[][] getStatistics()
	{
		String[][] retur = new String[counters.size()][3];
		int statIndex = 0;
		for(Enumeration e=counters.keys();e.hasMoreElements();)
		{
			String key = (String)e.nextElement();
			int[] stats = getStatistics(key);
			retur[statIndex][0] = key;
			retur[statIndex][1] = ""+ stats[0];
			retur[statIndex][2] = ""+ stats[1];
			statIndex++;
		}
		return retur;
		
	}
}
