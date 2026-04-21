package mg.haja.federationagricole.Entity;


public class BankAccount {
    private int id;
    private Account account;
    private String holderName;
    private String bankName;
    private String accountNumber;

    public BankAccount() {
    }

    public BankAccount(int id, Account account, String holderName, String bankName, String accountNumber) {
        this.id = id;
        this.account = account;
        this.holderName = holderName;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
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

    public String getBankName() {
        return bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
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

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}