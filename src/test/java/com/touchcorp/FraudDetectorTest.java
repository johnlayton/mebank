package com.touchcorp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class FraudDetectorTest
{
  @Test
  public void shouldFindSingleFraudulentTransaction()
  {
    final List<String> transactions = new ArrayList<>();
    transactions.add( "10d7ce2f43e35fa57d1bbf8b1e2,2014-04-29T13:15:54,10.00" );

    final List<String> cardsExceedingThreshold =
      FraudDetector.findCardsExceedingThreshold( transactions, LocalDate.of( 2014, 4, 29 ), 5.0 );
    assertEquals( 1, cardsExceedingThreshold.size() );
    assertEquals( "10d7ce2f43e35fa57d1bbf8b1e2", cardsExceedingThreshold.get( 0 ) );
  }

  @Test
  public void shouldFindCumulativeFraudulentTransaction()
  {
    final List<String> transactions = new ArrayList<>();
    transactions.add( "10d7ce2f43e35fa57d1bbf8b1e2,2014-04-29T13:15:54,10.00" );
    transactions.add( "10d7ce2f43e35fa57d1bbf8b1e2,2014-04-29T13:16:54,10.00" );

    final List<String> cardsExceedingThreshold =
      FraudDetector.findCardsExceedingThreshold( transactions, LocalDate.of( 2014, 4, 29 ), 15.0 );
    assertEquals( 1, cardsExceedingThreshold.size() );
    assertEquals( "10d7ce2f43e35fa57d1bbf8b1e2", cardsExceedingThreshold.get( 0 ) );
  }

  @Test
  public void shouldIgnoreTransactionsOnOtherDates()
  {
    final List<String> transactions = new ArrayList<>();
    transactions.add( "10d7ce2f43e35fa57d1bbf8b1e1,2014-04-29T13:15:54,10.00" );
    transactions.add( "10d7ce2f43e35fa57d1bbf8b1e1,2014-04-29T13:16:54,10.00" );
    transactions.add( "10d7ce2f43e35fa57d1bbf8b1e2,2014-04-28T13:15:54,10.00" );
    transactions.add( "10d7ce2f43e35fa57d1bbf8b1e2,2014-04-28T13:16:54,10.00" );

    final List<String> cardsExceedingThreshold =
      FraudDetector.findCardsExceedingThreshold( transactions, LocalDate.of( 2014, 4, 29 ), 15.0 );
    assertEquals( 1, cardsExceedingThreshold.size() );
    assertEquals( "10d7ce2f43e35fa57d1bbf8b1e1", cardsExceedingThreshold.get( 0 ) );
  }
}