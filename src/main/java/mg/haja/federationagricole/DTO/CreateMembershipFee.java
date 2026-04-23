package mg.haja.federationagricole.DTO;

import mg.haja.federationagricole.Entity.enums.Frequency;
import java.time.LocalDate;

public class CreateMembershipFee {
    private LocalDate eligibleFrom;
    private Frequency frequency;
    private double amount;
    private String label;

    public LocalDate getEligibleFrom()              { return eligibleFrom; }
    public void setEligibleFrom(LocalDate v)        { this.eligibleFrom = v; }
    public Frequency getFrequency()                 { return frequency; }
    public void setFrequency(Frequency v)           { this.frequency = v; }
    public double getAmount()                       { return amount; }
    public void setAmount(double v)                 { this.amount = v; }
    public String getLabel()                        { return label; }
    public void setLabel(String v)                  { this.label = v; }
}
