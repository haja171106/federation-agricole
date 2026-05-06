package mg.haja.federationagricole.Entity;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Setter
@Getter
public class Account {
    private String id;
    private String ownerType;
    private Collectivity collectivity;
    private Federation federation;
    private String type;
    private double balance;
    private LocalDate balanceDate;
    private String currency;

    public Account() {
    }

    public Account(String id, String ownerType, Collectivity collectivity, Federation federation,
                   String type, double balance, LocalDate balanceDate, String currency) {
        this.id = id;
        this.ownerType = ownerType;
        this.collectivity = collectivity;
        this.federation = federation;
        this.type = type;
        this.balance = balance;
        this.balanceDate = balanceDate;
        this.currency = currency;
    }
}