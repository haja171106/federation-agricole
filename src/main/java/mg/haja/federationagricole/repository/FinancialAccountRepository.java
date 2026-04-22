package mg.haja.federationagricole.repository;

import mg.haja.federationagricole.model.*;
import mg.haja.federationagricole.model.enums.Bank;
import mg.haja.federationagricole.model.enums.MobileBankingService;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.UUID;

@Repository
public class FinancialAccountRepository {

    private final Connection connection;

    public FinancialAccountRepository(Connection connection) {
        this.connection = connection;
    }

    public FinancialAccount findById(String accountId) throws SQLException {
        // Try cash account
        String sqlCash = "SELECT id, solde FROM compte WHERE id = ? AND type = 'CAISSE'";
        try (PreparedStatement ps = connection.prepareStatement(sqlCash)) {
            ps.setObject(1, UUID.fromString(accountId));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CashAccount acc = new CashAccount();
                acc.setId(rs.getString("id"));
                acc.setAmount(rs.getDouble("solde"));
                return acc;
            }
        }

        // Try mobile money account
        String sqlMobile = """
            SELECT c.id, c.solde, mm.titulaire, mm.service, mm.telephone
            FROM compte c
            JOIN compte_mobile_money mm ON mm.compte_id = c.id
            WHERE c.id = ?
            """;
        try (PreparedStatement ps = connection.prepareStatement(sqlMobile)) {
            ps.setObject(1, UUID.fromString(accountId));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                MobileBankingAccount acc = new MobileBankingAccount();
                acc.setId(rs.getString("id"));
                acc.setAmount(rs.getDouble("solde"));
                acc.setHolderName(rs.getString("titulaire"));
                acc.setMobileBankingService(MobileBankingService.valueOf(rs.getString("service")));
                acc.setMobileNumber(rs.getString("telephone"));
                return acc;
            }
        }

        // Try bank account
        String sqlBank = """
            SELECT c.id, c.solde, b.titulaire, b.banque, b.numero_compte
            FROM compte c
            JOIN compte_bancaire b ON b.compte_id = c.id
            WHERE c.id = ?
            """;
        try (PreparedStatement ps = connection.prepareStatement(sqlBank)) {
            ps.setObject(1, UUID.fromString(accountId));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                BankAccount acc = new BankAccount();
                acc.setId(rs.getString("id"));
                acc.setAmount(rs.getDouble("solde"));
                acc.setHolderName(rs.getString("titulaire"));
                acc.setBankName(Bank.valueOf(rs.getString("banque")));
                String num = rs.getString("numero_compte");
                acc.setBankCode(num.substring(0, 5));
                acc.setBankBranchCode(num.substring(5, 10));
                acc.setBankAccountNumber(num.substring(10, 21));
                acc.setBankAccountKey(num.substring(21, 23));
                return acc;
            }
        }

        return null;
    }

    public void updateBalance(String accountId, double delta) throws SQLException {
        String sql = "UPDATE compte SET solde = solde + ?, date_solde = CURRENT_DATE WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, delta);
            ps.setObject(2, UUID.fromString(accountId));
            ps.executeUpdate();
        }
    }
}
