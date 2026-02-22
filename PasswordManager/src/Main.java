import passwordmanager.PasswordManager;

public class Main {
    public static void main(String[] args) {
        PasswordManager passwordManager = new PasswordManager(args[0]);
        passwordManager.run();
    }
}