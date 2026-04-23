package mg.haja.federationagricole.repository;

import mg.haja.federationagricole.Entity.enums.Frequency;
import mg.haja.federationagricole.Entity.MembershipFee;
import mg.haja.federationagricole.DTO.CreateMembershipFee;
import mg.haja.federationagricole.Entity.enums.ActivityStatus;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            ps.setInt(1, Integer.parseInt(collectivityId));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                fees.add(mapRow(rs));
            }
        }
        return fees;
    }

    public List<MembershipFee> saveAll(String collectivityId, List<CreateMembershipFee> requests) throws SQLException {
        String sql = """
            INSERT INTO membership_fee (collectivity_id, eligible_from, frequency, amount, label, status)
            VALUES (?, ?, ?, ?, ?, ?) RETURNING id
            """;
        List<MembershipFee> created = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (CreateMembershipFee req : requests) {
                ps.setInt(1, Integer.parseInt(collectivityId));
                ps.setDate(2, Date.valueOf(req.getEligibleFrom()));
                ps.setString(3, req.getFrequency().name());
                ps.setDouble(4, req.getAmount());
                ps.setString(5, req.getLabel());
                ps.setString(6, ActivityStatus.ACTIVE.name());
                
                ResultSet rs = ps.executeQuery();
                int id = 0;
                if (rs.next()) {
                    id = rs.getInt(1);
                }

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
        String sql = "SELECT 1 FROM collectivity WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(collectivityId));
            return ps.executeQuery().next();
        }
    }

    private MembershipFee mapRow(ResultSet rs) throws SQLException {
        MembershipFee fee = new MembershipFee();
        fee.setId(rs.getInt("id"));
        fee.setEligibleFrom(rs.getDate("eligible_from").toLocalDate());
        fee.setFrequency(Frequency.valueOf(rs.getString("frequency")));
        fee.setAmount(rs.getDouble("amount"));
        fee.setLabel(rs.getString("label"));
        fee.setStatus(ActivityStatus.valueOf(rs.getString("status")));
        return fee;
    }
}
