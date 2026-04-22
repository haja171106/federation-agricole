package mg.haja.federationagricole.repository;

import mg.haja.federationagricole.model.CreateMemberPayment;
import mg.haja.federationagricole.model.FinancialAccount;
import mg.haja.federationagricole.model.MemberPayment;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.UUID;

@Repository
public class PaymentRepository {

    private final Connection connection;
    private final FinancialAccountRepository accountRepository;

    public PaymentRepository(Connection connection, FinancialAccountRepository accountRepository) {
        this.connection = connection;
        this.accountRepository = accountRepository;
    }

    public MemberPayment save(String memberId, CreateMemberPayment req) throws SQLException {
        String id = UUID.randomUUID().toString();
        String sql = """
            INSERT INTO member_payment
                (id, member_id, membership_fee_id, account_credited_id, amount, payment_mode, creation_date)
            VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE)
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, UUID.fromString(id));
            ps.setObject(2, UUID.fromString(memberId));
            ps.setObject(3, UUID.fromString(req.getMembershipFeeIdentifier()));
            ps.setObject(4, UUID.fromString(req.getAccountCreditedIdentifier()));
            ps.setDouble(5, req.getAmount());
            ps.setString(6, req.getPaymentMode().name());
            ps.executeUpdate();
        }

        FinancialAccount account = accountRepository.findById(req.getAccountCreditedIdentifier());

        MemberPayment payment = new MemberPayment();
        payment.setId(id);
        payment.setAmount(req.getAmount());
        payment.setPaymentMode(req.getPaymentMode());
        payment.setAccountCredited(account);
        payment.setCreationDate(LocalDate.now());
        return payment;
    }

    public String findCollectivityIdByMember(String memberId) throws SQLException {
        String sql = """
            SELECT collectivite_id FROM adhesion
            WHERE membre_id = ? AND actif = TRUE
            LIMIT 1
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, UUID.fromString(memberId));
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("collectivite_id") : null;
        }
    }

    public boolean memberExists(String memberId) throws SQLException {
        String sql = "SELECT 1 FROM membre WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, UUID.fromString(memberId));
            return ps.executeQuery().next();
        }
    }

    public boolean membershipFeeExists(String feeId) throws SQLException {
        String sql = "SELECT 1 FROM membership_fee WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, UUID.fromString(feeId));
            return ps.executeQuery().next();
        }
    }
}
