package passwordmanager.views;

import org.jline.terminal.Attributes;
import passwordmanager.data.BrowseTypes;
import passwordmanager.data.CommonData;
import passwordmanager.utils.FunkyNumbers;

import java.io.IOException;
import java.util.List;

public class SecurityCenterView extends AView {
    private final List<CommonData> allData;

    private final List<CommonData> repeating;
    private final List<CommonData> similar;
    private final List<CommonData> weak;
    private final List<CommonData> outdated;

    private final MenuSelection menu;

    public SecurityCenterView() {
        super(null);
        this.allData = reader.readCommonData();

        this.repeating = BrowseTypes.REPEATING.applyFilter(allData);
        this.similar = BrowseTypes.SIMILAR.applyFilter(allData);
        this.weak = BrowseTypes.WEAK.applyFilter(allData);
        this.outdated = BrowseTypes.OUTDATED.applyFilter(allData);

        String[] options = {
                "Repeating passwords: " + repeating.size(),
                "Similar passwords:   " + similar.size(),
                "Weak passwords:      " + weak.size(),
                "Outdated passwords:  " + outdated.size()
        };
        this.menu = new MenuSelection(options);
    }

    @Override
    public AView showContent() {
        Attributes originalAttributes = terminal.enterRawMode();

        try {
            while (true) {
                clearScreen();
                displayHeader();

                int score = calculateScore();

                terminal.writer().println(centerText("=== SECURITY CENTER ==="));
                terminal.writer().println(centerText("(W/S - Move, ENTER - Details, ESC - Back)"));
                terminal.writer().println();
                terminal.writer().println(centerText("OVERALL SAFETY GRADE"));

                List<String> funkyLines = FunkyNumbers.getFunkyScore(score);
                for (String line : funkyLines) {
                    terminal.writer().println(centerText(line));
                }

                Integer chosenOption = menu.render(terminal);

                if (menu.getLastKey() == 27) {
                    return new StartView(null);
                }

                if (chosenOption != null) {
                    return handleSelection(chosenOption);
                }
            }
        } catch (IOException e) {
            return new StartView("Error in Security Center.");
        } finally {
            terminal.setAttributes(originalAttributes);
        }
    }

    private int calculateScore() {
        if (allData.isEmpty()) return 100;

        double score = 100.0;
        score -= (repeating.size() * 10.0);
        score -= (weak.size() * 10.0);
        score -= (similar.size() * 8.0);
        score -= (outdated.size() * 2.0);

        return (int) Math.max(0, score);
    }

    private AView handleSelection(int choice) {
        return switch (choice) {
            case 0 -> new BrowsePasswordsView(BrowseTypes.REPEATING);
            case 1 -> new BrowsePasswordsView(BrowseTypes.SIMILAR);
            case 2 -> new BrowsePasswordsView(BrowseTypes.WEAK);
            case 3 -> new BrowsePasswordsView(BrowseTypes.OUTDATED);
            default -> this;
        };
    }
}