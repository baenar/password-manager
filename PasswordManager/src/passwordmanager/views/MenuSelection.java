package passwordmanager.views;

import lombok.Getter;
import org.jline.terminal.Terminal;

import java.io.IOException;

public class MenuSelection {
    private final String[] options;
    private int selectedIndex = 0;
    @Getter
    private int lastKey = 0;
    public MenuSelection(String[] options) {
        this.options = options;
    }

    public Integer render(Terminal terminal) throws IOException {
        terminal.writer().println();
        for (int i = 0; i < options.length; i++) {
            String text = (i == selectedIndex)
                    ? ">>>> [ " + options[i].toUpperCase() + " ] <<<<"
                    : "     " + options[i] + "     ";
            terminal.writer().println(AView.centerText(text));
        }
        terminal.writer().flush();

        lastKey =  terminal.reader().read();

        if (lastKey == 'w' || lastKey == 'W') {
            selectedIndex = (selectedIndex - 1 + options.length) % options.length;
        } else if (lastKey == 's' || lastKey == 'S') {
            selectedIndex = (selectedIndex + 1) % options.length;
        }
        if (lastKey == 13 || lastKey == 10)
            return selectedIndex;
        return null;

    }
}