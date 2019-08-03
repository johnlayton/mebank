#!/usr/bin/env bash

BIN="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

mkdir -p ./build/classes/java/main
javac -cp ./lib/junit-4.12.jar:./lib/hamcrest-core-1.3.jar -source 1.8 -target 1.8 \
  -d ./build/classes/java/main -sourcepath \
     src/main/java \
     src/main/java/com/mebank/Application.java \
     src/main/java/com/mebank/Dates.java \
     src/main/java/com/mebank/Transaction.java \
     src/main/java/com/mebank/Transactions.java \
     src/main/java/com/mebank/Tally.java \
     src/main/java/com/mebank/InvalidFormatException.java \
     src/main/java/com/mebank/UnexpectedTransactionException.java

mkdir -p ./build/classes/java/test
javac -cp ./build/classes/java/main:./lib/junit-4.12.jar:./lib/hamcrest-core-1.3.jar -source 1.8 -target 1.8 \
  -d ./build/classes/java/test -sourcepath \
     src/test/java \
     src/test/java/com/mebank/ApplicationTest.java \
     src/test/java/com/mebank/DatesTest.java \
     src/test/java/com/mebank/TransactionTest.java \
     src/test/java/com/mebank/TallyTest.java

java -cp ./build/classes/java/main:./build/classes/java/test:./src/test/resources:./lib/junit-4.12.jar:./lib/hamcrest-core-1.3.jar \
  org.junit.runner.JUnitCore \
  com.mebank.ApplicationTest \
  com.mebank.DatesTest \
  com.mebank.TransactionTest \
  com.mebank.TransactionsTest \
  com.mebank.TallyTest \

java -cp ./build/classes/java/main \
  com.mebank.Application file:///${BIN}/../src/test/resources/sample.csv