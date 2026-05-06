package mg.haja.federationagricole.Entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PostMandateFederation {
    private String id;
    private MandateFederation mandateFederation;
    private Member member;
    private String position;

    public PostMandateFederation() {
    }

    public PostMandateFederation(String id, MandateFederation mandateFederation, Member member, String position) {
        this.id = id;
        this.mandateFederation = mandateFederation;
        this.member = member;
        this.position = position;
    }
}