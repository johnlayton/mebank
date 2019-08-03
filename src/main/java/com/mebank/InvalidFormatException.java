package com.mebank;

/**
 * Exception representing invalid state for the transaction.
 */
public class InvalidFormatException
        extends RuntimeException {
    /**
     * Create an InvalidFormat specifying and appropriate format violation.
     * @param message the String message for the exception
     */
    public InvalidFormatException(final String message) {
        super(message);
    }
}
