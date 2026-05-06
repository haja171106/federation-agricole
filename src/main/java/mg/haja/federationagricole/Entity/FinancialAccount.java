package mg.haja.federationagricole.Entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = mg.haja.federationagricole.Entity.CashAccount.class,          name = "CASH"),
    @JsonSubTypes.Type(value = mg.haja.federationagricole.Entity.MobileBankingAccount.class, name = "MOBILE_BANKING"),
    @JsonSubTypes.Type(value = mg.haja.federationagricole.Entity.BankAccount.class,          name = "BANK")
})
@Setter
@Getter
public abstract class FinancialAccount {
    private String id;
    private double amount;

    public FinancialAccount(String id, double amount) {
        this.id = id;
        this.amount = amount;
    }

    public FinancialAccount() {

    }
}
