package mg.haja.federationagricole.repository;

import mg.haja.federationagricole.Entity.Collectivity;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CollectivityRepository {

    private final Connection connection;

    public CollectivityRepository(Connection connection) {
        this.connection = connection;
    }

    public Collectivity save(Collectivity c) throws SQLException {

        if (c.getId() == null || c.getId().isBlank()) {
            c.setId(UUID.randomUUID().toString());

            String sql = """
                INSERT INTO collectivity
                    (id, number, name, agricultural_specialty, city, creation_date, opening_authorization)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, c.getId());
                ps.setString(2, c.getNumber());
                ps.setString(3, c.getName());
                ps.setString(4, c.getAgriculturalSpecialty());
                ps.setString(5, c.getCity());
                ps.setDate(6, Date.valueOf(c.getCreationDate()));
                ps.setBoolean(7, c.isOpeningAuthorization());
                ps.executeUpdate();
            }

        } else {

            String sql = """
                UPDATE collectivity
                SET number = ?, name = ?, agricultural_specialty = ?,
                    city = ?, creation_date = ?, opening_authorization = ?
                WHERE id = ?
                """;

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, c.getNumber());
                ps.setString(2, c.getName());
                ps.setString(3, c.getAgriculturalSpecialty());
                ps.setString(4, c.getCity());
                ps.setDate(5, Date.valueOf(c.getCreationDate()));
                ps.setBoolean(6, c.isOpeningAuthorization());
                ps.setString(7, c.getId());
                ps.executeUpdate();
            }
        }

        return c;
    }

    public Optional<Collectivity> findById(String id) throws SQLException {
        String sql = "SELECT * FROM collectivity WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    public Optional<Collectivity> findByNumber(String number) throws SQLException {
        String sql = "SELECT * FROM collectivity WHERE number = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, number);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    public Optional<Collectivity> findByName(String name) throws SQLException {
        String sql = "SELECT * FROM collectivity WHERE name = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    public List<Collectivity> findAll() throws SQLException {
        String sql = "SELECT * FROM collectivity";
        List<Collectivity> list = new ArrayList<>();
        try (Statement s = connection.createStatement()) {
            ResultSet rs = s.executeQuery(sql);
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Collectivity mapRow(ResultSet rs) throws SQLException {
        Collectivity c = new Collectivity();
        c.setId(rs.getString("id"));
        c.setNumber(rs.getString("number"));
        c.setName(rs.getString("name"));
        c.setAgriculturalSpecialty(rs.getString("agricultural_specialty"));
        c.setCity(rs.getString("city"));
        c.setCreationDate(rs.getDate("creation_date").toLocalDate());
        c.setOpeningAuthorization(rs.getBoolean("opening_authorization"));
        return c;
    }
}