package mg.haja.federationagricole.Entity;

import lombok.Getter;
import lombok.Setter;
import mg.haja.federationagricole.Entity.enums.AttendanceStatus;

@Getter
@Setter
public class ActivityMemberAttendance {

    private String id;
    private MemberDescription memberDescription;
    private AttendanceStatus attendanceStatus;

    @Getter
    @Setter
    public static class MemberDescription {
        private String id;
        private String firstName;
        private String lastName;
        private String email;
        private String occupation;
    }
}