package mg.haja.federationagricole.controller;

import mg.haja.federationagricole.DTO.CreateMembershipFee;
import mg.haja.federationagricole.Entity.MembershipFee;
import mg.haja.federationagricole.service.MembershipFeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/collectivities/{id}/membershipFees")
public class MembershipFeeController {

    private final MembershipFeeService service;

    public MembershipFeeController(MembershipFeeService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<MembershipFee>> getAll(@PathVariable String id) {
        return ResponseEntity.ok(service.getByCollectivity(id));
    }

    @PostMapping
    public ResponseEntity<List<MembershipFee>> create(
            @PathVariable String id,
            @RequestBody List<CreateMembershipFee> requests) {
        return ResponseEntity.ok(service.create(id, requests));
    }
}