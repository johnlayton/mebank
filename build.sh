#!/usr/bin/env bash

mkdir -p ./build/classes/main
javac -cp ./lib/junit-4.12.jar:./lib/hamcrest-core-1.3.jar -source 1.8 -target 1.8 \
   -d ./build/classes/main -sourcepath src/test/java src/main/java/com/touchcorp/Transaction.java

mkdir -p ./build/classes/test
javac -cp ./build/classes/main:./lib/junit-4.12.jar:./lib/hamcrest-core-1.3.jar -source 1.8 -target 1.8 \
   -d ./build/classes/test -sourcepath src/test/java src/test/java/com/touchcorp/TransactionTest.java

java -cp ./build/classes/main:./build/classes/test:lib/junit-4.12.jar:lib/hamcrest-core-1.3.jar:lib/hamcrest-library-1.3.jar \
   org.junit.runner.JUnitCore com.touchcorp.TransactionTest