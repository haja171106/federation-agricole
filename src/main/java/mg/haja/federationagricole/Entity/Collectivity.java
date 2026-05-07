package mg.haja.federationagricole.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class Collectivity {

    private String id;
    private String number;
    private String name;
    private String agriculturalSpecialty;
    private String city;
    private LocalDate creationDate;
    private boolean openingAuthorization;
    private boolean identificationAssigned;
    private List<Member> members;

    public Collectivity(String id, String number, String name, String agriculturalSpecialty, String city, LocalDate creationDate, boolean openingAuthorization, boolean identificationAssigned, List<Member> members) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.agriculturalSpecialty = agriculturalSpecialty;
        this.city = city;
        this.creationDate = creationDate;
        this.openingAuthorization = openingAuthorization;
        this.identificationAssigned = identificationAssigned;
        this.members = members;
    }

    public Collectivity() {
    }
}