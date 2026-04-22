package mg.haja.federationagricole.repository;

import mg.haja.federationagricole.Entity.Collectivity;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CollectivityRepository {

    private final Map<Integer, Collectivity> db = new HashMap<>();
    private int currentId = 1;

    public Collectivity save(Collectivity collectivity) {
        if (collectivity.getId() == 0) {
            collectivity.setId(currentId++);
        }
        db.put(collectivity.getId(), collectivity);
        return collectivity;
    }

    public Optional<Collectivity> findById(int id) {
        return Optional.ofNullable(db.get(id));
    }

    public Optional<Collectivity> findByNumber(String number) {
        return db.values().stream()
                .filter(c -> c.getNumber().equals(number))
                .findFirst();
    }

    public Optional<Collectivity> findByName(String name) {
        return db.values().stream()
                .filter(c -> name.equals(c.getName()))
                .findFirst();
    }
    public List<Collectivity> findAll() {
        return new ArrayList<>(db.values());
    }


}