package org.tigris.mbt.GUI;

import org.apache.log4j.Logger;
import org.tigris.mbt.Util;

public class Status {
	static private Logger log = Util.setupLogger( Status.class );
	private int state = stopped;
	
	public static final int running = 1;
	public static final int next = 2;
	public static final int paused = 4;
	public static final int stopped = 8;
	public static final int executingJavaTest = 16;
	public static final int executingSoapTest = 32;
	
	
	public boolean isExecutingSoapTest() {
		log.debug( "State: " + state );
		return (state & executingSoapTest) == executingSoapTest;
	}
	public boolean isExecutingJavaTest() {
		log.debug( "State: " + state );
		return (state & executingJavaTest) == executingJavaTest;
	}
	public boolean isStopped() {
		log.debug( "State: " + state );
		return (state & stopped) == stopped;
	}
	public boolean isNext() {
		log.debug( "State: " + state );
		return (state & next) == next;
	}
	public boolean isRunning() {
		log.debug( "State: " + state );
		return (state & running) == running;
	}
	public boolean isPaused() {
		log.debug( "State: " + state );
		return (state & paused) == paused;
	}
	public void setState( int state ) {
		log.debug( "Set the state with: " + state );
		this.state |= state;
		log.debug( "State: " + this.state );
	}
	public void unsetState(int state) {
		log.debug( "Unset the state with: " + state );
		if ( (this.state & state) == state ) {
			this.state ^= state;
		}
		log.debug( "State: " + this.state );
	}
	public void reset() {
		log.debug( "Reset the state to stopped" );
		state = stopped;
	}
}
