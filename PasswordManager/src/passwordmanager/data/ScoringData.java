package passwordmanager.data;



import java.time.LocalDateTime;
import java.util.HashMap;


public class ScoringData {
    private Integer score;
    private HashMap<String, Integer> repeats;
    private HashMap<String, LocalDateTime> expiredPasswords;

    public ScoringData(CommonData completeData) {
        // TODO
    }
}
