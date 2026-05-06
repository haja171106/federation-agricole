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

    public String collectivityId;

    public List<SponsorInput> sponsors;

    public int membershipFee; // 50000
    public int annualContribution;

    public static class SponsorInput {
        public String memberId;
        public String collectivityId;
        public String relationship; // friend, family...
        public int seniorityDays;
    }
}
