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
 * Main application which takes the URI for the transaction file.
 */
public final class Application {

    private Application() {
    }

    /**
     * Run the application supplying the path to the transaction csv file.
     *
     * @param input the path to the transaction csv file.
     */
    private void run(final Path input) {

        try {
            Stream<String> transactions = Files.lines(input).skip(1);

            Scanner scanner = new Scanner(System.in);

            System.out.print("accountId: ");
            String accountId = scanner.nextLine();

            System.out.print("     from: ");
            LocalDateTime from = Dates.parse(scanner.nextLine());

            System.out.print("       to: ");
            LocalDateTime to = Dates.parse(scanner.nextLine());

            Tally tally = Transactions.tally(transactions, accountId, from, to);

            System.out.println(String.format("Relative balance for the period is: %s", tally.getBalance()));
            System.out.println(String.format("Number of transactions included is: %d", tally.getCount()));

        } catch (final DateTimeParseException | IOException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Main entry to the application.
     *
     * @param args supplied command line arguments, the first and only
     *             used argument is the path to the transactions csv file
     */
    public static void main(final String[] args) {
        new Application().run(Paths.get(URI.create(args[0])));
    }
}
