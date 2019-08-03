#!/usr/bin/env bash

BIN="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

mkdir -p ./build/classes/java/main
javac -source 1.8 -target 1.8 \
  -d ./build/classes/java/main -sourcepath \
     src/main/java \
     src/main/java/com/mebank/Application.java \
     src/main/java/com/mebank/Dates.java \
     src/main/java/com/mebank/Transaction.java \
     src/main/java/com/mebank/Transactions.java \
     src/main/java/com/mebank/Tally.java \
     src/main/java/com/mebank/InvalidFormatException.java \
     src/main/java/com/mebank/UnexpectedTransactionException.java

java -cp ./build/classes/java/main \
  com.mebank.Application file:///${BIN}/../src/test/resources/sample.csv