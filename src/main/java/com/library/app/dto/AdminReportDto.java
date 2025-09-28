package com.library.app.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AdminReportDto {
    // A list of the most popular books
    private List<Map<String, Object>> mostBorrowedBooks;

    // Key statistics for the dashboard
    private int totalBorrowingsLast30Days;
    private int newUsersLast30Days;
}