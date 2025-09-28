package com.library.app.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class BorrowingRepository {

    private final JdbcTemplate jdbcTemplate;

    // This method is specifically for our report
    public List<Map<String, Object>> findMostBorrowedBooks(int limit) {
        String sql = "SELECT TOP (?) b.title, b.author, COUNT(bo.book_id) as borrow_count " +
                "FROM Borrowings bo " +
                "JOIN Books b ON bo.book_id = b.book_id " +
                "GROUP BY b.title, b.author " +
                "ORDER BY borrow_count DESC";
        return jdbcTemplate.queryForList(sql, limit);
    }

    // Method to count borrowings in the last 30 days
    public int countTotalBorrowingsLast30Days() {
        String sql = "SELECT COUNT(*) FROM Borrowings WHERE borrow_date >= DATEADD(day, -30, GETDATE())";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return (count != null) ? count : 0;
    }
}