package com.mebank;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 *
 */
public final class Application {

    /**
     * Private constructor for main class.
     */
    private Application() {
    }

    public void run(final Path input) {

        try {
            Stream<String> transactions = Files.lines(input).skip(1);

            Scanner scanner = new Scanner(System.in);

            System.out.print("accountId: ");
            String accountId = scanner.nextLine();
            //            String accountId = "ACC334455";

            System.out.print("     from: ");
            LocalDateTime from = Dates.parse(scanner.nextLine());
            //            LocalDateTime from = Dates.parse("20/10/2018 12:00:00");

            System.out.print("       to: ");
            LocalDateTime to = Dates.parse(scanner.nextLine());
            //            LocalDateTime to = Dates.parse("20/10/2018 19:00:00");

            Tally tally = Transactions.tally(transactions, accountId, from, to);

            System.out.println(String.format("Relative balance for the period is: %s", tally.getBalance()));
            System.out.println(String.format("Number of transactions included is: %d", tally.getCount()));

        } catch (final DateTimeParseException | IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public static void main(final String[] args) {
        new Application().run(Paths.get(URI.create(args[0])));
    }
}
