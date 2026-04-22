package mg.haja.federationagricole.Entity;


public class PostMandateFederation {
    private int id;
    private MandateFederation mandateFederation;
    private Member member;
    private String position;

    public PostMandateFederation() {
    }

    public PostMandateFederation(int id, MandateFederation mandateFederation, Member member, String position) {
        this.id = id;
        this.mandateFederation = mandateFederation;
        this.member = member;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public MandateFederation getMandateFederation() {
        return mandateFederation;
    }

    public Member getMember() {
        return member;
    }

    public String getPosition() {
        return position;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMandateFederation(MandateFederation mandateFederation) {
        this.mandateFederation = mandateFederation;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}