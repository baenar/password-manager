package passwordmanager.views;

import org.jline.reader.Reference;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import passwordmanager.data.Validator;
import passwordmanager.reader.Reader;
import passwordmanager.writer.Writer;

import java.io.IOException;

public abstract class AView {
    protected static Terminal terminal;
    protected static LineReader lineReader;
    protected static Reader reader;
    protected static Writer writer;
    protected static Validator validator;
    protected static java.util.TimerTask pendingCleanupTask = null;
    protected static final java.util.Timer clipboardTimer = new java.util.Timer(true);

    private final static String name = "PASSWORD MANAGER";
    private final static Integer consoleWidth = 60;
    protected final static String separator = "=".repeat(consoleWidth);
    private final static String title = centerText(name);

    protected String infoMessage;

    static {
        try {
            terminal = TerminalBuilder.builder()
                    .system(true)
                    .nativeSignals(true)
                    .build();
            terminal.enterRawMode();

            lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .build();

            lineReader.getWidgets().put("esc-quit", () -> {
                throw new UserInterruptException("");
            });

            lineReader.getKeyMaps().get(LineReader.MAIN).bind(new Reference("esc-quit"), "\033");
        } catch (IOException e) {
            try {
                terminal = TerminalBuilder.builder().dumb(true).build();
                lineReader = LineReaderBuilder.builder().terminal(terminal).build();
            } catch (IOException ignored) {}
        }
    }

    public AView(String infoMessage) {
        this.infoMessage = infoMessage;
    }

    public AView() {
        this(null);
    }

    public static void init(String filepath) {
        reader = new Reader(filepath);
        writer = new Writer(filepath);
        validator = new Validator();
    }

    protected void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (Exception e) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }

    protected void displayHeader() {
        System.out.println("\n" + separator);
        System.out.println(title);
        System.out.println(separator);
        if (infoMessage != null && !infoMessage.isBlank()) {
            String wrapped = wrapText(infoMessage);
            for (String line : wrapped.split("\n")) {
                System.out.println(centerText(line));
            }
            System.out.println(separator);
        }
    }

    protected static String centerText(String text) {
        if (text == null) return "";
        int padding = (consoleWidth - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    }

    protected static String wrapText(String text) {
        if (text == null || text.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            sb.append(text.charAt(i));
            if ((i + 1) % consoleWidth == 0 && (i + 1) < text.length()) sb.append("\n");
        }
        return sb.toString();
    }

    public final AView show() {
        clearScreen();
        displayHeader();
        return showContent();
    }

    protected String prompt(String promptText) {
        return prompt(promptText, null);
    }

    protected String promptPassword() {
        return prompt("Password: ", '\0'); // Delegate with mask
    }

    private String prompt(String promptText, Character mask) {
        try {
            return lineReader.readLine(promptText, mask);
        } catch (Exception e) {
            return null;
        }
    }

    public abstract AView showContent();
}