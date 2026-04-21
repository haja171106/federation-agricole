package mg.haja.federationagricole.service;

import org.springframework.stereotype.Service;

@Service
public class CollectivityService {

    private final CollectivityRepository repository;

    public CollectivityService(CollectivityRepository repository) {
        this.repository = repository;
    }

    public Collectivity createCollectivity(CreateCollectivityRequest request) {

        repository.findByNumber(request.number)
                .ifPresent(c -> {
                    throw new RuntimeException("Collectivity number already exists");
                });

        if (!request.federationApproval) {
            throw new RuntimeException("Federation approval required");
        }

        if (request.members.size() < 10) {
            throw new RuntimeException("At least 10 members required");
        }

        Collectivity c = new Collectivity();
        c.setName(request.name);
        c.setNumber(request.number);
        c.setCity(request.city);
        c.setAgriculturalSpecialty(request.agriculturalSpecialty);
        c.setCreationDate(request.creationDate);
        c.setOpeningAuthorization(true);

        return repository.save(c);
    }
}