package org.tigris.mbt.conditions;

public class TimeDuration extends StopCondition {

	private double duration;
	private double start_time;

	public boolean isFulfilled() {
		return this.start_time + this.duration < System.currentTimeMillis();
	}

	public TimeDuration(long seconds) {
		this.start_time = System.currentTimeMillis();
		this.duration = seconds * 1000;
	}

	public double getFulfillment() {
		return (System.currentTimeMillis()-this.start_time) / this.duration;
	}
}
