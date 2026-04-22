package mg.haja.federationagricole.Entity;

public class Membership {
    private int id;
    private Member member;
    private Collectivity collectivity;
    private Member sponsor;
    private boolean active;

    public Membership() {
    }

    public Membership(int id, Member member, Collectivity collectivity, Member sponsor, boolean active) {
        this.id = id;
        this.member = member;
        this.collectivity = collectivity;
        this.sponsor = sponsor;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Collectivity getCollectivity() {
        return collectivity;
    }

    public Member getSponsor() {
        return sponsor;
    }

    public boolean isActive() {
        return active;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setCollectivity(Collectivity collectivity) {
        this.collectivity = collectivity;
    }

    public void setSponsor(Member sponsor) {
        this.sponsor = sponsor;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}