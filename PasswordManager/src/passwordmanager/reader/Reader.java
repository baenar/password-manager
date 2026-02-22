package passwordmanager.reader;

import passwordmanager.data.CommonData;
import passwordmanager.data.Hasher;
import passwordmanager.data.SettingsData;
import passwordmanager.utils.SecurityUtils;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Reader {
    private final String filepath;

    public Reader(String filepath) {
        this.filepath = filepath;
    }

    public ArrayList<CommonData> readCommonData() {
        Path path = Paths.get(filepath);

        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        try (Stream<String> lines = Files.lines(path)) {
            return lines
                    .skip(2)
                    .filter(line -> !line.isBlank())
                    .map(this::parseLine)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));

        } catch (IOException e) {
            System.err.println("Error reading database: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private CommonData parseLine(String line) {
        try {
            String[] parts = line.split("\\|", -1);

            if (parts.length < 5) {
                return null;
            }

            String service = SecurityUtils.decodeSafe(parts[0]);
            String username = SecurityUtils.decodeSafe(parts[1]);
            String password = SecurityUtils.decrypt(parts[2]);
            String notes = SecurityUtils.decodeSafe(parts[3]);
            LocalDateTime date = LocalDateTime.parse(parts[4]);

            // When error in decrypting, return null for whole row
            if (password == null) {
                return null;
            }

            CommonData data = new CommonData(username, service, password, notes);
            data.setLastUpdatedOn(date);

            return data;

        } catch (Exception e) {
            System.err.println("Skipping corrupted line: " + line);
            return null;
        }
    }

    public boolean verifyLogin(String inputUsername, char[] passwordChars) {
        Path path = Paths.get(filepath);
        if (!Files.exists(path)) return false;

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String firstLine = br.readLine();
            if (firstLine == null || firstLine.isBlank()) return false;

            String[] header = firstLine.split("\\|", -1);

            if (header.length < 4) return false;

            String storedUsername = header[0];
            String salt = header[1];
            String storedHash = header[2];
            int iterations = Integer.parseInt(header[3]);

            if (!storedUsername.equals(inputUsername)) return false;

            String generatedHash = Hasher.hashPassword(passwordChars, salt, iterations);

            if(storedHash.equals(generatedHash))
            {
                loadSettings();
                return true;
            }
            return false;

        } catch (Exception e) {
            System.err.println("Login verification failed: " + e.getMessage());
            return false;
        }
    }

    public void loadSettings() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            reader.readLine();

            String settingsLine = reader.readLine();
            if (settingsLine != null && settingsLine.startsWith("SETTINGS|")) {
                String[] parts = settingsLine.split("\\|");
                if (parts.length >= 3) {
                    SettingsData.passwordExpirationInMonths = Integer.parseInt(parts[1]);
                    SettingsData.generatedPasswordLength = Integer.parseInt(parts[2]);
                }
            }
        } catch (Exception e) {
            System.err.println("Using default settings (Could not load from file).");
        }
    }
}

