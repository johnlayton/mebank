package com.mebank;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;

public class ApplicationTest {

    private final InputStream systemIn = System.in;
    private final PrintStream systemOut = System.out;

    private ByteArrayInputStream testIn;
    private ByteArrayOutputStream testOut;

    @Before
    public void before() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    @After
    public void after() {
        System.setIn(systemIn);
        System.setOut(systemOut);
    }

    private void provideInput(String data) {
        testIn = new ByteArrayInputStream(data.getBytes());
        System.setIn(testIn);
    }

    private String getOutput() {
        return testOut.toString();
    }

    @Test
    public void shouldLoadFileAndScanForParameters() throws Exception {

        provideInput("ACC334455\n20/10/2018 12:00:00\n20/10/2018 19:00:00\n");

        Application.main(new String[] { this.getClass().getResource("/sample.csv").toURI().toString() });

        String output = getOutput();

        assertTrue(output.contains("Relative balance for the period is: -$25.00"));
        assertTrue(output.contains("Number of transactions included is: 1"));
    }

    @Test
    public void shouldNotifyUserForInvalidDateFormat() throws Exception {

        provideInput("ACC334455\n20/10/18 12:00:00\n20/10/2018 19:00:00\n");

        Application.main(new String[] { this.getClass().getResource("/sample.csv").toURI().toString() });

        String output = getOutput();

        assertTrue(output.contains("Text '20/10/18 12:00:00' could not be parsed at index 6"));
    }
}