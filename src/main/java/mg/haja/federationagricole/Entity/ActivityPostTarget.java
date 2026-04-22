package mg.haja.federationagricole.Entity;


public class ActivityPostTarget {
    private int id;
    private Activity activity;
    private String position;

    public ActivityPostTarget() {
    }

    public ActivityPostTarget(int id, Activity activity, String position) {
        this.id = id;
        this.activity = activity;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public Activity getActivity() {
        return activity;
    }

    public String getPosition() {
        return position;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}