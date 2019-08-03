package com.mebank;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class representing the transaction.
 */
public final class Transaction {

    private static final int FIELDS_IN_PAYMENT = 6;
    private static final int FIELDS_IN_REVERSAL = 7;

    private static final int POSITION_TRANSACTION_ID = 0;
    private static final int POSITION_FROM_ACCOUNT = 1;
    private static final int POSITION_TO_ACCOUNT = 2;
    private static final int POSITION_CREATED_AT = 3;
    private static final int POSITION_AMOUNT = 4;
    private static final int POSITION_ACTION = 5;
    private static final int POSITION_RELATED_TRANSACTION = 6;

    private static final String IS_NOT_DIGIT = "[^0-9]";

    static Predicate<Transaction> isFromAccount(final String accountId) {
        return transaction -> transaction.fromAccountId.equals(accountId);
    }

    static Predicate<Transaction> isToAccount(final String accountId) {
        return transaction -> transaction.toAccountId.equals(accountId);
    }

    static Predicate<Transaction> isOnOrAfter(final LocalDateTime from) {
        return transaction -> !transaction.createdAt.isBefore(from);
    }

    static Predicate<Transaction> isOnOrBefore(final LocalDateTime to) {
        return transaction -> !transaction.createdAt.isAfter(to);
    }

    static Predicate<Transaction> isPayment() {
        return transaction -> transaction.action.accept(new ActionVisitor<Boolean>() {
            @Override
            public Boolean visit(final Payment payment) {
                return true;
            }

            @Override
            public Boolean visit(final Reversal reversal) {
                return false;
            }
        });
    }

    static Predicate<Transaction> isReversal() {
        return transaction -> transaction.action.accept(new ActionVisitor<Boolean>() {
            @Override
            public Boolean visit(final Payment payment) {
                return false;
            }

            @Override
            public Boolean visit(final Reversal reversal) {
                return true;
            }
        });
    }

    static Predicate<Transaction> relatedInSequence(final Long start,
                                                    final Long end) {
        return transaction -> transaction.action.accept(new ActionVisitor<Boolean>() {
            @Override
            public Boolean visit(final Payment payment) {
                return false;
            }

            @Override
            public Boolean visit(final Reversal reversal) {
                return !(reversal.getSequence() < start) && !(reversal.getSequence() > end);
            }
        });
    }

    private final String transactionId;
    private final String fromAccountId;
    private final String toAccountId;
    private final LocalDateTime createdAt;
    private final BigDecimal amount;
    private final Action action;

    private Transaction(final String transactionId,
                        final String fromAccountId,
                        final String toAccountId,
                        final LocalDateTime createdAt,
                        final BigDecimal amount,
                        final Action action) {
        this.transactionId = transactionId;
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.createdAt = createdAt;
        this.amount = amount;
        this.action = action;
    }

    static Transaction parse(final String input) {
        final List<String> elements = Stream.of(input.split(",")).map(String::trim).collect(Collectors.toList());
        final Action action = createAction(elements);

        final String transactionId = elements.get(POSITION_TRANSACTION_ID);
        final String fromAccountId = elements.get(POSITION_FROM_ACCOUNT);
        final String toAccountId = elements.get(POSITION_TO_ACCOUNT);
        final LocalDateTime createdAt = Dates.parse(elements.get(POSITION_CREATED_AT));
        final BigDecimal amount = new BigDecimal(elements.get(POSITION_AMOUNT));

        return new Transaction(transactionId, fromAccountId, toAccountId, createdAt, amount, action);
    }

    private static Action createAction(final List<String> elements) {
        boolean isPayment = elements.size() == FIELDS_IN_PAYMENT && "PAYMENT".equals(elements.get(POSITION_ACTION));
        boolean isRefund = elements.size() == FIELDS_IN_REVERSAL && "REVERSAL".equals(elements.get(POSITION_ACTION));

        if (isPayment) {
            return Payment.INSTANCE;
        } else if (isRefund) {
            return new Reversal(elements.get(POSITION_RELATED_TRANSACTION));
        } else {
            throw new InvalidFormatException("Incorrect number of fields for transaction");
        }
    }

    /**
     * @return the long value of the transaction id by replacing the non digit prefix
     */
    Long getSequence() {
        return Long.parseLong(transactionId.replaceAll(IS_NOT_DIGIT, ""));
    }

    /**
     * Update balance attempts to return a new big decimal representing the supplied balance
     * and this transactions amount either;
     * added if this is a payment transaction and the from account matched the supplied account id or
     * if this is a reversal transaction and the to account matched the supplied account id
     * or;
     * subtracted if this is a payment transaction and the to account matched the supplied account id or
     * if this is a reversal transaction and the from account matched the supplied account id.
     * <p>
     * This operation is performed by visiting the action stored on the transaction.  The action being either
     * payment or reversal.
     *
     * @param accountId the account id from which to determine if to add or subtract
     * @param balance   the starting balance on which to tally
     * @return a new big decimal representing the result of either adding or subtracting this transaction amount
     */
    BigDecimal updateBalance(final String accountId,
                             final BigDecimal balance) {
        return action.accept(new ActionVisitor<BigDecimal>() {
            @Override
            public BigDecimal visit(final Payment payment) {
                if (accountId.equals(toAccountId)) {
                    return balance.add(amount);
                } else if (accountId.equals(fromAccountId)) {
                    return balance.subtract(amount);
                } else {
                    throw new UnexpectedTransactionException("Transaction not for account " + accountId);
                }
            }

            @Override
            public BigDecimal visit(final Reversal reversal) {
                if (accountId.equals(toAccountId)) {
                    return balance.subtract(amount);
                } else if (accountId.equals(fromAccountId)) {
                    return balance.add(amount);
                } else {
                    throw new UnexpectedTransactionException("Transaction not for account " + accountId);
                }
            }
        });
    }

    /**
     * Update the tally count
     * increment by one if this is a payment transaction, or;
     * decrement by one if this is a reversal transaction.
     *
     * @param count the current transaction count
     * @return the new transaction count
     */
    Long updateCount(final Long count) {
        return action.accept(new ActionVisitor<Long>() {
            @Override
            public Long visit(final Payment payment) {
                return count + 1;
            }

            @Override
            public Long visit(final Reversal reversal) {
                return count - 1;
            }
        });
    }

    /**
     * Action visitor which will visit any action and return a object of type <T>.
     *
     * @param <T> result of visiting the action
     */
    interface ActionVisitor<T> {
        T visit(Payment payment);

        T visit(Reversal reversal);
    }

    /**
     *
     */
    abstract static class Action {
        abstract <T> T accept(ActionVisitor<T> visitor);
    }

    /**
     *
     */
    static final class Payment extends Action {
        static final Action INSTANCE = new Payment();

        @Override
        <T> T accept(final ActionVisitor<T> visitor) {
            return visitor.visit(this);
        }
    }

    /**
     *
     */
    static final class Reversal extends Action {
        private final String transactionId;

        Reversal(final String transactionId) {
            this.transactionId = transactionId;
        }

        @Override
        <T> T accept(final ActionVisitor<T> visitor) {
            return visitor.visit(this);
        }

        Long getSequence() {
            return Long.parseLong(transactionId.replaceAll(IS_NOT_DIGIT, ""));
        }
    }
}
