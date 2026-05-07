package mg.haja.federationagricole.repository;

import mg.haja.federationagricole.DTO.CollectivityLocalStatistics;
import mg.haja.federationagricole.DTO.CollectivityOverallStatistics;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StatisticsRepository {

    private final Connection connection;

    public StatisticsRepository(Connection connection) {
        this.connection = connection;
    }
    public List<CollectivityLocalStatistics> findLocalStatistics(
            String collectivityId, LocalDate from, LocalDate to) throws SQLException {

        String sql = """
            WITH
            active_members AS (
                SELECT
                    m.id,
                    m.first_name,
                    m.last_name,
                    m.email,
                    mp2.position AS occupation
                FROM member m
                JOIN adhesion a
                    ON a.member_id       = m.id
                   AND a.collectivity_id = ?
                   AND a.active          = TRUE
                LEFT JOIN mandate man
                    ON man.collectivity_id = ?
                   AND CURRENT_DATE BETWEEN man.start_date AND man.end_date
                LEFT JOIN mandate_position mp2
                    ON mp2.mandate_id = man.id
                   AND mp2.member_id  = m.id
            ),

            earned AS (
                SELECT
                    mp.member_id,
                    COALESCE(SUM(mp.amount), 0) AS earned_amount
                FROM member_payment mp
                WHERE mp.creation_date BETWEEN ? AND ?
                GROUP BY mp.member_id
            ),

            expected AS (
                SELECT
                    am.id AS member_id,
                    COALESCE(SUM(
                        mf.amount *
                        CASE mf.frequency
                            WHEN 'WEEKLY'     THEN GREATEST(0,
                                ((?::date - GREATEST(mf.eligible_from, ?::date))::int / 7) + 1
                            )
                            WHEN 'MONTHLY'    THEN GREATEST(0,
                                (EXTRACT(YEAR  FROM AGE(?::date, GREATEST(mf.eligible_from, ?::date)))::int * 12
                                + EXTRACT(MONTH FROM AGE(?::date, GREATEST(mf.eligible_from, ?::date)))::int) + 1
                            )
                            WHEN 'ANNUALLY'   THEN GREATEST(0,
                                EXTRACT(YEAR FROM AGE(?::date, GREATEST(mf.eligible_from, ?::date)))::int + 1
                            )
                            WHEN 'PUNCTUALLY' THEN 1
                            ELSE 0
                        END
                    ), 0) AS expected_amount
                FROM active_members am
                CROSS JOIN membership_fee mf
                WHERE mf.collectivity_id = ?
                  AND mf.statut          = 'ACTIVE'
                  AND mf.eligible_from  <= ?::date
                GROUP BY am.id
            ),

            paid_per_fee AS (
                SELECT
                    mp.member_id,
                    COALESCE(SUM(mp.amount), 0) AS paid_amount
                FROM member_payment mp
                JOIN membership_fee mf
                    ON mf.id              = mp.membership_fee_id
                   AND mf.collectivity_id = ?
                   AND mf.statut          = 'ACTIVE'
                WHERE mp.creation_date <= ?::date
                GROUP BY mp.member_id
            )

            SELECT
                am.id,
                am.first_name,
                am.last_name,
                am.email,
                am.occupation,
                COALESCE(e.earned_amount,  0)                                              AS earned_amount,
                GREATEST(0, COALESCE(ex.expected_amount, 0) - COALESCE(p.paid_amount, 0)) AS unpaid_amount
            FROM active_members am
            LEFT JOIN earned       e  ON e.member_id  = am.id
            LEFT JOIN expected     ex ON ex.member_id = am.id
            LEFT JOIN paid_per_fee p  ON p.member_id  = am.id
            ORDER BY am.last_name, am.first_name
            """;

        List<CollectivityLocalStatistics> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            ps.setString(2, collectivityId);
            ps.setDate(3, Date.valueOf(from));
            ps.setDate(4, Date.valueOf(to));
            ps.setString(5, to.toString());
            ps.setString(6, from.toString());
            ps.setString(7, to.toString());
            ps.setString(8, from.toString());
            ps.setString(9, to.toString());
            ps.setString(10, from.toString());
            ps.setString(11, to.toString());
            ps.setString(12, from.toString());
            ps.setString(13, collectivityId);
            ps.setString(14, to.toString());
            ps.setString(15, collectivityId);
            ps.setString(16, to.toString());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CollectivityLocalStatistics.MemberDescription desc =
                        new CollectivityLocalStatistics.MemberDescription();
                desc.setId(rs.getString("id"));
                desc.setFirstName(rs.getString("first_name"));
                desc.setLastName(rs.getString("last_name"));
                desc.setEmail(rs.getString("email"));
                desc.setOccupation(rs.getString("occupation"));

                CollectivityLocalStatistics stat = new CollectivityLocalStatistics();
                stat.setMemberDescription(desc);
                stat.setEarnedAmount(rs.getDouble("earned_amount"));
                stat.setUnpaidAmount(rs.getDouble("unpaid_amount"));
                result.add(stat);
            }
        }

        return result;
    }

    public List<CollectivityOverallStatistics> findOverallStatistics(
            LocalDate from, LocalDate to) throws SQLException {

        String sql = """
            WITH
            active_members AS (
                SELECT a.collectivity_id, a.member_id
                FROM adhesion a
                WHERE a.active = TRUE
            ),

            new_members AS (
                SELECT a.collectivity_id, COUNT(*) AS new_count
                FROM adhesion a
                WHERE a.adhesion_date BETWEEN ? AND ?
                GROUP BY a.collectivity_id
            ),

            active_fees AS (
                SELECT
                    mf.collectivity_id,
                    mf.id            AS fee_id,
                    mf.amount        AS fee_amount,
                    mf.frequency,
                    mf.eligible_from
                FROM membership_fee mf
                WHERE mf.statut         = 'ACTIVE'
                  AND mf.eligible_from <= ?::date
            ),

            expected_per_member_fee AS (
                SELECT
                    am.collectivity_id,
                    am.member_id,
                    af.fee_id,
                    af.fee_amount *
                    CASE af.frequency
                        WHEN 'WEEKLY'     THEN GREATEST(0,
                            ((?::date - af.eligible_from)::int / 7) + 1
                        )
                        WHEN 'MONTHLY'    THEN GREATEST(0,
                            (EXTRACT(YEAR  FROM AGE(?::date, af.eligible_from))::int * 12
                            + EXTRACT(MONTH FROM AGE(?::date, af.eligible_from))::int) + 1
                        )
                        WHEN 'ANNUALLY'   THEN GREATEST(0,
                            EXTRACT(YEAR FROM AGE(?::date, af.eligible_from))::int + 1
                        )
                        WHEN 'PUNCTUALLY' THEN 1
                        ELSE 0
                    END AS expected_amount
                FROM active_members am
                JOIN active_fees af ON af.collectivity_id = am.collectivity_id
            ),

            paid_per_member_fee AS (
                SELECT
                    a.collectivity_id,
                    mp.member_id,
                    mp.membership_fee_id AS fee_id,
                    COALESCE(SUM(mp.amount), 0) AS paid_amount
                FROM member_payment mp
                JOIN adhesion a ON a.member_id = mp.member_id AND a.active = TRUE
                WHERE mp.creation_date <= ?::date
                GROUP BY a.collectivity_id, mp.member_id, mp.membership_fee_id
            ),

            member_current_status AS (
                SELECT
                    e.collectivity_id,
                    e.member_id,
                    BOOL_AND(COALESCE(p.paid_amount, 0) >= e.expected_amount) AS is_current
                FROM expected_per_member_fee e
                LEFT JOIN paid_per_member_fee p
                    ON p.collectivity_id = e.collectivity_id
                   AND p.member_id       = e.member_id
                   AND p.fee_id          = e.fee_id
                GROUP BY e.collectivity_id, e.member_id
            ),

            current_percentage AS (
                SELECT
                    collectivity_id,
                    COUNT(*) AS total_members,
                    SUM(CASE WHEN is_current THEN 1 ELSE 0 END) AS current_members
                FROM member_current_status
                GROUP BY collectivity_id
            )

            SELECT
                c.id                                                       AS collectivity_id,
                c.name                                                     AS collectivity_name,
                c.number                                                   AS collectivity_number,
                COALESCE(nm.new_count, 0)                                  AS new_members_number,
                CASE
                    WHEN COALESCE(cp.total_members, 0) = 0 THEN 0
                    ELSE ROUND(
                        (COALESCE(cp.current_members, 0)::numeric
                         / cp.total_members::numeric) * 100, 2
                    )
                END                                                        AS overall_percentage
            FROM collectivity c
            LEFT JOIN new_members        nm ON nm.collectivity_id = c.id
            LEFT JOIN current_percentage cp ON cp.collectivity_id = c.id
            ORDER BY c.name
            """;

        List<CollectivityOverallStatistics> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            ps.setDate(2, Date.valueOf(to));
            ps.setString(3, to.toString());
            ps.setString(4, to.toString());
            ps.setString(5, to.toString());
            ps.setString(6, to.toString());
            ps.setString(7, to.toString());
            ps.setString(8, to.toString());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CollectivityOverallStatistics.CollectivityInformation info =
                        new CollectivityOverallStatistics.CollectivityInformation();
                info.setId(rs.getString("collectivity_id"));
                info.setName(rs.getString("collectivity_name"));
                info.setNumber(rs.getString("collectivity_number"));

                CollectivityOverallStatistics stat = new CollectivityOverallStatistics();
                stat.setCollectivityInformation(info);
                stat.setNewMembersNumber(rs.getInt("new_members_number"));
                stat.setOverallMemberCurrentDuePercentage(rs.getDouble("overall_percentage"));
                result.add(stat);
            }
        }

        return result;
    }
}