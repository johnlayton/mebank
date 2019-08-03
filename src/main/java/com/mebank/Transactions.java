package com.mebank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class with functions for transactions.
 */
public final class Transactions {

    /**
     * Private constructor for utility class.
     */
    private Transactions() {
    }

    static Tally tally(final Stream<String> transactions,
                       final String accountId,
                       final LocalDateTime from,
                       final LocalDateTime to) {

        final List<Transaction> current = transactions.map(Transaction::parse)
                                                      .filter(Transaction.isFromAccount(accountId)
                                                                         .or(Transaction.isToAccount(accountId)))
                                                      .filter(Transaction.isOnOrAfter(from)
                                                                         .and(Transaction.isOnOrBefore(to)
                                                                                         .or(Transaction.isReversal())))
                                                      .collect(Collectors.toList());

        final LongSummaryStatistics stats = current.stream()
                                                   .filter(Transaction.isPayment())
                                                   .mapToLong(Transaction::getSequence)
                                                   .summaryStatistics();

        return current.stream()
                      .filter(Transaction.isPayment()
                                         .or(Transaction.reversalInSequence(stats.getMin(), stats.getMax())))
                      .reduce(new Tally(accountId, 0L, BigDecimal.ZERO), Tally::add, Tally::combine);
    }
}
