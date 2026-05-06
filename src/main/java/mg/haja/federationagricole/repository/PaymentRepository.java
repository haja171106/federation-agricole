package mg.haja.federationagricole.repository;

import mg.haja.federationagricole.DTO.CreateMemberPayment;
import mg.haja.federationagricole.Entity.FinancialAccount;
import mg.haja.federationagricole.Entity.MemberPayment;
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
        String newId = UUID.randomUUID().toString();

        String sql = """
            INSERT INTO member_payment
                (id, member_id, membership_fee_id, account_credited_id, amount, payment_mode, creation_date)
            VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE)
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // ✅ FIX : setString() partout — les IDs sont VARCHAR(50), pas des entiers
            ps.setString(1, newId);
            ps.setString(2, memberId);
            ps.setString(3, req.getMembershipFeeIdentifier());
            ps.setString(4, req.getAccountCreditedIdentifier());
            ps.setDouble(5, req.getAmount());
            ps.setString(6, req.getPaymentMode().name());
            ps.executeUpdate();
        }

        FinancialAccount account = accountRepository.findById(req.getAccountCreditedIdentifier());

        MemberPayment payment = new MemberPayment();
        payment.setId(newId);
        payment.setAmount(req.getAmount());
        payment.setPaymentMode(req.getPaymentMode());
        payment.setAccountCredited(account);
        payment.setCreationDate(LocalDate.now());
        return payment;
    }

    public String findCollectivityIdByMember(String memberId) throws SQLException {
        String sql = """
            SELECT collectivity_id FROM adhesion
            WHERE member_id = ? AND active = TRUE
            LIMIT 1
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, memberId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("collectivity_id") : null;
        }
    }

    public boolean memberExists(String memberId) throws SQLException {
        String sql = "SELECT 1 FROM member WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, memberId);
            return ps.executeQuery().next();
        }
    }

    public boolean membershipFeeExists(String feeId) throws SQLException {
        String sql = "SELECT 1 FROM membership_fee WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, feeId);
            return ps.executeQuery().next();
        }
    }
}