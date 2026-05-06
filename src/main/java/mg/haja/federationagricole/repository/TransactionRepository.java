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
            ps.setString(1, collectivityId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CollectivityTransaction tx = new CollectivityTransaction();

                tx.setId(String.valueOf(rs.getObject("id")));
                tx.setCreationDate(rs.getDate("creation_date").toLocalDate());
                tx.setAmount(rs.getDouble("amount"));
                tx.setPaymentMode(PaymentMode.valueOf(rs.getString("payment_mode")));
                tx.setMemberDebitedId(String.valueOf(rs.getObject("member_debited_id")));

                String accountId = rs.getString("account_credited_id");
                if (accountId != null && !accountId.isEmpty()) {
                    try {
                        FinancialAccount account = accountRepository.findById(accountId);
                        tx.setAccountCredited(account);
                    } catch (Exception e) {
                        System.out.println("WARNING: Cannot load account " + accountId + ": " + e.getMessage());
                    }
                } else {
                    tx.setAccountCredited(null);
                }

                transactions.add(tx);
            }
        }

        return transactions;
    }

    public CollectivityTransaction save(
            String collectivityId,
            String memberId,
            double amount,
            PaymentMode paymentMode,
            String accountCreditedId) throws SQLException {

        String sql = """
            INSERT INTO collectivity_transaction
                (collectivity_id, member_debited_id, amount, payment_mode, account_credited_id, creation_date)
            VALUES (?, ?, ?, ?, ?, CURRENT_DATE)
            RETURNING id
            """;

        String id;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            ps.setString(2, memberId);
            ps.setDouble(3, amount);
            ps.setString(4, paymentMode.name());
            ps.setString(5, accountCreditedId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                id = String.valueOf(rs.getObject(1));
            } else {
                throw new SQLException("Failed to insert transaction");
            }
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
        String sql = "SELECT 1 FROM collectivity WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            return ps.executeQuery().next();
        }
    }
}