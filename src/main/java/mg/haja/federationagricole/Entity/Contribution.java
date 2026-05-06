package mg.haja.federationagricole.Entity;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Setter
@Getter
public class Contribution {
    private String id;
    private Collectivity collectivity;
    private Member member;
    private double amount;
    private LocalDate collectionDate;
    private String paymentMode;
    private String type;
    private String reason;
    private double federationAmount;

    public Contribution() {
    }

    public Contribution(String id, Collectivity collectivity, Member member, double amount, LocalDate collectionDate, String paymentMode, String type, String reason, double federationAmount) {
        this.id = id;
        this.collectivity = collectivity;
        this.member = member;
        this.amount = amount;
        this.collectionDate = collectionDate;
        this.paymentMode = paymentMode;
        this.type = type;
        this.reason = reason;
        this.federationAmount = federationAmount;
    }
}