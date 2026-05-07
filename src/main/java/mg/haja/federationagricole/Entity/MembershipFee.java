package mg.haja.federationagricole.Entity;

import lombok.Getter;
import lombok.Setter;
import mg.haja.federationagricole.Entity.enums.ActivityStatus;
import mg.haja.federationagricole.Entity.enums.Frequency;

import java.time.LocalDate;

@Setter
@Getter
public class MembershipFee{
    private String id;
    private String collectivityId;
    private String label;
    private double amount;
    private Frequency frequency;
    private LocalDate eligibleFrom;
    private ActivityStatus status;


}
