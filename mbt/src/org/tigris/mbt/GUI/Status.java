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
	
	
	public boolean isExecutingJavaTest() {
		return (state & executingJavaTest) == executingJavaTest;
	}
	public boolean isStopped() {
		return (state & stopped) == stopped;
	}
	public boolean isNext() {
		return (state & next) == next;
	}
	public boolean isRunning() {
		return (state & running) == running;
	}
	public boolean isPaused() {
		return (state & paused) == paused;
	}
	public void setState( int state ) {
		log.debug( "Set the state with: " + state );
		this.state |= state;
		log.debug( "State: " + state );
	}
	public void unsetState(int state) {
		log.debug( "Unset the state with: " + state );
		this.state ^= state;
		log.debug( "State: " + state );
	}
	public void reset() {
		log.debug( "Reset the state to stopped" );
		state = stopped;
	}
}
