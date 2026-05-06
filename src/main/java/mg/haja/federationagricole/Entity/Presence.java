package mg.haja.federationagricole.Entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Presence {
    private String id;
    private Activity activity;
    private Member member;
    private String status;
    private boolean visitor;
    private String absenceReason;

    public Presence() {
    }

    public Presence(String id, Activity activity, Member member, String status, boolean visitor, String absenceReason) {
        this.id = id;
        this.activity = activity;
        this.member = member;
        this.status = status;
        this.visitor = visitor;
        this.absenceReason = absenceReason;
    }
}
