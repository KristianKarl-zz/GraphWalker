package org.graphwalker.conditions;

/**
 * Stops test execution after a certain amount of time has passed.
 * 
 * @author Johan Tejle
 * 
 */
public class TimeDuration extends StopCondition {

	private double duration;
	private double start_time;

	public boolean isFulfilled() {
		return getFulfilment() >= 0.99999;
	}

	public TimeDuration(long seconds) {
		this.start_time = System.currentTimeMillis();
		this.duration = seconds * 1000;
	}

	public double getFulfilment() {
		return (System.currentTimeMillis() - this.start_time) / this.duration;
	}

	public String toString() {
		return "DURATION=" + (duration / 1000) + "s";
	}

}
