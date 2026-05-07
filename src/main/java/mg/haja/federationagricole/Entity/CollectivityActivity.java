package mg.haja.federationagricole.Entity;

import lombok.Getter;
import lombok.Setter;
import mg.haja.federationagricole.Entity.enums.ActivityType;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class CollectivityActivity {

    private String id;
    private String label;
    private ActivityType activityType;
    private List<String> memberOccupationConcerned;

    private LocalDate executiveDate;
    private MonthlyRecurrenceRule recurrenceRule;

    @Getter
    @Setter
    public static class MonthlyRecurrenceRule {
        private int weekOrdinal;
        private String dayOfWeek;
    }
}