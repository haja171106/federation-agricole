package mg.haja.federationagricole.service;

import mg.haja.federationagricole.DTO.CreateMemberRequest;
import mg.haja.federationagricole.Entity.Member;
import mg.haja.federationagricole.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    public List<Member> createMembers(List<CreateMemberRequest> requests) {
        return requests.stream()
                .map(this::createMember)
                .toList();
    }

    public Member createMember(CreateMemberRequest request) {
        try {
            repository.findByEmail(request.email)
                    .ifPresent(m -> {
                        throw new RuntimeException("Email already used");
                    });

            if (request.sponsors.size() < 2) {
                throw new RuntimeException("At least 2 sponsors required");
            }

            long sameCollectivity = request.sponsors.stream()
                    .filter(s -> s.collectivityId == request.collectivityId)
                    .count();

            long others = request.sponsors.size() - sameCollectivity;

            if (sameCollectivity < others) {
                throw new RuntimeException("Invalid sponsor distribution");
            }

            Member m = new Member();
            m.setFirstName(request.firstName);
            m.setLastName(request.lastName);
            m.setBirthDate(request.birthDate);
            m.setGender(request.gender);
            m.setAddress(request.address);
            m.setProfession(request.profession);
            m.setPhone(request.phone);
            m.setEmail(request.email);
            m.setMembershipDate(LocalDate.now());
            m.setCollectivityId(request.collectivityId);

            return repository.save(m);
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }
}
