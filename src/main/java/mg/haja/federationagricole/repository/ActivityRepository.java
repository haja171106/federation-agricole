package mg.haja.federationagricole.repository;

import mg.haja.federationagricole.DTO.CreateActivityMemberAttendance;
import mg.haja.federationagricole.DTO.CreateCollectivityActivity;
import mg.haja.federationagricole.Entity.ActivityMemberAttendance;
import mg.haja.federationagricole.Entity.CollectivityActivity;
import mg.haja.federationagricole.Entity.enums.ActivityType;
import mg.haja.federationagricole.Entity.enums.AttendanceStatus;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class ActivityRepository {

    private final Connection connection;

    public ActivityRepository(Connection connection) {
        this.connection = connection;
    }

    public CollectivityActivity save(String collectivityId,
                                     CreateCollectivityActivity req) throws SQLException {

        if (req.executiveDate != null && req.recurrenceRule != null) {
            throw new IllegalArgumentException(
                    "Cannot provide both executiveDate and recurrenceRule at the same time");
        }

        String id = "ACT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        String dbType = mapActivityTypeToDb(req.activityType);

        Timestamp activityDate;
        String description = null;

        if (req.executiveDate != null) {
            activityDate = Timestamp.valueOf(req.executiveDate.atStartOfDay());
        } else if (req.recurrenceRule != null) {
            activityDate = Timestamp.valueOf(LocalDate.now().atStartOfDay());
            description = String.format(
                    "{\"recurrenceRule\":{\"weekOrdinal\":%d,\"dayOfWeek\":\"%s\"}}",
                    req.recurrenceRule.weekOrdinal,
                    req.recurrenceRule.dayOfWeek
            );
        } else {
            activityDate = Timestamp.valueOf(LocalDate.now().atStartOfDay());
        }

        String sql = """
            INSERT INTO activity
                (id, collectivity_id, type, title, description, date, mandatory)
            VALUES (?, ?, ?, ?, ?, ?, FALSE)
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, collectivityId);
            ps.setString(3, dbType);
            ps.setString(4, req.label);
            if (description != null) {
                ps.setString(5, description);
            } else {
                ps.setNull(5, Types.VARCHAR);
            }
            ps.setTimestamp(6, activityDate);
            ps.executeUpdate();
        }

        if (req.memberOccupationConcerned != null && !req.memberOccupationConcerned.isEmpty()) {
            saveTargetPositions(id, req.memberOccupationConcerned);
        }

        return buildActivityFromRequest(id, req);
    }

    private void saveTargetPositions(String activityId,
                                     List<String> occupations) throws SQLException {
        String sql = """
            INSERT INTO activity_target_position (activity_id, position)
            VALUES (?, ?)
            ON CONFLICT DO NOTHING
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (String occ : occupations) {
                ps.setString(1, activityId);
                ps.setString(2, mapOccupationToDb(occ));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public List<CollectivityActivity> findByCollectivityId(String collectivityId) throws SQLException {

        String sql = """
            SELECT
                a.id,
                a.title       AS label,
                a.type,
                a.date,
                a.description,
                ARRAY_AGG(atp.position ORDER BY atp.position)
                    FILTER (WHERE atp.position IS NOT NULL) AS positions
            FROM activity a
            LEFT JOIN activity_target_position atp ON atp.activity_id = a.id
            WHERE a.collectivity_id = ?
            GROUP BY a.id, a.title, a.type, a.date, a.description
            ORDER BY a.date DESC
            """;

        List<CollectivityActivity> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(mapActivityRow(rs));
            }
        }

        return result;
    }

    public List<ActivityMemberAttendance> saveAttendance(
            String activityId,
            List<CreateActivityMemberAttendance> requests) throws SQLException {

        List<ActivityMemberAttendance> result = new ArrayList<>();

        for (CreateActivityMemberAttendance req : requests) {

            String checkSql = """
                SELECT status FROM presence
                WHERE activity_id = ? AND member_id = ?
                """;

            try (PreparedStatement ps = connection.prepareStatement(checkSql)) {
                ps.setString(1, activityId);
                ps.setString(2, req.memberIdentifier);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String existing = rs.getString("status");
                    if ("PRESENT".equals(existing) || "ABSENT".equals(existing)) {
                        throw new IllegalStateException(
                                "Attendance already confirmed for member: "
                                        + req.memberIdentifier
                                        + " (status=" + existing + ")");
                    }
                }
            }

            String dbStatus = mapAttendanceStatusToDb(req.attendanceStatus);

            String absenceReason = AttendanceStatus.UNDEFINED.equals(req.attendanceStatus)
                    ? "UNDEFINED" : null;

            String presenceId = "PRE-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

            String upsertSql = """
                INSERT INTO presence (id, activity_id, member_id, status, is_visitor, absence_reason)
                VALUES (?, ?, ?, ?, FALSE, ?)
                ON CONFLICT (activity_id, member_id) DO UPDATE
                    SET status         = EXCLUDED.status,
                        absence_reason = EXCLUDED.absence_reason,
                        updated_at     = NOW()
                WHERE presence.status = 'EXCUSED'
                """;

            try (PreparedStatement ps = connection.prepareStatement(upsertSql)) {
                ps.setString(1, presenceId);
                ps.setString(2, activityId);
                ps.setString(3, req.memberIdentifier);
                ps.setString(4, dbStatus);
                if (absenceReason != null) {
                    ps.setString(5, absenceReason);
                } else {
                    ps.setNull(5, Types.VARCHAR);
                }
                ps.executeUpdate();
            }

            ActivityMemberAttendance att = fetchAttendanceForMember(
                    presenceId, req.memberIdentifier, req.attendanceStatus);
            result.add(att);
        }

        return result;
    }

    public List<ActivityMemberAttendance> findAttendance(String collectivityId,
                                                         String activityId) throws SQLException {
        String sql = """
            WITH
            target_positions AS (
                SELECT position FROM activity_target_position WHERE activity_id = ?
            ),

            current_mandate AS (
                SELECT id FROM mandate
                WHERE collectivity_id = ?
                  AND CURRENT_DATE BETWEEN start_date AND end_date
                LIMIT 1
            ),

            collectivity_members AS (
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

            concerned_members AS (
                SELECT cm.*
                FROM collectivity_members cm
                WHERE EXISTS (SELECT 1 FROM target_positions)
                  AND cm.occupation IN (SELECT position FROM target_positions)
                UNION ALL
                SELECT cm.*
                FROM collectivity_members cm
                WHERE NOT EXISTS (SELECT 1 FROM target_positions)
            ),

            recorded AS (
                SELECT p.id AS presence_id, p.member_id, p.status, p.is_visitor
                FROM presence p
                WHERE p.activity_id = ?
            )

            SELECT
                COALESCE(r.presence_id, 'VIRT-' || cm.id) AS id,
                cm.id        AS member_id,
                cm.first_name,
                cm.last_name,
                cm.email,
                cm.occupation,
                COALESCE(
                    CASE r.status
                        WHEN 'PRESENT' THEN 'ATTENDED'
                        WHEN 'ABSENT'  THEN 'MISSING'
                        ELSE 'UNDEFINED'
                    END,
                    'UNDEFINED'
                ) AS attendance_status
            FROM concerned_members cm
            LEFT JOIN recorded r ON r.member_id = cm.id

            UNION ALL

            SELECT
                r.presence_id AS id,
                r.member_id,
                m.first_name,
                m.last_name,
                m.email,
                NULL          AS occupation,
                'ATTENDED'    AS attendance_status
            FROM recorded r
            JOIN member m ON m.id = r.member_id
            WHERE r.status = 'PRESENT'
              AND r.member_id NOT IN (SELECT id FROM concerned_members)

            ORDER BY last_name, first_name
            """;

        List<ActivityMemberAttendance> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, activityId);
            ps.setString(2, collectivityId);
            ps.setString(3, collectivityId);
            ps.setString(4, activityId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ActivityMemberAttendance.MemberDescription desc =
                        new ActivityMemberAttendance.MemberDescription();
                desc.setId(rs.getString("member_id"));
                desc.setFirstName(rs.getString("first_name"));
                desc.setLastName(rs.getString("last_name"));
                desc.setEmail(rs.getString("email"));
                desc.setOccupation(rs.getString("occupation"));

                ActivityMemberAttendance att = new ActivityMemberAttendance();
                att.setId(rs.getString("id"));
                att.setMemberDescription(desc);
                att.setAttendanceStatus(AttendanceStatus.valueOf(rs.getString("attendance_status")));
                result.add(att);
            }
        }

        return result;
    }

    public boolean collectivityExists(String collectivityId) throws SQLException {
        String sql = "SELECT 1 FROM collectivity WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, collectivityId);
            return ps.executeQuery().next();
        }
    }

    public boolean activityExists(String activityId, String collectivityId) throws SQLException {
        String sql = "SELECT 1 FROM activity WHERE id = ? AND collectivity_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, activityId);
            ps.setString(2, collectivityId);
            return ps.executeQuery().next();
        }
    }

    private String mapActivityTypeToDb(String apiType) {
        return switch (apiType) {
            case "MEETING"  -> "GENERAL_MEETING";
            case "TRAINING" -> "JUNIOR_TRAINING";
            case "OTHER"    -> "EXCEPTIONAL";
            default -> throw new IllegalArgumentException("Unknown activityType: " + apiType);
        };
    }

    private ActivityType mapDbToActivityType(String dbType) {
        return switch (dbType) {
            case "GENERAL_MEETING" -> ActivityType.MEETING;
            case "JUNIOR_TRAINING" -> ActivityType.TRAINING;
            default                -> ActivityType.OTHER;
        };
    }

    private String mapOccupationToDb(String apiOcc) {
        return switch (apiOcc) {
            case "JUNIOR"         -> "JUNIOR_MEMBER";
            case "SENIOR"         -> "CONFIRMED_MEMBER";
            case "PRESIDENT"      -> "PRESIDENT";
            case "VICE_PRESIDENT" -> "VICE_PRESIDENT";
            case "TREASURER"      -> "TREASURER";
            case "SECRETARY"      -> "SECRETARY";
            default -> throw new IllegalArgumentException("Unknown occupation: " + apiOcc);
        };
    }

    private String mapDbToOccupation(String dbOcc) {
        if (dbOcc == null) return null;
        return switch (dbOcc) {
            case "JUNIOR_MEMBER"    -> "JUNIOR";
            case "CONFIRMED_MEMBER" -> "SENIOR";
            default                 -> dbOcc;
        };
    }

    private String mapAttendanceStatusToDb(AttendanceStatus status) {
        return switch (status) {
            case ATTENDED  -> "PRESENT";
            case MISSING   -> "ABSENT";
            case UNDEFINED -> "EXCUSED";
        };
    }

    private CollectivityActivity mapActivityRow(ResultSet rs) throws SQLException {
        CollectivityActivity act = new CollectivityActivity();
        act.setId(rs.getString("id"));
        act.setLabel(rs.getString("label"));
        act.setActivityType(mapDbToActivityType(rs.getString("type")));

        Timestamp ts = rs.getTimestamp("date");
        if (ts != null) {
            act.setExecutiveDate(ts.toLocalDateTime().toLocalDate());
        }

        Array posArray = rs.getArray("positions");
        if (posArray != null) {
            String[] positions = (String[]) posArray.getArray();
            List<String> occupations = new ArrayList<>();
            for (String p : positions) {
                if (p != null) occupations.add(mapDbToOccupation(p));
            }
            act.setMemberOccupationConcerned(occupations.isEmpty() ? null : occupations);
        }

        String description = rs.getString("description");
        if (description != null && description.contains("recurrenceRule")) {
            CollectivityActivity.MonthlyRecurrenceRule rule =
                    new CollectivityActivity.MonthlyRecurrenceRule();
            int woIdx = description.indexOf("\"weekOrdinal\":") + 14;
            int woEnd = description.indexOf(",", woIdx);
            rule.setWeekOrdinal(Integer.parseInt(description.substring(woIdx, woEnd).trim()));
            int dowIdx = description.indexOf("\"dayOfWeek\":\"") + 13;
            int dowEnd = description.indexOf("\"", dowIdx);
            rule.setDayOfWeek(description.substring(dowIdx, dowEnd));
            act.setRecurrenceRule(rule);
            act.setExecutiveDate(null);
        }

        return act;
    }

    private CollectivityActivity buildActivityFromRequest(String id,
                                                          CreateCollectivityActivity req) {
        CollectivityActivity act = new CollectivityActivity();
        act.setId(id);
        act.setLabel(req.label);
        act.setActivityType(ActivityType.valueOf(req.activityType));
        act.setMemberOccupationConcerned(req.memberOccupationConcerned);
        act.setExecutiveDate(req.executiveDate);

        if (req.recurrenceRule != null) {
            CollectivityActivity.MonthlyRecurrenceRule rule =
                    new CollectivityActivity.MonthlyRecurrenceRule();
            rule.setWeekOrdinal(req.recurrenceRule.weekOrdinal);
            rule.setDayOfWeek(req.recurrenceRule.dayOfWeek);
            act.setRecurrenceRule(rule);
        }

        return act;
    }

    private ActivityMemberAttendance fetchAttendanceForMember(String presenceId,
                                                              String memberId,
                                                              AttendanceStatus status) throws SQLException {
        String sql = "SELECT first_name, last_name, email FROM member WHERE id = ?";

        ActivityMemberAttendance att = new ActivityMemberAttendance();
        att.setId(presenceId);
        att.setAttendanceStatus(status);

        ActivityMemberAttendance.MemberDescription desc =
                new ActivityMemberAttendance.MemberDescription();
        desc.setId(memberId);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, memberId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                desc.setFirstName(rs.getString("first_name"));
                desc.setLastName(rs.getString("last_name"));
                desc.setEmail(rs.getString("email"));
            }
        }

        att.setMemberDescription(desc);
        return att;
    }
}