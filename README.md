# ME Bank Coding Challenge

## Design

1. The application takes one argument the uri of the file containing the transactions
2. The application uses nio paths and files to find the transactions file and create a stream of transactions
3. The application scans for user input for account, to and from dates then and parses the dates
4. The transactions utility class is used to produce a tally of transaction count and balance
    1. lines of transactions are parsed
    2. the stream of transactions are filtered to create a working list of transactions
    3. the working set of transactions if filtered again to remove reversal transactions which apply to transactions
       outside the working set of payment transactions
    4. the final set of transactions is then reduced using a tally to;
        1. keep a current map of payment transactions (from which the balance will be calculated)
        2. keep a current map of unmatched reversals (which may be from payments not made within the timeframe)
5. the application prints the transaction count and balance to the console 

## Testing

Using gradle
```bash
./gradlew test
```

Using the bash script

```bash
./bin/test.sh
```

## Running

Using gradle
```bash
./gradlew --console=plain clean build run
```

Using the bash script

```bash
./bin/run.sh
```


