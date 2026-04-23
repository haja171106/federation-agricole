package mg.haja.federationagricole.Entity;

import mg.haja.federationagricole.Entity.enums.PaymentMode;
import java.time.LocalDate;

public class CollectivityTransaction {
    private String id;
    private LocalDate creationDate;
    private double amount;
    private PaymentMode paymentMode;
    private String memberDebitedId;

    public String getId()                               { return id; }
    public void setId(String v)                         { this.id = v; }
    public LocalDate getCreationDate()                  { return creationDate; }
    public void setCreationDate(LocalDate v)            { this.creationDate = v; }
    public double getAmount()                           { return amount; }
    public void setAmount(double v)                     { this.amount = v; }
    public PaymentMode getPaymentMode()                 { return paymentMode; }
    public void setPaymentMode(PaymentMode v)           { this.paymentMode = v; }
    public String getMemberDebitedId()                  { return memberDebitedId; }
    public void setMemberDebitedId(String v)            { this.memberDebitedId = v; }

    private FinancialAccount accountCredited;
    public FinancialAccount getAccountCredited()        { return accountCredited; }
    public void setAccountCredited(FinancialAccount v)  { this.accountCredited = v; }
}
