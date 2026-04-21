package mg.haja.federationagricole.Entity;
public class MandatePosition {
    private int id;
    private Mandate mandate;
    private Member member;
    private String position;

    public MandatePosition() {
    }

    public MandatePosition(int id, Mandate mandate, Member member, String position) {
        this.id = id;
        this.mandate = mandate;
        this.member = member;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Mandate getMandate() {
        return mandate;
    }

    public void setMandate(Mandate mandate) {
        this.mandate = mandate;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}