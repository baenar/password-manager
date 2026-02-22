package passwordmanager.views;

import org.jline.terminal.Attributes;
import passwordmanager.data.CommonData;
import passwordmanager.data.Hasher;

import java.io.IOException;

public class AddPasswordView extends AView {

    private String service = "";
    private String username = "";
    private String password = "";
    private String notes = "";

    private int selectedIndex = 0;

    @Override
    public AView showContent() {
        Attributes originalAttributes = terminal.enterRawMode();

        try {
            while (true) {
                renderForm();

                int code = terminal.reader().read();

                int FIELD_COUNT = 6;
                if (code == 'w' || code == 'W') { // UP
                    selectedIndex = (selectedIndex - 1 + FIELD_COUNT) % FIELD_COUNT;
                }
                else if (code == 's' || code == 'S') { // DOWN
                    selectedIndex = (selectedIndex + 1) % FIELD_COUNT;
                }
                else if (code == 13 || code == 10) { // ENTER
                    AView actionResult = handleEnterKey();
                    if (actionResult != null) return actionResult;
                }
                else if (code == 27) { // ESC
                    return new StartView();
                }
            }
        } catch (IOException e) {
            return new StartView("Error in form view.");
        } finally {
            terminal.setAttributes(originalAttributes);
        }
    }

    private void renderForm() {
        clearScreen();
        displayHeader();
        terminal.writer().println(centerText("=== ADD NEW PASSWORD ==="));
        terminal.writer().println(centerText("(Use W/S to navigate, ENTER to edit)"));
        terminal.writer().println();

        printField(0, "Service ", service);
        printField(1, "Username", username);
        printField(2, "Password", password.isEmpty() ? "" : "********");
        printField(3, "Notes   ", notes);

        terminal.writer().println();

        printButton(4, "[ SAVE ENTRY ]");
        printButton(5, "[ CANCEL ]");

        terminal.flush();
    }

    private void printField(int index, String label, String value) {
        String prefix = (index == selectedIndex) ? " >> " : "    ";
        String valueDisplay = (value == null || value.isEmpty()) ? "<empty>" : value;
        terminal.writer().println(prefix + label + ": " + valueDisplay);
    }

    private void printButton(int index, String label) {
        String prefix = (index == selectedIndex) ? " >> " : "    ";
        terminal.writer().println(prefix + label);
    }

    private AView handleEnterKey() {
        Attributes raw = terminal.enterRawMode();
        terminal.setAttributes(terminal.getAttributes());

        try {
            switch (selectedIndex) {
                case 0:
                    service = editString("Service: ", service);
                    break;
                case 1:
                    username = editString("Username: ", username);
                    break;
                case 2:
                    password = editPassword(password);
                    break;
                case 3:
                    notes = editString("Notes: ", notes);
                    break;
                case 4:
                    if (validate()) {
                        if(password.isEmpty())
                            password = Hasher.generateStrongPassword();
                        CommonData data = new CommonData(username, service, password, notes);
                        writer.addNewPassword(data);
                        return new StartView("Entry added successfully!");
                    }
                    break;
                case 5:
                    return new StartView();
            }
        } finally {
            terminal.setAttributes(raw);
        }
        return null;
    }

    private String editString(String prompt, String currentValue) {
        try {
            String input = lineReader.readLine(prompt, null, currentValue);
            return input.trim();
        } catch (org.jline.reader.UserInterruptException e) {
            return currentValue;
        }
    }

    private String editPassword(String currentValue) {
        try {
            return lineReader.readLine("Password (leave empty to generate): ", '*', currentValue);
        } catch (org.jline.reader.UserInterruptException e) {
            return currentValue;
        }
    }

    private boolean validate() {
        if (service.isEmpty() || username.isEmpty()) {
            this.infoMessage = "!!! Error: Service and Username are required !!!";
            return false;
        }
        return true;
    }
}