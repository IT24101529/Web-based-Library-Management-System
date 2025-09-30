package com.example.libraryreporting.dao;

import com.example.libraryreporting.model.BookReport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class BookDao {

    private final JdbcTemplate jdbcTemplate;

    public BookDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<BookReport> getBookAvailability() {
        String sql = "SELECT b.book_id, b.title, b.author, b.quantity " +
                "FROM Books b";
        return jdbcTemplate.query(sql, new RowMapper<BookReport>() {
            @Override
            public BookReport mapRow(ResultSet rs, int rowNum) throws SQLException {
                BookReport report = new BookReport();
                int bookId = rs.getInt("book_id");
                report.setBookId(bookId);
                report.setTitle(rs.getString("title"));
                report.setAuthor(rs.getString("author"));
                int total = rs.getInt("quantity");
                report.setTotalQuantity(total);

                // Calculate active borrowings
                String borrowSql = "SELECT COUNT(*) FROM Borrowings WHERE book_id = ? AND status = 'BORROWED' AND return_date IS NULL";
                int borrowed = jdbcTemplate.queryForObject(borrowSql, Integer.class, bookId);
                report.setAvailableQuantity(total - borrowed);

                return report;
            }
        });
    }
}