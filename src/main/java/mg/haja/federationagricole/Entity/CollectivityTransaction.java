package mg.haja.federationagricole.Entity;

import lombok.Getter;
import lombok.Setter;
import mg.haja.federationagricole.Entity.enums.PaymentMode;
import java.time.LocalDate;

@Setter
@Getter
public class CollectivityTransaction {
    private String id;
    private LocalDate creationDate;
    private double amount;
    private PaymentMode paymentMode;
    private String memberDebitedId;


    public void setAccountCredited(FinancialAccount byId) {
    }
}
