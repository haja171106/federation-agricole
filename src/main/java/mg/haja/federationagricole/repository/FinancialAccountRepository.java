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

    public List<FinancialAccount> findByCollectivityId(int collectivityId) throws SQLException {
        String sql = "SELECT id, type, balance FROM account WHERE collectivity_id = ?";
        List<FinancialAccount> accounts = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, collectivityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String type = rs.getString("type");
                int id = rs.getInt("id");
                FinancialAccount acc = fetchAccountDetails(id, type, rs.getDouble("balance"));
                if (acc != null) {
                    accounts.add(acc);
                }
            }
        }
        return accounts;
    }

    public double getBalanceAtDate(int accountId, LocalDate at) throws SQLException {
        String sqlPayments = "SELECT SUM(amount) FROM member_payment WHERE account_credited_id = ? AND creation_date <= ?";
        String sqlTransactions = "SELECT SUM(amount) FROM collectivity_transaction WHERE account_credited_id = ? AND creation_date <= ?";
        
        double total = 0;
        try (PreparedStatement ps = connection.prepareStatement(sqlPayments)) {
            ps.setInt(1, accountId);
            ps.setDate(2, Date.valueOf(at));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total += rs.getDouble(1);
            }
        }
        try (PreparedStatement ps = connection.prepareStatement(sqlTransactions)) {
            ps.setInt(1, accountId);
            ps.setDate(2, Date.valueOf(at));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                total += rs.getDouble(1);
            }
        }
        return total;
    }

    private FinancialAccount fetchAccountDetails(int id, String type, double balance) throws SQLException {
        if ("CASH".equals(type)) {
            CashAccount acc = new CashAccount();
            acc.setId(String.valueOf(id));
            acc.setAmount(balance);
            return acc;
        } else if ("MOBILE_MONEY".equals(type)) {
            String sql = "SELECT holder, service, phone FROM mobile_money_account WHERE account_id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    MobileBankingAccount acc = new MobileBankingAccount();
                    acc.setId(String.valueOf(id));
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
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    BankAccount acc = new BankAccount();
                    acc.setId(String.valueOf(id));
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

    public FinancialAccount findById(String accountId) throws SQLException {
        String sql = "SELECT id, type, balance FROM account WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, Integer.parseInt(accountId));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return fetchAccountDetails(rs.getInt("id"), rs.getString("type"), rs.getDouble("balance"));
            }
        }
        return null;
    }

    public void updateBalance(String accountId, double delta) throws SQLException {
        String sql = "UPDATE account SET balance = balance + ?, balance_date = CURRENT_DATE WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, delta);
            ps.setInt(2, Integer.parseInt(accountId));
            ps.executeUpdate();
        }
    }
}
