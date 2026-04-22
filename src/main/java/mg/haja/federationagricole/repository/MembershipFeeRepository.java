package mg.haja.federationagricole.repository;

import mg.haja.federationagricole.Entity.enums.Frequency;
import mg.haja.federationagricole.model.MembershipFee;
import mg.haja.federationagricole.model.CreateMembershipFee;
import mg.haja.federationagricole.Entity.enums.ActivityStatus;
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
            ps.setObject(1, UUID.fromString(collectivityId));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                fees.add(mapRow(rs));
            }
        }
        return fees;
    }

    public List<MembershipFee> saveAll(String collectivityId, List<CreateMembershipFee> requests) throws SQLException {
        String sql = """
            INSERT INTO membership_fee (id, collectivity_id, eligible_from, frequency, amount, label, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        List<MembershipFee> created = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (CreateMembershipFee req : requests) {
                String id = UUID.randomUUID().toString();
                ps.setObject(1, UUID.fromString(id));
                ps.setObject(2, UUID.fromString(collectivityId));
                ps.setDate(3, Date.valueOf(req.getEligibleFrom()));
                ps.setString(4, req.getFrequency().name());
                ps.setDouble(5, req.getAmount());
                ps.setString(6, req.getLabel());
                ps.setString(7, ActivityStatus.ACTIVE.name());
                ps.executeUpdate();

                MembershipFee fee = new MembershipFee();
                fee.setId(id);
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
        String sql = "SELECT 1 FROM collectivite WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, UUID.fromString(collectivityId));
            return ps.executeQuery().next();
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
