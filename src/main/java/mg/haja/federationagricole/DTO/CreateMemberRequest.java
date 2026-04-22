package mg.haja.federationagricole.DTO;

import java.time.LocalDate;
import java.util.List;

public class CreateMemberRequest {

    public String firstName;
    public String lastName;
    public LocalDate birthDate;
    public String gender;

    public String address;
    public String profession;
    public String phone;
    public String email;

    public int collectivityId;

    public List<SponsorInput> sponsors;

    public int membershipFee;
    public int annualContribution;

    public static class SponsorInput {
        public int memberId;
        public int collectivityId;
        public String relationship;
        public int seniorityDays;
    }
}
