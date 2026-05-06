package mg.haja.federationagricole.Entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MandatePosition {
    private String id;
    private Mandate mandate;
    private Member member;
    private String position;

    public MandatePosition() {
    }

    public MandatePosition(String id, Mandate mandate, Member member, String position) {
        this.id = id;
        this.mandate = mandate;
        this.member = member;
        this.position = position;
    }
}