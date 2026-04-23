package mg.haja.federationagricole.repository;

import mg.haja.federationagricole.Entity.CollectivityTransaction;
import mg.haja.federationagricole.Entity.FinancialAccount;
import mg.haja.federationagricole.Entity.enums.PaymentMode;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
            ps.setInt(1, Integer.parseInt(collectivityId));
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CollectivityTransaction tx = new CollectivityTransaction();
                tx.setId(String.valueOf(rs.getInt("id")));
                tx.setCreationDate(rs.getDate("creation_date").toLocalDate());
                tx.setAmount(rs.getDouble("amount"));
                tx.setPaymentMode(PaymentMode.valueOf(rs.getString("payment_mode")));
                tx.setMemberDebitedId(String.valueOf(rs.getInt("member_debited_id")));
                FinancialAccount account = accountRepository.findById(String.valueOf(rs.getInt("account_credited_id")));
                tx.setAccountCredited(account);
                transactions.add(tx);
            }
        }
        return transactions;
    }

    public CollectivityTransaction save(
            String collectivityId, String memberId, double amount,
            PaymentMode paymentMode, String accountCreditedId) throws SQLException {

        String sql = """
            INSERT INTO collectivity_transaction
                (collectivity_id, member_debited_id, amount, payment_mode, account_credited_id, creation_date)
            VALUES (?, ?, ?, ?, ?, CURRENT_DATE) RETURNING id
            """;
        int id = 0;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(collectivityId));
            ps.setInt(2, Integer.parseInt(memberId));
            ps.setDouble(3, amount);
            ps.setString(4, paymentMode.name());
            ps.setInt(5, Integer.parseInt(accountCreditedId));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        }

        CollectivityTransaction tx = new CollectivityTransaction();
        tx.setId(String.valueOf(id));
        tx.setCreationDate(LocalDate.now());
        tx.setAmount(amount);
        tx.setPaymentMode(paymentMode);
        tx.setMemberDebitedId(memberId);
        tx.setAccountCredited(accountRepository.findById(accountCreditedId));
        return tx;
    }

    public boolean collectivityExists(String collectivityId) throws SQLException {
        String sql = "SELECT 1 FROM collectivity WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(collectivityId));
            return ps.executeQuery().next();
        }
    }
}
