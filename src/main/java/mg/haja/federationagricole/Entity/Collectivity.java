package mg.haja.federationagricole.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Collectivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String number;
    private String name;
    private String agriculturalSpecialty;
    private String city;
    private LocalDate creationDate;
    private Boolean openingAuthorization;

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

    public Boolean getOpeningAuthorization() {
        return openingAuthorization;
    }

    public void setOpeningAuthorization(Boolean openingAuthorization) {
        this.openingAuthorization = openingAuthorization;
    }
}