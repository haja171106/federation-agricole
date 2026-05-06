package mg.haja.federationagricole.Entity;

import lombok.Getter;
import lombok.Setter;
import mg.haja.federationagricole.Entity.enums.ActivityStatus;
import mg.haja.federationagricole.DTO.CreateMembershipFee;
@Setter
@Getter
public class MembershipFee extends CreateMembershipFee {
    private String id;
    private ActivityStatus status;

    public MembershipFee(String id, ActivityStatus status) {
        super();
        this.id = id;
        this.status = status;
    }

    public MembershipFee() {
        super();
    }


}
