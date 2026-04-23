package mg.haja.federationagricole.Entity;

import mg.haja.federationagricole.Entity.enums.ActivityStatus;
import mg.haja.federationagricole.DTO.CreateMembershipFee;

public class MembershipFee extends CreateMembershipFee {
    private int id;
    private ActivityStatus status;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public ActivityStatus getStatus() { return status; }
    public void setStatus(ActivityStatus status) { this.status = status; }
}
