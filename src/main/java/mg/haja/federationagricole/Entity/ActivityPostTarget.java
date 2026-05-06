package mg.haja.federationagricole.Entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ActivityPostTarget {
    private String id;
    private Activity activity;
    private String position;

    public ActivityPostTarget() {
    }

    public ActivityPostTarget(Activity activity, String id, String position) {
        this.activity = activity;
        this.id = id;
        this.position = position;
    }
}