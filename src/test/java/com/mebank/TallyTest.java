package com.mebank;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class TallyTest {

    @Test
    public void shouldOnlyCountPaymentTransactions() {
        assertEquals(new Long(0L), new Tally("ACC998877", Collections.emptyMap(), Collections.emptyMap()).getCount());
        assertEquals(new Long(1L), new Tally("ACC998877",
                                             Collections.singletonMap("TX10003", Transaction.parse("TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, PAYMENT")),
                                             Collections.emptyMap()).getCount());
        assertEquals(new Long(0L), new Tally("ACC998877",
                                             Collections.singletonMap("TX10003", Transaction.parse("TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, PAYMENT")),
                                             Collections.singletonMap("TX10003", Transaction.parse("TX10004, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, REVERSAL, TX10003"))).getCount());
    }

    @Test
    public void shouldFormatBalanceForReport() {
        assertEquals("$5.00", new Tally("ACC778899",
                                        Collections.singletonMap("TX10003", Transaction.parse("TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, PAYMENT")),
                                        Collections.emptyMap()).getBalance());
        assertEquals("-$5.00", new Tally("ACC998877",
                                        Collections.singletonMap("TX10003", Transaction.parse("TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, PAYMENT")),
                                        Collections.emptyMap()).getBalance());
    }

    @Test
    public void tallyShouldCombinePaymentTally() {
        final Tally first = new Tally("ACC778899",
                                      Collections.singletonMap("TX10001", Transaction.parse("TX10001, ACC998877, ACC778899, 20/10/2018 18:00:00, 10.00, PAYMENT")),
                                      Collections.emptyMap());
        final Tally second = new Tally("ACC778899",
                                       Collections.singletonMap("TX10002", Transaction.parse("TX10002, ACC998877, ACC778899, 20/10/2018 19:00:00, 5.00, PAYMENT")),
                                       Collections.emptyMap());
        final Tally third = new Tally("ACC778899",
                                      Collections.singletonMap("TX10003", Transaction.parse("TX10003, ACC778899, ACC998877, 20/10/2018 20:00:00, 12.00, PAYMENT")),
                                      Collections.emptyMap());

        assertEquals(new Long(2L), Tally.combine(first, second).getCount());
        assertEquals(new Long(2L), Tally.combine(second, third).getCount());

        assertEquals("$15.00", Tally.combine(first, second).getBalance());
        assertEquals("-$7.00", Tally.combine(second, third).getBalance());
    }

    @Test
    public void tallyShouldCombineDuplicatePayment() {
        final Tally first = new Tally("ACC778899",
                                      Collections.singletonMap("TX10001", Transaction.parse("TX10001, ACC998877, ACC778899, 20/10/2018 18:00:00, 10.00, PAYMENT")),
                                      Collections.emptyMap());
        final Tally second = new Tally("ACC778899",
                                       Collections.singletonMap("TX10001", Transaction.parse("TX10001, ACC998877, ACC778899, 20/10/2018 18:00:00, 10.00, PAYMENT")),
                                       Collections.emptyMap());
        final Tally third = new Tally("ACC778899",
                                      Collections.singletonMap("TX10003", Transaction.parse("TX10003, ACC778899, ACC998877, 20/10/2018 20:00:00, 12.00, PAYMENT")),
                                      Collections.emptyMap());

        assertEquals(new Long(1L), Tally.combine(first, second).getCount());
        assertEquals(new Long(2L), Tally.combine(second, third).getCount());

        assertEquals("$10.00", Tally.combine(first, second).getBalance());
        assertEquals("-$2.00", Tally.combine(second, third).getBalance());
    }


    @Test(expected = DuplicateTransactionException.class)
    public void tallyShouldFailCombineDuplicatePayment() {
        final Tally first = new Tally("ACC778899",
                                      Collections.singletonMap("TX10001", Transaction.parse("TX10001, ACC998877, ACC778899, 20/10/2018 18:00:00, 10.00, PAYMENT")),
                                      Collections.emptyMap());
        final Tally second = new Tally("ACC778899",
                                       Collections.singletonMap("TX10001", Transaction.parse("TX10002, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, PAYMENT")),
                                       Collections.emptyMap());
        final Tally third = new Tally("ACC778899",
                                      Collections.singletonMap("TX10003", Transaction.parse("TX10003, ACC778899, ACC998877, 20/10/2018 20:00:00, 12.00, PAYMENT")),
                                      Collections.emptyMap());

        Tally.combine(first, third);
        Tally.combine(first, second);
    }

    @Test
    public void tallyShouldCombineReversalTally() {
        final Tally first = new Tally("ACC778899",
                                      Collections.singletonMap("TX10001", Transaction.parse("TX10001, ACC998877, ACC778899, 20/10/2018 18:00:00, 10.00, PAYMENT")),
                                      Collections.emptyMap());
        final Tally second = new Tally("ACC778899",
                                       Collections.singletonMap("TX10002", Transaction.parse("TX10002, ACC998877, ACC778899, 20/10/2018 19:00:00, 5.00, PAYMENT")),
                                       Collections.emptyMap());
        final Tally third = new Tally("ACC778899",
                                      Collections.emptyMap(),
                                      Collections.singletonMap("TX10001", Transaction.parse("TX10003, ACC778899, ACC998877, 20/10/2018 20:00:00, 10.00, REVERSAL, TX10001")));

        assertEquals(new Long(2L), Tally.combine(first, second).getCount());
        assertEquals(new Long(0L), Tally.combine(first, third).getCount());

        assertEquals("$15.00", Tally.combine(first, second).getBalance());
        assertEquals("$0.00", Tally.combine(first, third).getBalance());

        assertEquals(new Long(1L), Tally.combine(Tally.combine(first, second), third).getCount());
        assertEquals("$5.00", Tally.combine(Tally.combine(first, second), third).getBalance());
    }

    @Test
    public void tallyShouldAddPaymentTransaction() {
        final Tally tally = new Tally("ACC998877",
                                      Collections.singletonMap("TX10001", Transaction.parse("TX10001, ACC998877, ACC778899, 20/10/2018 18:00:00, 10.00, PAYMENT")),
                                      Collections.emptyMap());
        final Transaction transaction = Transaction.parse("TX10002, ACC998877, ACC778899, 20/10/2018 19:00:00, 5.00, PAYMENT");

        assertEquals(new Long(2L), tally.add(transaction).getCount());
        assertEquals("-$15.00", tally.add(transaction).getBalance());
    }

    @Test
    public void tallyShouldAddReversalTransaction() {
        final Tally tally = new Tally("ACC998877",
                                      Collections.singletonMap("TX10001", Transaction.parse("TX10001, ACC998877, ACC778899, 20/10/2018 18:00:00, 10.00, PAYMENT")),
                                      Collections.emptyMap());
        final Transaction transaction = Transaction.parse("TX10002, ACC998877, ACC778899, 20/10/2018 19:00:00, 10.00, REVERSAL, TX10001");

        assertEquals(new Long(0L), tally.add(transaction).getCount());
        assertEquals("$0.00", tally.add(transaction).getBalance());
    }
}