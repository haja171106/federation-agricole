package mg.haja.federationagricole.repository;

import mg.haja.federationagricole.DTO.CreateMemberPayment;
import mg.haja.federationagricole.Entity.FinancialAccount;
import mg.haja.federationagricole.Entity.MemberPayment;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;

@Repository
public class PaymentRepository {

    private final Connection connection;
    private final FinancialAccountRepository accountRepository;

    public PaymentRepository(Connection connection, FinancialAccountRepository accountRepository) {
        this.connection = connection;
        this.accountRepository = accountRepository;
    }

    public MemberPayment save(String memberId, CreateMemberPayment req) throws SQLException {
        String sql = """
            INSERT INTO member_payment
                (member_id, membership_fee_id, account_credited_id, amount, payment_mode, creation_date)
            VALUES (?, ?, ?, ?, ?, CURRENT_DATE) RETURNING id
            """;
        int id = 0;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(memberId));
            ps.setInt(2, Integer.parseInt(req.getMembershipFeeIdentifier()));
            ps.setInt(3, Integer.parseInt(req.getAccountCreditedIdentifier()));
            ps.setDouble(4, req.getAmount());
            ps.setString(5, req.getPaymentMode().name());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        }

        FinancialAccount account = accountRepository.findById(req.getAccountCreditedIdentifier());

        MemberPayment payment = new MemberPayment();
        payment.setId(String.valueOf(id));
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
            ps.setInt(1, Integer.parseInt(memberId));
            ResultSet rs = ps.executeQuery();
            return rs.next() ? String.valueOf(rs.getInt("collectivity_id")) : null;
        }
    }

    public boolean memberExists(String memberId) throws SQLException {
        String sql = "SELECT 1 FROM member WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(memberId));
            return ps.executeQuery().next();
        }
    }

    public boolean membershipFeeExists(String feeId) throws SQLException {
        String sql = "SELECT 1 FROM membership_fee WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(feeId));
            return ps.executeQuery().next();
        }
    }
}
