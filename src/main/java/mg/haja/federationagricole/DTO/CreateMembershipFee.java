package mg.haja.federationagricole.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mg.haja.federationagricole.Entity.enums.Frequency;

import java.time.LocalDate;

@Getter
@Setter
public class CreateMembershipFee {

    private String label;
    private double amount;

    @JsonProperty("type")
    private Frequency frequency;

    private LocalDate eligibleFrom;

    @JsonProperty("type")
    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public void setCollectivityId(String collectivityId) {
    }
}