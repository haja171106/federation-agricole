package mg.haja.federationagricole.Entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MobileMoneyAccount {
    private String id;
    private Account account;
    private String holderName;
    private String service;
    private String phone;

    public MobileMoneyAccount() {
    }

    public MobileMoneyAccount(String id, Account account, String holderName, String service, String phone) {
        this.id = id;
        this.account = account;
        this.holderName = holderName;
        this.service = service;
        this.phone = phone;
    }
}