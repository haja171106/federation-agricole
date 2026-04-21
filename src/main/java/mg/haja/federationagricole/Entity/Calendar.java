package mg.haja.federationagricole.Entity;

import java.time.LocalDate;

public class Calendar {
    private int id;
    private Collectivity collectivity;
    private int year;
    private String generalAssemblyRule;
    private String youthTrainingRule;

    public Calendar() {
    }

    public Calendar(int id, Collectivity collectivity, int year, String generalAssemblyRule, String youthTrainingRule) {
        this.id = id;
        this.collectivity = collectivity;
        this.year = year;
        this.generalAssemblyRule = generalAssemblyRule;
        this.youthTrainingRule = youthTrainingRule;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Collectivity getCollectivity() {
        return collectivity;
    }

    public void setCollectivity(Collectivity collectivity) {
        this.collectivity = collectivity;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getGeneralAssemblyRule() {
        return generalAssemblyRule;
    }

    public void setGeneralAssemblyRule(String generalAssemblyRule) {
        this.generalAssemblyRule = generalAssemblyRule;
    }

    public String getYouthTrainingRule() {
        return youthTrainingRule;
    }

    public void setYouthTrainingRule(String youthTrainingRule) {
        this.youthTrainingRule = youthTrainingRule;
    }
}