package mg.haja.federationagricole.controller;

import mg.haja.federationagricole.model.CreateMemberPayment;
import mg.haja.federationagricole.model.MemberPayment;
import mg.haja.federationagricole.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members/{id}/payments")
public class MemberPaymentController {

    private final PaymentService service;

    public MemberPaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<List<MemberPayment>> create(
            @PathVariable String id,
            @RequestBody List<CreateMemberPayment> requests) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(id, requests));
    }
}