package mg.haja.federationagricole.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CollectivityOverallStatistics {

    private CollectivityInformation collectivityInformation;
    private int newMembersNumber;
    private double overallMemberCurrentDuePercentage;

    @Setter
    @Getter
    public static class CollectivityInformation {
        private String id;
        private String name;
        private String number;

    }
}