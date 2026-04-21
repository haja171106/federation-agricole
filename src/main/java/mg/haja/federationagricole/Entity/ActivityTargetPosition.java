package mg.haja.federationagricole.Entity;

public class ActivityTargetPosition {
    private int id;
    private Activity activity;
    private String position;

    public ActivityTargetPosition() {
    }

    public ActivityTargetPosition(int id, Activity activity, String position) {
        this.id = id;
        this.activity = activity;
        this.position = position;
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

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
