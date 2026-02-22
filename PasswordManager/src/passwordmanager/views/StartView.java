package passwordmanager.views;

import org.jline.terminal.Attributes;
import java.io.IOException;

public class StartView extends AView {
    private final String[] options = {
            "Add password",
            "Browse passwords",
            "Security center",
            "Settings",
            "Exit"
    };
    private final MenuSelection menu = new MenuSelection(options);

    public StartView(String s) {
        super(s);
    }

    public StartView() {
        super();
    }

    @Override
    public AView showContent() {
        Attributes originalAttributes = terminal.enterRawMode();

        try {
            while (true) {
                clearScreen();
                displayHeader();

                terminal.writer().println(centerText("=== MAIN MENU ==="));
                terminal.writer().println(centerText("(Use W/S to navigate, ENTER to select)"));

                Integer chosenOption = menu.render(terminal);

                if (chosenOption != null) {
                    return handleSelection(chosenOption);
                }
            }
        } catch (IOException e) {
            return null;
        } finally {
            terminal.setAttributes(originalAttributes);
        }
    }

    private AView handleSelection(int choice) {
        switch (choice) {
            case 0:
                return new AddPasswordView();
            case 1:
                return new BrowsePasswordsView();
            case 2:
                return new SecurityCenterView();
            case 3:
                return new SettingsView();
            case 4:
                clearScreen();
                terminal.writer().println(centerText("Goodbye!"));
                terminal.writer().flush();
                return null;
            default:
                return this;
        }
    }
}