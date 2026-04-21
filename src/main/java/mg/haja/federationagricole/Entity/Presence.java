package mg.haja.federationagricole.Entity;

public class Presence {
    private int id;
    private Activity activity;
    private Member member;
    private String status;
    private Boolean visitor;
    private String absenceReason;

    public Presence() {
    }

    public Presence(int id, Activity activity, Member member, String status, Boolean visitor, String absenceReason) {
        this.id = id;
        this.activity = activity;
        this.member = member;
        this.status = status;
        this.visitor = visitor;
        this.absenceReason = absenceReason;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getVisitor() {
        return visitor;
    }

    public void setVisitor(Boolean visitor) {
        this.visitor = visitor;
    }

    public String getAbsenceReason() {
        return absenceReason;
    }

    public void setAbsenceReason(String absenceReason) {
        this.absenceReason = absenceReason;
    }
}
