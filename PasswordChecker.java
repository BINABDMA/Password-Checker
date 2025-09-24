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

public class PasswordChecker extends Application {
    
    private TextField passwordField;
    private Label strengthLabel;
    private Label scoreLabel;
    private ProgressBar strengthBar;
    private VBox feedbackBox;
    private PasswordStrengthAnalyzer analyzer;
    
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
        
        //  main layout
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
        passwordField.setPrefWidth(300);
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
        strengthBar.setPrefWidth(200);
        
        strengthContainer.getChildren().addAll(strengthLabel, scoreLabel, strengthBar);
        
        // Feedback section
        Label feedbackTitle = new Label("Analysis Details:");
        feedbackTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        feedbackBox = new VBox(5);
        feedbackBox.setAlignment(Pos.CENTER_LEFT);
        feedbackBox.setPrefWidth(400);
        
        // Add components
        root.getChildren().addAll(
            titleLabel,
            passwordPrompt,
            passwordField,
            strengthContainer,
            feedbackTitle,
            feedbackBox
        );
        
        // Create scene and show stage
        Scene scene = new Scene(root, 500, 600);
        primaryStage.setTitle("Password Strength Checker");
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
    
    public static void main(String[] args) {
        launch(args);
    }
    
    // Password Strength Analyzer Class
    public static class PasswordStrengthAnalyzer {
        
        public PasswordAnalysisResult analyzePassword(String password) {
            if (password == null || password.isEmpty()) {
                return new PasswordAnalysisResult(0, "Not Analyzed", 0, 0, 0, false, false, "", new ArrayList<>());
            }
            
            int length = password.length();
            int characterTypes = getCharacterTypes(password);
            boolean hasCommonPatterns = hasCommonPatterns(password);
            boolean isInWeakDictionary = WEAK_PASSWORDS.contains(password.toLowerCase());
            double entropy = calculateEntropy(password);
            String detectedPatterns = getDetectedPatterns(password);
            
            // Calculate score
            int score = calculateScore(length, characterTypes, hasCommonPatterns, isInWeakDictionary, entropy);
            
            // Determine strength category
            String strengthCategory = getStrengthCategory(score);
            
            // Generate recommendations
            List<String> recommendations = generateRecommendations(length, characterTypes, hasCommonPatterns, isInWeakDictionary);
            
            return new PasswordAnalysisResult(
                score, strengthCategory, length, characterTypes, entropy,
                hasCommonPatterns, isInWeakDictionary, detectedPatterns, recommendations
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
                                 boolean isInWeakDictionary, double entropy) {
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
            
            return Math.max(0, Math.min(100, score));
        }
        
        private String getStrengthCategory(int score) {
            if (score >= 70) return "Strong";
            else if (score >= 40) return "Medium";
            else return "Weak";
        }
        
        private List<String> generateRecommendations(int length, int characterTypes, 
                                                   boolean hasCommonPatterns, boolean isInWeakDictionary) {
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
            if (length >= 16 && characterTypes == 4 && !hasCommonPatterns && !isInWeakDictionary) {
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
        
        public PasswordAnalysisResult(int score, String strengthCategory, int length, 
                                   int characterTypes, double entropy, boolean hasCommonPatterns,
                                   boolean isInWeakDictionary, String detectedPatterns, 
                                   List<String> recommendations) {
            this.score = score;
            this.strengthCategory = strengthCategory;
            this.length = length;
            this.characterTypes = characterTypes;
            this.entropy = entropy;
            this.hasCommonPatterns = hasCommonPatterns;
            this.isInWeakDictionary = isInWeakDictionary;
            this.detectedPatterns = detectedPatterns;
            this.recommendations = recommendations;
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
    }
}
