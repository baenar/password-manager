package passwordmanager.data;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum BrowseTypes {
    ALL("ALL PASSWORDS", data -> data),

    WEAK("WEAK PASSWORDS", data -> data.stream()
            .filter(d -> Validator.shannonEntropy(d.getPassword()) < 3.3)
            .collect(Collectors.toList())),

    OUTDATED("OUTDATED PASSWORDS", data -> {
        LocalDateTime threshold = LocalDateTime.now().minusMonths(SettingsData.passwordExpirationInMonths);
        return data.stream()
                .filter(d -> d.getLastUpdatedOn().isBefore(threshold))
                .collect(Collectors.toList());
    }),

    REPEATING("REPEATING PASSWORDS", data -> {
        Map<String, Long> counts = data.stream()
                .collect(Collectors.groupingBy(CommonData::getPassword, Collectors.counting()));
        return data.stream()
                .filter(d -> counts.get(d.getPassword()) > 1)
                .collect(Collectors.toList());
    }),

    SIMILAR("SIMILAR PASSWORDS", data -> {
        List<CommonData> result = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.size(); j++) {
                if (i == j) continue;
                if (isSimilar(data.get(i).getPassword(), data.get(j).getPassword())) {
                    result.add(data.get(i));
                    break;
                }
            }
        }
        return result;
    });

    @Getter
    private final String label;
    private final Function<List<CommonData>, List<CommonData>> filterLogic;

    BrowseTypes(String label, Function<List<CommonData>, List<CommonData>> filterLogic) {
        this.label = label;
        this.filterLogic = filterLogic;
    }

    public List<CommonData> applyFilter(List<CommonData> data) {
        return filterLogic.apply(data);
    }

    private static boolean isSimilar(String p1, String p2) {
        if (p1.equals(p2)) return false;
        int distance = calculateLevenshtein(p1, p2);
        return distance > 0 && distance <= 2;
    }

    private static int calculateLevenshtein(String a, String b) {
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
}