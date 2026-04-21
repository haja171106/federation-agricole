package mg.haja.federationagricole.Entity;

import java.time.LocalDateTime;

public class Activity {
    private int id;
    private Collectivity collectivity;
    private String type;
    private String title;
    private String description;
    private LocalDateTime date;
    private Boolean mandatory;

    public Activity() {
    }

    public Activity(int id, Collectivity collectivity, String type, String title, String description,
                    LocalDateTime date, Boolean mandatory) {
        this.id = id;
        this.collectivity = collectivity;
        this.type = type;
        this.title = title;
        this.description = description;
        this.date = date;
        this.mandatory = mandatory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Collectivity getCollectivity() {
        return collectivity;
    }

    public void setCollectivity(Collectivity collectivity) {
        this.collectivity = collectivity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }
}
