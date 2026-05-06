package mg.haja.federationagricole.Entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Mandate {
    private String id;
    private Collectivity collectivity;

    public Mandate() {
    }

    public Mandate(String id, Collectivity collectivity) {
        this.id = id;
        this.collectivity = collectivity;
    }
}
