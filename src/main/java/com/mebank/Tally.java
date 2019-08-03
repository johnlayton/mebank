package com.mebank;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Represents the intermediate and final reckoning of both;
 *   Effective transaction count, and;
 *   Relative balance change,
 * for the specified account.
 */
final class Tally {

    private final String accountId;
    private final Long count;
    private final BigDecimal balance;

    Tally(final String accountId,
          final Long count,
          final BigDecimal balance) {
        this.accountId = accountId;
        this.count = count;
        this.balance = balance;
    }

    /**
     * @return the net number of effective transactions.
     */
    Long getCount() {
        return count;
    }

    /**
     * @return the string representation of the change in balance useful for reporting.
     */
    String getBalance() {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            return String.format("-$%s", balance.abs().setScale(2, RoundingMode.HALF_UP));
        } else {
            return String.format("$%s", balance.abs().setScale(2, RoundingMode.HALF_UP));
        }
    }

    /**
     * Accumulate the details from the supplied transaction onto a new Tally.
     *
     * The count is calculated as described in the updateCount method of transaction.
     * The balance is calculated as described in the updateBalance method of transaction.
     *
     * @param transaction the transaction to add to the talley.
     * @return the result of adding the transaction to the tally.
     */
    Tally add(final Transaction transaction) {
        return new Tally(accountId, transaction.updateCount(count), transaction.updateBalance(accountId, balance));
    }

    /**
     * Binary operator to combine two different tally(s).
     *
     * @param first the first tally from which to extract count and balance change.
     * @param second the second tally from which to extract count and balance change.
     * @return the result of combining two tally objects, both the count and the balance
     *         are the sum of the respective values in each provided tally.
     */
    static Tally combine(final Tally first,
                         final Tally second) {
        return new Tally(first.accountId, first.count + second.count, first.balance.add(second.balance));
    }
}
