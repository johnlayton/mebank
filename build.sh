#!/usr/bin/env bash

mkdir -p ./classes/main
javac -cp ./lib/junit-4.12.jar:./lib/hamcrest-core-1.3.jar -source 1.8 -target 1.8 \
   -d ./classes/main -sourcepath src/test/java src/main/java/com/touchcorp/Transaction.java \
     src/main/java/com/touchcorp/FraudDetector.java \
     src/main/java/com/touchcorp/InvalidFormatException.java

mkdir -p ./classes/test
javac -cp ./classes/main:./lib/junit-4.12.jar:./lib/hamcrest-core-1.3.jar -source 1.8 -target 1.8 \
   -d ./classes/test -sourcepath src/test/java src/test/java/com/touchcorp/TransactionTest.java \
     src/test/java/com/touchcorp/FraudDetectorTest.java

java -cp ./classes/main:./classes/test:lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar \
   org.junit.runner.JUnitCore com.touchcorp.TransactionTest com.touchcorp.FraudDetectorTest