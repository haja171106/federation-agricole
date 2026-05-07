package mg.haja.federationagricole.DTO;

import mg.haja.federationagricole.Entity.enums.ActivityType;

import java.time.LocalDate;
import java.util.List;

public class CreateCollectivityActivity {

    public String label;
    public ActivityType activityType;

    public List<String> memberOccupationConcerned;

    public LocalDate executiveDate;
    public MonthlyRecurrenceRule recurrenceRule;

    public static class MonthlyRecurrenceRule {
        public int weekOrdinal;
        public String dayOfWeek;
    }
}