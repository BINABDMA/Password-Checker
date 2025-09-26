import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class PasswordCheckerEnhanced extends Application {
    
    private TextField passwordField;
    private Label strengthLabel;
    private Label scoreLabel;
    private ProgressBar strengthBar;
    private VBox feedbackBox;
    private PasswordStrengthAnalyzer analyzer;
    private HIBPClient hibpClient;
    private Label breachStatusLabel;
    private TextArea breachDetailsArea;
    
    // Common weak passwords dictionary
    private static final Set<String> WEAK_PASSWORDS = new HashSet<>(Arrays.asList(
        "password", "123456", "123456789", "qwerty", "abc123", "password123",
        "admin", "letmein", "welcome", "monkey", "1234567890", "password1",
        "qwerty123", "dragon", "master", "hello", "freedom", "whatever",
        "qazwsx", "trustno1", "654321", "jordan23", "harley", "password1",
        "shadow", "superman", "qwertyuiop", "michael", "football", "baseball",
        "welcome123", "1234567", "12345678", "1234567890", "princess", "azerty",
        "login", "passw0rd", "master", "hello123", "freedom", "whatever",
        "qazwsx", "trustno1", "654321", "jordan23", "harley", "password1",
        "shadow", "superman", "qwertyuiop", "michael", "football", "baseball",
        "welcome123", "1234567", "12345678", "1234567890", "princess", "azerty",
        "login", "passw0rd", "iloveyou", "sunshine", "charlie", "aa123456",
        "donald", "password1", "qwerty123", "dragon", "master", "hello",
        "freedom", "whatever", "qazwsx", "trustno1", "654321", "jordan23",
        "harley", "password1", "shadow", "superman", "qwertyuiop", "michael",
        "football", "baseball", "welcome123", "1234567", "12345678", "1234567890"
    ));
    
    @Override
    public void start(Stage primaryStage) {
        analyzer = new PasswordStrengthAnalyzer();
        hibpClient = new HIBPClient();
        
        // Create main layout
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        
        // Title
        Label titleLabel = new Label("Password Strength Checker");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKBLUE);
        
        // Password input
        Label passwordPrompt = new Label("Enter your password:");
        passwordPrompt.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        passwordField = new TextField();
        passwordField.setPromptText("Type your password here...");
        passwordField.setPrefWidth(450);
        passwordField.setFont(Font.font("Arial", 14));
        
        // Real-time analysis
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            analyzePassword(newValue);
        });
        
        // Strength display
        HBox strengthContainer = new HBox(10);
        strengthContainer.setAlignment(Pos.CENTER);
        
        strengthLabel = new Label("Strength: Not Analyzed");
        strengthLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        scoreLabel = new Label("Score: 0/100");
        scoreLabel.setFont(Font.font("Arial", 14));
        
        strengthBar = new ProgressBar(0);
        strengthBar.setPrefWidth(350);
        
        strengthContainer.getChildren().addAll(strengthLabel, scoreLabel, strengthBar);
        
        // Breach status
        breachStatusLabel = new Label("Checking breach status...");
        breachStatusLabel.setFont(Font.font("Arial", 12));
        breachStatusLabel.setTextFill(Color.GRAY);
        
        // Breach details area with collapsible slider
        HBox breachDetailsHeader = new HBox(10);
        breachDetailsHeader.setAlignment(Pos.CENTER_LEFT);
        
        Label breachDetailsTitle = new Label("Detailed Analysis:");
        breachDetailsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        // Add toggle button for collapsible section
        Button toggleButton = new Button("▼");
        toggleButton.setFont(Font.font("Arial", 12));
        
        breachDetailsHeader.getChildren().addAll(breachDetailsTitle, toggleButton);
        
        breachDetailsArea = new TextArea();
        breachDetailsArea.setPrefRowCount(6);
        breachDetailsArea.setPrefWidth(550);
        breachDetailsArea.setEditable(false);
        breachDetailsArea.setWrapText(true);
        breachDetailsArea.setFont(Font.font("Arial", 11));
        
        // Initially hide the details area
        breachDetailsArea.setVisible(false);
        breachDetailsArea.setManaged(false);
        
        // Toggle functionality
        toggleButton.setOnAction(e -> {
            boolean isVisible = breachDetailsArea.isVisible();
            breachDetailsArea.setVisible(!isVisible);
            breachDetailsArea.setManaged(!isVisible);
            toggleButton.setText(isVisible ? "▶" : "▼");
        });
        
        // Feedback section
        Label feedbackTitle = new Label("Analysis Details:");
        feedbackTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        feedbackBox = new VBox(5);
        feedbackBox.setAlignment(Pos.CENTER_LEFT);
        feedbackBox.setPrefWidth(550);
        
        // Add components to root
        root.getChildren().addAll(
            titleLabel,
            passwordPrompt,
            passwordField,
            strengthContainer,
            breachStatusLabel,
            breachDetailsHeader,
            breachDetailsArea,
            feedbackTitle,
            feedbackBox
        );
        
        // Create scene and show stage
        Scene scene = new Scene(root, 700, 800);
        primaryStage.setTitle("Password Strength Checker - Enhanced");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        
        // Initial analysis
        analyzePassword("");
    }
    
    private void analyzePassword(String password) {
        PasswordAnalysisResult result = analyzer.analyzePassword(password);
        
        // Update strength label and color
        strengthLabel.setText("Strength: " + result.getStrengthCategory());
        strengthLabel.setTextFill(getStrengthColor(result.getStrengthCategory()));
        
        // Update score
        scoreLabel.setText("Score: " + result.getScore() + "/100");
        
        // Update progress bar
        strengthBar.setProgress(result.getScore() / 100.0);
        strengthBar.setStyle(getProgressBarStyle(result.getStrengthCategory()));
        
        // Update feedback
        updateFeedback(result);
        
        // Check for breaches asynchronously
        checkPasswordBreach(password);
    }
    
    private Color getStrengthColor(String strength) {
        switch (strength.toLowerCase()) {
            case "weak": return Color.RED;
            case "medium": return Color.ORANGE;
            case "strong": return Color.GREEN;
            default: return Color.GRAY;
        }
    }
    
    private String getProgressBarStyle(String strength) {
        String color;
        switch (strength.toLowerCase()) {
            case "weak": color = "red"; break;
            case "medium": color = "orange"; break;
            case "strong": color = "green"; break;
            default: color = "gray"; break;
        }
        return "-fx-accent: " + color + ";";
    }
    
    private void updateFeedback(PasswordAnalysisResult result) {
        feedbackBox.getChildren().clear();
        
        // Length feedback
        Label lengthLabel = new Label("✓ Length: " + result.getLength() + " characters " + 
            (result.getLength() >= 8 ? "(Good)" : "(Too short - minimum 8 recommended)"));
        lengthLabel.setTextFill(result.getLength() >= 8 ? Color.GREEN : Color.RED);
        
        // Character variety feedback
        Label varietyLabel = new Label("✓ Character Variety: " + result.getCharacterTypes() + " types used");
        varietyLabel.setTextFill(result.getCharacterTypes() >= 3 ? Color.GREEN : Color.ORANGE);
        
        // Pattern detection feedback
        if (result.hasCommonPatterns()) {
            Label patternLabel = new Label("⚠ Common patterns detected: " + result.getDetectedPatterns());
            patternLabel.setTextFill(Color.ORANGE);
            feedbackBox.getChildren().add(patternLabel);
        }
        
        // Dictionary check feedback
        if (result.isInWeakDictionary()) {
            Label dictLabel = new Label("⚠ Password found in common weak passwords list");
            dictLabel.setTextFill(Color.RED);
            feedbackBox.getChildren().add(dictLabel);
        }
        
        // Entropy feedback
        Label entropyLabel = new Label("✓ Entropy: " + String.format("%.1f", result.getEntropy()) + " bits");
        entropyLabel.setTextFill(result.getEntropy() >= 50 ? Color.GREEN : 
                               result.getEntropy() >= 30 ? Color.ORANGE : Color.RED);
        
        // Recommendations
        if (!result.getRecommendations().isEmpty()) {
            Label recTitle = new Label("Recommendations:");
            recTitle.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            recTitle.setTextFill(Color.DARKBLUE);
            feedbackBox.getChildren().add(recTitle);
            
            for (String rec : result.getRecommendations()) {
                Label recLabel = new Label("• " + rec);
                recLabel.setTextFill(Color.DARKBLUE);
                feedbackBox.getChildren().add(recLabel);
            }
        }
        
        // Add all feedback labels
        feedbackBox.getChildren().addAll(0, Arrays.asList(lengthLabel, varietyLabel, entropyLabel));
    }
    
    private void checkPasswordBreach(String password) {
        if (password == null || password.isEmpty()) {
            breachStatusLabel.setText("Enter a password to check breach status");
            breachStatusLabel.setTextFill(Color.GRAY);
            breachDetailsArea.setText("");
            return;
        }
        
        breachStatusLabel.setText("Checking breach status...");
        breachStatusLabel.setTextFill(Color.BLUE);
        breachDetailsArea.setText("Searching Have I Been Pwned database...");
        
        // Show the details area when checking
        breachDetailsArea.setVisible(true);
        breachDetailsArea.setManaged(true);
        
        // Check breach status asynchronously to avoid blocking UI
        CompletableFuture.supplyAsync(() -> {
            try {
                return hibpClient.getPasswordBreachInfo(password);
            } catch (Exception e) {
                return null; // Indicates error
            }
        }).thenAccept(breachInfo -> {
            javafx.application.Platform.runLater(() -> {
                if (breachInfo == null) {
                    breachStatusLabel.setText("Unable to check breach status (offline/error)");
                    breachStatusLabel.setTextFill(Color.ORANGE);
                    breachDetailsArea.setText("Unable to connect to Have I Been Pwned API.\n" +
                                            "This could be due to:\n" +
                                            "• No internet connection\n" +
                                            "• API service temporarily unavailable\n" +
                                            "• Network firewall blocking the request");
                    breachDetailsArea.setVisible(true);
                    breachDetailsArea.setManaged(true);
                } else if (breachInfo.isPwned()) {
                    breachStatusLabel.setText("PASSWORD FOUND IN DATA BREACHES!");
                    breachStatusLabel.setTextFill(Color.RED);
                    
                    // Show detailed breach information
                    StringBuilder details = new StringBuilder();
                    details.append("🚨 CRITICAL SECURITY ALERT 🚨\n\n");
                    details.append("This password has been found in data breaches!\n\n");
                    details.append("📊 BREACH STATISTICS:\n");
                    details.append("• Total occurrences: ").append(breachInfo.getOccurrenceCount()).append("\n");
                    details.append("• Severity: ").append(getSeverityLevel(breachInfo.getOccurrenceCount())).append("\n\n");
                    
                    details.append("🔍 WHAT THIS MEANS:\n");
                    details.append("• Your password is publicly available\n");
                    details.append("• It's likely being used in automated attacks\n");
                    details.append("• Anyone can find it in breach databases\n\n");
                    
                    details.append("⚠️ IMMEDIATE ACTION REQUIRED:\n");
                    details.append("• Change this password immediately\n");
                    details.append("• Use a unique, strong password\n");
                    details.append("• Enable 2FA where possible\n");
                    details.append("• Check if you've used this password elsewhere\n\n");
                    
                    details.append("💡 BREACH CONTEXT:\n");
                    details.append("This password appears ").append(breachInfo.getOccurrenceCount());
                    if (breachInfo.getOccurrenceCount() == 1) {
                        details.append(" time in the Have I Been Pwned database.\n");
                    } else {
                        details.append(" times in the Have I Been Pwned database.\n");
                    }
                    details.append("The database contains over 8 billion compromised passwords\n");
                    details.append("from thousands of data breaches worldwide.");
                    
                    breachDetailsArea.setText(details.toString());
                    breachDetailsArea.setVisible(true);
                    breachDetailsArea.setManaged(true);
                } else {
                    breachStatusLabel.setText("Password not found in known breaches");
                    breachStatusLabel.setTextFill(Color.GREEN);
                    breachDetailsArea.setText("✅ SECURITY STATUS: CLEAN\n\n" +
                                            "This password has NOT been found in any known data breaches.\n\n" +
                                            "🔒 WHAT THIS MEANS:\n" +
                                            "• Your password is not publicly available\n" +
                                            "• It hasn't been exposed in major data breaches\n" +
                                            "• It's relatively safe from automated attacks\n\n" +
                                            "⚠️ IMPORTANT REMINDERS:\n" +
                                            "• This only checks known breaches\n" +
                                            "• Use unique passwords for each account\n" +
                                            "• Enable 2FA when available\n" +
                                            "• Keep your passwords strong and long");
                    breachDetailsArea.setVisible(true);
                    breachDetailsArea.setManaged(true);
                }
            });
        });
    }
    
    private String getSeverityLevel(int count) {
        if (count >= 1000000) return "EXTREMELY HIGH (1M+ occurrences)";
        else if (count >= 100000) return "VERY HIGH (100K+ occurrences)";
        else if (count >= 10000) return "HIGH (10K+ occurrences)";
        else if (count >= 1000) return "MODERATE (1K+ occurrences)";
        else if (count >= 100) return "LOW (100+ occurrences)";
        else return "MINIMAL (<100 occurrences)";
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
    // Password Strength Analyzer Class
    public static class PasswordStrengthAnalyzer {
        
        public PasswordAnalysisResult analyzePassword(String password) {
            if (password == null || password.isEmpty()) {
                return new PasswordAnalysisResult(0, "Not Analyzed", 0, 0, 0, false, false, "", new ArrayList<>(), false);
            }
            
            int length = password.length();
            int characterTypes = getCharacterTypes(password);
            boolean hasCommonPatterns = hasCommonPatterns(password);
            boolean isInWeakDictionary = WEAK_PASSWORDS.contains(password.toLowerCase());
            double entropy = calculateEntropy(password);
            String detectedPatterns = getDetectedPatterns(password);
            boolean isBreached = false; // Will be updated by HIBP check
            
            // Calculate score
            int score = calculateScore(length, characterTypes, hasCommonPatterns, isInWeakDictionary, entropy, isBreached);
            
            // Determine strength category
            String strengthCategory = getStrengthCategory(score);
            
            // Generate recommendations
            List<String> recommendations = generateRecommendations(length, characterTypes, hasCommonPatterns, isInWeakDictionary, isBreached);
            
            return new PasswordAnalysisResult(
                score, strengthCategory, length, characterTypes, entropy,
                hasCommonPatterns, isInWeakDictionary, detectedPatterns, recommendations, isBreached
            );
        }
        
        private int getCharacterTypes(String password) {
            boolean hasLower = password.matches(".*[a-z].*");
            boolean hasUpper = password.matches(".*[A-Z].*");
            boolean hasDigit = password.matches(".*[0-9].*");
            boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");
            
            int types = 0;
            if (hasLower) types++;
            if (hasUpper) types++;
            if (hasDigit) types++;
            if (hasSpecial) types++;
            
            return types;
        }
        
        private boolean hasCommonPatterns(String password) {
            String lower = password.toLowerCase();
            
            // Check for sequential patterns
            if (lower.contains("1234") || lower.contains("abcd") || lower.contains("qwerty") ||
                lower.contains("asdf") || lower.contains("zxcv") || lower.contains("5678") ||
                lower.contains("9876") || lower.contains("4321") || lower.contains("dcba")) {
                return true;
            }
            
            // Check for repeated characters
            for (int i = 0; i < password.length() - 2; i++) {
                if (password.charAt(i) == password.charAt(i + 1) && 
                    password.charAt(i + 1) == password.charAt(i + 2)) {
                    return true;
                }
            }
            
            // Check for keyboard patterns
            String[] keyboardPatterns = {"qwerty", "asdfgh", "zxcvbn", "qwertyuiop", "asdfghjkl", "zxcvbnm"};
            for (String pattern : keyboardPatterns) {
                if (lower.contains(pattern)) {
                    return true;
                }
            }
            
            return false;
        }
        
        private String getDetectedPatterns(String password) {
            List<String> patterns = new ArrayList<>();
            String lower = password.toLowerCase();
            
            if (lower.contains("1234")) patterns.add("Sequential numbers");
            if (lower.contains("abcd")) patterns.add("Sequential letters");
            if (lower.contains("qwerty")) patterns.add("Keyboard pattern");
            if (lower.contains("asdf")) patterns.add("Keyboard pattern");
            
            // Check for repeated characters
            for (int i = 0; i < password.length() - 2; i++) {
                if (password.charAt(i) == password.charAt(i + 1) && 
                    password.charAt(i + 1) == password.charAt(i + 2)) {
                    patterns.add("Repeated characters");
                    break;
                }
            }
            
            return String.join(", ", patterns);
        }
        
        private double calculateEntropy(String password) {
            Map<Character, Integer> charCount = new HashMap<>();
            for (char c : password.toCharArray()) {
                charCount.put(c, charCount.getOrDefault(c, 0) + 1);
            }
            
            double entropy = 0;
            int length = password.length();
            
            for (int count : charCount.values()) {
                double probability = (double) count / length;
                entropy -= probability * (Math.log(probability) / Math.log(2));
            }
            
            // Calculate theoretical maximum entropy based on character set
            int charsetSize = getCharacterSetSize(password);
            double maxEntropy = Math.log(charsetSize) / Math.log(2);
            double theoreticalMax = maxEntropy * length;
            
            // Use the actual entropy, but cap it at theoretical maximum
            double actualEntropy = entropy * length;
            
            // Bonus for using all character types
            if (getCharacterTypes(password) == 4) {
                actualEntropy *= 1.1; // 10% bonus for using all character types
            }
            
            return Math.min(actualEntropy, theoreticalMax);
        }
        
        private int getCharacterSetSize(String password) {
            boolean hasLower = password.matches(".*[a-z].*");
            boolean hasUpper = password.matches(".*[A-Z].*");
            boolean hasDigit = password.matches(".*[0-9].*");
            boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");
            
            int charsetSize = 0;
            if (hasLower) charsetSize += 26;
            if (hasUpper) charsetSize += 26;
            if (hasDigit) charsetSize += 10;
            if (hasSpecial) charsetSize += 32; // Common special characters
            
            return charsetSize;
        }
        
        private int calculateScore(int length, int characterTypes, boolean hasCommonPatterns, 
                                 boolean isInWeakDictionary, double entropy, boolean isBreached) {
            int score = 0;
            
            // Length scoring (0-35 points) 
            if (length >= 16) score += 35;      
            else if (length >= 12) score += 30;
            else if (length >= 8) score += 20;
            else if (length >= 6) score += 10;
            
            // Character variety scoring (0-30 points) 
            score += characterTypes * 7.5;     
            
            // Entropy scoring (0-35 points) 
            if (entropy >= 80) score += 35;   
            else if (entropy >= 60) score += 30;
            else if (entropy >= 40) score += 25;
            else if (entropy >= 30) score += 20;
            else if (entropy >= 20) score += 15;
            else if (entropy >= 10) score += 10;
            
            // Bonus points for exceptional passwords (up to 10 points)
            if (length >= 16 && characterTypes == 4 && entropy >= 60 && !hasCommonPatterns && !isInWeakDictionary) {
                score += 10;  
            }
            
            // Penalties (reduced to allow higher scores)
            if (hasCommonPatterns) score -= 10;  
            if (isInWeakDictionary) score -= 15;
            if (isBreached) score -= 25;  // Severe penalty for breached passwords
            
            return Math.max(0, Math.min(100, score));
        }
        
        private String getStrengthCategory(int score) {
            if (score >= 70) return "Strong";
            else if (score >= 40) return "Medium";
            else return "Weak";
        }
        
        private List<String> generateRecommendations(int length, int characterTypes, 
                                                   boolean hasCommonPatterns, boolean isInWeakDictionary, boolean isBreached) {
            List<String> recommendations = new ArrayList<>();
            
            if (length < 8) {
                recommendations.add("Use at least 8 characters");
            }
            if (characterTypes < 3) {
                recommendations.add("Use a mix of letters, numbers, and symbols");
            }
            if (hasCommonPatterns) {
                recommendations.add("Avoid common patterns and sequences");
            }
            if (isInWeakDictionary) {
                recommendations.add("Choose a more unique password");
            }
            if (length < 12) {
                recommendations.add("Consider using 12+ characters for better security");
            }
            if (length < 16) {
                recommendations.add("Use 16+ characters for maximum score (35 points)");
            }
            if (characterTypes < 4) {
                recommendations.add("Use all character types (lowercase, uppercase, digits, symbols) for maximum variety");
            }
            if (isBreached) {
                recommendations.add("🚨 CRITICAL: This password has been found in data breaches - CHANGE IMMEDIATELY!");
            }
            if (length >= 16 && characterTypes == 4 && !hasCommonPatterns && !isInWeakDictionary && !isBreached) {
                recommendations.add("Excellent! This password meets all criteria for maximum security");
            }
            
            return recommendations;
        }
    }
    
    // Result class to hold analysis data
    public static class PasswordAnalysisResult {
        private final int score;
        private final String strengthCategory;
        private final int length;
        private final int characterTypes;
        private final double entropy;
        private final boolean hasCommonPatterns;
        private final boolean isInWeakDictionary;
        private final String detectedPatterns;
        private final List<String> recommendations;
        private final boolean isBreached;
        
        public PasswordAnalysisResult(int score, String strengthCategory, int length, 
                                   int characterTypes, double entropy, boolean hasCommonPatterns,
                                   boolean isInWeakDictionary, String detectedPatterns, 
                                   List<String> recommendations, boolean isBreached) {
            this.score = score;
            this.strengthCategory = strengthCategory;
            this.length = length;
            this.characterTypes = characterTypes;
            this.entropy = entropy;
            this.hasCommonPatterns = hasCommonPatterns;
            this.isInWeakDictionary = isInWeakDictionary;
            this.detectedPatterns = detectedPatterns;
            this.recommendations = recommendations;
            this.isBreached = isBreached;
        }
        
        // Getters
        public int getScore() { return score; }
        public String getStrengthCategory() { return strengthCategory; }
        public int getLength() { return length; }
        public int getCharacterTypes() { return characterTypes; }
        public double getEntropy() { return entropy; }
        public boolean hasCommonPatterns() { return hasCommonPatterns; }
        public boolean isInWeakDictionary() { return isInWeakDictionary; }
        public String getDetectedPatterns() { return detectedPatterns; }
        public List<String> getRecommendations() { return recommendations; }
        public boolean isBreached() { return isBreached; }
    }
    
    // Breach information class
    public static class BreachInfo {
        private final boolean pwned;
        private final int occurrenceCount;
        
        public BreachInfo(boolean pwned, int occurrenceCount) {
            this.pwned = pwned;
            this.occurrenceCount = occurrenceCount;
        }
        
        public boolean isPwned() { return pwned; }
        public int getOccurrenceCount() { return occurrenceCount; }
    }
    
    // Enhanced Have I Been Pwned API Client
    public static class HIBPClient {
        private static final String HIBP_API_URL = "https://api.pwnedpasswords.com/range/";
        
        /**
         * Get detailed breach information for a password
         * @param password The password to check
         * @return BreachInfo object with occurrence count, or null if not found
         * @throws Exception if API call fails
         */
        public BreachInfo getPasswordBreachInfo(String password) throws Exception {
            if (password == null || password.isEmpty()) {
                return new BreachInfo(false, 0);
            }
            
            // Generate SHA-1 hash of the password
            String sha1Hash = getSHA1Hash(password).toUpperCase();
            
            // Extract first 5 characters (prefix) and remaining characters (suffix)
            String prefix = sha1Hash.substring(0, 5);
            String suffix = sha1Hash.substring(5);
            
            // Query the HIBP API with the prefix
            String response = queryHIBPAPI(prefix);
            
            // Parse the response to find our suffix and get occurrence count
            return parseBreachResponse(response, suffix);
        }
        
        /**
         * Check if a password has been found in data breaches (simple boolean check)
         * @param password The password to check
         * @return true if password was found in breaches, false otherwise
         * @throws Exception if API call fails
         */
        public boolean isPasswordPwned(String password) throws Exception {
            BreachInfo info = getPasswordBreachInfo(password);
            return info != null && info.isPwned();
        }
        
        /**
         * Parse the HIBP API response to find occurrence count
         * @param response The API response containing hash suffixes and counts
         * @param suffix The suffix we're looking for
         * @return BreachInfo object with occurrence count
         */
        private BreachInfo parseBreachResponse(String response, String suffix) {
            String[] lines = response.split("\n");
            for (String line : lines) {
                if (line.contains(":")) {
                    String[] parts = line.split(":");
                    if (parts.length >= 2 && parts[0].equals(suffix)) {
                        try {
                            int count = Integer.parseInt(parts[1].trim());
                            return new BreachInfo(true, count);
                        } catch (NumberFormatException e) {
                            // Invalid count format, continue searching
                        }
                    }
                }
            }
            return new BreachInfo(false, 0);
        }
        
        /**
         * Generate SHA-1 hash of the password
         * @param password The password to hash
         * @return SHA-1 hash as uppercase hex string
         * @throws NoSuchAlgorithmException if SHA-1 is not available
         */
        private String getSHA1Hash(String password) throws NoSuchAlgorithmException {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        }
        
        /**
         * Query the HIBP API for password suffixes
         * @param prefix First 5 characters of SHA-1 hash
         * @return Response containing suffixes and their occurrence counts
         * @throws Exception if API call fails
         */
        private String queryHIBPAPI(String prefix) throws Exception {
            URL url = new URL(HIBP_API_URL + prefix);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "PasswordChecker/1.0");
            conn.setConnectTimeout(5000); // 5 second timeout
            conn.setReadTimeout(10000);   // 10 second timeout
            
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new Exception("HIBP API returned response code: " + responseCode);
            }
            
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine).append("\n");
            }
            in.close();
            conn.disconnect();
            
            return content.toString();
        }
    }
}
