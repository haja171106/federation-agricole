package mg.haja.federationagricole.controller;

import mg.haja.federationagricole.DTO.CreateCollectivityRequest;
import mg.haja.federationagricole.Entity.Collectivity;
import mg.haja.federationagricole.service.CollectivityService;
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
}