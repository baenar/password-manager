package passwordmanager.data;

import java.util.HashMap;

public class Validator {
    // return null if no error found, error message otherwise
    public String validatePassword(String password, String passwordRepeat) {
        if (password == null || password.isEmpty()) {
            return "Password is empty, try again";
        }

        if (!password.equals(passwordRepeat)) {
            return "Passwords must be identical, try again";
        }

        if (password.length() < 12) {
            return "Password must be at least 12 characters long, try again";
        }

        if (!password.matches(".*[A-Z].*")) {
            return "Password needs at least one uppercase letter, try again";
        }

        if (!password.matches(".*[a-z].*")) {
            return "Password needs at least one lowercase letter, try again";
        }

        if (!password.matches(".*[0-9].*")) {
            return "Password needs at least one digit, try again";
        }

        if (Validator.shannonEntropy(password) < 3.0) {
            return "Password is too predictable, try a more random sequence";
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            return "Password needs at least one special character, try again";
        }

        return null;
    }

    public String validateInput(String input, String fieldName) {
        if (input == null || input.isEmpty()) {
            return fieldName + " is empty, try again";
        }

        if (input.contains("|")) {
            return fieldName + " cannot contain pipe sign \"|\", try again";
        }

        return null;
    }

    public static double shannonEntropy(String s) {
        if (s == null || s.isEmpty()) return 0.0;

        HashMap<Character, Integer> frequencyMap = new HashMap<>();
        for (char c : s.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }

        double entropy = 0.0;
        int length = s.length();
        for (int count : frequencyMap.values()) {
            double probability = (double) count / length;
            entropy -= probability * (Math.log(probability) / Math.log(2));
        }

        return entropy;
    }
}
