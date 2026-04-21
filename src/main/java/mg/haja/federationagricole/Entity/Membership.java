package mg.haja.federationagricole.Entity;
public class Membership {
    private int id;
    private Member member;
    private Collectivity collectivity;

    public Membership() {
    }

    public Membership(int id, Member member, Collectivity collectivity) {
        this.id = id;
        this.member = member;
        this.collectivity = collectivity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Collectivity getCollectivity() {
        return collectivity;
    }

    public void setCollectivity(Collectivity collectivity) {
        this.collectivity = collectivity;
    }
}