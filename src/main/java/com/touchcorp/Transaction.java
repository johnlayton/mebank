package com.touchcorp;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

public final class Transaction
{
  private final String hash;
  private final long day;
  private BigDecimal amount;

  public Transaction( final String hash,
                      final long day,
                      final BigDecimal amount )
  {
    this.hash = hash;
    this.day = day;
    this.amount = amount;
  }

  public static Transaction parse( final String input )
  {
    final String[] elements = input.split( "," );
    final String hash = elements[ 0 ];
    final long day = DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse( elements[ 1 ] ).getLong( ChronoField.EPOCH_DAY );
    final BigDecimal amount = new BigDecimal( elements[2] );
    return new Transaction( hash, day, amount );
  }

  public String getHash()
  {
    return hash;
  }

  public long getDay()
  {
    return day;
  }

  public BigDecimal getAmount()
  {
    return amount;
  }
}
