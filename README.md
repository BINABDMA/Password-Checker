# Password Strength Checker

A comprehensive JavaFX application that analyzes password strength using multiple criteria including length, character variety, pattern detection, entropy calculation, and dictionary checking.

## Features

### Password Analysis
- **Length Check**: Minimum 8 characters recommended
- **Character Variety**: Analyzes use of lowercase, uppercase, digits, and symbols
- **Pattern Detection**: Identifies common sequences (1234, abcd, qwerty) and repeated characters
- **Entropy Calculation**: Measures password randomness and unpredictability
- **Dictionary Check**: Compares against a list of common weak passwords

### Scoring System
- **Score Range**: 0-100 points
- **Strength Categories**: Weak (0-39), Medium (40-69), Strong (70-100)
- **Real-time Feedback**: Live analysis as you type

### GUI Features
- **Modern JavaFX Interface**: Clean, responsive design
- **Visual Indicators**: Color-coded strength display and progress bar
- **Detailed Feedback**: Comprehensive analysis with specific recommendations
- **Real-time Updates**: Instant analysis as you type

## Requirements

- Java 11 or higher
- JavaFX 11 or higher
- Windows/Linux/macOS

## Installation & Setup

### Quick Start (Recommended)
The application includes smart scripts that automatically detect JavaFX installations:

**Windows:**
```cmd
run.bat
```

**Linux/macOS:**
```bash
chmod +x run.sh
./run.sh
```

### Manual Setup
If automatic detection fails, set the `JAVAFX_HOME` environment variable:

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

### JavaFX Installation
1. Download JavaFX SDK from [https://openjfx.io/](https://openjfx.io/)
2. Extract to a folder (e.g., `C:\javafx-sdk-21`)
3. Optionally set `JAVAFX_HOME` environment variable

### Using an IDE
1. Import the project into your IDE (IntelliJ IDEA, Eclipse, VS Code)
2. Add JavaFX libraries to your project
3. Configure VM arguments: `--module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml`
4. Run the `PasswordChecker` class

For detailed setup instructions, see [SETUP.md](SETUP.md).

## How It Works

### Scoring Algorithm
- **Length (0-30 points)**: Longer passwords get more points
- **Character Variety (0-25 points)**: More character types = higher score
- **Entropy (0-25 points)**: Higher randomness = better score
- **Penalties**: Common patterns (-15) and weak dictionary passwords (-20)

### Analysis Components
1. **Length Analysis**: Checks minimum requirements and optimal length
2. **Character Set Analysis**: Identifies lowercase, uppercase, digits, symbols
3. **Pattern Recognition**: Detects keyboard patterns, sequences, repetitions
4. **Entropy Calculation**: Measures information content and unpredictability
5. **Dictionary Check**: Compares against 100+ common weak passwords

### Recommendations
The system provides specific suggestions for improvement:
- Use at least 8 characters
- Mix different character types
- Avoid common patterns
- Choose unique passwords
- Consider 12+ characters for better security

## Example Outputs

### Weak Password (Score: 15/100)
- **Input**: "password123"
- **Issues**: Common dictionary word, simple pattern
- **Recommendations**: Use unique words, add symbols, increase length

### Medium Password (Score: 55/100)
- **Input**: "MyPass123"
- **Issues**: Short length, limited character variety
- **Recommendations**: Add symbols, increase length

### Strong Password (Score: 85/100)
- **Input**: "Tr0ub4dor&3"
- **Strengths**: Good length, multiple character types, reasonable entropy
- **Minor improvements**: Could be longer for maximum security

## Technical Details

### Architecture
- **Main Class**: `PasswordChecker` - JavaFX application entry point
- **Analyzer Class**: `PasswordStrengthAnalyzer` - Core analysis logic
- **Result Class**: `PasswordAnalysisResult` - Data container for analysis results

### Security Considerations
- Passwords are analyzed locally (no network transmission)
- No password storage or logging
- Real-time analysis for immediate feedback

## Customization

### Adding More Weak Passwords
Edit the `WEAK_PASSWORDS` set in the main class to include additional common passwords.

### Adjusting Scoring
Modify the `calculateScore()` method to change point values and thresholds.

### UI Modifications
The JavaFX layout can be customized by modifying the `start()` method in the main class.

## Troubleshooting

### JavaFX Not Found
- Ensure JavaFX is properly installed
- Update the module path in compilation commands
- Check Java version compatibility

### Compilation Errors
- Verify Java version (11+ required)
- Check JavaFX installation
- Ensure all dependencies are available

## License

This project is open source and available under the MIT License.
