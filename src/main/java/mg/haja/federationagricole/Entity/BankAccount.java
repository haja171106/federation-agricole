package mg.haja.federationagricole.Entity;

import lombok.Getter;
import lombok.Setter;
import mg.haja.federationagricole.Entity.enums.Bank;

@Getter
@Setter
public class BankAccount extends FinancialAccount {
    private String holderName;
    private Bank bankName;
    private String bankCode;
    private String bankBranchCode;
    private String bankAccountNumber;
    private String bankAccountKey;

}
