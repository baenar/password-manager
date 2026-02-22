package passwordmanager.views;

import org.jline.terminal.Attributes;
import passwordmanager.data.SettingsData;
import java.io.IOException;

public class SettingsView extends AView {

    private int selectedIndex = 0;

    @Override
    public AView showContent() {
        Attributes originalAttributes = terminal.enterRawMode();

        try {
            while (true) {
                renderSettings();

                int code = terminal.reader().read();

                int FIELD_COUNT = 3;
                if (code == 'w' || code == 'W') {
                    selectedIndex = (selectedIndex - 1 + FIELD_COUNT) % FIELD_COUNT;
                } else if (code == 's' || code == 'S') {
                    selectedIndex = (selectedIndex + 1) % FIELD_COUNT;
                }
                else if (code == 13 || code == 10) {
                    AView result = handleEnter();
                    if (result != null) return result;
                }
                else if (code == 27) {
                    saveAndExit();
                    return new StartView();
                }
            }
        } catch (IOException e) {
            return new StartView("Error in settings.");
        } finally {
            terminal.setAttributes(originalAttributes);
        }
    }

    private void renderSettings() {
        clearScreen();
        displayHeader();
        terminal.writer().println(centerText("=== APPLICATION SETTINGS ==="));
        terminal.writer().println(centerText("(Use W/S to navigate, ENTER to change)"));
        terminal.writer().println();

        printOption(0, "Password Expiration (Months)", String.valueOf(SettingsData.passwordExpirationInMonths));
        printOption(1, "Generated Password Length   ", String.valueOf(SettingsData.generatedPasswordLength));

        terminal.writer().println();
        printButton();

        terminal.flush();
    }

    private void printOption(int index, String label, String value) {
        String prefix = (index == selectedIndex) ? " >> " : "    ";
        terminal.writer().println(prefix + label + ": " + value);
    }

    private void printButton() {
        String prefix = (2 == selectedIndex) ? " >> " : "    ";
        terminal.writer().println(prefix + "[ SAVE & BACK ]");
    }

    private AView handleEnter() {
        Attributes raw = terminal.enterRawMode();
        terminal.setAttributes(terminal.getAttributes());

        try {
            switch (selectedIndex) {
                case 0:
                    String exp = lineReader.readLine("Enter new expiration (months): ", null, String.valueOf(SettingsData.passwordExpirationInMonths));
                    try {
                        SettingsData.passwordExpirationInMonths = Integer.parseInt(exp);
                    } catch (NumberFormatException _) { }
                    break;

                case 1:
                    String len = lineReader.readLine("Enter new length (chars): ", null, String.valueOf(SettingsData.generatedPasswordLength));
                    try {
                        SettingsData.generatedPasswordLength = Integer.parseInt(len);
                    } catch (NumberFormatException _) {}
                    break;

                case 2:
                    saveAndExit();
                    return new StartView();
            }
        } catch (Exception _) {
        } finally {
            terminal.setAttributes(raw);
        }
        return null;
    }

    private void saveAndExit() {
        writer.saveSettings();
    }
}