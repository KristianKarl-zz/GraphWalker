package org.tigris.mbt.conditions;

public class TimeDuration extends StopCondition {

	private long end_time;
	private long start_time;

	public boolean isFulfilled() {
		return this.end_time < System.currentTimeMillis();
	}

	public TimeDuration(long seconds) {
		this.start_time = System.currentTimeMillis();
		this.end_time = seconds*1000 + this.start_time;
	}

	public double getFulfillment() {
		return (System.currentTimeMillis()-this.start_time) / (this.end_time-this.start_time);
	}
}
