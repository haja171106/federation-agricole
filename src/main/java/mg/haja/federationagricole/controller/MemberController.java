package mg.haja.federationagricole.controller;

import mg.haja.federationagricole.DTO.CreateMemberRequest;
import mg.haja.federationagricole.Entity.Member;
import mg.haja.federationagricole.service.MemberService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService service;

    public MemberController(MemberService service) {
        this.service = service;
    }

    @PostMapping
    public Member createMember(@RequestBody CreateMemberRequest request) {
        return service.createMember(request);
    }
}
