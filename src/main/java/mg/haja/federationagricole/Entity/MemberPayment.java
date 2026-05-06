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

    public MemberPayment(String id, double amount, PaymentMode paymentMode, FinancialAccount accountCredited, LocalDate creationDate) {
        this.id = id;
        this.amount = amount;
        this.paymentMode = paymentMode;
        this.accountCredited = accountCredited;
        this.creationDate = creationDate;
    }

    public MemberPayment() {

    }
}
