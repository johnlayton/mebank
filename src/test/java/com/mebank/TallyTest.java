package com.mebank;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class TallyTest {

    @Test
    public void shouldFormatBalanceForReport() {
        assertEquals(new Long(0L), new Tally("ACC998877", 0L, BigDecimal.ZERO).getCount());
        assertEquals("$0.00", new Tally("ACC998877", 0L, BigDecimal.ZERO).getBalance());
        assertEquals("$1.00", new Tally("ACC998877", 0L, BigDecimal.ONE).getBalance());
        assertEquals("-$1.00", new Tally("ACC998877", 0L, new BigDecimal(-1)).getBalance());
    }

    @Test
    public void tallyShouldCombineTwoTallies() {
        final Tally first = new Tally("ACC998877", 3L, BigDecimal.TEN);
        final Tally second = new Tally("ACC998877", 5L, BigDecimal.ONE);
        final Tally third = new Tally("ACC998877", 1L, new BigDecimal(-1));

        assertEquals(new Long(8L), Tally.combine(first, second).getCount());
        assertEquals(new Long(6L), Tally.combine(second, third).getCount());

        assertEquals("$11.00", Tally.combine(first, second).getBalance());
        assertEquals("$0.00", Tally.combine(second, third).getBalance());
    }

    @Test
    public void tallyShouldAddPaymentTransaction() {
        final Tally tally = new Tally("ACC998877", 3L, BigDecimal.TEN);
        final Transaction transaction = Transaction.parse("TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, PAYMENT");

        assertEquals(new Long(4L), tally.add(transaction).getCount());
        assertEquals("$5.00", tally.add(transaction).getBalance());
    }

    @Test
    public void tallyShouldAddReversalTransaction() {
        final Tally tally = new Tally("ACC998877", 3L, BigDecimal.TEN);
        final Transaction transaction = Transaction.parse("TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, REVERSAL, TX10002");

        assertEquals(new Long(2L), tally.add(transaction).getCount());
        assertEquals("$15.00", tally.add(transaction).getBalance());
    }
}