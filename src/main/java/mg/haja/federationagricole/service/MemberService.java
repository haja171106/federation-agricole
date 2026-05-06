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
                        throw new RuntimeException("Email already used: " + request.email);
                    });

            if (request.collectivityId == null || request.collectivityId.isBlank()) {
                throw new RuntimeException("Collectivity ID is required");
            }

            if (request.sponsors == null || request.sponsors.size() < 2) {
                throw new RuntimeException("At least 2 sponsors required");
            }

            List<Member> sponsorMembers = request.sponsors.stream()
                    .map(s -> {
                        try {
                            return repository.findByIdWithCollectivity(s.id, request.collectivityId)
                                    .orElseThrow(() -> new RuntimeException(
                                            "Sponsor not found or not active in collectivity: " + s.id));
                        } catch (SQLException e) {
                            throw new RuntimeException("Database error while resolving sponsor: " + s.id, e);
                        }
                    })
                    .toList();

            long sameCollectivity = sponsorMembers.stream()
                    .filter(s -> request.collectivityId.equals(s.getCollectivityId()))
                    .count();

            long others = sponsorMembers.size() - sameCollectivity;

            if (sameCollectivity <= others) {
                throw new RuntimeException(
                        "Invalid sponsor distribution: majority must be from collectivity " + request.collectivityId
                                + " (found " + sameCollectivity + "/" + sponsorMembers.size() + " from same collectivity)");
            }
            Member m = new Member();
            m.setFirstName(request.firstName);
            m.setLastName(request.lastName);
            m.setBirthDate(request.birthDate);
            m.setGender(request.gender);
            m.setAddress(request.address);
            m.setProfession(request.profession);
            m.setPhone(request.phoneNumber);
            m.setEmail(request.email);
            m.setMembershipDate(LocalDate.now());
            m.setCollectivityId(request.collectivityId);

            repository.save(m);

            repository.saveAdhesion(m.getId(), request.collectivityId);

            String adhesionId = "ADH-" + m.getId() + "-" + request.collectivityId;
            for (CreateMemberRequest.SponsorInput sponsor : request.sponsors) {
                repository.saveAdhesionReferent(adhesionId, sponsor.id);
            }

            if (request.payments != null) {
                for (CreateMemberRequest.PaymentInput payment : request.payments) {
                    repository.savePayment(
                            m.getId(),
                            payment.membershipFeeId,
                            payment.accountCreditedId,
                            payment.amount,
                            payment.paymentMethod
                    );
                }
            }

            return m;

        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }
}