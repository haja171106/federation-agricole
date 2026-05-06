package mg.haja.federationagricole.Entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Membership {
    private String id;
    private Member member;
    private Collectivity collectivity;
    private Member sponsor;
    private boolean active;

    public Membership() {
    }

    public Membership(String id, Member member, Collectivity collectivity, Member sponsor, boolean active) {
        this.id = id;
        this.member = member;
        this.collectivity = collectivity;
        this.sponsor = sponsor;
        this.active = active;
    }
}