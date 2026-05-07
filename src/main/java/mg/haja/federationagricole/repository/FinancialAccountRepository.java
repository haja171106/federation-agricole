package mg.haja.federationagricole.repository;

import mg.haja.federationagricole.Entity.*;
import mg.haja.federationagricole.Entity.enums.Bank;
import mg.haja.federationagricole.Entity.enums.MobileBankingService;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FinancialAccountRepository {

    private final Connection connection;

    public FinancialAccountRepository(Connection connection) {
        this.connection = connection;
    }

    public List<FinancialAccount> findByCollectivityId(String collectivityId) throws SQLException {
        String sql = "SELECT id, type, balance FROM account WHERE collectivity_id = ?";
        List<FinancialAccount> accounts = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String id   = rs.getString("id");
                String type = rs.getString("type");
                double bal  = rs.getDouble("balance");
                FinancialAccount acc = fetchAccountDetails(id, type, bal);
                if (acc != null) {
                    accounts.add(acc);
                }
            }
        }
        return accounts;
    }

    public FinancialAccount findById(String accountId) throws SQLException {
        String sql = "SELECT id, type, balance FROM account WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, accountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return fetchAccountDetails(
                        rs.getString("id"),
                        rs.getString("type"),
                        rs.getDouble("balance")
                );
            }
        }
        return null;
    }

    public void updateBalance(String accountId, double delta) throws SQLException {
        String sql = "UPDATE account SET balance = balance + ?, balance_date = CURRENT_DATE WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, delta);
            ps.setString(2, accountId);
            ps.executeUpdate();
        }
    }

    public double getBalanceAtDate(String accountId, LocalDate at) throws SQLException {
        String sqlPayments = """
            SELECT COALESCE(SUM(amount), 0)
            FROM member_payment
            WHERE account_credited_id = ? AND creation_date <= ?
        """;
        String sqlTransactions = """
            SELECT COALESCE(SUM(amount), 0)
            FROM collectivity_transaction
            WHERE account_credited_id = ? AND creation_date <= ?
        """;

        double total = 0;

        try (PreparedStatement ps = connection.prepareStatement(sqlPayments)) {
            ps.setString(1, accountId);
            ps.setDate(2, Date.valueOf(at));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) total += rs.getDouble(1);
        }

        try (PreparedStatement ps = connection.prepareStatement(sqlTransactions)) {
            ps.setString(1, accountId);
            ps.setDate(2, Date.valueOf(at));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) total += rs.getDouble(1);
        }

        return total;
    }

    private FinancialAccount fetchAccountDetails(String id, String type, double balance) throws SQLException {

        if ("CASH".equals(type)) {
            CashAccount acc = new CashAccount();
            acc.setId(id);
            acc.setAmount(balance);
            return acc;

        } else if ("MOBILE_MONEY".equals(type)) {
            String sql = "SELECT holder, service, phone FROM mobile_money_account WHERE account_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    MobileBankingAccount acc = new MobileBankingAccount();
                    acc.setId(id);
                    acc.setAmount(balance);
                    acc.setHolderName(rs.getString("holder"));
                    acc.setMobileBankingService(MobileBankingService.valueOf(rs.getString("service")));
                    acc.setMobileNumber(rs.getString("phone"));
                    return acc;
                }
            }

        } else if ("BANK".equals(type)) {
            String sql = "SELECT holder, bank_name, account_number FROM bank_account WHERE account_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    BankAccount acc = new BankAccount();
                    acc.setId(id);
                    acc.setAmount(balance);
                    acc.setHolderName(rs.getString("holder"));
                    acc.setBankName(Bank.valueOf(rs.getString("bank_name")));
                    String num = rs.getString("account_number");
                    acc.setBankCode(num.substring(0, 5));
                    acc.setBankBranchCode(num.substring(5, 10));
                    acc.setBankAccountNumber(num.substring(10, 21));
                    acc.setBankAccountKey(num.substring(21, 23));
                    return acc;
                }
            }
        }

        return null;
    }
}