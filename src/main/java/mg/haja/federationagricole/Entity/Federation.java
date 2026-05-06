package mg.haja.federationagricole.Entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Federation {
    private String id;
    private String name;
    private double reverseContributionRate;

    public Federation() {
    }

    public Federation(String id, String name, double reverseContributionRate) {
        this.id = id;
        this.name = name;
        this.reverseContributionRate = reverseContributionRate;
    }
}