package com.touchcorp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;

public class FraudDetector
{
  public static List<String> findCardsExceedingThreshold( final List<String> transactions,
                                                          final LocalDate date,
                                                          final double amount )
  {
    final long day = date.getLong( ChronoField.EPOCH_DAY );
    final BigDecimal threshold = new BigDecimal( amount );

    // Find all transactions for supplied date
    final Stream<Transaction> transactionStream = transactions.stream()
      .map( Transaction::parse )
      .filter( transaction -> transaction.getDay() == day );

    // Group transactions by hash and sum transaction totals
    final Map<String, BigDecimal> dailyCardTotals = transactionStream.collect(
      groupingBy( Transaction::getHash, reducing( BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add ) ) );

    // Find entries where total is above the threshold
    final Stream<Map.Entry<String, BigDecimal>> fraudulentCardEntryList =
      dailyCardTotals.entrySet().stream().filter( e -> e.getValue().compareTo( threshold ) > 0 );

    return fraudulentCardEntryList.map( Entry::getKey ).collect( toList() );
  }
}
