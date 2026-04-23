package mg.haja.federationagricole.service;

import mg.haja.federationagricole.DTO.AssignIdentificationRequest;
import mg.haja.federationagricole.DTO.CreateCollectivityRequest;
import mg.haja.federationagricole.DTO.AccountWithBalance;
import mg.haja.federationagricole.Entity.Collectivity;
import mg.haja.federationagricole.Entity.FinancialAccount;
import mg.haja.federationagricole.repository.CollectivityRepository;
import mg.haja.federationagricole.repository.MemberRepository;
import mg.haja.federationagricole.repository.FinancialAccountRepository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Service
public class CollectivityService {

    private final CollectivityRepository repository;
    private final MemberRepository memberRepository;
    private final FinancialAccountRepository accountRepository;

    public CollectivityService(CollectivityRepository repository, 
                               MemberRepository memberRepository, 
                               FinancialAccountRepository accountRepository) {
        this.repository = repository;
        this.memberRepository = memberRepository;
        this.accountRepository = accountRepository;
    }

    public Collectivity getCollectivityById(String id) {
        try {
            Collectivity c = repository.findById(Integer.parseInt(id))
                    .orElseThrow(() -> new RuntimeException("Collectivity not found"));
            
            c.setMembers(memberRepository.findByCollectivityId(c.getId()));
            return c;
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    public List<AccountWithBalance> getAccountsWithBalance(String collectivityId, LocalDate at) {
        try {
            List<FinancialAccount> accounts = accountRepository.findByCollectivityId(Integer.parseInt(collectivityId));
            List<AccountWithBalance> result = new ArrayList<>();
            
            for (FinancialAccount acc : accounts) {
                AccountWithBalance dto = new AccountWithBalance();
                dto.setAccount(acc);
                if (at != null) {
                    dto.setBalanceAtDate(accountRepository.getBalanceAtDate(Integer.parseInt(acc.getId()), at));
                } else {
                    dto.setBalanceAtDate(acc.getAmount());
                }
                result.add(dto);
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    public List<Collectivity> createCollectivities(List<CreateCollectivityRequest> requests) {
        return requests.stream()
                .map(this::createCollectivity)
                .toList();
    }

    public Collectivity createCollectivity(CreateCollectivityRequest request) {

        if (!request.federationApproval) {
            throw new RuntimeException("Federation approval required");
        }

        if (request.members == null || request.members.size() < 10) {
            throw new RuntimeException("At least 10 members required");
        }

        Collectivity c = new Collectivity();

        c.setCity(request.city);
        c.setAgriculturalSpecialty(request.agriculturalSpecialty);
        c.setCreationDate(request.creationDate);
        c.setOpeningAuthorization(true);

        try {
            return repository.save(c);
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }

    public Collectivity assignInformations(String id, AssignIdentificationRequest request) {

        try {
            Collectivity collectivity = repository.findById(Integer.parseInt(id))
                    .orElseThrow(() -> new RuntimeException("Collectivity not found"));

            if (collectivity.getName() != null || collectivity.getNumber() != null) {
                throw new RuntimeException("Identification already assigned");
            }

            if (repository.findByName(request.getName()).isPresent()) {
                throw new RuntimeException("Name already exists");
            }

            if (repository.findByNumber(request.getNumber()).isPresent()) {
                throw new RuntimeException("Number already exists");
            }

            collectivity.setName(request.getName());
            collectivity.setNumber(request.getNumber());

            return repository.save(collectivity);
        } catch (SQLException e) {
            throw new RuntimeException("Database error", e);
        }
    }
}