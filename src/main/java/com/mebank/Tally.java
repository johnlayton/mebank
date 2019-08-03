package com.mebank;

import com.mebank.Transaction.ActionVisitor;
import com.mebank.Transaction.Payment;
import com.mebank.Transaction.Reversal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents the intermediate and final reckoning of both;
 * Payment transaction count, and;
 * Relative balance change,
 * for the specified account.
 */
final class Tally {

    private final String accountId;

    private final Map<String, Transaction> payments;
    private final Map<String, Transaction> reversals;

    Tally(final String accountId) {
        this.accountId = accountId;
        this.payments = new HashMap<>();
        this.reversals = new HashMap<>();
    }

    Tally(final String accountId,
          final Map<String, Transaction> payments,
          final Map<String, Transaction> reversals) {
        this.accountId = accountId;
        this.payments = new HashMap<>(payments);
        this.reversals = new HashMap<>(reversals);
        this.payments.entrySet().removeIf(findRelatedTransaction(reversals));
        this.reversals.entrySet().removeIf(findRelatedTransaction(payments));
    }

    /**
     * @return the net number of effective transactions.
     */
    Long getCount() {
        return (long) payments.size();
    }

    /**
     * @return the string representation of the change in balance useful for reporting.
     */
    String getBalance() {
        final BigDecimal balance = payments.values().stream().map(transaction -> transaction.getBalance(accountId)).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            return String.format("-$%s", balance.abs().setScale(2, RoundingMode.HALF_UP));
        } else {
            return String.format("$%s", balance.abs().setScale(2, RoundingMode.HALF_UP));
        }
    }

    /**
     * Accumulate the details from the supplied transaction onto a new Tally.
     * <p>
     * The new Tally is calculated by adding the transaction to either the payments
     * or reversals maps.  In both cases the key to the map is the original transaction id.
     *
     * @param transaction the transaction to add to the talley.
     * @return the result of adding the transaction to the tally.
     */
    Tally add(final Transaction transaction) {
        return transaction.tally(new ActionVisitor<Tally>() {
            @Override
            public Tally visit(Payment payment) {
                return new Tally(accountId,
                                 merge(payments, Collections.singletonMap(transaction.getTransactionId(), transaction)),
                                 merge(reversals, Collections.emptyMap()));
            }

            @Override
            public Tally visit(Reversal reversal) {
                return new Tally(accountId,
                                 merge(payments, Collections.emptyMap()),
                                 merge(reversals, Collections.singletonMap(reversal.getTransactionId(), transaction)));
            }
        });
    }

    /**
     * Binary operator to combine two different tally(s).
     *
     * @param first  the first tally from which to extract count and balance change.
     * @param second the second tally from which to extract count and balance change.
     * @return the result of combining two tally objects, both the payments and reversals maps are merged
     */
    static Tally combine(final Tally first,
                         final Tally second) {
        return new Tally(first.accountId, merge(first.payments, second.payments), merge(first.reversals, second.reversals));
    }

    private static Map<String, Transaction> merge(final Map<String, Transaction> first,
                                                  final Map<String, Transaction> second) {
        return Stream.of(first, second)
                     .flatMap(map -> map.entrySet().stream())
                     .collect(Collectors.toMap(
                             Map.Entry::getKey,
                             Map.Entry::getValue,
                             mergeTransactions()));
    }

    private static Predicate<Map.Entry<String, Transaction>> findRelatedTransaction(final Map<String, Transaction> control) {
        return entry -> control.containsKey(entry.getKey()) &&
                control.get(entry.getKey()).getAmount().equals(entry.getValue().getAmount());
    }

    private static BinaryOperator<Transaction> mergeTransactions() {
        return (transaction1, transaction2) -> {
            if (transaction1.getAmount().equals(transaction2.getAmount()) &&
                    transaction1.getTransactionId().equals(transaction2.getTransactionId())) {
                return transaction1;
            } else {
                throw new DuplicateTransactionException("Problem merging transactions with the same key but different details");
            }
        };
    }

}
