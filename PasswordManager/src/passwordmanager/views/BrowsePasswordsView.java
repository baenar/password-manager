package passwordmanager.views;

import org.jline.terminal.Attributes;
import passwordmanager.data.BrowseTypes;
import passwordmanager.data.CommonData;

import java.util.List;
import java.util.stream.Collectors;

public class BrowsePasswordsView extends AView {
    private final BrowseTypes browseType;

    public BrowsePasswordsView() {
        this(BrowseTypes.ALL);
    }

    public BrowsePasswordsView(BrowseTypes type) {
        super(null);
        this.browseType = type;
    }

    @Override
    public AView showContent() {
        Attributes originalAttributes = terminal.enterRawMode();
        StringBuilder query = new StringBuilder();

        // 1. POBIERZ DANE I ZASTOSUJ FILTR Z ENUMA
        List<CommonData> sourceData = reader.readCommonData();
        List<CommonData> preFilteredData = browseType.applyFilter(sourceData);

        try {
            while (true) {
                // 2. FILTROWANIE
                String currentFilter = query.toString().toLowerCase();
                List<CommonData> filtered = preFilteredData.stream()
                        .filter(d -> d.getService().toLowerCase().contains(currentFilter) ||
                                d.getUsername().toLowerCase().contains(currentFilter))
                        .limit(8)
                        .collect(Collectors.toList());

                // 3. RENDEROWANIE INTERFEJSU
                clearScreen();
                displayHeader();
                terminal.writer().println(centerText("=== " + browseType.getLabel() + " ==="));
                terminal.writer().println(centerText("(Type to search, ENTER to select, ESC to exit)"));
                terminal.writer().println();

                terminal.writer().println("   Search: " + query + "_");
                terminal.writer().println("   " + "-".repeat(50));

                if (filtered.isEmpty()) {
                    terminal.writer().println(centerText("   No results found...   "));
                } else {
                    for (CommonData d : filtered) {
                        terminal.writer().println(formatRow(d, false));
                    }
                }
                terminal.writer().flush();

                // 4. OBSŁUGA KLAWIATURY
                int code = terminal.reader().read();

                if (code == 27) { // ESC
                    return (browseType == BrowseTypes.ALL) ? new StartView(null) : new SecurityCenterView();
                } else if (code == 13 || code == 10) { // ENTER
                    if (!filtered.isEmpty()) {
                        CommonData chosen = enterSelectionMode(filtered);
                        if (chosen != null) return new PasswordDetailsView(chosen, browseType);
                    }
                } else if (code == 127 || code == 8) { // BACKSPACE
                    if (query.length() > 0) {
                        query.deleteCharAt(query.length() - 1);
                    }
                } else if (code >= 32 && code <= 126) { // ZNAKI ALFANUMERYCZNE
                    query.append((char) code);
                }
            }
        } catch (Exception e) {
            return new StartView("Error: " + e.getMessage());
        } finally {
            terminal.setAttributes(originalAttributes);
        }
    }

    private CommonData enterSelectionMode(List<CommonData> options) throws Exception {
        int selectedIndex = 0;
        while (true) {
            clearScreen();
            displayHeader();
            terminal.writer().println(centerText("=== SELECT AN ENTRY ==="));
            terminal.writer().println(centerText("(W/S to navigate, ENTER to confirm, ESC to back to search)"));
            terminal.writer().println();

            for (int i = 0; i < options.size(); i++) {
                terminal.writer().println(formatRow(options.get(i), i == selectedIndex));
            }
            terminal.writer().flush();

            int code = terminal.reader().read();
            if (code == 'w' || code == 'W') {
                selectedIndex = (selectedIndex - 1 + options.size()) % options.size();
            } else if (code == 's' || code == 'S') {
                selectedIndex = (selectedIndex + 1) % options.size();
            } else if (code == 13 || code == 10) {
                return options.get(selectedIndex);
            } else if (code == 27) {
                return null;
            }
        }
    }

    private String formatRow(CommonData d, boolean selected) {
        String service = String.format("%-20s", truncate(d.getService(), 19));
        String user = truncate(d.getUsername(), 25);
        String row = String.format("   %-20s | %s", service, user);

        if (selected) {
            return centerText(">>>> [ " + row.toUpperCase() + " ] <<<<");
        }
        return "        " + row;
    }

    private String truncate(String text, int max) {
        if (text == null) return "";
        return text.length() <= max ? text : text.substring(0, max - 2) + "..";
    }
}