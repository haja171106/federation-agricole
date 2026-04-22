package mg.haja.federationagricole.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class Collectivity {

    private int id;
    private String number;
    private String name;
    private String agriculturalSpecialty;
    private String city;
    private LocalDate creationDate;
    private boolean openingAuthorization;


}