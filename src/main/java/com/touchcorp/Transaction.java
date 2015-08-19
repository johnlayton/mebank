package com.touchcorp;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

public final class Transaction
{
  private final String hash;
  private final long day;

  public Transaction( final String hash,
                      final long day )
  {
    this.hash = hash;
    this.day = day;
  }

  public static Transaction parse( final String input )
  {
    final String[] elements = input.split( "," );
    final String hash = elements[ 0 ];
    final long day = DateTimeFormatter.ISO_LOCAL_DATE_TIME.parse( elements[ 1 ] ).getLong( ChronoField.EPOCH_DAY );
    return new Transaction( hash, day );
  }

  public String getHash()
  {
    return hash;
  }

  public long getDay()
  {
    return day;
  }
}
