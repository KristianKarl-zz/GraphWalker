package org.tigris.mbt.conditions;

public class TimeDuration implements StopCondition {

	private long end_time;

	public boolean isFulfilled() {
		return this.end_time < System.currentTimeMillis();
	}

	public TimeDuration(long seconds) {
		this.end_time = seconds*1000 + System.currentTimeMillis();
	}
}
