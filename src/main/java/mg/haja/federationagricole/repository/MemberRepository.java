package mg.haja.federationagricole.repository;

import mg.haja.federationagricole.Entity.Member;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepository {

    private final Connection connection;

    public MemberRepository(Connection connection) {
        this.connection = connection;
    }

    // ----------------------------------------------------------------
    // SAVE (INSERT or UPDATE)
    // ----------------------------------------------------------------

    public Member save(Member m) throws SQLException {
        if (m.getId() == null || m.getId().isBlank()) {
            return insert(m);
        } else {
            return update(m);
        }
    }

    private Member insert(Member m) throws SQLException {
        // Generate a simple ID: MBR-<timestamp>
        String newId = "MBR-" + System.currentTimeMillis();
        m.setId(newId);

        String sql = """
            INSERT INTO member
                (id, last_name, first_name, birth_date, gender,
                 address, profession, phone, email, membership_date)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, m.getId());
            ps.setString(2, m.getLastName());
            ps.setString(3, m.getFirstName());
            ps.setDate(4, Date.valueOf(m.getBirthDate()));
            ps.setString(5, m.getGender());
            ps.setString(6, m.getAddress());
            ps.setString(7, m.getProfession());
            ps.setString(8, m.getPhone());
            ps.setString(9, m.getEmail());
            ps.setDate(10, Date.valueOf(m.getMembershipDate()));
            ps.executeUpdate();
        }

        return m;
    }

    private Member update(Member m) throws SQLException {
        String sql = """
            UPDATE member
            SET last_name = ?, first_name = ?, birth_date = ?, gender = ?,
                address = ?, profession = ?, phone = ?, email = ?, membership_date = ?,
                updated_at = NOW()
            WHERE id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, m.getLastName());
            ps.setString(2, m.getFirstName());
            ps.setDate(3, Date.valueOf(m.getBirthDate()));
            ps.setString(4, m.getGender());
            ps.setString(5, m.getAddress());
            ps.setString(6, m.getProfession());
            ps.setString(7, m.getPhone());
            ps.setString(8, m.getEmail());
            ps.setDate(9, Date.valueOf(m.getMembershipDate()));
            ps.setString(10, m.getId());
            ps.executeUpdate();
        }

        return m;
    }

    // ----------------------------------------------------------------
    // ADHESION
    // ----------------------------------------------------------------

    /**
     * Creates an adhesion row linking the member to the collectivity.
     * adhesion.id = "ADH-" + memberId + "-" + collectivityId
     */
    public void saveAdhesion(String memberId, String collectivityId) throws SQLException {
        String adhesionId = "ADH-" + memberId + "-" + collectivityId;

        String sql = """
            INSERT INTO adhesion (id, member_id, collectivity_id, adhesion_date, active)
            VALUES (?, ?, ?, CURRENT_DATE, TRUE)
            ON CONFLICT DO NOTHING
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, adhesionId);
            ps.setString(2, memberId);
            ps.setString(3, collectivityId);
            ps.executeUpdate();
        }
    }

    // ----------------------------------------------------------------
    // ADHESION REFERENTS (sponsors)
    // ----------------------------------------------------------------

    /**
     * Links a sponsor (referent) to an adhesion.
     * referent_id is the existing member ID of the sponsor.
     */
    public void saveAdhesionReferent(String adhesionId, String referentId) throws SQLException {
        String referentRowId = "REF-" + adhesionId + "-" + referentId;

        String sql = """
            INSERT INTO adhesion_referent (id, adhesion_id, referent_id, created_at)
            VALUES (?, ?, ?, NOW())
            ON CONFLICT DO NOTHING
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, referentRowId);
            ps.setString(2, adhesionId);
            ps.setString(3, referentId);
            ps.executeUpdate();
        }
    }

    // ----------------------------------------------------------------
    // MEMBER PAYMENT
    // ----------------------------------------------------------------

    public void savePayment(String memberId, String membershipFeeId,
                            String accountCreditedId, double amount,
                            String paymentMethod) throws SQLException {
        String paymentId = "PAY-" + memberId + "-" + System.currentTimeMillis();

        String sql = """
            INSERT INTO member_payment
                (id, member_id, membership_fee_id, account_credited_id,
                 amount, payment_mode, creation_date)
            VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, paymentId);
            ps.setString(2, memberId);
            ps.setString(3, membershipFeeId);
            ps.setString(4, accountCreditedId);
            ps.setDouble(5, amount);
            ps.setString(6, paymentMethod);
            ps.executeUpdate();
        }
    }

    // ----------------------------------------------------------------
    // FIND METHODS
    // ----------------------------------------------------------------

    public Optional<Member> findById(String id) throws SQLException {
        String sql = "SELECT * FROM member WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    public Optional<Member> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM member WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    /**
     * Returns members who have an active adhesion in the given collectivity.
     * collectivityId is stored in the adhesion table.
     */
    public Optional<Member> findByIdWithCollectivity(String memberId, String collectivityId) throws SQLException {
        String sql = """
            SELECT m.* FROM member m
            JOIN adhesion a ON a.member_id = m.id
            WHERE m.id = ?
              AND a.collectivity_id = ?
              AND a.active = TRUE
            LIMIT 1
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, memberId);
            ps.setString(2, collectivityId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Member m = mapRow(rs);
                m.setCollectivityId(collectivityId);
                return Optional.of(m);
            }
        }
        return Optional.empty();
    }

    public List<Member> findByCollectivityId(String collectivityId) throws SQLException {
        String sql = """
            SELECT m.* FROM member m
            JOIN adhesion a ON a.member_id = m.id
            WHERE a.collectivity_id = ?
              AND a.active = TRUE
        """;

        List<Member> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Member m = mapRow(rs);
                m.setCollectivityId(collectivityId);
                list.add(m);
            }
        }
        return list;
    }

    public List<Member> findAll() throws SQLException {
        String sql = "SELECT * FROM member";
        List<Member> list = new ArrayList<>();
        try (Statement s = connection.createStatement()) {
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Member mapRow(ResultSet rs) throws SQLException {
        Member m = new Member();
        m.setId(rs.getString("id"));
        m.setLastName(rs.getString("last_name"));
        m.setFirstName(rs.getString("first_name"));
        m.setBirthDate(rs.getDate("birth_date").toLocalDate());
        m.setGender(rs.getString("gender"));
        m.setAddress(rs.getString("address"));
        m.setProfession(rs.getString("profession"));
        m.setPhone(rs.getString("phone"));
        m.setEmail(rs.getString("email"));
        m.setMembershipDate(rs.getDate("membership_date").toLocalDate());
        return m;
    }
}