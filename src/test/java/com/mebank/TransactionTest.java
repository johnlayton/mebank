package com.mebank;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TransactionTest {

    @Test(expected = InvalidFormatException.class)
    public void shouldHandleMissingAction() {
        Transaction.parse("TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00");
    }

    @Test(expected = InvalidFormatException.class)
    public void shouldHandleMissingRelatedTransaction() {
        Transaction.parse("TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, REVERSAL");
    }

    @Test
    public void shouldParseValidPayment() {
        Transaction transaction = Transaction.parse("TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, PAYMENT");
        assertTrue(Transaction.isFromAccount("ACC998877").test(transaction));
        assertTrue(Transaction.isToAccount("ACC778899").test(transaction));
    }

    @Test
    public void shouldParseValidReversal() {
        Transaction transaction = Transaction.parse("TX10004, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, REVERSAL, TX10003");
        assertTrue(Transaction.isFromAccount("ACC998877").test(transaction));
        assertTrue(Transaction.isToAccount("ACC778899").test(transaction));
    }

    @Test
    public void shouldCalculateThePaymentTransfer() {
        Transaction transaction = Transaction.parse("TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, PAYMENT");
        assertEquals(new BigDecimal("-5.00"), transaction.getTransfer("ACC998877"));
        assertEquals(new BigDecimal("5.00"), transaction.getTransfer("ACC778899"));
    }

    @Test
    public void shouldCalculateTheReversalTransfer() {
        Transaction transaction = Transaction.parse("TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, REVERSAL, TX10002");
        assertEquals(new BigDecimal("5.00"), transaction.getTransfer("ACC998877"));
        assertEquals(new BigDecimal("-5.00"), transaction.getTransfer("ACC778899"));
    }

    @Test(expected = UnexpectedTransactionException.class)
    public void shouldHandleWrongAccountOnPaymentTransfer() {
        Transaction transaction = Transaction.parse("TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, PAYMENT");
        transaction.getTransfer("ACC998871");
    }

    @Test(expected = UnexpectedTransactionException.class)
    public void shouldHandleWrongAccountOnReversalTransfer() {
        Transaction transaction = Transaction.parse("TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, REVERSAL, TX10002");
        transaction.getTransfer("ACC998871");
    }

    @Test
    public void shouldCalculateOnOrAfter() {
        Transaction transaction = Transaction.parse("TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, PAYMENT");
        assertTrue(Transaction.isOnOrAfter(Dates.parse("20/10/2018 18:00:00")).test(transaction));
        assertTrue(Transaction.isOnOrAfter(Dates.parse("20/10/2018 17:59:59")).test(transaction));
        assertFalse(Transaction.isOnOrAfter(Dates.parse("20/10/2018 18:00:01")).test(transaction));
    }

    @Test
    public void shouldCalculateOnOrBefore() {
        Transaction transaction = Transaction.parse("TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, PAYMENT");
        assertTrue(Transaction.isOnOrBefore(Dates.parse("20/10/2018 18:00:00")).test(transaction));
        assertFalse(Transaction.isOnOrBefore(Dates.parse("20/10/2018 17:59:59")).test(transaction));
        assertTrue(Transaction.isOnOrBefore(Dates.parse("20/10/2018 18:00:01")).test(transaction));
    }

    @Test
    public void shouldFilterPaymentTransaction() {
        Transaction transaction = Transaction.parse("TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, PAYMENT");
        assertTrue(Transaction.isPayment().test(transaction));
        assertFalse(Transaction.isReversal().test(transaction));
    }

    @Test
    public void shouldFilterReversalTransaction() {
        Transaction transaction = Transaction.parse("TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, REVERSAL, TX10002");
        assertFalse(Transaction.isPayment().test(transaction));
        assertTrue(Transaction.isReversal().test(transaction));
    }

    @Test
    public void shouldFilterInSequencePaymentTransaction() {
        Transaction transaction = Transaction.parse("TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, PAYMENT");
        assertFalse(Transaction.reversalInSequence(100L, 200L).test(transaction));
    }

    @Test
    public void shouldFilterInSequenceReversalTransaction() {
        Transaction transaction = Transaction.parse("TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, REVERSAL, TX10002");
        assertTrue(Transaction.reversalInSequence(10000L, 10005L).test(transaction));
        assertFalse(Transaction.reversalInSequence(10005L, 10006L).test(transaction));
    }


}