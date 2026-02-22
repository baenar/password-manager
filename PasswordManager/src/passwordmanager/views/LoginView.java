package passwordmanager.views;

import passwordmanager.utils.SecurityUtils;
import java.util.Arrays;

public class LoginView extends AView {
    public LoginView() {
        super(null);
    }

    public LoginView(String infoMessage) {
        super(infoMessage);
    }

    @Override
    public AView showContent() {
        terminal.writer().println(centerText("=== INSERT YOUR CREDENTIALS ==="));
        terminal.flush();

        String username;
        String password;

        try {
            username = prompt("User: ");
            password = promptPassword();

        } catch (org.jline.reader.UserInterruptException e) {
            return new LoginView("Input cancelled. Please try again.");
        }

        SecurityUtils.setSECRET_KEY(password);
        char[] passwordChars = password.toCharArray();
        boolean isAuthenticated = reader.verifyLogin(username, passwordChars);

        Arrays.fill(passwordChars, ' ');

        if (isAuthenticated) {
            return new StartView("Login successful!");
        } else {
            return new LoginView("Login unsuccessful, try again.");
        }
    }
}