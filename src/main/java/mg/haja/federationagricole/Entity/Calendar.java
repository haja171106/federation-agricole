package mg.haja.federationagricole.Entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Calendar {
    private String id;
    private Collectivity collectivity;
    private int year;
    private String generalAssemblyRule;
    private String trainingRule;

    public Calendar() {
    }

    public Calendar(String id, Collectivity collectivity, int year, String generalAssemblyRule, String trainingRule) {
        this.id = id;
        this.collectivity = collectivity;
        this.year = year;
        this.generalAssemblyRule = generalAssemblyRule;
        this.trainingRule = trainingRule;
    }
}