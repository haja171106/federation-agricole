package mg.haja.federationagricole.Entity;


public class MobileMoneyAccount {
    private int id;
    private Account account;
    private String holderName;
    private String service;
    private String phone;

    public MobileMoneyAccount() {
    }

    public MobileMoneyAccount(int id, Account account, String holderName, String service, String phone) {
        this.id = id;
        this.account = account;
        this.holderName = holderName;
        this.service = service;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public Account getAccount() {
        return account;
    }

    public String getHolderName() {
        return holderName;
    }

    public String getService() {
        return service;
    }

    public String getPhone() {
        return phone;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}