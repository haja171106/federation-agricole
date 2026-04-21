package mg.haja.federationagricole.Entity;

public class Federation {
    private int id;
    private String name;
    private double reverseContributionRate;

    public Federation() {
    }

    public Federation(int id, String name, double reverseContributionRate) {
        this.id = id;
        this.name = name;
        this.reverseContributionRate = reverseContributionRate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getReverseContributionRate() {
        return reverseContributionRate;
    }

    public void setReverseContributionRate(double reverseContributionRate) {
        this.reverseContributionRate = reverseContributionRate;
    }
}
