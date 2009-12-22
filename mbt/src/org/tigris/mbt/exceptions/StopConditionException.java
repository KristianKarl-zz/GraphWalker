package org.tigris.mbt.exceptions;

/**
 * This exception should be used whenever there is something wrong with the stop
 * condition.
 */
public class StopConditionException extends Exception {

	/**
	 * @param message
	 *          A string containing a message describing the failure in detail.
	 */
	public StopConditionException(String message) {
		super(message);
	}

	private static final long serialVersionUID = -5843521571692420560L;
}
