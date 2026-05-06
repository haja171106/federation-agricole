package mg.haja.federationagricole.Entity;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Setter
@Getter
public class MandateFederation {
    private String id;
    private Federation federation;
    private LocalDate startDate;
    private LocalDate endDate;

    public MandateFederation() {
    }

    public MandateFederation(String id, Federation federation, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.federation = federation;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}