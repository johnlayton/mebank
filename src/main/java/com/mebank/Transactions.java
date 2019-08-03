package com.mebank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    /**
     * Create a tally from the supplied stream of transactions, represented as strings.
     * <p>
     * The stream of strings are first mapped to the java representation of a transaction using the
     * parse method.
     * <p>
     * The transactions are filtered to produce a working list of current transactions containing;
     * all payment transactions to or from the specified account between the from and to date,
     * all reversal transactions to or from the specified account after the from date.
     * <p>
     * The final step is to reduce the filtered transactions using the Tally to manage the accumulation
     * and combination.
     *
     * @param transactions a stream of strings representing the raw transaction data.
     * @param accountId    the account for which a tally is required
     * @param from         the date from which to select transactions
     * @param to           the date upto which the effective balance is required.
     * @return a tally containing the account, payment transactions and outstanding unmatched reversals
     */
    static Tally tally(final Stream<String> transactions,
                       final String accountId,
                       final LocalDateTime from,
                       final LocalDateTime to) {

        return transactions.map(Transaction::parse)
                           .filter(Transaction.isFromAccount(accountId)
                                              .or(Transaction.isToAccount(accountId)))
                           .filter(Transaction.isOnOrAfter(from)
                                              .and(Transaction.isOnOrBefore(to).or(Transaction.isReversal())))
                           .reduce(new Tally(accountId), Tally::add, Tally::combine);
    }
}
