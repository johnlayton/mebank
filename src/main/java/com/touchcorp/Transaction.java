package com.touchcorp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

public final class Transaction
{
  private final String hash;
  private final LocalDateTime date;
  private final BigDecimal amount;

  public static Transaction parse( final String input )
  {
    final String[] elements = input.split( "," );
    if ( 3 != elements.length )
    {
      throw new InvalidFormat( "Invalid transaction details " + input );
    }
    final String hash = elements[ 0 ].trim();
    if ( hash.isEmpty() )
    {
      throw new InvalidFormat( "Missing credit card hash " + input );
    }
    if ( !hash.matches( "[0-9a-z]+" ) )
    {
      throw new InvalidFormat( "Invalid credit card hash " + input );
    }
    final LocalDateTime date = LocalDateTime.parse( elements[ 1 ].trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME );
    final BigDecimal amount = new BigDecimal( elements[ 2 ].trim() );
    return new Transaction( hash, date, amount );
  }

  private Transaction( final String hash, final LocalDateTime date, final BigDecimal amount )
  {
    this.hash = hash;
    this.date = date;
    this.amount = amount;
  }

  public String getHash()
  {
    return hash;
  }

  public long getDay()
  {
    return date.getLong( ChronoField.EPOCH_DAY );
  }

  public BigDecimal getAmount()
  {
    return amount;
  }
}
