#!/bin/bash
echo "Password Strength Checker"
echo "========================"

# Try to find JavaFX automatically
JAVAFX_PATH=""

# Check if user has set JAVAFX_HOME environment variable
if [ ! -z "$JAVAFX_HOME" ]; then
    if [ -d "$JAVAFX_HOME/lib" ]; then
        JAVAFX_PATH="$JAVAFX_HOME/lib"
    fi
fi

# Check common JavaFX installation paths on Linux/Mac
if [ -z "$JAVAFX_PATH" ]; then
    if [ -d "/usr/share/openjfx/lib" ]; then
        JAVAFX_PATH="/usr/share/openjfx/lib"
    elif [ -d "/opt/javafx/lib" ]; then
        JAVAFX_PATH="/opt/javafx/lib"
    elif [ -d "/usr/lib/jvm/java-11-openjdk/lib/javafx" ]; then
        JAVAFX_PATH="/usr/lib/jvm/java-11-openjdk/lib/javafx"
    elif [ -d "/usr/lib/jvm/java-17-openjdk/lib/javafx" ]; then
        JAVAFX_PATH="/usr/lib/jvm/java-17-openjdk/lib/javafx"
    fi
fi

# If still not found, prompt user
if [ -z "$JAVAFX_PATH" ]; then
    echo "JavaFX not found automatically. Please set JAVAFX_HOME environment variable"
    echo "or install JavaFX using your package manager."
    echo ""
    echo "Example: export JAVAFX_HOME=/path/to/javafx-sdk-21"
    echo ""
    echo "For Ubuntu/Debian: sudo apt install openjfx"
    echo "For macOS: brew install openjfx"
    echo "Download from: https://openjfx.io/"
    exit 1
fi

echo "Found JavaFX at: $JAVAFX_PATH"
echo ""

echo "Compiling Password Strength Checker..."
javac --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml PasswordChecker.java

if [ $? -ne 0 ]; then
    echo "Compilation failed! Please check your JavaFX installation."
    echo "Make sure JavaFX is properly installed and JAVAFX_HOME is set correctly."
    exit 1
fi

echo "Compilation successful!"
echo "Running Password Strength Checker..."
echo ""

java --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml PasswordChecker
