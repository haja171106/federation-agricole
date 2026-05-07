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
    public String phoneNumber;
    public String email;

    public String collectivityId;

    public List<SponsorInput> sponsors;
    public List<PaymentInput> payments;

    public static class SponsorInput {
        public String id;
    }

    public static class PaymentInput {
        public String membershipFeeId;
        public String accountCreditedId;
        public double amount;
        public String paymentMethod;
    }
}