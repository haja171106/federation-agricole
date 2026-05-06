package mg.haja.federationagricole.repository;

import mg.haja.federationagricole.DTO.CreateMembershipFee;
import mg.haja.federationagricole.Entity.MembershipFee;
import mg.haja.federationagricole.Entity.enums.ActivityStatus;
import mg.haja.federationagricole.Entity.enums.Frequency;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class MembershipFeeRepository {

    private final Connection connection;

    public MembershipFeeRepository(Connection connection) {
        this.connection = connection;
    }

    public List<MembershipFee> findByCollectivityId(String collectivityId) throws SQLException {

        String sql = """
            SELECT id, eligible_from, frequency, amount, label, status
            FROM membership_fee
            WHERE collectivity_id = ?
        """;

        List<MembershipFee> fees = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, collectivityId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                fees.add(mapRow(rs));
            }
        }

        return fees;
    }

    public List<MembershipFee> saveAll(String collectivityId, List<CreateMembershipFee> requests) throws SQLException {
        String sql = """
        INSERT INTO membership_fee (id, collectivity_id, eligible_from, frequency, amount, label, status, statut) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?) 
        RETURNING id
        """;

        List<MembershipFee> created = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (CreateMembershipFee req : requests) {
                // Générer ID UUID (36 chars, fits varchar(50))
                String id = UUID.randomUUID().toString().toUpperCase();

                ps.setString(1, id);  // id
                ps.setString(2, collectivityId);  // collectivity_id
                ps.setDate(3, Date.valueOf(req.getEligibleFrom()));  // eligible_from
                ps.setString(4, req.getFrequency().name());  // frequency (MONTHLY -> MONTHLY)
                ps.setDouble(5, req.getAmount());  // amount
                ps.setString(6, req.getLabel());  // label
                ps.setString(7, ActivityStatus.ACTIVE.name());  // status
                ps.setString(8, ActivityStatus.ACTIVE.name());  // statut

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                    }
                }
                MembershipFee fee = new MembershipFee();
                fee.setId(id);
                fee.setCollectivityId(collectivityId);
                fee.setEligibleFrom(req.getEligibleFrom());
                fee.setFrequency(req.getFrequency());
                fee.setAmount(req.getAmount());
                fee.setLabel(req.getLabel());
                fee.setStatus(ActivityStatus.ACTIVE);
                created.add(fee);
            }
        }
        return created;
    }

    public boolean collectivityExists(String collectivityId) throws SQLException {

        String sql = "SELECT 1 FROM collectivity WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, collectivityId);

            return ps.executeQuery().next(); // true = la collectivité existe
        }
    }

    private MembershipFee mapRow(ResultSet rs) throws SQLException {

        MembershipFee fee = new MembershipFee();

        fee.setId(rs.getString("id"));
        fee.setEligibleFrom(rs.getDate("eligible_from").toLocalDate());
        fee.setFrequency(Frequency.valueOf(rs.getString("frequency")));
        fee.setAmount(rs.getDouble("amount"));
        fee.setLabel(rs.getString("label"));
        fee.setStatus(ActivityStatus.valueOf(rs.getString("status")));

        return fee;
    }
}