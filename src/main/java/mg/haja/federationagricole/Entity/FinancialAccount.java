package mg.haja.federationagricole.Entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = mg.haja.federationagricole.Entity.CashAccount.class,          name = "CASH"),
    @JsonSubTypes.Type(value = mg.haja.federationagricole.Entity.MobileBankingAccount.class, name = "MOBILE_BANKING"),
    @JsonSubTypes.Type(value = mg.haja.federationagricole.Entity.BankAccount.class,          name = "BANK")
})
public abstract class FinancialAccount {
    private String id;
    private double amount;

    public String getId()              { return id; }
    public void setId(String id)       { this.id = id; }
    public double getAmount()          { return amount; }
    public void setAmount(double amount){ this.amount = amount; }
}
