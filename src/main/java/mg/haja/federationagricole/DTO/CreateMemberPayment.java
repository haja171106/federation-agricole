package mg.haja.federationagricole.DTO;

import lombok.Getter;
import lombok.Setter;
import mg.haja.federationagricole.Entity.enums.PaymentMode;

@Setter
@Getter
public class CreateMemberPayment {
    private double amount;
    private String membershipFeeIdentifier;
    private String accountCreditedIdentifier;
    private PaymentMode paymentMode;

}
