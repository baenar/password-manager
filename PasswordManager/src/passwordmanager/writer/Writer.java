package passwordmanager.writer;

import passwordmanager.data.CommonData;
import passwordmanager.data.Hasher;
import passwordmanager.data.SettingsData;
import passwordmanager.utils.SecurityUtils;

import java.io.*;

public class Writer {
    private final String filepath;

    public Writer(String filepath) {
        this.filepath = filepath;
    }

    public String createDatabase(String username, String password) {
        try (FileWriter fileWriter = new FileWriter(filepath, true)){
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(Hasher.createDatabaseFirstLine(username, password));

            String settingsLine = String.format("SETTINGS|%d|%d",
                    SettingsData.passwordExpirationInMonths,
                    SettingsData.generatedPasswordLength);
            printWriter.println(settingsLine);
        } catch (Exception e) {
            return "ERROR! : " + e.getMessage();
        }
        return null;
    }

    public void addNewPassword(CommonData data) {
        try (FileWriter fileWriter = new FileWriter(filepath, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {

            String encryptedPass = SecurityUtils.encrypt(data.getPassword());

            String safeService = SecurityUtils.encodeSafe(data.getService());
            String safeUser = SecurityUtils.encodeSafe(data.getUsername());
            String safeNotes = SecurityUtils.encodeSafe(data.getNotes() != null ? data.getNotes() : "");

            String line = String.format("%s|%s|%s|%s|%s",
                    safeService,
                    safeUser,
                    encryptedPass,
                    safeNotes,
                    data.getLastUpdatedOn().toString()
            );

            printWriter.println(line);

        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    public void deleteEntry(CommonData entry) {
        File file = new File(filepath);
        File tempFile = new File(filepath + ".tmp");

        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {

            String header = reader.readLine();
            if (header != null) {
                writer.println(header);
            }

            String targetService = SecurityUtils.encodeSafe(entry.getService());
            String targetUser = SecurityUtils.encodeSafe(entry.getUsername());

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2) {
                    if (parts[0].equals(targetService) && parts[1].equals(targetUser)) {
                        continue;
                    }
                }
                writer.println(line);
            }

        } catch (IOException e) {
            System.err.println("Error during deletion: " + e.getMessage());
            return;
        }

        if (!file.delete() || !tempFile.renameTo(file)) {
            System.err.println("Could not update database file after deletion.");
        }
    }

    public void updateEntry(CommonData entry) {
        File file = new File(filepath);
        File tempFile = new File(filepath + ".tmp");

        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {

            String header = reader.readLine();
            if (header != null) writer.println(header);

            String targetService = SecurityUtils.encodeSafe(entry.getService());
            String targetUser = SecurityUtils.encodeSafe(entry.getUsername());

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2 && parts[0].equals(targetService) && parts[1].equals(targetUser)) {
                    String encryptedPass = SecurityUtils.encrypt(entry.getPassword());
                    String safeNotes = SecurityUtils.encodeSafe(entry.getNotes() != null ? entry.getNotes() : "");

                    writer.printf("%s|%s|%s|%s|%s%n",
                            targetService, targetUser, encryptedPass, safeNotes, entry.getLastUpdatedOn());
                } else {
                    writer.println(line);
                }
            }
        } catch (IOException e) {
            return;
        }
        file.delete();
        tempFile.renameTo(file);
    }

    public void saveSettings() {
        File file = new File(filepath);
        File tempFile = new File(filepath + ".tmp");

        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {

            String header = reader.readLine();
            if (header != null) writer.println(header);

            reader.readLine();

            String newSettings = String.format("SETTINGS|%d|%d",
                    SettingsData.passwordExpirationInMonths,
                    SettingsData.generatedPasswordLength);
            writer.println(newSettings);

            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }

        } catch (IOException e) {
            System.err.println("Error saving settings: " + e.getMessage());
            return;
        }

        if (file.delete()) {
            tempFile.renameTo(file);
        } else {
            System.err.println("Could not update database file.");
        }
    }
}
