package mg.haja.federationagricole.DTO;

import java.time.LocalDate;
import java.util.List;

public class CreateCollectivityActivity {

    public String label;
    public String activityType;

    public List<String> memberOccupationConcerned;

    public LocalDate executiveDate;
    public MonthlyRecurrenceRule recurrenceRule;

    public static class MonthlyRecurrenceRule {
        public int weekOrdinal;
        public String dayOfWeek;
    }
}