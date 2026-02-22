package passwordmanager;

import passwordmanager.views.AView;
import passwordmanager.views.LoginView;
import passwordmanager.views.SetupView;

import java.nio.file.Files;
import java.nio.file.Paths;

public class PasswordManager {
    private AView currentView;
    public PasswordManager(String filepath) {
        AView.init(filepath);
        if (!Files.exists(Paths.get(filepath))) {
            currentView = new SetupView();
        } else {
            currentView = new LoginView();
        }
    }

    public void run() {
        while (currentView != null) {
            currentView = currentView.show();
        }
    }
}
