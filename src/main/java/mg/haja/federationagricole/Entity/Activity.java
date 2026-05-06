package mg.haja.federationagricole.Entity;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class Activity {
    private String id;
    private Collectivity collectivity;
    private String type;
    private String title;
    private String description;
    private LocalDateTime date;
    private boolean mandatory;

    public Activity() {
    }

    public Activity(String id, Collectivity collectivity, String type, String title, String description, LocalDateTime date, boolean mandatory) {
        this.id = id;
        this.collectivity = collectivity;
        this.type = type;
        this.title = title;
        this.description = description;
        this.date = date;
        this.mandatory = mandatory;
    }

}