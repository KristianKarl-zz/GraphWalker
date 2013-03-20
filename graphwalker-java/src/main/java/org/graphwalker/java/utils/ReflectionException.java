package org.graphwalker.java.utils;

/**
 * <p>ReflectionException class.</p>
 *
 * @author nilols
 * @version $Id: $
 */
public class ReflectionException extends RuntimeException {

    /**
     * <p>Constructor for ReflectionException.</p>
     *
     * @param cause a {@link java.lang.String} object.
     */
    public ReflectionException(String cause) {
        super(cause);
    }

    /**
     * <p>Constructor for ReflectionException.</p>
     *
     * @param cause a {@link java.lang.String} object.
     * @param throwable a {@link java.lang.Throwable} object.
     */
    public ReflectionException(String cause, Throwable throwable) {
        super(cause, throwable);
    }
}

