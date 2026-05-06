package mg.haja.federationagricole.Entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class Cotisation {

    private String id;
    private String collectiviteId;
    private String membreId;

    private BigDecimal montant;
    private LocalDate dateEncaissement;

    private String modePaiement;
    private String type;
    private String motif;

    private BigDecimal montantReverseFederation;

    public Cotisation(String id, String collectiviteId, String membreId, BigDecimal montant, LocalDate dateEncaissement, String modePaiement, String type, String motif, BigDecimal montantReverseFederation) {
        this.id = id;
        this.collectiviteId = collectiviteId;
        this.membreId = membreId;
        this.montant = montant;
        this.dateEncaissement = dateEncaissement;
        this.modePaiement = modePaiement;
        this.type = type;
        this.motif = motif;
        this.montantReverseFederation = montantReverseFederation;
    }
}
