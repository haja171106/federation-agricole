package mg.haja.federationagricole.DTO;

import mg.haja.federationagricole.Entity.FinancialAccount;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountWithBalance {
    private FinancialAccount account;
    private double balanceAtDate;
}
