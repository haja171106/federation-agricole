package mg.haja.federationagricole.Entity;


public class Presence {
    private int id;
    private Activity activity;
    private Member member;
    private String status;
    private boolean visitor;
    private String absenceReason;

    public Presence() {
    }

    public Presence(int id, Activity activity, Member member, String status, boolean visitor, String absenceReason) {
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

    public Activity getActivity() {
        return activity;
    }

    public Member getMember() {
        return member;
    }

    public String getStatus() {
        return status;
    }

    public boolean isVisitor() {
        return visitor;
    }

    public String getAbsenceReason() {
        return absenceReason;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setVisitor(boolean visitor) {
        this.visitor = visitor;
    }

    public void setAbsenceReason(String absenceReason) {
        this.absenceReason = absenceReason;
    }
}
