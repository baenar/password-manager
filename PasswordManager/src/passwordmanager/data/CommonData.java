package passwordmanager.data;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CommonData implements Serializable {
    private String username;
    private String service;
    private String password;
    private LocalDateTime lastUpdatedOn;
    private String notes;

    public CommonData(String username, String service, String password, String notes) {
        this.username = username;
        this.service = service;
        this.password = password;
        this.lastUpdatedOn = LocalDateTime.now();
        this.notes = notes;
    }
}

