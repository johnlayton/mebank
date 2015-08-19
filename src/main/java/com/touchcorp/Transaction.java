package com.touchcorp;

public final class Transaction
{
  private final String hash;

  public Transaction( final String hash )
  {
    this.hash = hash;
  }

  public static Transaction parse( final String input )
  {
    final String[] elements = input.split( "," );
    return new Transaction( elements[0] );
  }

  public String getHash()
  {
    return hash;
  }
}
