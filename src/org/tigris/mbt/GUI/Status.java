package org.tigris.mbt.GUI;

public class Status {
	boolean running = false;
	boolean next = false;
	boolean paused = false;
	
	public boolean isNext() {
		return next;
	}
	public void setNext() {
		next = true;
		paused = false;
		running = false;
	}
	public boolean isRunning() {
		return running;
	}
	public void setRunning() {
		next = false;
		paused = false;
		running = true;
	}
	public boolean isPaused() {
		return paused;
	}
	public void setPaused() {
		next = false;
		paused = true;
		running = false;
	}

}
