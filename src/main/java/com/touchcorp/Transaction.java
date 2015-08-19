package com.touchcorp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

/**
 * Class representing the credit card transaction.
 */
public final class Transaction {
    private static final int TRANSACTION_ELEMENT_COUNT = 3;
    private final String hash;
    private final LocalDateTime date;
    private final BigDecimal amount;

    /**
     * Private construction for the transaction.
     *
     * @param creditCardHash    the String representing the credit card hash
     * @param localDate         the LocalDateTime representing the date and time of the transaction
     * @param transactionAmount the BigDecimal representing the amount for this transaction
     */
    private Transaction(final String creditCardHash,
                        final LocalDateTime localDate,
                        final BigDecimal transactionAmount) {
        this.hash = creditCardHash;
        this.date = localDate;
        this.amount = transactionAmount;
    }

    /**
     * Parse the string representation of the credit card transaction into a object.
     * <p>
     * "hash,date,amount" eg. '10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, 10.00'
     *
     * @param input the string transaction
     * @return the constructed Transaction
     */
    public static Transaction parse(final String input) {
        if (null == input) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        final String[] elements = input.split(",");
        if (TRANSACTION_ELEMENT_COUNT != elements.length) {
            throw new InvalidFormatException("Invalid transaction details " + input);
        }
        final String hash = elements[0].trim();
        if (hash.isEmpty()) {
            throw new InvalidFormatException("Missing credit card hash " + input);
        }
        if (!hash.matches("[0-9a-z]+")) {
            throw new InvalidFormatException("Invalid credit card hash " + input);
        }
        final LocalDateTime date = LocalDateTime.parse(elements[1].trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        final BigDecimal amount = new BigDecimal(elements[2].trim());
        return new Transaction(hash, date, amount);
    }

    public String getHash() {
        return hash;
    }

    public long getDay() {
        return date.getLong(ChronoField.EPOCH_DAY);
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
