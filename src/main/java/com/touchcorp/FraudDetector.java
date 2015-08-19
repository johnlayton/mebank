package com.touchcorp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Map.Entry;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;

/**
 * Utility class with functions for fraud detection.
 */
public final class FraudDetector {

    /**
     * Private constructor for utility class.
     */
    private FraudDetector() {
    }

    /**
     * Utility method for analysing a list of credit card transactions.
     * <p>
     * This method will find within a list of transactions of the csv format;
     * "hash,date,amount" eg. '10d7ce2f43e35fa57d1bbf8b1e2, 2014-04-29T13:15:54, 10.00'
     * those credit cards whose total across all transactions for a given date exceed the specified threshold
     * are to be returned, marking them as fraudulent
     *
     * @param transactions a list of credit card transactions
     * @param date         the date on which to check
     * @param amount       the threshold amount which the total amount for all transactions may not exceed.
     * @return a list of hashed credit card numbers that have been identified as fraudulent.
     */
    public static List<String> findCardsExceedingThreshold(final List<String> transactions,
                                                           final LocalDate date,
                                                           final double amount) {
        if (null == transactions) {
            throw new IllegalArgumentException("transactions cannot be null");
        }
        if (null == date) {
            throw new IllegalArgumentException("date cannot be null");
        }
        if (0 > amount) {
            throw new IllegalArgumentException("threshold amount cannot be < 0");
        }

        final long transactionDate = date.getLong(ChronoField.EPOCH_DAY);
        final BigDecimal threshold = new BigDecimal(amount);

        return transactions.stream()
                // Turn string of transaction log to object model
                .map(Transaction::parse)
                // Find all transactions for supplied date
                .filter(transaction -> {
                    return transaction.getDay() == transactionDate;
                })
                // Group transactions by hash and sum transaction totals
                .collect(groupingBy(Transaction::getHash,
                        reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)))
                // Find entries where daily total is above the threshold
                .entrySet().stream().filter(e -> {
                    return e.getValue().compareTo(threshold) > 0;
                })
                // Return a list of credit card hash values from the transaction
                .map(Entry::getKey).collect(toList());
    }
}
