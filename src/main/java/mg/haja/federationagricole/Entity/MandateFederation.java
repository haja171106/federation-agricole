package mg.haja.federationagricole.Entity;


import java.time.LocalDate;

public class MandateFederation {
    private int id;
    private Federation federation;
    private LocalDate startDate;
    private LocalDate endDate;

    public MandateFederation() {
    }

    public MandateFederation(int id, Federation federation, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.federation = federation;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public Federation getFederation() {
        return federation;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFederation(Federation federation) {
        this.federation = federation;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}