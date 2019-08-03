package com.mebank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.time.temporal.ChronoField.YEAR;

/**
 * Utility class with method to parse custom date format.
 */
final class Dates {

    private static final int TWO_DIGIT = 2;
    private static final int FOUR_DIGIT = 4;

    private static final DateTimeFormatter LOCAL_DATE_TIME = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(DAY_OF_MONTH, TWO_DIGIT)
            .appendLiteral('/')
            .appendValue(MONTH_OF_YEAR, TWO_DIGIT)
            .appendLiteral('/')
            .appendValue(YEAR, FOUR_DIGIT)
            .appendLiteral(' ')
            .appendValue(HOUR_OF_DAY, TWO_DIGIT)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, TWO_DIGIT)
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, TWO_DIGIT)
            .toFormatter(Locale.getDefault());

    private Dates() {
    }

    static LocalDateTime parse(final String input) {
        return LocalDateTime.parse(input, LOCAL_DATE_TIME);
    }
}
