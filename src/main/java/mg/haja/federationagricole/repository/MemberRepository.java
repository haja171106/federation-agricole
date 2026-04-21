package mg.haja.federationagricole.repository;

import mg.haja.federationagricole.Entity.*;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MemberRepository {

    private final Map<Integer, Member> db = new HashMap<>();
    private int currentId = 1;

    public Member save(Member member) {
        if (member.getId() == 0) {
            member.setId(currentId++);
        }
        db.put(member.getId(), member);
        return member;
    }

    public Optional<Member> findById(int id) {
        return Optional.ofNullable(db.get(id));
    }

    public Optional<Member> findByEmail(String email) {
        return db.values().stream()
                .filter(m -> m.getEmail().equals(email))
                .findFirst();
    }

    public List<Member> findAll() {
        return new ArrayList<>(db.values());
    }
}