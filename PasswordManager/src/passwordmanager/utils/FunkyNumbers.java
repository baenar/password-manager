package passwordmanager.utils;

import java.util.ArrayList;
import java.util.List;

public class FunkyNumbers {
    private static final String[][] DIGITS = {
            // 0
            {" _____ ", "|  _  |", "| |/' |", "|  /| |", "\\ |_/ /", " \\___/ ", "       "},
            // 1
            {"  __   ", " /  |  ", " `| |  ", "  | |  ", " _| |_ ", " \\___/ ", "       "},
            // 2
            {" _____ ", "/ __  \\", "`' / /'", "  / /  ", "./ /___", "\\_____/", "       "},
            // 3
            {" _____ ", "|____ |", "    / /", "    \\ \\", ".___/ /", "\\____/", "       "},
            // 4
            {"   ___ ", "  /   |", " / /| |", "/ /_| |", "\\___  |", "    |_/", "       "},
            // 5
            {" _____ ", "|  ___|", "|___ \\ ", "    \\ \\", "/\\__/ /", "\\____/ ", "       "},
            // 6
            {"  ____ ", " / ___|", "/ /___ ", "| ___ \\", "| \\_/ |", "\\_____/", "       "},
            // 7
            {" ______", "|___  /", "   / / ", "  / /  ", "./ /   ", "\\_/    ", "       "},
            // 8
            {" _____ ", "|  _  |", " \\ V / ", " / _ \\ ", "| |_| |", "\\_____/", "       "},
            // 9
            {" _____ ", "|  _  |", "| |_| |", "\\____ |", ".___/ /", "\\____/", "       "}
    };

    private static final String[] SLASH = {
            "     __",
            "    / /",
            "   / / ",
            "  / /  ",
            " / /   ",
            "/_/    ",
            "       "
    };

    public static List<String> getFunkyScore(int score) {
        String scoreStr = score + "/100";
        List<String> lines = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            StringBuilder sb = new StringBuilder();
            for (char c : scoreStr.toCharArray()) {
                if (c == '/') {
                    sb.append(SLASH[i]);
                } else {
                    int digit = Character.getNumericValue(c);
                    sb.append(DIGITS[digit][i]);
                }
            }
            lines.add(sb.toString());
        }
        return lines;
    }
}