package com.mebank;

import org.junit.Test;

import java.time.Month;

import static org.junit.Assert.*;

public class DatesTest {
    @Test
    public void shouldParseValidDate() {
        assertEquals(2018, Dates.parse("10/10/2018 10:10:10").getYear());
        assertEquals(10, Dates.parse("10/10/2018 10:10:10").getMonth().getValue());
        assertEquals(10, Dates.parse("10/10/2018 10:10:10").getDayOfMonth());
    }
}
