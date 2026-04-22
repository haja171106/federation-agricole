package mg.haja.federationagricole.controller;

import mg.haja.federationagricole.DTO.AssignIdentificationRequest;
import mg.haja.federationagricole.DTO.CreateCollectivityRequest;
import mg.haja.federationagricole.Entity.Collectivity;
import mg.haja.federationagricole.service.CollectivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/collectivities")
public class CollectivityController {

    private final CollectivityService service;

    public CollectivityController(CollectivityService service) {
        this.service = service;
    }

    @PostMapping
    public Collectivity createCollectivity(@RequestBody CreateCollectivityRequest request) {
        return service.createCollectivity(request);
    }

    @PostMapping("/{id}/identification")
    public ResponseEntity<Collectivity> assignIdentification(
            @PathVariable int id,
            @RequestBody AssignIdentificationRequest request
    ) {
        Collectivity result = service.assignIdentification(id, request);
        return ResponseEntity.ok(result);
    }
}