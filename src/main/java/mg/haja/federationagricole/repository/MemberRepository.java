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

    public Member save(Member m) throws SQLException {
        if (m.getId() == 0) {
            String sql = "INSERT INTO member (last_name, first_name, birth_date, gender, address, profession, phone, email, membership_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
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
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    m.setId(rs.getInt(1));
                }
            }
            
            String sqlAdhesion = "INSERT INTO adhesion (member_id, collectivity_id, adhesion_date, active) VALUES (?, ?, CURRENT_DATE, TRUE)";
            try (PreparedStatement ps = connection.prepareStatement(sqlAdhesion)) {
                ps.setInt(1, m.getId());
                ps.setInt(2, m.getCollectivityId());
                ps.executeUpdate();
            }
        } else {
            String sql = "UPDATE member SET last_name = ?, first_name = ?, birth_date = ?, gender = ?, address = ?, profession = ?, phone = ?, email = ?, membership_date = ? WHERE id = ?";
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
                ps.setInt(10, m.getId());
                ps.executeUpdate();
            }
        }
        return m;
    }

    public Optional<Member> findById(int id) throws SQLException {
        String sql = "SELECT * FROM member WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
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

    public List<Member> findByCollectivityId(int collectivityId) throws SQLException {
        String sql = """
            SELECT m.* FROM member m
            JOIN adhesion a ON a.member_id = m.id
            WHERE a.collectivity_id = ? AND a.active = TRUE
            """;
        List<Member> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, collectivityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
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
        m.setId(rs.getInt("id"));
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