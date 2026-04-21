package mg.haja.federationagricole.Entity;


import java.time.LocalDate;

public class Contribution {
    private int id;
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

    public Contribution(int id, Collectivity collectivity, Member member, double amount,
                        LocalDate collectionDate, String paymentMode, String type,
                        String reason, double federationAmount) {
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

    public int getId() {
        return id;
    }

    public Collectivity getCollectivity() {
        return collectivity;
    }

    public Member getMember() {
        return member;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getCollectionDate() {
        return collectionDate;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public String getType() {
        return type;
    }

    public String getReason() {
        return reason;
    }

    public double getFederationAmount() {
        return federationAmount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCollectivity(Collectivity collectivity) {
        this.collectivity = collectivity;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCollectionDate(LocalDate collectionDate) {
        this.collectionDate = collectionDate;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setFederationAmount(double federationAmount) {
        this.federationAmount = federationAmount;
    }
}