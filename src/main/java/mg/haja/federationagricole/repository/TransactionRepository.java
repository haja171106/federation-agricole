package mg.haja.federationagricole.repository;

import mg.haja.federationagricole.model.CollectivityTransaction;
import mg.haja.federationagricole.model.FinancialAccount;
import mg.haja.federationagricole.Entity.enums.PaymentMode;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class TransactionRepository {

    private final Connection connection;
    private final FinancialAccountRepository accountRepository;

    public TransactionRepository(Connection connection, FinancialAccountRepository accountRepository) {
        this.connection = connection;
        this.accountRepository = accountRepository;
    }

    public List<CollectivityTransaction> findByCollectivityAndPeriod(
            String collectivityId, LocalDate from, LocalDate to) throws SQLException {

        String sql = """
            SELECT id, creation_date, amount, payment_mode, account_credited_id, member_debited_id
            FROM collectivity_transaction
            WHERE collectivity_id = ?
              AND creation_date BETWEEN ? AND ?
            ORDER BY creation_date DESC
            """;
        List<CollectivityTransaction> transactions = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, UUID.fromString(collectivityId));
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CollectivityTransaction tx = new CollectivityTransaction();
                tx.setId(rs.getString("id"));
                tx.setCreationDate(rs.getDate("creation_date").toLocalDate());
                tx.setAmount(rs.getDouble("amount"));
                tx.setPaymentMode(PaymentMode.valueOf(rs.getString("payment_mode")));
                tx.setMemberDebitedId(rs.getString("member_debited_id"));
                FinancialAccount account = accountRepository.findById(rs.getString("account_credited_id"));
                tx.setAccountCredited(account);
                transactions.add(tx);
            }
        }
        return transactions;
    }

    public CollectivityTransaction save(
            String collectivityId, String memberId, double amount,
            PaymentMode paymentMode, String accountCreditedId) throws SQLException {

        String id = UUID.randomUUID().toString();
        String sql = """
            INSERT INTO collectivity_transaction
                (id, collectivity_id, member_debited_id, amount, payment_mode, account_credited_id, creation_date)
            VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE)
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, UUID.fromString(id));
            ps.setObject(2, UUID.fromString(collectivityId));
            ps.setObject(3, UUID.fromString(memberId));
            ps.setDouble(4, amount);
            ps.setString(5, paymentMode.name());
            ps.setObject(6, UUID.fromString(accountCreditedId));
            ps.executeUpdate();
        }

        CollectivityTransaction tx = new CollectivityTransaction();
        tx.setId(id);
        tx.setCreationDate(LocalDate.now());
        tx.setAmount(amount);
        tx.setPaymentMode(paymentMode);
        tx.setMemberDebitedId(memberId);
        tx.setAccountCredited(accountRepository.findById(accountCreditedId));
        return tx;
    }

    public boolean collectivityExists(String collectivityId) throws SQLException {
        String sql = "SELECT 1 FROM collectivite WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setObject(1, UUID.fromString(collectivityId));
            return ps.executeQuery().next();
        }
    }
}
