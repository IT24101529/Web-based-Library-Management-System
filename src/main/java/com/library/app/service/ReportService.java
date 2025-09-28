package com.library.app.service;

import com.library.app.dto.AdminReportDto;
import com.library.app.repository.BorrowingRepository;
import com.library.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final BorrowingRepository borrowingRepository;
    private final UserRepository userRepository;

    public AdminReportDto generateAdminDashboardReport() {
        // 1. Get the top 5 most borrowed books
        List<Map<String, Object>> mostBorrowed = borrowingRepository.findMostBorrowedBooks(5);

        // 2. Get key statistics
        int borrowingsLastMonth = borrowingRepository.countTotalBorrowingsLast30Days();
        int newUsersLastMonth = userRepository.countNewUsersLast30Days();

        // 3. Assemble the data into our DTO
        AdminReportDto reportDto = new AdminReportDto();
        reportDto.setMostBorrowedBooks(mostBorrowed);
        reportDto.setTotalBorrowingsLast30Days(borrowingsLastMonth);
        reportDto.setNewUsersLast30Days(newUsersLastMonth);

        return reportDto;
    }
}