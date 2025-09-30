package com.example.libraryreporting.controller;

import com.example.libraryreporting.dao.StaffDao;
import com.example.libraryreporting.model.StaffLog;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class StaffController {

    private final StaffDao staffDao;

    public StaffController(StaffDao staffDao) {
        this.staffDao = staffDao;
    }

    @GetMapping("/staff/attendance")
    public String getStaffAttendance(Model model) {
        List<StaffLog> logs = staffDao.getStaffAttendanceLogs();
        model.addAttribute("logs", logs);
        return "staff-attendance";
    }
}