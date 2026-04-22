package mg.haja.federationagricole.model;

import mg.haja.federationagricole.Entity.enums.PaymentMode;

public class CreateMemberPayment {
    private double amount;
    private String membershipFeeIdentifier;
    private String accountCreditedIdentifier;
    private PaymentMode paymentMode;

    public double getAmount()                               { return amount; }
    public void setAmount(double v)                         { this.amount = v; }
    public String getMembershipFeeIdentifier()              { return membershipFeeIdentifier; }
    public void setMembershipFeeIdentifier(String v)        { this.membershipFeeIdentifier = v; }
    public String getAccountCreditedIdentifier()            { return accountCreditedIdentifier; }
    public void setAccountCreditedIdentifier(String v)      { this.accountCreditedIdentifier = v; }
    public PaymentMode getPaymentMode()                     { return paymentMode; }
    public void setPaymentMode(PaymentMode v)               { this.paymentMode = v; }
}
