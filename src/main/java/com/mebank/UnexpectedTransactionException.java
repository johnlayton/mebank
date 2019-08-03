package com.mebank;

/**
 * Exception representing invalid state when tallying transactions.
 */
public class UnexpectedTransactionException
        extends RuntimeException {
    /**
     * Create an UnexpectedTransactionException.
     * @param message the String message for the exception
     */
    public UnexpectedTransactionException(final String message) {
        super(message);
    }
}
