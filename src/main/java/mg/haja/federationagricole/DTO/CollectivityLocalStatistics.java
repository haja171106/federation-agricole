package mg.haja.federationagricole.DTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CollectivityLocalStatistics {

    private MemberDescription memberDescription;
    private double earnedAmount;
    private double unpaidAmount;

    @Setter
    @Getter
    public static class MemberDescription {
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private String occupation;

    }
}