package mg.haja.federationagricole.Entity;
import java.time.LocalDate;

public class Collectivity {

    private int id;
    private String number;
    private String name;
    private String agriculturalSpecialty;
    private String city;
    private LocalDate creationDate;
    private boolean openingAuthorization;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAgriculturalSpecialty() {
        return agriculturalSpecialty;
    }

    public void setAgriculturalSpecialty(String agriculturalSpecialty) {
        this.agriculturalSpecialty = agriculturalSpecialty;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isOpeningAuthorization() {
        return openingAuthorization;
    }

    public void setOpeningAuthorization(boolean openingAuthorization) {
        this.openingAuthorization = openingAuthorization;
    }
}