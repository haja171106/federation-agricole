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
            current_mandate AS (
                SELECT id FROM mandate
                WHERE collectivity_id = ?
                  AND CURRENT_DATE BETWEEN start_date AND end_date
                LIMIT 1
            ),

            active_members AS (
                SELECT
                    m.id,
                    m.first_name,
                    m.last_name,
                    m.email,
                    mp.position AS occupation
                FROM member m
                JOIN adhesion a
                    ON a.member_id       = m.id
                   AND a.collectivity_id = ?
                   AND a.active          = TRUE
                LEFT JOIN mandate_position mp
                    ON mp.mandate_id = (SELECT id FROM current_mandate)
                   AND mp.member_id  = m.id
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
                                ((?::date - GREATEST(mf.eligible_from, ?::date))::int / 7) + 1)
                            WHEN 'MONTHLY'    THEN GREATEST(0,
                                (EXTRACT(YEAR  FROM AGE(?::date, GREATEST(mf.eligible_from, ?::date)))::int * 12
                                + EXTRACT(MONTH FROM AGE(?::date, GREATEST(mf.eligible_from, ?::date)))::int) + 1)
                            WHEN 'ANNUALLY'   THEN GREATEST(0,
                                EXTRACT(YEAR FROM AGE(?::date, GREATEST(mf.eligible_from, ?::date)))::int + 1)
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
            ),

            period_activities AS (
                SELECT a.id AS activity_id
                FROM activity a
                WHERE a.collectivity_id = ?
                  AND a.date::date BETWEEN ? AND ?
            ),

            member_concerned_activities AS (
                SELECT
                    am.id         AS member_id,
                    pa.activity_id
                FROM active_members am
                JOIN period_activities pa ON TRUE
                WHERE
                    NOT EXISTS (
                        SELECT 1 FROM activity_target_position atp
                        WHERE atp.activity_id = pa.activity_id
                    )
                    OR
                    EXISTS (
                        SELECT 1 FROM activity_target_position atp
                        WHERE atp.activity_id = pa.activity_id
                          AND atp.position    = am.occupation
                    )
            ),

            member_attended AS (
                SELECT
                    p.member_id,
                    COUNT(*) AS attended_count
                FROM presence p
                JOIN member_concerned_activities mca
                    ON mca.activity_id = p.activity_id
                   AND mca.member_id   = p.member_id
                WHERE p.status = 'PRESENT'
                GROUP BY p.member_id
            ),

            member_concerned_count AS (
                SELECT
                    member_id,
                    COUNT(*) AS concerned_count
                FROM member_concerned_activities
                GROUP BY member_id
            ),

            assiduity AS (
                SELECT
                    mcc.member_id,
                    CASE
                        WHEN mcc.concerned_count = 0 THEN 100.0
                        ELSE ROUND(
                            (COALESCE(ma.attended_count, 0)::numeric
                             / mcc.concerned_count::numeric) * 100, 2
                        )
                    END AS assiduity_percentage
                FROM member_concerned_count mcc
                LEFT JOIN member_attended ma ON ma.member_id = mcc.member_id
            )

            SELECT
                am.id,
                am.first_name,
                am.last_name,
                am.email,
                am.occupation,
                COALESCE(e.earned_amount,  0)                                              AS earned_amount,
                GREATEST(0, COALESCE(ex.expected_amount, 0) - COALESCE(p.paid_amount, 0)) AS unpaid_amount,
                COALESCE(ass.assiduity_percentage, 100.0)                                  AS assiduity_percentage
            FROM active_members am
            LEFT JOIN earned        e   ON e.member_id   = am.id
            LEFT JOIN expected      ex  ON ex.member_id  = am.id
            LEFT JOIN paid_per_fee  p   ON p.member_id   = am.id
            LEFT JOIN assiduity     ass ON ass.member_id = am.id
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
            ps.setString(17, collectivityId);
            ps.setDate(18, Date.valueOf(from));
            ps.setDate(19, Date.valueOf(to));

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
                stat.setAssiduityPercentage(rs.getDouble("assiduity_percentage"));
                result.add(stat);
            }
        }

        return result;
    }

    public List<CollectivityOverallStatistics> findOverallStatistics(
            LocalDate from, LocalDate to) throws SQLException {

        String sql = """
            WITH
            -- Membres actifs par collectivité avec occupation
            active_members AS (
                SELECT
                    a.collectivity_id,
                    a.member_id,
                    mp.position AS occupation
                FROM adhesion a
                LEFT JOIN mandate man
                    ON man.collectivity_id = a.collectivity_id
                   AND CURRENT_DATE BETWEEN man.start_date AND man.end_date
                LEFT JOIN mandate_position mp
                    ON mp.mandate_id = man.id
                   AND mp.member_id  = a.member_id
                WHERE a.active = TRUE
            ),

            new_members AS (
                SELECT a.collectivity_id, COUNT(*) AS new_count
                FROM adhesion a
                WHERE a.adhesion_date BETWEEN ? AND ?
                GROUP BY a.collectivity_id
            ),

            active_fees AS (
                SELECT mf.collectivity_id, mf.id AS fee_id,
                       mf.amount AS fee_amount, mf.frequency, mf.eligible_from
                FROM membership_fee mf
                WHERE mf.statut = 'ACTIVE' AND mf.eligible_from <= ?::date
            ),

            expected_per_member_fee AS (
                SELECT
                    am.collectivity_id, am.member_id, af.fee_id,
                    af.fee_amount *
                    CASE af.frequency
                        WHEN 'WEEKLY'     THEN GREATEST(0,
                            ((?::date - af.eligible_from)::int / 7) + 1)
                        WHEN 'MONTHLY'    THEN GREATEST(0,
                            (EXTRACT(YEAR  FROM AGE(?::date, af.eligible_from))::int * 12
                            + EXTRACT(MONTH FROM AGE(?::date, af.eligible_from))::int) + 1)
                        WHEN 'ANNUALLY'   THEN GREATEST(0,
                            EXTRACT(YEAR FROM AGE(?::date, af.eligible_from))::int + 1)
                        WHEN 'PUNCTUALLY' THEN 1
                        ELSE 0
                    END AS expected_amount
                FROM active_members am
                JOIN active_fees af ON af.collectivity_id = am.collectivity_id
            ),

            paid_per_member_fee AS (
                SELECT
                    a.collectivity_id, mp.member_id, mp.membership_fee_id AS fee_id,
                    COALESCE(SUM(mp.amount), 0) AS paid_amount
                FROM member_payment mp
                JOIN adhesion a ON a.member_id = mp.member_id AND a.active = TRUE
                WHERE mp.creation_date <= ?::date
                GROUP BY a.collectivity_id, mp.member_id, mp.membership_fee_id
            ),

            member_current_status AS (
                SELECT
                    e.collectivity_id, e.member_id,
                    BOOL_AND(COALESCE(p.paid_amount, 0) >= e.expected_amount) AS is_current
                FROM expected_per_member_fee e
                LEFT JOIN paid_per_member_fee p
                    ON p.collectivity_id = e.collectivity_id
                   AND p.member_id = e.member_id
                   AND p.fee_id    = e.fee_id
                GROUP BY e.collectivity_id, e.member_id
            ),

            current_percentage AS (
                SELECT
                    collectivity_id,
                    COUNT(*) AS total_members,
                    SUM(CASE WHEN is_current THEN 1 ELSE 0 END) AS current_members
                FROM member_current_status
                GROUP BY collectivity_id
            ),

            period_activities AS (
                SELECT a.id AS activity_id, a.collectivity_id
                FROM activity a
                WHERE a.date::date BETWEEN ? AND ?
            ),

            member_concerned_activities AS (
                SELECT
                    am.collectivity_id,
                    am.member_id,
                    pa.activity_id
                FROM active_members am
                JOIN period_activities pa ON pa.collectivity_id = am.collectivity_id
                WHERE
                    NOT EXISTS (
                        SELECT 1 FROM activity_target_position atp
                        WHERE atp.activity_id = pa.activity_id
                    )
                    OR EXISTS (
                        SELECT 1 FROM activity_target_position atp
                        WHERE atp.activity_id = pa.activity_id
                          AND atp.position    = am.occupation
                    )
            ),

            -- Présences PRESENT par membre
            member_attended AS (
                SELECT p.member_id, pa.collectivity_id, COUNT(*) AS attended_count
                FROM presence p
                JOIN period_activities pa ON pa.activity_id = p.activity_id
                WHERE p.status = 'PRESENT'
                GROUP BY p.member_id, pa.collectivity_id
            ),

            -- Total activités concernées par membre
            member_concerned_count AS (
                SELECT collectivity_id, member_id, COUNT(*) AS concerned_count
                FROM member_concerned_activities
                GROUP BY collectivity_id, member_id
            ),

            -- Assiduité individuelle par membre
            member_assiduity AS (
                SELECT
                    mcc.collectivity_id,
                    mcc.member_id,
                    CASE
                        WHEN mcc.concerned_count = 0 THEN 100.0
                        ELSE ROUND(
                            (COALESCE(ma.attended_count, 0)::numeric
                             / mcc.concerned_count::numeric) * 100, 2
                        )
                    END AS assiduity_pct
                FROM member_concerned_count mcc
                LEFT JOIN member_attended ma
                    ON ma.member_id       = mcc.member_id
                   AND ma.collectivity_id = mcc.collectivity_id
            ),

            -- Moyenne d'assiduité par collectivité
            collectivity_assiduity AS (
                SELECT
                    collectivity_id,
                    ROUND(AVG(assiduity_pct), 2) AS avg_assiduity
                FROM member_assiduity
                GROUP BY collectivity_id
            )

            SELECT
                c.id                                                        AS collectivity_id,
                c.name                                                      AS collectivity_name,
                c.number                                                    AS collectivity_number,
                COALESCE(nm.new_count, 0)                                   AS new_members_number,
                CASE
                    WHEN COALESCE(cp.total_members, 0) = 0 THEN 0
                    ELSE ROUND(
                        (COALESCE(cp.current_members, 0)::numeric
                         / cp.total_members::numeric) * 100, 2
                    )
                END                                                         AS overall_percentage,
                COALESCE(ca.avg_assiduity, 100.0)                          AS overall_assiduity
            FROM collectivity c
            LEFT JOIN new_members          nm ON nm.collectivity_id = c.id
            LEFT JOIN current_percentage   cp ON cp.collectivity_id = c.id
            LEFT JOIN collectivity_assiduity ca ON ca.collectivity_id = c.id
            ORDER BY c.name
            """;

        List<CollectivityOverallStatistics> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));  // new_members from
            ps.setDate(2, Date.valueOf(to));    // new_members to
            ps.setString(3, to.toString());     // active_fees eligible_from <=
            ps.setString(4, to.toString());     // expected WEEKLY to
            ps.setString(5, to.toString());     // expected MONTHLY AGE to
            ps.setString(6, to.toString());     // expected MONTHLY AGE to (MONTH)
            ps.setString(7, to.toString());     // expected ANNUALLY AGE to
            ps.setString(8, to.toString());     // paid_per_member_fee creation_date <=
            ps.setDate(9, Date.valueOf(from));  // period_activities from
            ps.setDate(10, Date.valueOf(to));   // period_activities to

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
                stat.setOverallMemberAssiduityPercentage(rs.getDouble("overall_assiduity"));
                result.add(stat);
            }
        }

        return result;
    }
}