package mg.haja.federationagricole.service;

import mg.haja.federationagricole.DTO.AssignIdentificationRequest;
import mg.haja.federationagricole.DTO.CreateCollectivityRequest;
import mg.haja.federationagricole.Entity.Collectivity;
import mg.haja.federationagricole.repository.CollectivityRepository;
import org.springframework.stereotype.Service;

@Service
public class CollectivityService {

    private final CollectivityRepository repository;

    public CollectivityService(CollectivityRepository repository) {
        this.repository = repository;
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

        return repository.save(c);
    }

    public Collectivity assignIdentification(int id, AssignIdentificationRequest request) {

        Collectivity collectivity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Collectivity not found"));

        if (collectivity.getName() != null || collectivity.getNumber() != null) {
            throw new RuntimeException("Identification already assigned");
        }

        repository.findByName(request.getName())
                .ifPresent(c -> {
                    throw new RuntimeException("Name already exists");
                });

        repository.findByNumber(request.getNumber())
                .ifPresent(c -> {
                    throw new RuntimeException("Number already exists");
                });

        collectivity.setName(request.getName());
        collectivity.setNumber(request.getNumber());

        return repository.save(collectivity);
    }
}