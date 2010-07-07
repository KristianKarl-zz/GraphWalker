package org.graphwalker.exceptions;

/**
 * This exception should be used whenever data extraction from the data space
 * from a EFSM machine fails.
 * 
 */
public class InvalidDataException extends Exception {

	/**
	 * @param message
	 *          A string containing a message describing the failure in detail.
	 */
	public InvalidDataException(String message) {
		super(message);
	}

	private static final long serialVersionUID = -2445201301297201999L;
}
