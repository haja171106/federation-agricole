package mg.haja.federationagricole.controller;

import mg.haja.federationagricole.DTO.CreateMemberRequest;
import mg.haja.federationagricole.Entity.Member;
import mg.haja.federationagricole.service.MemberService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService service;

    public MemberController(MemberService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<List<Member>> createMembers(@RequestBody List<CreateMemberRequest> requests) {
        return ResponseEntity.ok(service.createMembers(requests));
    }
}
