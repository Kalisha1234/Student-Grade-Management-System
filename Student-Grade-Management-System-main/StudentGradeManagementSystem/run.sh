#!/bin/bash
# Script to run the Student Grade Management System

cd "$(dirname "$0")"
mvn clean compile -Dcheckstyle.skip=true -q
mvn exec:java -Dexec.mainClass="org.example.Main" -Dcheckstyle.skip=true -q
