package com.mebank;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
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

    Long getCount() {
        return count;
    }

    String getBalance() {
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            return String.format("-$%s", balance.abs().setScale(2, RoundingMode.HALF_UP));
        } else {
            return String.format("$%s", balance.abs().setScale(2, RoundingMode.HALF_UP));
        }
    }

    Tally add(final Transaction transaction) {
        return new Tally(accountId, count + transaction.getCount(), balance.add(transaction.getTransfer(accountId)));
    }

    static Tally combine(final Tally first,
                         final Tally second) {
        return new Tally(first.accountId, first.count + second.count, first.balance.add(second.balance));
    }
}
