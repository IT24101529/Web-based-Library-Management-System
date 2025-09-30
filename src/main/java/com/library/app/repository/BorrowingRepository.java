package com.library.app.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import com.library.app.dto.MostBorrowedBookDto;
import org.springframework.jdbc.core.RowMapper;

@Repository
@RequiredArgsConstructor
public class BorrowingRepository {

    private final JdbcTemplate jdbcTemplate;

    // This method is specifically for our report
    public List<MostBorrowedBookDto> findMostBorrowedBooks(int limit) {
        String sql = "SELECT TOP (?) b.title, b.author, COUNT(bo.book_id) as borrow_count " +
                "FROM Borrowings bo " +
                "JOIN Books b ON bo.book_id = b.book_id " +
                "GROUP BY b.title, b.author " +
                "ORDER BY borrow_count DESC";

        RowMapper<MostBorrowedBookDto> rowMapper = (rs, rowNum) -> {
            MostBorrowedBookDto dto = new MostBorrowedBookDto();
            dto.setTitle(rs.getString("title"));
            dto.setAuthor(rs.getString("author"));
            dto.setBorrowCount(rs.getInt("borrow_count"));
            return dto;
        };

        return jdbcTemplate.query(sql, new Object[]{limit}, rowMapper);
    }

    // Method to count borrowings in the last 30 days
    public int countTotalBorrowingsLast30Days() {
        String sql = "SELECT COUNT(*) FROM Borrowings WHERE borrow_date >= DATEADD(day, -30, GETDATE())";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return (count != null) ? count : 0;
    }
}