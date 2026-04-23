package mg.haja.federationagricole.controller;

import mg.haja.federationagricole.DTO.AssignIdentificationRequest;
import mg.haja.federationagricole.DTO.CreateCollectivityRequest;
import mg.haja.federationagricole.DTO.AccountWithBalance;
import mg.haja.federationagricole.Entity.Collectivity;
import mg.haja.federationagricole.service.CollectivityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/collectivities")
public class CollectivityController {

    private final CollectivityService service;

    public CollectivityController(CollectivityService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Collectivity> getCollectivity(@PathVariable String id) {
        return ResponseEntity.ok(service.getCollectivityById(id));
    }

    @GetMapping("/{id}/financialAccounts")
    public ResponseEntity<List<AccountWithBalance>> listFinancialAccountsAtDate(
            @PathVariable String id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate at
    ) {
        return ResponseEntity.ok(service.getAccountsWithBalance(id, at));
    }

    @PostMapping
    public ResponseEntity<List<Collectivity>> createCollectivities(@RequestBody List<CreateCollectivityRequest> requests) {
        return ResponseEntity.ok(service.createCollectivities(requests));
    }

    @PutMapping("/{id}/informations")
    public ResponseEntity<Collectivity> assignInformations(
            @PathVariable String id,
            @RequestBody AssignIdentificationRequest request
    ) {
        Collectivity result = service.assignInformations(id, request);
        return ResponseEntity.ok(result);
    }
}