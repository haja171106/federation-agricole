package mg.haja.federationagricole.Entity;


import java.time.LocalDateTime;

public class Activity {
    private int id;
    private Collectivity collectivity;
    private String type;
    private String title;
    private String description;
    private LocalDateTime date;
    private boolean mandatory;

    public Activity() {
    }

    public Activity(int id, Collectivity collectivity, String type, String title, String description,
                    LocalDateTime date, boolean mandatory) {
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

    public Collectivity getCollectivity() {
        return collectivity;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCollectivity(Collectivity collectivity) {
        this.collectivity = collectivity;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
}