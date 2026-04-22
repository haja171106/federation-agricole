package mg.haja.federationagricole.Entity;

import lombok.Getter;
import lombok.Setter;
import mg.haja.federationagricole.Entity.enums.PaymentMode;
import java.time.LocalDate;

@Getter
@Setter
public class MemberPayment {
    private String id;
    private double amount;
    private PaymentMode paymentMode;
    private FinancialAccount accountCredited;
    private LocalDate creationDate;
}
