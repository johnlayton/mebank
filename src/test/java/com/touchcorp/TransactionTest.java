package com.touchcorp;

import java.math.BigDecimal;
import org.junit.Test;
import static org.junit.Assert.*;

public class TransactionTest
{
  @Test
  public void shouldParseLogEntry()
  {
    final Transaction transaction = Transaction.parse( "10d7ce2f43e35fa57d1bbf8b1e2,2014-04-29T13:15:54,10.00" );
    assertEquals( "10d7ce2f43e35fa57d1bbf8b1e2", transaction.getHash() );
    assertEquals( 16189, transaction.getDay() );
    assertEquals( new BigDecimal( "10.00" ), transaction.getAmount() );
  }

}
