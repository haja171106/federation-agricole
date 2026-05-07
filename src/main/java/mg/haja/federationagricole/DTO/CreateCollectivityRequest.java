package mg.haja.federationagricole.DTO;

import java.time.LocalDate;
import java.util.List;

public class CreateCollectivityRequest {

    public String name;
    public String number;
    public String city;
    public String agriculturalSpecialty;
    public LocalDate creationDate;

    public boolean federationApproval;

    public List<MemberInput> members;

    public static class MemberInput {
        public int memberId;
        public LocalDate membershipDate;
        public String role;
    }
}
