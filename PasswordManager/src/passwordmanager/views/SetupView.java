package passwordmanager.views;

public class SetupView extends AView{
    public SetupView(String message) {
        super(message);
    }

    public SetupView() {}

    @Override
    public AView showContent() {
        terminal.writer().println(centerText("=== DATABASE SETUP ==="));
        terminal.writer().println(centerText("No database has been found stored on this computer"));
        terminal.writer().println(centerText("Complete your credentials below."));
        terminal.writer().println(centerText("Make sure to pick a strong password."));

        String username, password, rPassword;
        try {
            username = lineReader.readLine("User: ");
            password = lineReader.readLine("Password: ", '\0');
            rPassword = lineReader.readLine("Repeat password: ", '\0');
        }catch (org.jline.reader.UserInterruptException e) {
            return new SetupView("Input cancelled. Please try again.");
        }

        String errorMessage = validator.validateInput(username, "Username");
        if(errorMessage != null) return new SetupView(errorMessage);

        errorMessage = validator.validatePassword(password, rPassword);
        if(errorMessage != null) return new SetupView(errorMessage);

        errorMessage = writer.createDatabase(username, password);
        if(errorMessage != null) return new SetupView(errorMessage);

        return new LoginView("Database created successfully! Try logging in!");

    }
}
