#!/bin/bash

# Navigate to the project directory (just in case)
cd "$(dirname "$0")"

echo "=== Building StrongOpening ==="

# 1. Clean old bin files and recreate bin directory
rm -rf bin/*
mkdir -p bin

# 2. Compile Java source files with JavaFX modules and local libraries
/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home/bin/javac \
    --add-modules javafx.controls,javafx.fxml \
    -cp "lib/*" \
    -d bin \
    src/*.java

# 3. Copy the dark mode stylesheet into bin so the app can load it
cp src/style.css bin/

echo "=== Launching StrongOpening App ==="

# 4. Run the application
/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home/bin/java \
    --add-modules javafx.controls,javafx.fxml \
    -cp "bin:lib/*" \
    StrongOpeningApp