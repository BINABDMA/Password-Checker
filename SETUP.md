# Password Strength Checker - Setup Guide

This guide will help you set up the Password Strength Checker on your system.

## Prerequisites

- **Java 11 or higher** (Java 17+ recommended)
- **JavaFX SDK** (version 11, 17, or 21)

## Quick Start

### Option 1: Automatic Detection (Recommended)
The provided scripts will automatically detect JavaFX installations:

**Windows:**
```cmd
run.bat
```

**Linux/macOS:**
```bash
chmod +x run.sh
./run.sh
```

### Option 2: Manual Setup

If automatic detection fails, you can set the `JAVAFX_HOME` environment variable:

**Windows:**
```cmd
set JAVAFX_HOME=C:\path\to\javafx-sdk-21
run.bat
```

**Linux/macOS:**
```bash
export JAVAFX_HOME=/path/to/javafx-sdk-21
./run.sh
```

## JavaFX Installation

### Windows

1. **Download JavaFX SDK:**
   - Go to [https://openjfx.io/](https://openjfx.io/)
   - Download the JavaFX SDK for your Java version
   - Extract to a folder (e.g., `C:\javafx-sdk-21`)

2. **Set Environment Variable (Optional):**
   ```cmd
   setx JAVAFX_HOME "C:\javafx-sdk-21"
   ```

### Linux (Ubuntu/Debian)

```bash
# Install JavaFX using package manager
sudo apt update
sudo apt install openjfx

# Or download manually from https://openjfx.io/
```

### macOS

```bash
# Using Homebrew
brew install openjfx

# Or download manually from https://openjfx.io/
```

## Manual Compilation & Execution

If you prefer to compile manually:

```bash
# Compile
javac --module-path "$JAVAFX_HOME/lib" --add-modules javafx.controls,javafx.fxml PasswordChecker.java

# Run
java --module-path "$JAVAFX_HOME/lib" --add-modules javafx.controls,javafx.fxml PasswordChecker
```

## IDE Setup

### IntelliJ IDEA

1. **Create New Project:**
   - File → New → Project
   - Choose "Java" → Next
   - Select your Java version → Next
   - Name your project → Finish

2. **Add JavaFX:**
   - File → Project Structure → Libraries
   - Click "+" → Java
   - Navigate to your JavaFX lib folder
   - Select all .jar files → OK

3. **Configure Run Configuration:**
   - Run → Edit Configurations
   - Add VM Options: `--module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml`

### Eclipse

1. **Create Java Project:**
   - File → New → Java Project
   - Name your project → Finish

2. **Add JavaFX Libraries:**
   - Right-click project → Properties
   - Java Build Path → Libraries → Add External JARs
   - Select all .jar files from JavaFX lib folder

3. **Configure Run Configuration:**
   - Run → Run Configurations
   - Arguments → VM Arguments: `--module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml`

### Visual Studio Code

1. **Install Extensions:**
   - Extension Pack for Java
   - JavaFX Support

2. **Configure launch.json:**
   ```json
   {
       "type": "java",
       "name": "PasswordChecker",
       "request": "launch",
       "mainClass": "PasswordChecker",
       "vmArgs": "--module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml"
   }
   ```

## Troubleshooting

### "JavaFX not found" Error

1. **Check JavaFX Installation:**
   ```bash
   # Verify JavaFX is installed
   ls $JAVAFX_HOME/lib
   ```

2. **Set Environment Variable:**
   ```bash
   # Windows
   set JAVAFX_HOME=C:\javafx-sdk-21
   
   # Linux/macOS
   export JAVAFX_HOME=/path/to/javafx-sdk-21
   ```

3. **Check Java Version:**
   ```bash
   java -version
   javac -version
   ```

### "Could not find or load main class" Error

1. **Compile First:**
   ```bash
   javac --module-path "$JAVAFX_HOME/lib" --add-modules javafx.controls,javafx.fxml PasswordChecker.java
   ```

2. **Check Class Files:**
   ```bash
   ls *.class
   ```

### GUI Not Displaying

1. **Check Display Settings:**
   - Ensure you're not running in headless mode
   - Verify X11 forwarding (Linux)

2. **Try Headless Mode:**
   ```bash
   java -Djava.awt.headless=false --module-path "$JAVAFX_HOME/lib" --add-modules javafx.controls,javafx.fxml PasswordChecker
   ```

## System Requirements

- **Minimum RAM:** 512MB
- **Recommended RAM:** 1GB+
- **Display:** 1024x768 minimum resolution
- **OS:** Windows 10+, macOS 10.14+, or Linux (Ubuntu 18.04+)

## Support

If you encounter issues:

1. Check the [Troubleshooting](#troubleshooting) section
2. Verify your Java and JavaFX versions are compatible
3. Ensure all environment variables are set correctly
4. Try the manual compilation steps

## Contributing

When contributing to this project:

1. Test on multiple platforms (Windows, Linux, macOS)
2. Ensure the automatic detection works
3. Update documentation if adding new features
4. Test with different JavaFX versions
