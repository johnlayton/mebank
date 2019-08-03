package com.mebank;

import org.junit.Test;

import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class TransactionsTest {

    @Test
    public void shouldTallySampleTransactions() {
        Tally tally = Transactions.tally(
                Stream.of(
                        "TX10001, ACC334455, ACC778899, 20/10/2018 12:47:55, 25.00, PAYMENT",
                        "TX10002, ACC334455, ACC998877, 20/10/2018 17:33:43, 10.50, PAYMENT",
                        "TX10003, ACC998877, ACC778899, 20/10/2018 18:00:00, 5.00, PAYMENT",
                        "TX10004, ACC334455, ACC998877, 20/10/2018 19:45:00, 10.50, REVERSAL, TX10002",
                        "TX10005, ACC334455, ACC778899, 21/10/2018 09:30:00, 7.25, PAYMENT"
                ),
                "ACC334455",
                Dates.parse("20/10/2018 12:00:00"),
                Dates.parse("20/10/2018 19:00:00")
        );

        assertEquals(new Long(1L), tally.getCount());
        assertEquals("-$25.00", tally.getBalance());
    }

}