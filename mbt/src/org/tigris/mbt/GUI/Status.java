package org.tigris.mbt.GUI;

public class Status {
	boolean running = false;
	boolean next = false;
	boolean stopped = true;
	boolean paused = false;
	
	public boolean isNext() {
		return next;
	}
	public void setNext() {
		next = true;
		paused = false;
		stopped = false;
		running = false;
	}
	public boolean isRunning() {
		return running;
	}
	public void setRunning() {
		next = false;
		paused = false;
		stopped = false;
		running = true;
	}
	public boolean isStopped() {
		return stopped;
	}
	public void setStopped() {
		next = false;
		paused = false;
		stopped = true;
		running = false;
	}
	public boolean isPaused() {
		return paused;
	}
	public void setPaused() {
		next = false;
		paused = true;
		stopped = false;
		running = false;
	}

}
