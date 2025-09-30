package com.example.libraryreporting.dao;

import com.example.libraryreporting.model.StaffLog;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class StaffDao {

    private final JdbcTemplate jdbcTemplate;

    public StaffDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<StaffLog> getStaffAttendanceLogs() {
        String sql = "SELECT u.full_name, sl.action, sl.details, sl.created_at " +
                "FROM SystemLogs sl " +
                "INNER JOIN Users u ON sl.user_id = u.user_id " +
                "INNER JOIN UserRoles ur ON u.user_id = ur.user_id " +
                "INNER JOIN Roles r ON ur.role_id = r.role_id " +
                "WHERE r.role_name = 'STAFF' " +
                "ORDER BY sl.created_at DESC";
        return jdbcTemplate.query(sql, new RowMapper<StaffLog>() {
            @Override
            public StaffLog mapRow(ResultSet rs, int rowNum) throws SQLException {
                StaffLog log = new StaffLog();
                log.setFullName(rs.getString("full_name"));
                log.setAction(rs.getString("action"));
                log.setDetails(rs.getString("details"));
                log.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return log;
            }
        });
    }
}