package com.mebank;

/**
 * Exception representing invalid state when tallying transactions.
 */
public class DuplicateTransactionException
        extends RuntimeException {
    /**
     * Create an UnexpectedTransactionException.
     * @param message the String message for the exception
     */
    public DuplicateTransactionException(final String message) {
        super(message);
    }
}
