package mg.haja.federationagricole.Entity;

import java.time.LocalDate;

public class Account {
    private int id;
    private String ownerType;
    private Collectivity collectivity;
    private Federation federation;
    private String type;
    private double balance;
    private LocalDate balanceDate;
    private String currency;

    public Account() {
    }

    public Account(int id, String ownerType, Collectivity collectivity, Federation federation,
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }

    public Collectivity getCollectivity() {
        return collectivity;
    }

    public void setCollectivity(Collectivity collectivity) {
        this.collectivity = collectivity;
    }

    public Federation getFederation() {
        return federation;
    }

    public void setFederation(Federation federation) {
        this.federation = federation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public LocalDate getBalanceDate() {
        return balanceDate;
    }

    public void setBalanceDate(LocalDate balanceDate) {
        this.balanceDate = balanceDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
