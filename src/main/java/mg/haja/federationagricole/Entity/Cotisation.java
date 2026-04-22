package mg.haja.federationagricole.Entity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class Cotisation {

    private int id;
    private int collectiviteId;
    private int membreId;

    private BigDecimal montant;
    private LocalDate dateEncaissement;

    private String modePaiement;
    private String type;
    private String motif;

    private BigDecimal montantReverseFederation;

}
