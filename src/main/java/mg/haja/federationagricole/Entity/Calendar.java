package mg.haja.federationagricole.Entity;


public class Calendar {
    private int id;
    private Collectivity collectivity;
    private int year;
    private String generalAssemblyRule;
    private String trainingRule;

    public Calendar() {
    }

    public Calendar(int id, Collectivity collectivity, int year, String generalAssemblyRule, String trainingRule) {
        this.id = id;
        this.collectivity = collectivity;
        this.year = year;
        this.generalAssemblyRule = generalAssemblyRule;
        this.trainingRule = trainingRule;
    }

    public int getId() {
        return id;
    }

    public Collectivity getCollectivity() {
        return collectivity;
    }

    public int getYear() {
        return year;
    }

    public String getGeneralAssemblyRule() {
        return generalAssemblyRule;
    }

    public String getTrainingRule() {
        return trainingRule;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCollectivity(Collectivity collectivity) {
        this.collectivity = collectivity;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setGeneralAssemblyRule(String generalAssemblyRule) {
        this.generalAssemblyRule = generalAssemblyRule;
    }

    public void setTrainingRule(String trainingRule) {
        this.trainingRule = trainingRule;
    }
}